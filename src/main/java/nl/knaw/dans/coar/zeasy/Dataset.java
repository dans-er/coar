package nl.knaw.dans.coar.zeasy;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.knaw.dans.coar.tika.TikaProfile;



public class Dataset
{
    
    private List<TikaProfile> profiles;
    
    public Dataset(String datasetId) {
        profiles = Zeasy.getTikaProfileStore().getProfiles(datasetId);
    }
    
    public List<TikaProfile> getProfiles() {
        return profiles;
    }

}
