package nl.knaw.dans.coar.rdb;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.joda.time.DateTime;

@MappedSuperclass
public abstract class DBEntity implements Entity
{

    private static final long serialVersionUID = -4182350384996292535L;


    @Column(name = "recordcreationdate", nullable = false)
    private Date recordCreationDate;
    
    @Version
    private Date recordlastmodified;

    /**
     * Creates a new Entity with its creationDate set to the current system time.
     */
    public DBEntity()
    {
        recordCreationDate = new Date();
    }
    
    @Override
    public void updateLastModified()
    {
        recordlastmodified = new Date();
    }

    public boolean isPersisted()
    {
        return getId() != null;
    }

    public boolean isTransient()
    {
        return getId() == null;
    }

    public DateTime getRecordCreationDate()
    {
        return convert(recordCreationDate);
    }
    
    public DateTime getRecordLastModified()
    {
        return convert(recordlastmodified);
    }

    public static DateTime convert(Date date)
    {
        if (date == null)
        {
            return null;
        }
        else
        {
            return new DateTime(date.getTime());
        }
    }

    public static Date convert(DateTime dt)
    {
        if (dt == null)
        {
            return null;
        }
        else
        {
            return dt.toDate();
        }
    }

}
