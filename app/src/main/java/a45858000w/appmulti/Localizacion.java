package a45858000w.appmulti;

import java.io.Serializable;

/**
 * Created by 45858000w on 03/03/17.
 */

public class Localizacion implements Serializable {

    public double longitude;
    public double latitude;

    public Localizacion(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Localizacion{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
