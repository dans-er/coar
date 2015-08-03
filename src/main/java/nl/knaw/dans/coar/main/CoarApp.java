package nl.knaw.dans.coar.main;

import nl.knaw.dans.coar.fedora.FedoraConnector;
import nl.knaw.dans.coar.shed.Conveyor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class CoarApp
{
    
public static final String APP_CONTEXT_LOCATION = "cfg/application-context.xml";
    
    private static Logger logger = LoggerFactory.getLogger(CoarApp.class);

    public static void main(String[] args) throws Exception
    {
        String appContextLocation;
        if (args.length > 0) {
            appContextLocation = args[0];
        } else {
            appContextLocation = APP_CONTEXT_LOCATION;
        }
        logger.info("Configuration file: {}", appContextLocation);
        FileSystemXmlApplicationContext applicationContext = new FileSystemXmlApplicationContext(appContextLocation);
        
        try
        {
            FedoraConnector connector = (FedoraConnector) applicationContext.getBean("fedora-connector");
            connector.connect();
            
            Conveyor conveyor = (Conveyor) applicationContext.getBean("conveyor");
            conveyor.run();
        }
        catch (Exception e)
        {
            logger.error("Last capture caught: ", e);
            throw e;
        } finally {
            applicationContext.close();
        }
        
    }

}
