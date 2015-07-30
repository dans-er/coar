package nl.knaw.dans.coar.tika;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import nl.knaw.dans.coar.fedora.EMD;
import nl.knaw.dans.coar.geo.RDBox;
import nl.knaw.dans.coar.geo.RDPoint;
import nl.knaw.dans.coar.rdb.DBEntity;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "profile", indexes = {
        @Index(name = "profile_fedora_identifier_index", columnList="fedora_identifier", unique = true)})
public class TikaProfile extends DBEntity
{

    private static final long serialVersionUID = -5025773153112962980L;
    
    @Id
    @GeneratedValue
    @Column(name = "tikaprofile_id")
    private Long id;
    
    @NaturalId
    @Column(name = "fedora_identifier", nullable = false, unique = true)
    private String identifier;
    
    @Column(name = "datasetId", nullable = false)
    private String datasetId;
    
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade({CascadeType.ALL})
    private Set<TikaMeta> metadata;
    
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade({CascadeType.ALL})
    private Set<Spatial> spatials;
    
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade({CascadeType.ALL})
    private Set<ArchisNummer> archisNummers;
    
    @Column(name = "type")
    private String type;
    
    @Column(name = "subtype")
    private String subtype;
    
    @Column(name = "parameters")
    private String parameters;
    
    @Column(name = "ds_label")
    private String dsLabel;
    
    @Column(name = "ds_mediatype")
    private String dsMediatype;
    
    @Column(name = "ds_state")
    private String dsState;
    
    @Column(name = "ds_size")
    private long dsSize;
    
    @Column(name = "ds_creation_date")
    private Date dsCreationDate;
    
    @Column(name = "emd_archis_vondst")
    private String emdArchisVondst;
    
    @Column(name = "emd_archis_waarneming")
    private String emdArchisWaarneming;
    
    @Column(name = "emd_archis_omn")
    private String emdArchisOMN;
    
    @Column(name = "emd_title")
    private String emdTitle;
    
    @Column(name = "emd_rightsholder")
    private String emdRightsHolder;
    
    
    
    protected TikaProfile() {}
    
    public TikaProfile(String fileId, String datasetId) {
        setIdentifier(fileId);
        setDatasetId(datasetId);
    }

    @Override
    public Long getId()
    {
        return id;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }

    public String getDatasetId()
    {
        return datasetId;
    }

    public void setDatasetId(String datasetId)
    {
        this.datasetId = datasetId;
    }

    public String getDsLabel()
    {
        return dsLabel;
    }

    public void setDsLabel(String dsLabel)
    {
        this.dsLabel = dsLabel;
    }

    public String getDsMediatype()
    {
        return dsMediatype;
    }

    public void setDsMediatype(String dsMediatype)
    {
        this.dsMediatype = dsMediatype;
    }

    public String getDsState()
    {
        return dsState;
    }

    public void setDsState(String dsState)
    {
        this.dsState = dsState;
    }

    public long getDsSize()
    {
        return dsSize;
    }

    public void setDsSize(long dsSize)
    {
        this.dsSize = dsSize;
    }

    public Date getDsCreationDate()
    {
        return dsCreationDate;
    }

    public void setDsCreationDate(Date dsCreationDate)
    {
        this.dsCreationDate = dsCreationDate;
    }
    
    public void setEmd(EMD emd) {
        setEmdArchisVondst(emd.getArchisVondstMelding());
        setEmdArchisWaarneming(emd.getArchisWaarneming());
        setEmdArchisOMN(emd.getArchisOnderzoeksMeldingsNummer());
        setEmdRightsHolder(emd.getFirstRightsHolder());
        setEmdTitle(emd.getFirstTitle());
        for (String type : emd.getDCMITypes()) {
            TikaMeta tm = new TikaMeta("emdDCMIType", type);
            addMeta(tm);
        }
        for (RDPoint point : emd.getSpatialPoints()) {
            Spatial spatial = new Spatial("emd", point);
            addSpatial(spatial);
        }
        for (RDBox box : emd.getSpatialBoxes()) {
            Spatial spatial = new Spatial("emd", box);
            addSpatial(spatial);
        }
    }

    public String getEmdArchisVondst()
    {
        return emdArchisVondst;
    }

    public void setEmdArchisVondst(String emdArchisVondst)
    {
        this.emdArchisVondst = emdArchisVondst;
    }

    public String getEmdArchisWaarneming()
    {
        return emdArchisWaarneming;
    }

    public void setEmdArchisWaarneming(String emdArchisWaarneming)
    {
        this.emdArchisWaarneming = emdArchisWaarneming;
    }

    public String getEmdArchisOMN()
    {
        return emdArchisOMN;
    }

    public void setEmdArchisOMN(String emdArchisOMN)
    {
        this.emdArchisOMN = emdArchisOMN;
    }

    public String getEmdTitle()
    {
        return emdTitle;
    }

    public void setEmdTitle(String emdTitle)
    {
        this.emdTitle = emdTitle;
    }

    public String getEmdRightsHolder()
    {
        return emdRightsHolder;
    }

    public void setEmdRightsHolder(String emdRightsHolder)
    {
        this.emdRightsHolder = emdRightsHolder;
    }

    public String getType()
    {
        return type;
    }

    public String getSubtype()
    {
        return subtype;
    }

    public void setContentType(String contentType)
    {
        String[] types = contentType.split("/");
        this.type = types[0];
        if (types.length > 1) {
            String[] subs = types[1].split(";");
            this.subtype = subs[0];
            if (subs.length > 1) {
                this.parameters = subs[1].trim();
            }
        }
    }
    
    public Set<TikaMeta> getMetadata() {
        if (metadata == null) {
            metadata = new HashSet<TikaMeta>();
        }
        return metadata;
    }
    
    public TikaMeta addMeta(TikaMeta meta) {
        meta.setIdentifier(getIdentifier(), this);
        getMetadata().add(meta);
        return meta;
    }
    
    public Set<Spatial> getSpatials() {
        if (spatials == null) {
            spatials = new HashSet<Spatial>();
        }
        return spatials;
    }
    
    public Spatial addSpatial(Spatial spatial) {
        spatial.setIdentifier(getIdentifier(), this);
        getSpatials().add(spatial);
        return spatial;
    }
    
    public Set<ArchisNummer> getArchisNummers() {
        if (archisNummers == null) {
            archisNummers = new HashSet<ArchisNummer>();
        }
        return archisNummers;    
    }
    
    public ArchisNummer addArchisNummer(ArchisNummer an) {
        an.setIdentifier(getIdentifier(), this);
        getArchisNummers().add(an);
        return an;
    }
    
}
