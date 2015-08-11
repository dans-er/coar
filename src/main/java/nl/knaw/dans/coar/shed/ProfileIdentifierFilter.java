package nl.knaw.dans.coar.shed;

import javax.persistence.EntityTransaction;

import nl.knaw.dans.coar.rdb.JPAUtil;
import nl.knaw.dans.coar.tika.TikaProfile;
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

    public boolean accept(String identifier)
    {
        boolean accept = false;
        EntityTransaction tx = store.newTransAction();
        tx.begin();
        try
        {
            TikaProfile profile = store.findByNaturalId(identifier);
            if (profile == null) {
                accept = true;
            } else {
                accept = false;
                logger.info("Not accepting, profile stored: " + identifier);
            }
            tx.commit();
        } catch (Exception e)   {
            tx.rollback();
            throw e;
        } finally {
            store.clear();
        }
        return accept;
    }

}
