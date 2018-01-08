package ir.androidexample.geomaticgps.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.gps.utils.LatLonPoint;
import org.gps.utils.ReferenceEllipsoids;
import org.gps.utils.UTMPoint;
import org.gps.utils.UTMUtils;

import java.util.ArrayList;

import ir.androidexample.geomaticgps.MainActivity;
import ir.androidexample.geomaticgps.R;
import ir.androidexample.geomaticgps.activities.ShowProjectDetailActivity;
import ir.androidexample.geomaticgps.objects.PointObject;
import ir.androidexample.geomaticgps.utils.database.userDatabaseHelper;
import ir.androidexample.geomaticgps.utils.prefrence.GetUserSettings;

/**
 * Created by moein on 12/18/17.
 */

public class pointShowAdapter extends RecyclerView.Adapter<pointShowAdapter.VH>{

    private Context context;
    private ArrayList<PointObject> objects;
    private String TableName;
    private PointObject object;

    public pointShowAdapter(Context c,ArrayList<PointObject> pointObjects,String tableName){
        this.context = c;
        this.objects = pointObjects;
        this.TableName = tableName;
    }

    public pointShowAdapter(Context c){
        this.context = c;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(context).inflate(R.layout.row_point,parent,false));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        object = (PointObject) objects.get(position);
        GetUserSettings userSettings = new GetUserSettings(context);
        int show = Integer.parseInt(userSettings.GetShowCoordinateSystem());
        if (show == 1){
            holder.lat_tv.setText(object.getLatitude());
            holder.lon_tv.setText(object.getLongitude());
        }else if (show == 2){
            LatLonPoint point = new LatLonPoint(Double.valueOf(object.getLatitude()), Double.valueOf(object.getLongitude()));
            UTMPoint utm = UTMUtils.LLtoUTM(ReferenceEllipsoids.WGS_84, point.getLatitude(), point.getLongitude());
            holder.lat_tv.setText(String.valueOf(utm.getEasting()));
            holder.lon_tv.setText(String.valueOf(utm.getNorthing()));
        }
        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDeleteDialog().show();
            }
        });
    }

    public android.support.v7.app.AlertDialog MyDeleteDialog(){
        final userDatabaseHelper helper = new userDatabaseHelper(context);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context)
                .setTitle(R.string.row_delete_dialog_title)
                .setMessage(R.string.row_delete_dialog_dialog_message)
                .setPositiveButton(R.string.delete_project_dialog_p_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        helper.DeleteRow(TableName,object.getId());
                        Intent reset = new Intent(context,ShowProjectDetailActivity.class);
                        reset.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(reset);
                    }
                })
                .setNegativeButton(R.string.delete_project_dialog_n_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        android.support.v7.app.AlertDialog dialog = builder.create();
        return dialog;
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    class VH extends RecyclerView.ViewHolder{
        TextView lat_tv,lon_tv;
        Button delete_btn;
        public VH(View itemView) {
            super(itemView);
            lat_tv = (TextView) itemView.findViewById(R.id.row_point_lat_e);
            lon_tv = (TextView) itemView.findViewById(R.id.row_point_lon_n);
            delete_btn = (Button) itemView.findViewById(R.id.row_point_delete);
        }
    }
}
