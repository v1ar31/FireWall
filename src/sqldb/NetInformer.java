package sqldb;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;

public class NetInformer extends Thread {
 
    private String host;
    private int indexName;

    private DBAdapter db;

    /**
     * IP addresses for site add to DB for blocking
     * @param host site name
     * @param indexName index site in db
     * @param db DBAdapter
     */
    public NetInformer(String host, int indexName, DBAdapter db) {
        this.host = host;
        this.indexName = indexName;
        this.db = db;
    }
 
    public void run()  {
        try {
            InetAddress[] addresses = InetAddress.getAllByName(this.host);

            for (InetAddress address: addresses) {
                db.insert("insert into ips (ip,namesid) values ('" + address.getHostAddress()
                        + "', " + Integer.toString(indexName) + ")");
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
 
}
