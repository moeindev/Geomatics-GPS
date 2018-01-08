package ir.androidexample.geomaticgps.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IInterface;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.gps.utils.LatLonPoint;
import org.gps.utils.LatLonUtils;
import org.gps.utils.ReferenceEllipsoids;
import org.gps.utils.UTMPoint;
import org.gps.utils.UTMUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import ir.androidexample.geomaticgps.utils.database.userDataBaseContract;
import au.com.bytecode.opencsv.CSVWriter;
import ir.androidexample.geomaticgps.R;
import ir.androidexample.geomaticgps.adapters.pointShowAdapter;
import ir.androidexample.geomaticgps.objects.PointObject;
import ir.androidexample.geomaticgps.utils.database.userDatabaseHelper;
import ir.androidexample.geomaticgps.utils.design.EnableHomeButton;
import ir.androidexample.geomaticgps.utils.font.titleChanger;
import ir.androidexample.geomaticgps.utils.permission.checkExternalStorage;
import ir.androidexample.geomaticgps.utils.prefrence.GetUserSettings;

public class ShowProjectDetailActivity extends AppCompatActivity {

    public static String PROJECT_NAME;
    titleChanger titleChanger;
    EnableHomeButton homeButton;
    AlertDialog.Builder DeleteDialogBuilder;
    userDatabaseHelper helper;
    AlertDialog DeleteDialog;
    checkExternalStorage checkExternalStorage;
    GetUserSettings userSettings;
    Cursor rowData;
    AlertDialog.Builder EmptyRowDialogError;
    AlertDialog EmptyRowDialog;
    fileCreator creator;
    SwipeRefreshLayout refreshLayout;
    RecyclerView point_list;
    pointShowAdapter adapter;
    DataLoaderToRecycler dataLoaderToRecycler;
    AlertDialog deleteDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_project_detail);
        titleChanger = new titleChanger(this,getSupportActionBar());
        titleChanger.set(PROJECT_NAME);
        homeButton = new EnableHomeButton(getSupportActionBar());
        homeButton.enable();
        helper = new userDatabaseHelper(this);
        rowData = helper.getRows(PROJECT_NAME);
        checkExternalStorage = new checkExternalStorage(this,this);
        checkExternalStorage.CheckItOut();
        userSettings = new GetUserSettings(this);
        InitDialogs();
        CreateDirectory();
        creator = new fileCreator();
        dataLoaderToRecycler = new DataLoaderToRecycler();
        EmptyRowDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (!creator.isCancelled()){
                    creator.cancel(false);
                }
                if (dataLoaderToRecycler.isCancelled()){
                    dataLoaderToRecycler.cancel(false);
                }
            }
        });
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.row_point_list_ref);
        point_list = (RecyclerView) findViewById(R.id.row_point_show_list);
        point_list.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchToRecycler();
            }
        });
        FetchToRecycler();
    }

    public void FetchToRecycler(){
        rowData = helper.getRows(PROJECT_NAME);
        new DataLoaderToRecycler().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FetchToRecycler();
    }

    public class DataLoaderToRecycler extends AsyncTask<Object,Object,ArrayList<PointObject>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (rowData.getCount() == 0){
                EmptyRowDialog.show();
            }else {
                if (!refreshLayout.isRefreshing()){
                    refreshLayout.setRefreshing(true);
                }
            }

        }

        @Override
        protected ArrayList<PointObject> doInBackground(Object... objects) {
            ArrayList<PointObject> pointObjects = new ArrayList<PointObject>();
            if (rowData.moveToFirst()){
                do {
                    PointObject object = new PointObject();
                    object.setId(rowData.getString(0));
                    object.setLatitude(rowData.getString(1));
                    object.setLongitude(rowData.getString(2));
                    object.setDescription(rowData.getString(3));
                    pointObjects.add(object);
                }while (rowData.moveToNext());
            }
            return pointObjects;
        }

        @Override
        protected void onPostExecute(ArrayList<PointObject> pointObjects) {
            super.onPostExecute(pointObjects);
            adapter = new pointShowAdapter(ShowProjectDetailActivity.this,pointObjects,PROJECT_NAME);
            point_list.setAdapter(adapter);
            if (refreshLayout.isRefreshing()){
                refreshLayout.setRefreshing(false);
            }
        }
    }

    private void InitDialogs(){
        DeleteDialogBuilder = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setIcon(R.drawable.ic_delete_white_24dp)
                .setTitle(R.string.delete_project_dialog_title)
                .setMessage(R.string.delete_project_dialog_message)
                .setPositiveButton(R.string.delete_project_dialog_p_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        helper.DeleteTable(PROJECT_NAME);
                    }
                })
                .setNegativeButton(R.string.delete_project_dialog_n_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        DeleteDialog = DeleteDialogBuilder.create();
        EmptyRowDialogError = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setIcon(R.drawable.ic_empty_24dp)
                .setTitle(R.string.empty_row_dialog_title)
                .setMessage(R.string.empty_row_dialog_message)
                .setPositiveButton(R.string.empty_row_dialog_p_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        add_project();
                    }
                })
                .setNegativeButton(R.string.empty_row_dialog_n_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        EmptyRowDialog = EmptyRowDialogError.create();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkExternalStorage.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_detail_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }else if (id == R.id.detail_delete_project){
            delete_project();
        }else if (id == R.id.detail_save_as_project){
            save_project();
        }else if (id == R.id.detail_add_project){
            add_project();
        }else if (id == R.id.detail_show_in_map){
            show_in_map();
        }else if (id == R.id.detail_guide){
            guide_project();
        }else if (id == R.id.detail_settings){
            Intent settings = new Intent(this,SettingsActivity.class);
            startActivity(settings);
        }
        return super.onOptionsItemSelected(item);
    }

    private void delete_project(){
        DeleteDialog.show();
    }

    private void save_project(){
        new fileCreator().execute();
    }

    private class fileCreator extends AsyncTask<Object,Object,String>{
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ShowProjectDetailActivity.this);
            dialog.setTitle(R.string.p_dialog_create_file);
            dialog.setCancelable(false);
            if (rowData.getCount() == 0){
                EmptyRowDialog.show();
            }else {
                dialog.show();
            }
        }

        @Override
        protected String doInBackground(Object... objects) {
            String check = userSettings.GetSaveCoordinateSystem();
            if (check.equals("")){
                Intent intent = new Intent(ShowProjectDetailActivity.this,SettingsActivity.class);
                startActivity(intent);
            }else {
                int WGSOrLatLon = Integer.parseInt(userSettings.GetSaveCoordinateSystem());
                if (WGSOrLatLon == 1) {
                    String FileName = PROJECT_NAME + ".csv";
                    DirectoryCreator creator = new DirectoryCreator();
                    creator.execute();
                    File dir = new File(Environment.getExternalStorageDirectory(), "/Geo GPS/projects");
                    File ProjectFile = new File(dir, FileName);
                    FileWriter writer = null;
                    CSVWriter csvWriter = null;
                    try {
                        writer = new FileWriter(ProjectFile, false);
                        csvWriter = new CSVWriter(writer);
                        String column[] = {userDataBaseContract.database_contract.ID,
                                userDataBaseContract.database_contract.LATITUDE,
                                userDataBaseContract.database_contract.LONGITUDE,
                                userDataBaseContract.database_contract.DESCRIPTION};
                        csvWriter.writeNext(column);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (rowData.moveToFirst()) {
                        do {
                            try {
                                csvWriter.writeNext(rowData.getString(0) + "," + rowData.getString(1) + "," + rowData.getString(2) + "," + rowData.getString(3) + "\n");
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        } while (rowData.moveToNext());
                        try {
                            csvWriter.flush();
                            csvWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (WGSOrLatLon == 2) {
                    String FileName = PROJECT_NAME + ".csv";
                    DirectoryCreator creator = new DirectoryCreator();
                    creator.execute();
                    File dir = new File(Environment.getExternalStorageDirectory(), "/Geo GPS/projects");
                    File ProjectFile = new File(dir, FileName);
                    FileWriter writer = null;
                    CSVWriter csvWriter = null;
                    try {
                        writer = new FileWriter(ProjectFile, false);
                        csvWriter = new CSVWriter(writer);
                        String columns[] = {"P","E","N","D"};
                        csvWriter.writeNext(columns);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (rowData.moveToFirst()) {
                        do {
                            LatLonPoint point = new LatLonPoint(Double.valueOf(rowData.getString(1)), Double.valueOf(rowData.getString(2)));
                            UTMPoint utm = UTMUtils.LLtoUTM(ReferenceEllipsoids.WGS_84, point.getLatitude(), point.getLongitude());
                            try {
                                csvWriter.writeNext(rowData.getString(0) + "," + String.valueOf(utm.getEasting()) + "," + String.valueOf(utm.getNorthing()) + "," + rowData.getString(3) + "\n");
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        } while (rowData.moveToNext());
                        try {
                            csvWriter.flush();
                            csvWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
            return "Created";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
        }
    }

    private void add_project(){
        LocationActivity.PROJECT_NAME = PROJECT_NAME;
        Intent loc = new Intent(this,LocationActivity.class);
        startActivity(loc);
    }

    private void show_in_map(){
        ShowPointsInMapActivity.PROJECT_NAME = PROJECT_NAME;
        Intent MAP = new Intent(this,ShowPointsInMapActivity.class);
        startActivity(MAP);
    }

    private void guide_project(){
        Intent telegram = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://t.me/geogps/6"));
        startActivity(Intent.createChooser(telegram,"گرفتن راهنما از طریق :"));
    }

    private void CreateDirectory(){
        DirectoryCreator creator = new DirectoryCreator();
        creator.execute();
    }

    private class DirectoryCreator extends AsyncTask<Object,Object,String>{
        @Override
        protected String doInBackground(Object... objects) {
            File MainDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Geo GPS");
            File ProjectDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Geo GPS/projects");
            if (!MainDirectory.exists()){
                if (MainDirectory.mkdir()){
                    Log.d("Main Directory","Created");
                    if (!ProjectDirectory.exists()){
                        if (ProjectDirectory.mkdirs()){
                            Log.d("Project Directory","Created");
                        }
                    }else {
                        Log.d("Project Directory","Exists");
                    }
                }
            }else {
                Log.d("Main Directory","Exists");
            }
            return "Created";
        }
    }
}
