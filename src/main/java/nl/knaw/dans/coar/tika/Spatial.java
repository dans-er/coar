package nl.knaw.dans.coar.tika;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import nl.knaw.dans.coar.geo.RDBox;
import nl.knaw.dans.coar.geo.RDPoint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity
@Table(name = "tbl_spatial", indexes = {
        @Index(name = "spatial_fedora_identifier_index", columnList="fedora_identifier", unique = false)
        })
public class Spatial implements Serializable, Comparable<Spatial>
{

    private static final long serialVersionUID = -2827629056893908065L;
    
    @Id
    @GeneratedValue
    @Column(name = "spatial_id")
    private Long id;
    
    @Column(name = "fedora_identifier", nullable = false)
    private String identifier;
    
    @ManyToOne(optional = false)
    private TikaProfile parent;
    
    @Column(name = "source")
    private String source;
    
    @Column(name = "spatial_type")
    private String spatialType;
    
    @Column(name = "coor_x")
    private int coorX;
    
    @Column(name = "coor_y")
    private int coorY;
    
    @Column(name = "within_bounds_x")
    private boolean withinBoundsX;
    
    @Column(name = "within_bounds_y")
    private boolean withinBoundsY;
    
    @Column(name = "xy_exchanged")
    private boolean xyExchanged;
    
    @Column(name = "lat")
    private String lat;
    
    @Column(name = "lon")
    private String lon;
    
    @Column(name = "limit_north")
    private int north;
    
    @Column(name = "limit_east")
    private int east;
    
    @Column(name = "limit_south")
    private int south;
    
    @Column(name = "limit_west")
    private int west;
    
    @Column(name = "point_index")
    private int pointIndex;
    
    @Column(name = "method")
    private int method;
    
    public Spatial() {}
    
    public Spatial(String source, RDPoint point) {
        this.spatialType = "point";
        this.source = source;
        setPoint(point);
    }
    
    public Spatial(String source, RDBox box) {
        this.spatialType = "box";
        this.source = source;
        this.north = box.getNorthLimit();
        this.east = box.getEastLimit();
        this.south = box.getSouthLimit();
        this.west = box.getWestLimit();
        setPoint(box.getCenterPoint());
    }

    public void setPoint(RDPoint point)
    {
        this.coorX = point.getX();
        this.coorY = point.getY();
        this.withinBoundsX = point.isXWithinBounds();
        this.withinBoundsY = point.isYWithinBounds();
        this.xyExchanged = point.isXyExchanged();
        this.lat = point.asWGS84Point().latAsString();
        this.lon = point.asWGS84Point().lonAsString();
    }
    
    public Long getId()
    {
        return id;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    protected void setIdentifier(String identifier, TikaProfile parent)
    {
        this.identifier = identifier;
        this.parent = parent;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public int getCoorX()
    {
        return coorX;
    }

    public void setCoorX(int coorX)
    {
        this.coorX = coorX;
    }

    public int getCoorY()
    {
        return coorY;
    }

    public void setCoorY(int coorY)
    {
        this.coorY = coorY;
    }

    public boolean isWithinBoundsX()
    {
        return withinBoundsX;
    }

    public void setWithinBoundsX(boolean withinBoundsX)
    {
        this.withinBoundsX = withinBoundsX;
    }

    public boolean isWithinBoundsY()
    {
        return withinBoundsY;
    }

    public void setWithinBoundsY(boolean withinBoundsY)
    {
        this.withinBoundsY = withinBoundsY;
    }

    public boolean isXyExchanged()
    {
        return xyExchanged;
    }

    public void setXyExchanged(boolean xyExchanged)
    {
        this.xyExchanged = xyExchanged;
    }

    public String getSpatialType()
    {
        return spatialType;
    }

    public void setSpatialType(String spatialType)
    {
        this.spatialType = spatialType;
    }

    public String getLat()
    {
        return lat;
    }

    public void setLat(String lat)
    {
        this.lat = lat;
    }

    public String getLon()
    {
        return lon;
    }

    public void setLon(String lon)
    {
        this.lon = lon;
    }

    public int getNorth()
    {
        return north;
    }

    public void setNorth(int north)
    {
        this.north = north;
    }

    public int getEast()
    {
        return east;
    }

    public void setEast(int east)
    {
        this.east = east;
    }

    public int getSouth()
    {
        return south;
    }

    public void setSouth(int south)
    {
        this.south = south;
    }

    public int getWest()
    {
        return west;
    }

    public void setWest(int west)
    {
        this.west = west;
    }
    
    public int getPointIndex()
    {
        return pointIndex;
    }

    public void setPointIndex(int pointIndex)
    {
        this.pointIndex = pointIndex;
    }

    public int getMethod()
    {
        return method;
    }

    public void setMethod(int method)
    {
        this.method = method;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Spatial) {
            Spatial other = (Spatial) obj;
            return new EqualsBuilder()
                .append(other.coorX, this.coorX)
                .append(other.coorY, this.coorY)
                .append(other.east, this.east)
                .append(other.north, this.north)
                .append(other.south, this.south)
                .append(other.west, this.west)
                .append(other.source, this.source)
                .append(other.spatialType, this.spatialType)
                .isEquals();
        }
        return false;
    }
    
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
            .append(coorX)
            .append(coorY)
            .append(east)
            .append(north)
            .append(south)
            .append(west)
            .append(source)
            .append(spatialType)
            .toHashCode();
    }
    
    @Override
    public String toString()
    {
        return new StringBuilder()
            .append("pointIndex=").append(pointIndex)
            .append(" x=").append(coorX)
            .append(" y=").append(coorY)
            //.append(" north=").append(north)
            //.append(" east=").append(east)
            //.append(" south=").append(south)
            //.append(" west=").append(west)
            .append(" source=").append(source)
            .append(" spatialType=").append(spatialType)
            .append(" method=").append(method)
            .toString();
    }

    @Override
    public int compareTo(Spatial o)
    {
        return this.pointIndex - o.pointIndex;
    }
    
    
}
