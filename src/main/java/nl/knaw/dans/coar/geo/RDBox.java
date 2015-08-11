package nl.knaw.dans.coar.geo;

import java.util.Arrays;

public class RDBox
{

    private final int north;
    private final int east;
    private final int south;
    private final int west;
    
    private RDPoint centerPoint;
    
    public RDBox(int north, int east, int south, int west) {
        // we assume: N > S > E > W
        int[] limits = {north, east, south, west};
        Arrays.sort(limits);
        this.north = limits[3];
        this.east = limits[1];
        this.south = limits[2];
        this.west = limits[0];
    }
    
    public RDBox(String north, String east, String south, String west) {
        this(Integer.parseInt(north.replaceAll("[^0-9]+", "")), 
                Integer.parseInt(east.replaceAll("[^0-9]+", "")), 
                Integer.parseInt(south.replaceAll("[^0-9]+", "")), 
                Integer.parseInt(west.replaceAll("[^0-9]+", "")));
    }

    public int getNorthLimit()
    {
        return north;
    }

    public int getEastLimit()
    {
        return east;
    }

    public int getSouthLimit()
    {
        return south;
    }

    public int getWestLimit()
    {
        return west;
    }
    
    public RDPoint getCenterPoint() {
        if (centerPoint == null) {
            centerPoint = new RDPoint((east + west)/2, (north + south)/2);
        }
        return centerPoint;
    }
    
    @Override
    public String toString()
    {
        // northlimit=399055; eastlimit=111470; southlimit=399025; westlimit=111350; units=m; projection=Dutch National Grid;
        return new StringBuilder()
            .append("northlimit=")
            .append(north)
            .append("; eastlimit=")
            .append(east)
            .append("; southlimit=")
            .append(south)
            .append("; westlimit=")
            .append(west)
            .append("; units=m; projection=Dutch National Grid")
            .toString();
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof RDBox) {
            RDBox other = (RDBox) obj;
            return this.north == other.north && this.east == other.east
                    && this.south == other.south && this.west == other.west;
        }
        return false;
    }
}
