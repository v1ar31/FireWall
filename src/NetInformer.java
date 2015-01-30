import sqldb.DBAdapter;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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
            List<InetAddress> addresses = new ArrayList<>(Arrays.asList(InetAddress.getAllByName(this.host)));
            addresses.addAll(Arrays.asList(Inet6Address.getAllByName(this.host)));
            HashSet<InetAddress> hs = new HashSet<>();
            hs.addAll(addresses);
            addresses.clear();
            addresses.addAll(hs);

            List<String> fields = DBAdapter.initArrayList("namesid", "ip", "port");

            for (InetAddress address: addresses) {
                final String addr = address.getHostAddress();
                List<String> values = DBAdapter.initArrayList( Integer.toString(indexName), addr, null);

                db.insert("list", fields, values);
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
 
}
