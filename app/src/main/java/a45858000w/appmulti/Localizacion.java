package a45858000w.appmulti;

import java.io.Serializable;

/**
 * Created by 45858000w on 03/03/17.
 */

public class Localizacion implements Serializable {

    public double longitude;
    public double latitude;
    public String pathPhoto;

    public Localizacion(double longitude, double latitude,String path) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.pathPhoto=path;
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

    public String getPathPhoto() {
        return pathPhoto;
    }

    public void setPathPhoto(String pathPhoto) {
        this.pathPhoto = pathPhoto;
    }

    @Override
    public String toString() {
        return "Localizacion{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", pathPhoto='" + pathPhoto + '\'' +
                '}';
    }
}
