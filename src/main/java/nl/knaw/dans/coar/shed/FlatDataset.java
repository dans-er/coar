package nl.knaw.dans.coar.shed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import nl.knaw.dans.coar.rdb.DBEntity;

import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "tbl_datasets")
public class FlatDataset extends DBEntity
{
    
    private static final long serialVersionUID = 4152248415330555313L;

    @Id
    @GeneratedValue
    @Column(name = "fds_id")
    private Long id;
    
    @NaturalId
    @Column(name = "datasetId", nullable = false, unique = true)
    private String datasetId;
    
    @Column(name = "metadata_format")
    private String metadataFormat;
    
    @Column(name = "count_files")
    private int countFiles;
    
    @Column(name = "count_pdfs")
    private int countPdfs;
    
    public FlatDataset() {
        
    }
    
    public FlatDataset(String datasetId) {
        this.datasetId = datasetId;
    }
    
    @Override
    public Long getId()
    {
        return id;
    }
    
    public void setValues(FlatDataset dataset) {
        datasetId = dataset.datasetId;
        metadataFormat = dataset.metadataFormat;
        countFiles = dataset.countFiles;
        countPdfs = dataset.countPdfs;
    }

    public String getDatasetId()
    {
        return datasetId;
    }

    public void setDatasetId(String datasetId)
    {
        this.datasetId = datasetId;
    }

    public String getMetadataFormat()
    {
        return metadataFormat;
    }

    public void setMetadataFormat(String metadataFormat)
    {
        this.metadataFormat = metadataFormat;
    }

    public int getCountFiles()
    {
        return countFiles;
    }

    public void setCountFiles(int countFiles)
    {
        this.countFiles = countFiles;
    }

    public int getCountPdfs()
    {
        return countPdfs;
    }

    public void setCountPdfs(int countPdfs)
    {
        this.countPdfs = countPdfs;
    }
    

}
