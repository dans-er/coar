package nl.knaw.dans.coar.shed;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.persistence.EntityTransaction;

import nl.knaw.dans.coar.fedora.DatasetIterator;
import nl.knaw.dans.coar.fedora.EMD;
import nl.knaw.dans.coar.fedora.Fedora;
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
    
    private final DatasetIterator datasetIterator;
    private boolean savingFiles;
    
    private TikaProfileStore store;
    private EntityTransaction currentTx;
    
    private Fedora fedora;
    private PDFProcessor pdfProcessor;
    
    private ProfileIdentifierFilter idFilter;
    private int fileCount;
    
    public Conveyor(DatasetIterator datasetIterator) {
        this.datasetIterator = datasetIterator;
        fedora = Fedora.instance();
        store = new TikaProfileStore(JPAUtil.getEntityManager());
        idFilter = new ProfileIdentifierFilter();
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
        //int countDatasets = 0;
        while (datasetIterator.hasNext()) {
            String datasetId = datasetIterator.next();
            //countDatasets++;
            EMD emd = null;
            try
            {
                emd = new EMD(fedora.getEMD(datasetId));
            }
            catch (Exception e)
            {
                logger.error("While fetching EMD for {}", datasetId);
                Reporter.report("error_emd.csv", datasetId + ";" + e.getMessage());
                continue;
            }
            if ("ARCHAEOLOGY".equals(emd.getMetadataFormat())
                    || emd.getAudiences().contains("easy-discipline:2")) {
                processArchDataset(datasetId, emd);
            }
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
        if (fileIds.size() > 1000) {
            logger.info("Too many files: {}", datasetId);
            Reporter.report("gt_1000_files.csv", datasetId + ";" + fileIds.size());
        }
        for (String fileId : fileIds) {
            if (idFilter.accept(fileId)) {
                try
                {
                    DatastreamProfile dsProfile = fedora.getDSProfile(fileId);
                    if ("application/pdf".equals(dsProfile.getDsMIME())) {
                        processPDF(datasetId, emd, fileId, dsProfile);
                    }
                }
                catch (FedoraClientException e)
                {
                    logger.error("While fetching dsProfile for {}", fileId);
                    Reporter.report("error_dsprofile.csv", datasetId + ";" + fileId + ";" + e.getMessage());
                }
            }
        }
        
    }

    protected void processPDF(String datasetId, EMD emd, String fileId, DatastreamProfile dsProfile)
    {
//        if ("easy-file:1288551".equals(fileId) 
//                || "easy-file:1298487".equals(fileId)
//                || "easy-file:1302237".equals(fileId)) {
//            logger.info("XXXXXXXXXXXX Not processing {}", fileId);
//            return;
//        }
        logger.info(">>>>>>>>>>>>> Start processing pdf {} {} {}", datasetId, fileId, dsProfile.getDsLabel());
        
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
        
        //TikaProfile tikaProfile = store.findByNaturalId(fileId);
        try
        {
            TikaProfile profile = processProfile(datasetId, emd, fileId, dsProfile);
            store.saveOrUpdate(profile);
            currentTx.commit();
            fileCount++;
        }
        catch (Exception e)
        {
            logger.error("While fetching dsProfile for {}", fileId);
            Reporter.report("error_file_data.csv", datasetId + ";" + fileId + ";" + e.getMessage());
            currentTx.rollback();
        } finally {
            store.clear();
            currentTx = null;
            logger.info("///////// READY processing #{}# pdf {} {} {}", fileCount, datasetId, fileId, dsProfile.getDsLabel());
        }
        ///////////////////////////////////////////////////////////////////////////
    }
    

    private TikaProfile processProfile(String datasetId, EMD emd, String fileId, DatastreamProfile dsProfile) throws Exception
    {

        logger.info("Creating new profile for {}", dsProfile.getDsLabel());
        
        TikaProfile profile = new TikaProfile(fileId, datasetId);
        
        profile.setDatasetId(datasetId);
        profile.setDsLabel(dsProfile.getDsLabel());
        profile.setDsMediatype(dsProfile.getDsMIME());
        profile.setDsState(dsProfile.getDsState());
        if (dsProfile.getDsSize() != null)
            profile.setDsSize(dsProfile.getDsSize().longValue());
        if (dsProfile.getDsCreateDate() != null)
            profile.setDsCreationDate(dsProfile.getDsCreateDate().toGregorianCalendar().getTime());

        profile.setEmd(emd);
        logger.info("Created new profile for {}", profile.getDsLabel());
        
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
