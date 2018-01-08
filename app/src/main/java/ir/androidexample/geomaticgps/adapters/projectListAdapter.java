package ir.androidexample.geomaticgps.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import ir.androidexample.geomaticgps.R;
import ir.androidexample.geomaticgps.activities.ShowProjectDetailActivity;
import ir.androidexample.geomaticgps.objects.TableObject;

/**
 * Created by moein on 12/13/17.
 */

public class projectListAdapter extends RecyclerView.Adapter<projectListAdapter.VH>{
    private Context context;
    private ArrayList<TableObject> tableObjects;

    public projectListAdapter(Context c,ArrayList<TableObject> objects){
        this.context = c;
        this.tableObjects = objects;
    }
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(context).inflate(R.layout.row_project,parent,false));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        final TableObject object = (TableObject) tableObjects.get(position);
        holder.p_name.setText(context.getString(R.string.row_p_name)+object.getTable_name());
        holder.p_count.setText(context.getString(R.string.row_p_count)+object.getTable_count());
        holder.p_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowProjectDetailActivity.PROJECT_NAME = object.getTable_name();
                Intent showDetail = new Intent(context,ShowProjectDetailActivity.class);
                context.startActivity(showDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tableObjects.size();
    }

     class VH extends RecyclerView.ViewHolder{
        TextView p_name,p_count;
        Button p_show;
        public VH(View itemView) {
            super(itemView);
            p_name = (TextView) itemView.findViewById(R.id.row_project_name);
            p_count = (TextView) itemView.findViewById(R.id.row_project_count);
            p_show = (Button) itemView.findViewById(R.id.row_project_show);
        }
    }
}
