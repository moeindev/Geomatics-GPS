package ir.androidexample.geomaticgps.objects;

/**
 * Created by moein on 12/13/17.
 */

public class TableObject {

    private String table_name;
    private String table_count;

    public TableObject(){

    }
    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getTable_count() {
        return table_count;
    }

    public void setTable_count(String table_count) {
        this.table_count = table_count;
    }

    public TableObject(String table_name, String table_count){
        this.table_name = table_name;
        this.table_count = table_count;
    }
}
