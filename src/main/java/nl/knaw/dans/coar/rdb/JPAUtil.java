package nl.knaw.dans.coar.rdb;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JPAUtil
{
    
    private static final Logger logger = LoggerFactory.getLogger(JPAUtil.class);
    

     public static final String PERSISTENCE_UNIT_DEFAULT = "coar";

     public static final String PERSISTENCE_UNIT_TEST = "coar-test";

     public static EntityManagerFactory EM_FACTORY;

     public static EntityManagerFactory getEntityManagerFactory()
     {
         if (EM_FACTORY == null)
         {
             EM_FACTORY = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_DEFAULT);
             logger.debug("Created default EntityManagerFactory. unit=" + PERSISTENCE_UNIT_DEFAULT);
         }
         return EM_FACTORY;
     }

     public static EntityManager getEntityManager()
     {
         EntityManagerFactory emf = getEntityManagerFactory();
         return emf.createEntityManager();
     }

     public static void close()
     {
         if (EM_FACTORY != null)
         {
             EM_FACTORY.close();
             logger.debug("Closed EntityManagerFactory.");
             EM_FACTORY = null;
         }
         else
         {
             logger.debug("No EntityManagerFactory used. Nothing to close.");
         }
     }

     public static void setTestState(boolean testing)
     {
         if (testing && EM_FACTORY == null)
         {
             EM_FACTORY = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_TEST);
             logger.debug("Created EntityManagerFactory for tests. unit=" + PERSISTENCE_UNIT_TEST);
         }
     }

}