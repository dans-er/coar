package nl.knaw.dans.coar.tika;

import java.util.List;

import javax.persistence.EntityTransaction;

import nl.knaw.dans.coar.geo.RDPoint;
import nl.knaw.dans.coar.rdb.JPAUtil;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class TikaProfileStoreTest
{
    
    @BeforeClass
    public static void beforeClass() {
        JPAUtil.setTestState(false);
    }
    
    @Test
    public void storeProfile() throws Exception {
        TikaProfileStore store = new TikaProfileStore(JPAUtil.getEntityManager());
        
        EntityTransaction tx = store.newTransAction();
        tx.begin();
        TikaProfile tp = new TikaProfile("foo:1", "bar:2");
        Spatial spatial = new Spatial("test", new RDPoint(100000, 400000));
        tp.addSpatial(spatial);
        
        store.saveOrUpdate(tp);
        tx.commit();
        store.clear();
    }
    
    @Test
    public void getDistinctDatasetIds() throws Exception {
        TikaProfileStore store = new TikaProfileStore(JPAUtil.getEntityManager());
        List<String> list = store.getDistinctDatasetIds();
        for (String datasetId : list) {
            System.err.println(datasetId);
            List<TikaProfile> dsProfiles = store.getProfiles(datasetId);
            for (TikaProfile tp : dsProfiles) {
                
                
            }
        }
    }

}
