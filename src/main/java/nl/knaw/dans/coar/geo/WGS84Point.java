package nl.knaw.dans.coar.geo;

import com.ibm.icu.text.DecimalFormat;

public class WGS84Point
{
    
    private static DecimalFormat decimalFormat = new DecimalFormat("#,##0.000000");
    
    private double lat;
    private double lon;
    
    
    
    // The sixth decimal place is worth up to 0.11 m
    
    public WGS84Point(long lat, long lon) {
        
    }
    
    public WGS84Point(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat()
    {
        return lat;
    }

    public double getLon()
    {
        return lon;
    }
    
    public String latAsString() {
        return decimalFormat.format(lat);
    }
    
    public String lonAsString() {
        return decimalFormat.format(lon);
    }
    
    @Override
    public String toString()
    {
        return new StringBuilder()
            .append(decimalFormat.format(lat))
            .append(",")
            .append(decimalFormat.format(lon))
            .toString();
    }
    
    public String getGoogleMapsURL() {
        // http://maps.google.com/maps?q=51.013766+5.785849
        return new StringBuilder()
            .append("http://maps.google.com/maps?q=")
            .append(latAsString())
            .append("+")
            .append(lonAsString())
            .toString();
    }
    
    public String getOpenStreetMapsURL() {
        // http://www.openstreetmap.org/?mlat=51.013766&mlon=5.785849
        return new StringBuilder()
            .append("http://www.openstreetmap.org/?mlat=")
            .append(latAsString())
            .append("&mlon=")
            .append(lonAsString())
            .toString();
    }
    
    public String getOpenStreetMapsSearchURL() {
        // http://www.openstreetmap.org/search?query=51.013766%2C5.785849#map=12/51.0138/5.7251
        // http://www.openstreetmap.org/search?query=51.013766%2C5.785849&mlat=51.013766&mlon=5.785849#map=12/51.0138/5.7855
        return new StringBuilder()
            .append("http://www.openstreetmap.org/search?query=")
            .append(latAsString())
            .append("%2C")
            .append(lonAsString())
            .append("&mlat=")
            .append(latAsString())
            .append("&mlon=")
            .append(lonAsString())
            .append("#map=15/")
            .append(latAsString())
            .append("/")
            .append(lonAsString())
            .toString();
    }
    
    public String getGeoNamesPostalCodeURL(String username) {
        // http://api.geonames.org/findNearbyPostalCodes?lat=52.713778&lng=4.931245&username=demo
        return new StringBuilder()
            .append("http://api.geonames.org/findNearbyPostalCodes?lat=")
            .append(latAsString())
            .append("&lng=")
            .append(lonAsString())
            .append("&username=")
            .append(username)
            .toString();
    }

}
