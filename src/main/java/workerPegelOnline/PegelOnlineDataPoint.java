package workerPegelOnline;

public final class PegelOnlineDataPoint {
	
    private String name;
    
    private double lon;
    
    private double lat;

    private double meas;

    private double flood;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    
    public double getMeas() {
        return meas;
    }
    
    public void setMeas(double meas) {
        this.meas = meas;
    }
    
    public double getFlood() {
        return flood;
    }

    public void setFlood(double flood) {
            this.flood = flood;
    }
    
}
