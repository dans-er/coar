package nl.knaw.dans.coar.fedora;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.request.FindObjects;
import com.yourmediashelf.fedora.client.response.FindObjectsResponse;

public class DateBoundDatasetIterator extends DatasetIterator
{
    
    public static final String START_DATE = "2011-01-01";
    public static final String FILE_LOCATION = "datepointer.txt";
    
    private static Logger logger = LoggerFactory.getLogger(DateBoundDatasetIterator.class);
    
    private String fileLocation;
    private String startDate;
    private DateTime datePointer;
    
    private DateTimeFormatter format;
    private int count;
    
    @Override
    public String next()
    {
        count++;
        String datasetId = super.next();
        logger.info(datasetId + " is #" + count + " for " + getFormat().print(datePointer));
        return datasetId;
    }
    
    public boolean hasNext()
    {
        if (super.hasNext()) {
            return true;
        } else {
            String currentIdentifier = null;
            while (!super.hasNext() && getDatePointer().isBeforeNow()) {
                writeDate(datePointer, count);
                datePointer = datePointer.plusDays(1);
                count = 0;
                currentIdentifier = findNextIdentifier();
                logger.debug("Looking for {} and starting with {}", getFormat().print(datePointer), currentIdentifier);
            }
            return currentIdentifier != null;
        }
    }
    
    public int getCount()
    {
        return count;
    }

    public DateTime getDatePointer() {
        if (datePointer == null) {            
            datePointer = getStartDate();
            logger.debug("starting with '{}'", getFormat().print(datePointer));
        }
        return datePointer;
    }
    
    @Override
    protected String getQuery()
    {
        // "pid%7Eeasy-dataset:* mDate%3E%3D2014-10-20 mDate%3C2014-10-28"
        DateTime start = getDatePointer();
        DateTime end = start.plusDays(1);
        return new StringBuilder() //
            .append("pid%7Eeasy-dataset:*").append(" ")
            .append("cDate%3E%3D").append(getFormat().print(start)).append(" ")
            .append("cDate%3C").append(getFormat().print(end))
            .toString();
    }
    
    protected void startFindDatasets() throws FedoraClientException {
        FindObjectsResponse response = new FindObjects() //
            .query(getQuery()) //
            .pid() //
            .maxResults(getMaxResults()) //
            .execute();
        pids = response.getPids();
        token = null; // we do all datasets for cDate.
        pidIter = pids.iterator();
        logger.info("Start find datasets found {} identifiers for {}.", pids.size(), getFormat().print(getDatePointer()));
    }
    
    public DateTime getStartDate()
    {
        if (startDate != null) {
            return new DateTime(startDate);
        } else {
            String lastDate = readStartDate();
            if (lastDate != null) {
                return new DateTime(lastDate).plusDays(1);
            } else {
                return new DateTime(START_DATE);
            }
        }
    }
    
    protected String readStartDate()
    {
        logger.debug("Reading startDate from {}.", getFileLocation());
        File file = new File(getFileLocation());
        String lastDate = null;
        if (file.exists()) {
            try
            {
                List<String> lines = FileUtils.readLines(file, "UTF-8");
                if (lines.size() > 0) {
                    lastDate = lines.get(lines.size() - 1).split(";")[0];
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return lastDate;
    }
    
    public String getFileLocation()
    {
        if (fileLocation == null) {
            fileLocation = FILE_LOCATION;
        }
        return fileLocation;
    }

    public void setFileLocation(String fileLocation)
    {
        this.fileLocation = fileLocation;
    }
    
    protected void writeDate(DateTime date, int count) {
        File file = new File(getFileLocation());
        String line = getFormat().print(date) + ";" + count + "\n";
        try
        {
            FileUtils.write(file, line, "UTF-8", true);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    
    private DateTimeFormatter getFormat() {
        if (format == null) {
            format = DateTimeFormat.forPattern("yyyy-MM-dd");
        }
        return format;
    }

}
