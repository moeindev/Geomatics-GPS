package ir.androidexample.geomaticgps.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

import ir.androidexample.geomaticgps.MainActivity;
import ir.androidexample.geomaticgps.objects.TableObject;

/**
 * Created by moein on 12/13/17.
 */

public class userDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    public userDatabaseHelper(Context context) {
        super(context, userDataBaseContract.DB_NAME, null, userDataBaseContract.DB_VERSION);
        Log.d(userDataBaseContract.DB_TAG,"Database created/opened...");
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(userDataBaseContract.QUERY.SIMPLE_QUERY);
        Log.d(userDataBaseContract.DB_TAG,"Simple data table Created/Opened...");
    }

    public Cursor getTableNames(){
        return getReadableDatabase().rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name!='android_metadata' AND name!='sqlite_sequence' order by name",null);
    }

    public int getTableRowCounts(String table){
        String[] columns = new String[]{userDataBaseContract.database_contract.ID
        ,userDataBaseContract.database_contract.LATITUDE,
        userDataBaseContract.database_contract.LONGITUDE,
        userDataBaseContract.database_contract.DESCRIPTION};
        Cursor database = getReadableDatabase().query(table,columns,null,null,null,null,null);
        int po = database.getCount();
        database.close();
        return po;
    }

    public Cursor getRows(String TABLE_NAME){
        String[] projections = new String[]{userDataBaseContract.database_contract.ID
        ,userDataBaseContract.database_contract.LATITUDE
        ,userDataBaseContract.database_contract.LONGITUDE
        ,userDataBaseContract.database_contract.DESCRIPTION};
        return getReadableDatabase().query(TABLE_NAME,projections,null,null,null,null,null);
    }

    public void SimpleDataImport(String Latitude ,String Longitude){
        ContentValues values = new ContentValues();
        values.put(userDataBaseContract.database_contract.LATITUDE,Latitude);
        values.put(userDataBaseContract.database_contract.LONGITUDE,Longitude);
        values.put(userDataBaseContract.database_contract.DESCRIPTION,"Description");
        SQLiteDatabase database = this.getWritableDatabase();
        database.insert(userDataBaseContract.database_contract.TABLE_NAME,null,values);
        Log.d(userDataBaseContract.DB_TAG,"Simple data inserted...");
    }

    public void AddRow(String TB_NAME,String lat,String lon,String des){
        ContentValues CV = new ContentValues();
        CV.put(userDataBaseContract.database_contract.LATITUDE,lat);
        CV.put(userDataBaseContract.database_contract.LONGITUDE,lon);
        CV.put(userDataBaseContract.database_contract.DESCRIPTION,des);
        SQLiteDatabase database = this.getWritableDatabase();
        database.insert(TB_NAME,null,CV);
        Log.d(userDataBaseContract.DB_TAG,"Row with lat="+lat+" and lon="+lon+" was added to "+TB_NAME);
    }

    public void CreateTable(String TABLE_NAME){
        TableCreator tableCreator = new TableCreator(TABLE_NAME,context);
        tableCreator.execute();
        Log.d(userDataBaseContract.DB_TAG,"TABLE Created...");
    }
    class TableCreator extends AsyncTask<String , String ,String>{
        private String TableName;
        private Context context;
        public TableCreator(String tn,Context c){
            this.TableName = tn;
            context = c;
        }
        @Override
        protected String doInBackground(String... strings) {
            userDatabaseHelper helper = new userDatabaseHelper(context);
            SQLiteDatabase database = helper.getWritableDatabase();
            String MY_QUERY = "CREATE TABLE if not exists "+TableName+userDataBaseContract.QUERY.FINAL_QUERY;
            database.execSQL(MY_QUERY);
            helper.close();
            return "Creating Table "+TableName;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
    public void DeleteTable(String TABLE_NAME){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        Log.d(userDataBaseContract.DB_TAG,"Table Deleted : "+TABLE_NAME);
        Intent DirectToMain = new Intent(context, MainActivity.class);
        DirectToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(DirectToMain);
    }
    public void DeleteRow(String TableName,String where){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TableName,userDataBaseContract.database_contract.ID+"="+where,null);
        Log.d(userDataBaseContract.DB_TAG,"row from "+TableName+" in "+where+" deleted");
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(userDataBaseContract.QUERY.SIMPLE_QUERY_UPGRADE);
        Log.d(userDataBaseContract.DB_TAG,"Database upgraded");
    }
}
