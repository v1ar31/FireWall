package sqldb;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Created by v1ar on 30.10.14.
 */
public class PostgreSQL_properties {
    private final String    path;
    private String    host       = "host",
                            login      = "login",
                            password   = "password";
    private Properties prop;

    public PostgreSQL_properties(String path) throws IOException {
        this.path = path;
        prop = new Properties();
        prop.load(new FileInputStream(this.path));
        setHost(getHost());
    }


    public void setProperties () throws IOException {
        OutputStream os = new FileOutputStream(path);
        prop.store(os,"_last_");
        os.close();
    }

    public String getHost () throws IOException {
        return prop.getProperty(this.host);
    }

    public String getLogin () throws IOException {
        return prop.getProperty(this.login);
    }

    public String getPassword () throws IOException {
        return prop.getProperty(this.password);
    }

    public void setLogin (String login) throws IOException {
        prop.setProperty("login",login);
    }

    public void setPassword (String password) throws IOException {
        prop.setProperty("password",password);
    }

    public void setHost (String host) throws IOException {
        prop.setProperty("host",host);
    }


}
