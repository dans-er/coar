package nl.knaw.dans.coar.rdb;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

public abstract class AbstractGenericStore<T extends Entity, ID extends Serializable> implements GenericStore<T, ID>
{

     public static final String WC = "%";

     protected String      entityName;

     protected Class<T>    entityBeanType;

     private EntityManager em;

     @SuppressWarnings("unchecked")
     public AbstractGenericStore(Class<? extends T> type)
     {
         entityBeanType = (Class<T>) type;
         entityName = entityBeanType.getName();
     }

     public void setEntityManager(EntityManager em)
     {
         this.em = em;
     }

     public EntityManager getEntityManager()
     {
         if (em == null)
             throw new IllegalStateException("EntityManager has not been set on DAO before usage");
         return em;
     }
     
     public EntityTransaction newTransAction()
     {
         return getEntityManager().getTransaction();
     }

     public Class<T> getEntityBeanType()
     {
         return entityBeanType;
     }

     @SuppressWarnings("unchecked")
     public List<T> findAll()
     {
         return getEntityManager().createQuery("from " + getEntityBeanType().getName()).getResultList();
     }
     
     @SuppressWarnings("unchecked")
    public List<T> findAll(int first, int max)
     {
         return getEntityManager().createQuery("from " + getEntityBeanType().getName())
                 .setFirstResult(first)
                 .setMaxResults(max)
                 .getResultList();
     }

     @SuppressWarnings("unchecked")
     public List<T> findByExample(T exampleInstance, String...excludeProperty)
     {
         // Using Hibernate, more difficult with EntityManager and EJB-QL
         org.hibernate.Criteria crit = ((org.hibernate.jpa.HibernateEntityManager) getEntityManager()).getSession()
                 .createCriteria(getEntityBeanType());
         org.hibernate.criterion.Example example = org.hibernate.criterion.Example.create(exampleInstance);
         for (String exclude : excludeProperty)
         {
             example.excludeProperty(exclude);
         }
         crit.add(example);
         return crit.list();
     }

     public T findById(ID id, boolean lock)
     {
         T entity;
         if (lock)
         {
             entity = getEntityManager().find(getEntityBeanType(), id);
             em.lock(entity, javax.persistence.LockModeType.WRITE);
         }
         else
         {
             entity = getEntityManager().find(getEntityBeanType(), id);
         }
         return entity;
     }

     public void remove(T entity)
     {
         getEntityManager().remove(entity);
     }

     public T makePersistent(T entity)
     {
         entity.updateLastModified();
         return getEntityManager().merge(entity);
     }

     public T merge(T entity)
     {
         entity.updateLastModified();
         return getEntityManager().merge(entity);
     }

     public T saveOrUpdate(T entity)
     {
         if (entity.isTransient())
         {
             return makePersistent(entity);
         }
         else
         {
             return persist(entity);
         }
     }

     // 2011 03 19
        public T persist(T entity)
        {
            entity.updateLastModified();
            getEntityManager().persist(entity);
            return entity;
        }

     public void makeTransient(T entity)
     {
         getEntityManager().remove(entity);
     }

     public void flush()
     {
         getEntityManager().flush();
     }

     public void clear()
     {
         getEntityManager().clear();
     }

//     @SuppressWarnings("unchecked")
//     public SearchResult<T> search(SearchRequest<T> request)
//     {
//         Criteria crit = getSession().createCriteria(getEntityBeanType());
//         SearchResult<T> result = new SearchResult<T>(request);
//         if (request.isTotalWanted())
//         {
//             result.setTotal(getTotal(request));
//         }
//         result.setResults(request.addAll(crit).list());
//         return result;
//     }

//     public int getTotal(SearchRequest<T> request)
//     {
//         Criteria crit = getSession().createCriteria(getEntityBeanType());
//         Integer result = (Integer) request.addRestrictions(crit).setProjection(Projections.rowCount()).list().get(0);
//         return result;
//     }

     /**
      * Use this inside subclasses as a convenience method.
      */
     @SuppressWarnings("unchecked")
     protected List<T> findByCriteria(Criterion... criterion)
     {
         Criteria crit = getSession().createCriteria(getEntityBeanType());
         for (Criterion c : criterion)
         {
             crit.add(c);
         }
         return crit.list();
     }
     
     @SuppressWarnings("unchecked")
     public List<T> executeSQLQuery(String sql)
     {
         List<T> list = ((org.hibernate.jpa.HibernateEntityManager) getEntityManager()).getSession()
                 .createSQLQuery(sql)
                 .addEntity(getEntityBeanType())
                 .list();
         return list;
     }

     public Session getSession()
     {
         return ((org.hibernate.jpa.HibernateEntityManager) getEntityManager()).getSession();
     }

}
