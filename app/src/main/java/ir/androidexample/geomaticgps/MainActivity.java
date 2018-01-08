package ir.androidexample.geomaticgps;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import co.ronash.pushe.Pushe;
import ir.androidexample.geomaticgps.activities.ConvertActivityActivity;
import ir.androidexample.geomaticgps.activities.SettingsActivity;
import ir.androidexample.geomaticgps.adapters.projectListAdapter;
import ir.androidexample.geomaticgps.dialogs.CreateTableDialog;
import ir.androidexample.geomaticgps.objects.TableObject;
import ir.androidexample.geomaticgps.utils.database.userDatabaseHelper;
import ir.androidexample.geomaticgps.utils.database.userDataBaseContract;
import ir.androidexample.geomaticgps.utils.font.titleChanger;
import ir.androidexample.geomaticgps.utils.permission.CheckLocationPermission;
import ir.androidexample.geomaticgps.utils.permission.checkExternalStorage;
import ir.androidexample.geomaticgps.utils.prefrence.FirstTimeChecker;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private SwipeRefreshLayout project_refresh;
    private RecyclerView project_list;
    private checkExternalStorage checkExternalStorage;
    private CheckLocationPermission checkLocationPermission;
    String PACKAGE_NAME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Pushe.initialize(this,true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        PACKAGE_NAME = getPackageName();
        project_refresh = (SwipeRefreshLayout) findViewById(R.id.sw_project_ref);
        project_list = (RecyclerView) findViewById(R.id.rec_project_list);
        project_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setSimpleData();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_project);
        CreateTableDialog createTableDialog = new CreateTableDialog(MainActivity.this);
        final android.support.v7.app.AlertDialog builder = createTableDialog.table_dialog().create();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.show();
                Snackbar.make(view, R.string.dialog_is_open, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                setSimpleData();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                setSimpleData();
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //GridSpacingItemDecoration gridSpacingItemDecoration = new GridSpacingItemDecoration(2,15,true);
        //project_list.addItemDecoration(gridSpacingItemDecoration);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        project_list.setItemAnimator(new DefaultItemAnimator());
        project_list.setLayoutManager(layoutManager);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        titleChanger titleChanger = new titleChanger(MainActivity.this,getSupportActionBar());
        titleChanger.set(getResources().getString(R.string.app_name));
        checkExternalStorage = new checkExternalStorage(this,this);
        checkLocationPermission = new CheckLocationPermission(this,this);
        checkExternalStorage.CheckItOut();
        checkLocationPermission.CheckItOut();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkExternalStorage.onRequestPermissionsResult(requestCode,permissions,grantResults);
        checkLocationPermission.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSimpleData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setSimpleData();
    }

    public void setSimpleData(){
        FirstTimeChecker checker = new FirstTimeChecker(MainActivity.this);
        userDatabaseHelper helper = new userDatabaseHelper(this);
        if (checker.IsFirstTime()){
            for (int i = 0 ; i < userDataBaseContract.SIMPLE_LATITUDE.length;i++){
                helper.SimpleDataImport(userDataBaseContract.SIMPLE_LATITUDE[i],
                        userDataBaseContract.SIMPLE_LONGITUDE[i]);
            }
            checker.ChangeFirstTime(false);

        }else {
            getProjectNames();
        }
    }

    public void getProjectNames(){
        GetArrayOfStrings arrayOfStrings = new GetArrayOfStrings();
        arrayOfStrings.execute();
    }

    private class GetArrayOfStrings extends AsyncTask<Object,Object,ArrayList<TableObject>>{
        userDatabaseHelper helper = new userDatabaseHelper(MainActivity.this);

        @Override
        protected void onProgressUpdate(Object... values) {
            if (!project_refresh.isRefreshing()){
                project_refresh.setRefreshing(true);
            }
        }

        @Override
        protected ArrayList<TableObject> doInBackground(Object... objects) {
            ArrayList<TableObject> tableObjects = new ArrayList<TableObject>();
            Cursor c = helper.getTableNames();
            if (c.moveToFirst()){
                do {
                    TableObject tableObject = new TableObject();
                    tableObject.setTable_name(c.getString(c.getColumnIndex("name")));
                    tableObject.setTable_count(String.valueOf(helper.getTableRowCounts(c.getString(c.getColumnIndex("name")))));
                    tableObjects.add(tableObject);
                }while (c.moveToNext());
            }
            c.close();
            return tableObjects;
        }

        @Override
        protected void onPostExecute(ArrayList<TableObject> objects) {
            project_refresh.setRefreshing(false);
            project_list.setAdapter(new projectListAdapter(MainActivity.this,objects));
            helper.close();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            OpenSettings();
        }

        return super.onOptionsItemSelected(item);
    }
    private void OpenSettings(){
        Intent set = new Intent(this, SettingsActivity.class);
        startActivity(set);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_convert_coordinate){
            Intent coordinate_transform = new Intent(this, ConvertActivityActivity.class);
            startActivity(coordinate_transform);
        } else if (id == R.id.nav_settings) {
            OpenSettings();
        } else if (id == R.id.nav_guide){
            Intent telegram = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://t.me/geogps/6"));
            startActivity(Intent.createChooser(telegram,"گرفتن راهنما از طریق :"));
        } else if (id == R.id.nav_about) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.title_ab)
                    .setMessage(R.string.title_ms)
                    .setIcon(R.drawable.ic_launcher_background)
                    .setPositiveButton(R.string.ok_ms, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
        } else if (id == R.id.nav_rate_us) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //intent.setPackage("ir.tgbs.android.iranapp");
            intent.setData(Uri.parse("http://iranapps.ir/app/"+PACKAGE_NAME+"?a=comment&r=5"));
            startActivity(intent);
        } else if (id == R.id.nav_other_apps){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //intent.setPackage("ir.tgbs.android.iranapp");
            intent.setData(Uri.parse("http://iranapps.ir/user/محمد معین عبدی"));
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //intent.setPackage("ir.tgbs.android.iranapp");
            intent.setData(Uri.parse("http://iranapps.ir/app/"+PACKAGE_NAME));
            startActivity(intent);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
