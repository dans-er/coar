package nl.knaw.dans.coar.fedora;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.request.FindObjects;
import com.yourmediashelf.fedora.client.response.FindObjectsResponse;

public class DatasetIterator implements Iterator<String>
{
    
    public static final int MAX_RESULTS = 10;
    
    private static Logger logger = LoggerFactory.getLogger(DatasetIterator.class);
    
    protected List<String> pids;
    protected Iterator<String> pidIter;
    protected String token = "start";
    private String nextIdentifier = "start";
    
    public boolean hasNext()
    {
        if ("start".equals(nextIdentifier)) {
            findNextIdentifier();
        }
        return nextIdentifier != null;
    }
    
    public String next()
    {
        if (nextIdentifier == null) {
            throw new NoSuchElementException();
        }
        String current = nextIdentifier;
        findNextIdentifier();
        return current;
    }
    
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
    
    protected String findNextIdentifier()
    { 
        try
        {
            if (hasNextPid()) {
                nextIdentifier = nextPid();
            } else {
                nextIdentifier = null;
                pids = null;
            }
            return nextIdentifier;
        }
        catch (FedoraClientException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    protected boolean hasNextPid() throws FedoraClientException {
        if (pids == null) {
            startFindDatasets();
        }
        if (!pidIter.hasNext() && token != null) {
            continueFindDatasets();
        }
        return pidIter.hasNext();
    }
    
    protected String nextPid() throws FedoraClientException {
        if (pids == null) {
            startFindDatasets();
        }
        if (!pidIter.hasNext() && token != null) {
            continueFindDatasets();
        }
        return pidIter.next();
    }
    
    protected String getQuery()
    {
        return "pid%7Eeasy-dataset:*";
    }
    
    protected int getMaxResults() {
        return MAX_RESULTS;
    }
    
    protected void startFindDatasets() throws FedoraClientException {
        FindObjectsResponse response = new FindObjects() //
            .query(getQuery()) //
            .pid() //
            .maxResults(getMaxResults()) //
            .execute();
        pids = response.getPids();
        token = response.getToken();
        pidIter = pids.iterator();
        logger.debug("Start find files found {} identifiers.", pids.size());
    }
    
    protected void continueFindDatasets() throws FedoraClientException {
        FindObjectsResponse response = new FindObjects() //
            .sessionToken(token) //
            .pid() //
            .maxResults(MAX_RESULTS) //
            .execute();
        pids = response.getPids();
        token = response.getToken();
        pidIter = pids.iterator();
        logger.debug("Continue find files found {} identifiers.", pids.size());
    }

}
