package nl.knaw.dans.coar.shed;

import nl.knaw.dans.coar.rdb.JPAUtil;
import nl.knaw.dans.coar.tika.TikaProfileStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfileIdentifierFilter
{
    
    private Logger logger = LoggerFactory.getLogger(ProfileIdentifierFilter.class);
    private TikaProfileStore store;
    
    
    public ProfileIdentifierFilter()
    {
        store = new TikaProfileStore(JPAUtil.getEntityManager());
    }
    
// // stops after x calls
//    public boolean accept(String identifier)
//    {
//        boolean accept = false;
//        EntityTransaction tx = store.newTransAction();
//        tx.begin();
//        try
//        {
//            TikaProfile profile = store.findByNaturalId(identifier);
//            if (profile == null) {
//                accept = true;
//            } else {
//                accept = false;
//                logger.info("Not accepting, profile stored: " + identifier);
//            }
//            tx.commit();
//        } catch (Exception e)   {
//            tx.rollback();
//            throw e;
//        } finally {
//            store.clear();
//        }
//        return accept;
//    }
    
    public boolean accept(String identifier) {
        boolean accept = store.recordExists(identifier);
        if (!accept) {
            logger.info("Not accepting, profile stored: " + identifier);
        }
        return accept;
    }

}
