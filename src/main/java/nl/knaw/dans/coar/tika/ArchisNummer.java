package nl.knaw.dans.coar.tika;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity
@Table(name = "tbl_nummer", indexes = {
        @Index(name = "nummer_fedora_identifier_index", columnList="fedora_identifier", unique = false)
        })
public class ArchisNummer implements Serializable, Comparable<ArchisNummer>
{
    
    private static final long serialVersionUID = 8646429603262161456L;
    
    @Id
    @GeneratedValue
    @Column(name = "num_id")
    private Long id;
    
    @Column(name = "fedora_identifier", nullable = false)
    private String identifier;
    
    @ManyToOne(optional = false)
    private TikaProfile parent;
    
    @Column(name = "source")
    private String source;
    
    @Column(name = "nkey")
    private String key;
    
    @Column(name = "value")
    private String value;
    
    @Column(name = "nummer_index")
    private int nummerIndex;
    
    @Column(name = "method")
    private int method;
    
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

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public int getNummerIndex()
    {
        return nummerIndex;
    }

    public void setNummerIndex(int nummerIndex)
    {
        this.nummerIndex = nummerIndex;
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
        if (obj instanceof ArchisNummer) {
            ArchisNummer other = (ArchisNummer) obj;
            return new EqualsBuilder()
                .append(this.key, other.key)
                .append(this.value, other.value)
                .isEquals();
        }
        return false;
    }
    
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
            .append(key)
            .append(value)
            .toHashCode();
    }
    
    @Override
    public String toString()
    {
        return new StringBuilder()
            .append("nummerIndex=").append(nummerIndex)
            .append(" key=").append(key)
            .append(" value=").append(value)
            .append(" source=").append(source)
            .append(" method=").append(method)
            .toString();
    }

    @Override
    public int compareTo(ArchisNummer o)
    {
        return this.nummerIndex - o.nummerIndex;
    }

}
