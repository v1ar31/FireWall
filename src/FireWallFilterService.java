import sqldb.DBAdapter;
import windivert.FilterService;
import windivert.HeaderIPv4;
import windivert.HeaderIPv6;

import java.sql.SQLException;


public class FireWallFilterService extends FilterService {
    public static final int DIRECTION_OUTBOUND = 0;
    public static final int DIRECTION_INBOUND = 1;
    public static final int TCP = 0x11;
    public static final int UDP = 0x06;

    private SingleFireWall fireWall;
    private DBAdapter dbAdapter;

    public FireWallFilterService() {
        super();
        fireWall = SingleFireWall.getInstance();
        try {
            dbAdapter = DBAdapter.getInstance();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean filteredIPv4Header(HeaderIPv4 ipv4Header, int direction) {
        String Address = null;
        String Port = null;

        Address = (direction == DIRECTION_OUTBOUND)? ipv4Header.destinationIPAddress
                                                   : ipv4Header.sourceIPAddress;


        if ((ipv4Header.protocol == UDP) || (ipv4Header.protocol == TCP)) {
            int tmpPort = (direction == DIRECTION_OUTBOUND)? ipv4Header.destinationPort
                                                           : ipv4Header.sourcePort;
            Port = Integer.toString(tmpPort);
        }

        if (blockListContains(Address, null)
                || ((Port != null) && (blockListContains(null, Port) || blockListContains(Address, Port))) ) {

            fireWall.notifyObservers(direction, Address, Port);

            return true;
        }
        return false;
    }

    @Override
    public boolean filteredIPv6Header(HeaderIPv6 ipv6Header, int direction) {
        // pass
        return false;
    }

    public boolean blockListContains(String Address, String Port) {
        return true;
    }

/*
    public boolean blockIPListContains (String source, String dest)  {
        ResultSet resSet;
        int indexName = 0;
        synchronized (Main.db.statement) {
            try {
                resSet = Main.db.select("SELECT COUNT(1) from ips where exists (select null from ips where ip in ('" + source + "', '" + dest + "'))");

                indexName = 0;
                if (resSet.next()) {
                    indexName = resSet.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return indexName > 0;
    }

    public boolean blockPortListContains (String source, String dest) {
        ResultSet resSet;
        int indexName = 0;
        synchronized (Main.db.statement) {
            try {
                resSet = Main.db.select("SELECT COUNT(1) from ports where exists (select null from ports where port in ('" + source + "', '" + dest + "'))");

                indexName = 0;
                if (resSet.next()) {
                    indexName = resSet.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return indexName > 0;
    }*/
}
