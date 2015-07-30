package nl.knaw.dans.coar.fedora;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.request.FedoraRequest;

public class FedoraConnector
{
    
    public static final String DEFAULT_PROPS_LOCATION = "non-pub/remote_testing.properties";
    public static final String PROP_HOST = "fedora.host";
    public static final String PROP_PORT = "fedora.port";
    public static final String PROP_PATH = "fedora.path";
    public static final String PROP_USER = "fedora.user";
    public static final String PROP_PASS = "fedora.pass";
    
    private static Logger logger = LoggerFactory.getLogger(FedoraConnector.class);
    
    
    private String propsLocation;
    private String host;
    private int port;
    private String path;
    private String user;
    private String pass;
    
    private Properties props;
    
    public void connect() throws MalformedURLException {
        String fedoraAddress = getHost() + ":" //
                + getPort() + "/" //
                + getPath();
        FedoraCredentials credentials = new FedoraCredentials(
                fedoraAddress, //
                getUser(), //
                getPass());
        logger.debug("Setting up a FedoraClient for {}", fedoraAddress);
        FedoraClient fedora = new FedoraClient(credentials);
        
        FedoraRequest.setDefaultClient(fedora);
    }
    
    public String getHost()
    {
        if (host == null) {
            host = getProps().getProperty(PROP_HOST);
        }
        return host;
    }
    
    public void setHost(String host)
    {
        this.host = host;
    }
    
    public int getPort()
    {
        if (port == 0) {
            port = Integer.parseInt(getProps().getProperty(PROP_PORT, "0"));
        }
        return port;
    }
    
    public void setPort(int port)
    {
        this.port = port;
    }
    
    public String getPath()
    {
        if (path == null) {
            path = getProps().getProperty(PROP_PATH);
        }
        return path;
    }
    
    public void setPath(String path)
    {
        this.path = path;
    }
    
    public String getUser()
    {
        if (user == null) {
            user = getProps().getProperty(PROP_USER);
        }
        return user;
    }
    
    public void setUser(String user)
    {
        this.user = user;
    }
    
    public String getPass()
    {
        if (pass == null) {
            pass = getProps().getProperty(PROP_PASS);
        }
        return pass;
    }
    
    public void setPass(String pass)
    {
        this.pass = pass;
    }
    
    public String getPropsLocation()
    {
        if (propsLocation == null) {
            propsLocation = DEFAULT_PROPS_LOCATION;
        }
        return propsLocation;
    }

    public void setPropsLocation(String propsLocation)
    {
        this.propsLocation = propsLocation;
    }

    public Properties getProps()
    {
        try
        {
            if (props == null) {
                props = new Properties();
                InputStream ins = FileUtils.openInputStream(new File(getPropsLocation()));
                props.load(ins);
                ins.close();
            }
            return props;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
