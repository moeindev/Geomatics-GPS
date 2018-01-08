package ir.androidexample.geomaticgps.utils.database;

/**
 * Created by moein on 12/13/17.
 */

public class userDataBaseContract {
    public static final String DB_NAME = "GeoGPS.db";
    public static int DB_VERSION = 1;
    public static String DB_TAG = "user Database Contract";

    public static String[] SIMPLE_LATITUDE = new String[]{
            "34","35","36","37","34"
    };
    public static String[] SIMPLE_LONGITUDE = new String[]{
            "47","46","45","47","46"
    };
    public static final class database_contract{
        public static final String TABLE_NAME = "simple";
        public static final String ID = "_id";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String DESCRIPTION = "description";
    }
    public static final class QUERY{
        public static final String SIMPLE_QUERY = "CREATE TABLE "+database_contract.TABLE_NAME+"("+
                database_contract.ID+" integer primary key autoincrement,"+
                database_contract.LATITUDE+" TEXT,"+
                database_contract.LONGITUDE+" TEXT,"+
                database_contract.DESCRIPTION+" TEXT);";

        public static final String SIMPLE_QUERY_UPGRADE = "CREATE TABLE if not exists "+database_contract.TABLE_NAME+"("+
                database_contract.ID+" integer primary key autoincrement,"+
                database_contract.LATITUDE+" TEXT,"+
                database_contract.LONGITUDE+" TEXT,"+
                database_contract.DESCRIPTION+" TEXT);";

        public static final String FINAL_QUERY = "("+
                database_contract.ID+" integer primary key autoincrement,"+
                database_contract.LATITUDE+" TEXT,"+
                database_contract.LONGITUDE+" TEXT,"+
                database_contract.DESCRIPTION+" TEXT);";
    }
}
