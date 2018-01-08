package ir.androidexample.geomaticgps.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ir.androidexample.geomaticgps.MainActivity;
import ir.androidexample.geomaticgps.utils.database.userDatabaseHelper;
import ir.androidexample.geomaticgps.R;

public class CreateTableDialog{
    private Context context;
    String TABLE_NAME;
    public CreateTableDialog(@NonNull Context context) {
        this.context = context;
    }
    public AlertDialog.Builder table_dialog(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.create_table_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.dialog_table_title));
        builder.setView(dialogView);
        final EditText table_name_input = (EditText) dialogView.findViewById(R.id.table_name_input);
        builder.setPositiveButton(R.string.dialog_table_create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (table_name_input.getText().toString().isEmpty()){
                    Toast.makeText(context,R.string.dialog_warning_empty,Toast.LENGTH_SHORT).show();
                }else if (table_name_input.getText().toString().contains(" ")){
                    Toast.makeText(context,R.string.dialog_warning_space,Toast.LENGTH_SHORT).show();
                }else {
                    userDatabaseHelper helper = new userDatabaseHelper(context);
                    TABLE_NAME = table_name_input.getText().toString();
                    helper.CreateTable(TABLE_NAME);
                }
            }
        });
        builder.setNegativeButton(R.string.dialog_table_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        return builder;
    }

    public String getTABLE_NAME(){
        return TABLE_NAME;
    }
}
