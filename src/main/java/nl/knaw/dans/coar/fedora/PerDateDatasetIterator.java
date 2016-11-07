package nl.knaw.dans.coar.fedora;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerDateDatasetIterator extends DatasetIterator
{
    
    public static final String START_DATE = "2010-01-01";
    public static final String FILE_LOCATION_FINISH = "datepointer.txt";
    
    private static Logger logger = LoggerFactory.getLogger(PerDateDatasetIterator.class);
    
    private String startDate;
    private DateTime datePointer;
    
    private DateTimeFormatter format;
    private int count;
    
    private List<String> allIdentifiers;
    private Iterator<String> allIterator;
    private DatasetIterator currentIterator;
    
    
    @Override
    public boolean hasNext()
    {
        testAll();
        return allIterator.hasNext();
    }
    
    @Override
    public String next()
    {
        testAll();
        count++;
        return allIterator.next();
    }
    
    public String getDate() {
        return getFormat().print(getDatePointer());
    }
    
    protected void testAll() {
        getAll();
        while (!allIterator.hasNext() && !datePointer.isAfter(new DateTime())) {
            logger.info(getFormat().print(datePointer) + " --> " + count);
            writeDate(new File(FILE_LOCATION_FINISH), getDatePointer(), count);
            //System.err.println(getFormat().print(datePointer) + " --> " + count);
            datePointer = datePointer.plusDays(1);
            count = 0;
            allIdentifiers = null;
            allIterator = null;
            getAll();
            logger.info("=========> FOUND {} datasets for {} <========", allIdentifiers.size(), getFormat().print(datePointer) );
        }
        
    }
    
    protected void getAll() {
        if (allIdentifiers == null) {
            currentIterator = new DatasetIterator() {
                @Override
                protected String getQuery()
                {
                    return getDateQuery();
                }
            };
            allIdentifiers = new ArrayList<String>();
            while (currentIterator.hasNext()) {
                allIdentifiers.add(currentIterator.next());
            }
            allIterator = allIdentifiers.iterator();
        }
    }
    
    
    protected String getDateQuery()
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
    
    public DateTime getDatePointer() {
        if (datePointer == null) {            
            datePointer = getStartDate();
            logger.debug("starting with '{}'", getFormat().print(datePointer));
        }
        return datePointer;
    }
    
    private DateTimeFormatter getFormat() {
        if (format == null) {
            format = DateTimeFormat.forPattern("yyyy-MM-dd");
        }
        return format;
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
        logger.debug("Reading startDate from {}.", FILE_LOCATION_FINISH);
        File file = new File(FILE_LOCATION_FINISH);
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
    
    
    protected void writeDate(File file, DateTime date, int count) {
        
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



}
