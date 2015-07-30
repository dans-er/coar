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
@Table(name = "tikameta", indexes = {
        @Index(name = "tikameta_fedora_identifier_index", columnList="fedora_identifier", unique = false),
        @Index(name = "tikameta_meta_name_index", columnList="meta_name", unique = false)
        })
public class TikaMeta implements Serializable
{

    private static final long serialVersionUID = 5854329616229134441L;
    
    @Id
    @GeneratedValue
    @Column(name = "meta_id")
    private Long id;
    
    @Column(name = "fedora_identifier", nullable = false)
    private String identifier;
    
    @Column(name = "meta_name")
    private String metaName;
    
    @Column(name = "meta_value")
    private String metaValue;
    
    @ManyToOne(optional = false)
    private TikaProfile parent;
    
    public TikaMeta()
    {
        
    }
    
    public TikaMeta(String name, String content) {
        this.metaName = name;
        this.metaValue = content;
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

    public String getMetaName()
    {
        return metaName;
    }

    public void setMetaName(String metaName)
    {
        this.metaName = metaName;
    }

    public String getMetaContent()
    {
        return metaValue;
    }

    public void setMetaContent(String metaContent)
    {
        this.metaValue = metaContent;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(obj == this) return true;
        if(obj == null) return false;
        if (obj instanceof TikaMeta) {
            TikaMeta other = (TikaMeta) obj;
            return new EqualsBuilder()
                .append(identifier, other.identifier)
                .append(metaName, other.metaName)
                .append(metaValue, other.metaValue)
                .isEquals();
        }
        return false;
    }
    
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
            .append(identifier)
            .append(metaName)
            .append(metaValue)
            .toHashCode();
    }
    
}
