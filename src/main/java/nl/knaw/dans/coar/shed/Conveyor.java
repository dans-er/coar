package nl.knaw.dans.coar.shed;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.persistence.EntityTransaction;

import nl.knaw.dans.coar.fedora.EMD;
import nl.knaw.dans.coar.fedora.Fedora;
import nl.knaw.dans.coar.fedora.PerDateDatasetIterator;
import nl.knaw.dans.coar.rdb.JPAUtil;
import nl.knaw.dans.coar.tika.TikaProfile;
import nl.knaw.dans.coar.tika.TikaProfileStore;
import nl.knaw.dans.coar.util.Reporter;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.generated.management.DatastreamProfile;

public class Conveyor
{
    
    private static Logger logger = LoggerFactory.getLogger(Conveyor.class);
    
    private final PerDateDatasetIterator datasetIterator;
    private boolean savingFiles;
    
    private TikaProfileStore store;
    private FlatDatasetStore datasetStore;
    private EntityTransaction currentTx;
    private FlatDataset currentDataset;
    
    private Fedora fedora;
    private PDFProcessor pdfProcessor;
    
    //private ProfileIdentifierFilter idFilter;
    private DatasetDiscriminator datasetDiscriminator;
    private int fileCount;
    
    public Conveyor(PerDateDatasetIterator datasetIterator) {
        this.datasetIterator = datasetIterator;
        fedora = Fedora.instance();
        store = new TikaProfileStore(JPAUtil.getEntityManager());
        datasetStore = new FlatDatasetStore(JPAUtil.getEntityManager());
        //idFilter = new ProfileIdentifierFilter();
        datasetDiscriminator = new DatasetDiscriminator();
    }
    
    public PDFProcessor getPdfProcessor()
    {
        if (pdfProcessor == null) {
            pdfProcessor = new PDFProcessor()
            {
                
                public void process(TikaProfile profile, InputStream data)
                {
                    logger.warn("No PDFProcessor provided.");
                }
            };
        }
        return pdfProcessor;
    }


    public void setPdfProcessor(PDFProcessor pdfProcessor)
    {
        this.pdfProcessor = pdfProcessor;
    }
    
    public boolean isSavingFiles()
    {
        return savingFiles;
    }

    public void setSavingFiles(boolean savingFiles)
    {
        this.savingFiles = savingFiles;
    }


    public void run() {

        while (datasetIterator.hasNext()) {
            String datasetId = datasetIterator.next();
            currentDataset = new FlatDataset(datasetId);
            
            processDataset(datasetId);
            
            // write dataset properties
            EntityTransaction tx = datasetStore.newTransAction();
            tx.begin();
            
            FlatDataset dataset = datasetStore.findByNaturalId(datasetId);
            if (dataset == null) {
                dataset = new FlatDataset(datasetId);
            }
            dataset.setValues(currentDataset);
            datasetStore.saveOrUpdate(dataset);
            tx.commit();
            datasetStore.clear();
            currentDataset = null;
        }
    }

    public void processDataset(String datasetId)
    {
        if (datasetDiscriminator.accept(datasetId)) {
            
            EMD emd = null;
            try
            {
                emd = new EMD(fedora.getEMD(datasetId));
            }
            catch (Exception e)
            {
                logger.error("While fetching EMD for {}", datasetId);
                Reporter.report("error_emd.csv", datasetId + ";" + e.getMessage());
                return;
            }
            
            String metadataFormat = emd.getMetadataFormat();
            currentDataset.setMetadataFormat(metadataFormat);
            
            if ("ARCHAEOLOGY".equals(metadataFormat)
                    || emd.getAudiences().contains("easy-discipline:2")) {
                processArchDataset(datasetId, emd);
            }
        } else {
            logger.info("Discriminating {}", datasetId);
        }
    }
    
    protected void processArchDataset(String datasetId, EMD emd)
    {
        logger.info("Processing archeological dataset {}", datasetId);
        List<String> fileIds;
        try
        {
            fileIds = fedora.getFileIdentifiers(datasetId);
        }
        catch (Exception e)
        {
            logger.error("While fetching identifiers for {}", datasetId);
            Reporter.report("error_file_ids.csv", datasetId + ";" + e.getMessage());
            return;
        }
        
        int filecount = fileIds.size();
        currentDataset.setCountFiles(filecount);
        
        int pdfcount = 0;
        for (String fileId : fileIds) {
                try
                {
                    DatastreamProfile dsProfile = fedora.getDSProfile(fileId);
                    if ("application/pdf".equals(dsProfile.getDsMIME())) {
                        pdfcount++;
                        processPDF(datasetId, emd, fileId, dsProfile);
                    }
                }
                catch (FedoraClientException e)
                {
                    logger.error("While fetching dsProfile for {}", fileId);
                    Reporter.report("error_dsprofile.csv", datasetId + ";" + fileId + ";" + e.getMessage());
                }
        }
        currentDataset.setCountPdfs(pdfcount);
        
    }

    protected void processPDF(String datasetId, EMD emd, String fileId, DatastreamProfile dsProfile)
    {
        
        if (currentTx != null)
        {
            logger.warn("===============> Previous transaction not closed.");
            if (currentTx.isActive())
            {
                logger.warn("=================> Rolling back previous transaction.");
                currentTx.rollback();
            }
            store.clear();
        }
        
        
        currentTx = store.newTransAction();
        currentTx.begin();
        
        Long id = store.getId(fileId);
        
        //
        
        if (id == null) {
            TikaProfile testprofile = store.findByNaturalId(fileId);
            if (testprofile != null) {
                logger.warn("HIBERNATE NOT REALY WORKING {} {} {}", datasetId, fileId, dsProfile.getDsLabel());
                System.exit(-1);
            }
            logger.info("");
            logger.info(">>>>>>>>>>>>> Start processing pdf {} {} {}", datasetId, fileId, dsProfile.getDsLabel());
            TikaProfile profile = new TikaProfile(fileId, datasetId);
            
            //profile = new TikaProfile(fileId, datasetId);
            profile.setDatasetId(datasetId);
            profile.setDsLabel(dsProfile.getDsLabel());
            profile.setDsMediatype(dsProfile.getDsMIME());
            profile.setDsState(dsProfile.getDsState());
            if (dsProfile.getDsSize() != null)
                profile.setDsSize(dsProfile.getDsSize().longValue());
            if (dsProfile.getDsCreateDate() != null)
                profile.setDsCreationDate(dsProfile.getDsCreateDate().toGregorianCalendar().getTime());

            profile.setEmd(emd);
            if (datasetIterator != null)
                logger.info("date {} Created new profile for {}", datasetIterator.getDate(), profile.getDsLabel());
        
            try
            {
                processProfile(profile, datasetId, emd, fileId, dsProfile);
                store.saveOrUpdate(profile);
                currentTx.commit();
                fileCount++;
            }
            catch (Exception e)
            {
                logger.error("While storing profile for {}", fileId);
                Reporter.report("error_file_data.csv", datasetId + ";" + fileId + ";" + e.getMessage());
                currentTx.rollback();
            } finally {
                store.clear();
                currentTx = null;
                logger.info("///////// READY processing #{}# pdf {} {} {}", fileCount, datasetId, fileId, dsProfile.getDsLabel());
            }
        } else {
            if (datasetIterator != null)
                logger.info("XXXXXXXXXXX date {} Not redoing analysis of {}", datasetIterator.getDate(), dsProfile.getDsLabel());
            currentTx.commit();
            fileCount++;
            store.clear();
            currentTx = null;
            return;
        }
            
        ///////////////////////////////////////////////////////////////////////////
    }
    

    private TikaProfile processProfile(TikaProfile profile, String datasetId, EMD emd, String fileId, DatastreamProfile dsProfile) throws Exception
    {
        
        InputStream currentData = null;
        try
        {
            currentData = fedora.getFileData(fileId);
            if (isSavingFiles()) {
                currentData = saveFile(datasetId, emd, fileId, dsProfile, currentData);
            }
            logger.info("Analyzing {} {} {}", datasetId, fileId, dsProfile.getDsLabel());
            getPdfProcessor().process(profile, currentData);
        }
        catch (Exception e)
        {
            logger.error("While fetching dsProfile for {}", fileId);
            Reporter.report("error_file_data.csv", datasetId + ";" + fileId + ";" + e.getMessage());
        } finally {
            IOUtils.closeQuietly(currentData);
            
        }
        return profile;
    }

    private InputStream saveFile(String datasetId, EMD emd, String fileId, DatastreamProfile dsProfile, InputStream data) throws IOException
    {
        File dir = new File("COAR_FILES");
        if (!dir.exists()) {
            dir.mkdir();
        }
        String dsId = "eds_" + datasetId.split(":")[1];
        File dsIdDir = new File(dir, dsId);
        if (!dsIdDir.exists()) {
            dsIdDir.mkdirs();
        }
        String fId = "ef_" + fileId.split(":")[1];
        File fIdDir = new File(dsIdDir, fId);
        if (!fIdDir.exists()) {
            fIdDir.mkdirs();
        }
        String filename = dsProfile.getDsLabel();
        if (filename == null | "".equals(filename)) {
            filename = fId;
        }
        FileOutputStream fos = new FileOutputStream(new File(fIdDir, filename));
        IOUtils.copyLarge(data, fos);
        IOUtils.closeQuietly(fos);
        
        return new FileInputStream(new File(fIdDir, filename));
    }

}
