package nl.knaw.dans.coar.shed;

import javax.persistence.EntityManager;

import nl.knaw.dans.coar.rdb.AbstractGenericStore;

public class FlatDatasetStore extends AbstractGenericStore<FlatDataset, Long>
{

    public FlatDatasetStore()
    {
        super(FlatDataset.class);
    }
    
    public FlatDatasetStore(EntityManager em) {
        super(FlatDataset.class);
        setEntityManager(em);
    }
    
    // find by fedora dataset identifier
    public FlatDataset findByNaturalId(String datasetId) {
        FlatDataset fds =  (FlatDataset) ((org.hibernate.jpa.HibernateEntityManager) getEntityManager()).getSession()
                .byNaturalId(getEntityBeanType()).using("datasetId", datasetId).load();
        return fds;
    }

}
