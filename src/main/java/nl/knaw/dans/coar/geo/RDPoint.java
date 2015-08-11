package nl.knaw.dans.coar.geo;

import java.util.Arrays;


public class RDPoint
{
   // bron: https://nl.wikipedia.org/wiki/Rijksdriehoeksco%C3%B6rdinaten
    public static final int X_MIN_M = -7000;
    public static final int X_MAX_M = 300000;
    public static final int Y_MIN_M = 289000;
    public static final int Y_MAX_M = 629000;

    private final int x;
    private final int y;
    private boolean xyExchanged;
    private WGS84Point wgs84Point;
    
    public RDPoint(int x, int y) {
        this(x,  y, true);
    }
    
    public RDPoint(int x, int y, boolean correctXY) {
        if (correctXY) {
            int[] coordinates = {x, y};
            Arrays.sort(coordinates);
            if (coordinates[0] != x) {
                xyExchanged = true;
            }
            this.x = coordinates[0];
            this.y = coordinates[1];
            
        } else {
            this.x = x;
            this.y = y;
        }
    }
    
    public RDPoint(String[] coordinates) {
        this(coordinates[0], coordinates[1]);
    }
    
    public RDPoint(String x, String y) {
        this(Integer.parseInt(x.replaceAll("[^0-9]+", "")), 
                Integer.parseInt(y.replaceAll("[^0-9]+", "")), true);
    }
    
    public RDPoint(String x, String y, boolean correctXY) {
        this(Integer.parseInt(x), Integer.parseInt(y), correctXY);
    }

    public boolean isXyExchanged()
    {
        return xyExchanged;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }
    
    public boolean isXWithinBounds() {
        return x >= X_MIN_M && x <= X_MAX_M;
    }
    
    public boolean isYWithinBounds() {
        return y >= Y_MIN_M && y <= Y_MAX_M;
    }
    
    public boolean isWithinBounds() {
        return isXWithinBounds() && isYWithinBounds();
    }
    
    public WGS84Point asWGS84Point() {
        if (wgs84Point == null) {
            wgs84Point = convert(this);
        }
        return wgs84Point;
    }
    
    public int getDistance(RDPoint other) {
        double xdis = x - other.x;
        double ydis = y - other.y;
        int diagonal = (int) Math.sqrt(xdis * xdis + ydis * ydis);
        int sign = xdis < 0 ? -1: ydis < 0 ? -1 : 1;
        return diagonal * sign;
    }
    
    @Override
    public String toString()
    {
        // east=180222; north=585684; units=m; projection=Dutch National Grid;
        return new StringBuilder()
            .append("east=")
            .append(x)
            .append("; north=")
            .append(y)
            .append("; units=m; projection=Dutch National Grid")
            .toString();
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof RDPoint) {
            RDPoint other = (RDPoint) obj;
            return this.x == other.x && this.y == other.y;
        }
        return false;
    }
    
    public String getGoogleMapsURL() {
        return asWGS84Point().getGoogleMapsURL();
    }
    
    public String getOpenStreetMapsURL() {
        return asWGS84Point().getOpenStreetMapsURL();
    }
    
    public String getOpenStreetMapsSearchURL() {
        return asWGS84Point().getOpenStreetMapsSearchURL();
    }
    
    public String getGeoNamesPostalCodeURL(String username) {
        return asWGS84Point().getGeoNamesPostalCodeURL(username);
    }
    
//  <!-- =================================================================================== -->
//  <!-- RD x, y to WGS84 latitude longitude. See: http://www.regiolab-delft.nl/?q=node/36   -->
//  <!-- =================================================================================== -->
    public static WGS84Point convert(int x, int y) {
        return convert(new RDPoint(x, y));
    }
    
    public static WGS84Point convert(RDPoint point) {
        double p = (point.getX() - 155000.00D)/100000.00D;
        double q = (point.getY() - 463000.00D)/100000.00D;
        double df = ((q*3235.65389D)+(p*p*-32.58297D)+(q*q*-0.24750D)+(p*p*q*-0.84978D)+(q*q*q*-0.06550D)+(p*p*q*q*-0.01709D)
                +(p*-0.00738D)+(p*p*p*p*0.00530D)+(p*p*q*q*q*-0.00039D)+(p*p*p*p*q*0.00033D)+(p*q*-0.00012D))/3600.00D;
        double dl = ((p*5260.52916D)+(p*q*105.94684D)+(p*q*q*2.45656D)+(p*p*p*-0.81885D)+(p*q*q*q*0.05594D)+(p*p*p*q*-0.05607D)
                +(q*0.01199D)+(p*p*p*q*q*-0.00256D)+(p*q*q*q*q*0.00128D)+(q*q*0.00022D)+(p*p*-0.00022D)+(p*p*p*p*p*0.00026D))/3600.00D;
        double lat = (Math.round((52.15517440D+df)*100000000.00D))/100000000.00D;
        double lon = (Math.round((5.387206210D+dl)*100000000.00D))/100000000.00D;
        return new WGS84Point(lat, lon);
    }

}
