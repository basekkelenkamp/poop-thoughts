package nl.hr.cmtprg037.poopthoughts;

import java.io.Serializable;

public class Post implements Serializable {


    //Init
    private static int id;
    public String name = "";
    public String message = "";
    public double latitude = 0;
    public double longitude = 0;

    public Post(String name_, String message_) {
        id++;
        name = name_;
        message = message_;
    }

    public static int getId() {
        return id;
    }

    @Override
    public String toString() {

        return name + ": " + message;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLocation(double latitude_, double longitude_) {
        latitude = latitude_;
        longitude = longitude_;
    }
}
