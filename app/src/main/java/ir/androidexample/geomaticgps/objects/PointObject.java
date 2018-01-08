package ir.androidexample.geomaticgps.objects;

/**
 * Created by moein on 12/16/17.
 */

public class PointObject {
    private String id;
    private String latitude;
    private String longitude;
    private String description;
    public PointObject(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PointObject(String id, String latitude, String longitude, String description){
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = longitude;
    }
}
