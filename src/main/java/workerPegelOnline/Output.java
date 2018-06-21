package workerPegelOnline;

public final class Output {

    private Point point;

    private Boolean bool;

    private String name;

    private double lat;

    private double lon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
         this.name = name;
    }

    public Boolean getBool() {
        return bool;
    }

    public void setBool(Boolean bool) {
         this.bool = bool;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
         this.lat = lat;
    }

}
