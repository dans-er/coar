package nl.knaw.dans.coar.tika;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import nl.knaw.dans.coar.rdb.AbstractGenericStore;

public class TikaProfileStore extends AbstractGenericStore<TikaProfile, Long>
{

    public TikaProfileStore()
    {
        super(TikaProfile.class);
    }
    
    public TikaProfileStore(EntityManager em) {
        super(TikaProfile.class);
        setEntityManager(em);
    }
    
    // find by fedora file identifier
    public TikaProfile findByNaturalId(String identifier) {
        TikaProfile tprofile =  (TikaProfile) ((org.hibernate.jpa.HibernateEntityManager) getEntityManager()).getSession()
                .byNaturalId(getEntityBeanType()).using("identifier", identifier).load();
        return tprofile;
    }
    
    // get a list of distinct datasetIds
    @SuppressWarnings("unchecked")
    public List<String> getDistinctDatasetIds() {
        String query = "select distinct(datasetId) from profile order by datasetId";
        return ((org.hibernate.jpa.HibernateEntityManager) getEntityManager()).getSession().createSQLQuery(query).list();
    }
    
    // get the list of TikaProfiles for the specified datasetID
    @SuppressWarnings("unchecked")
    public List<TikaProfile> getProfiles(String datasetId) {
        Criteria crit = ((org.hibernate.jpa.HibernateEntityManager) getEntityManager()).getSession().createCriteria(getEntityBeanType());
        crit.add(Restrictions.eq("datasetId", datasetId));
        List<TikaProfile> results = crit.list();
        return results;
    }

}
