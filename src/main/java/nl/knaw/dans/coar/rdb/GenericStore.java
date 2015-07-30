package nl.knaw.dans.coar.rdb;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public interface GenericStore<T extends Entity, ID extends Serializable>
{

     void setEntityManager(EntityManager em);
     
     EntityTransaction newTransAction();

     T findById(ID id, boolean lock);

     List<T> findAll();

     List<T> findByExample(T exampleInstance, String... excludeProperty);

     T makePersistent(T entity);

     void makeTransient(T entity);

     T merge(T entity);

     T saveOrUpdate(T entity);

     //T persist(T entity);

     void flush();

     //SearchResult<T> search(SearchRequest<T> request);

     //int getTotal(SearchRequest<T> request);

}
