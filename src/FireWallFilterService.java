import javafx.util.Pair;
import sqldb.DBAdapter;
import windivert.FilterService;
import windivert.HeaderIPv4;
import windivert.HeaderIPv6;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


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
        String address = null;
        String Port = null;

        address = (direction == DIRECTION_OUTBOUND)? ipv4Header.destinationIPAddress
                                                   : ipv4Header.sourceIPAddress;


        if ((ipv4Header.protocol == UDP) || (ipv4Header.protocol == TCP)) {
            int tmpPort = (direction == DIRECTION_OUTBOUND)? ipv4Header.destinationPort
                                                           : ipv4Header.sourcePort;
            Port = Integer.toString(tmpPort);
        }

        if (blockListContains(address, null)
                || ((Port != null) && (blockListContains(null, Port) || blockListContains(address, Port) ))) {

            try {
                fireWall.notifyObservers(direction, address, Port);
            } catch (NotificationException e) {
                e.printStackTrace();
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean filteredIPv6Header(HeaderIPv6 ipv6Header, int direction) {
        // pass
        return false;
    }


    public boolean blockListContains (final String address, final String port)  {
        int indexName = 0;

        try {
            List<Pair<String, String>> values= new ArrayList<Pair<String, String>>(){{
                add(new Pair<>("ip", address));
                add(new Pair<>("port", port));
            }};

            ResultSet resSet = dbAdapter.selectAllWith("list", values);

            indexName = 0;
            if (resSet.next()) {
                indexName++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return indexName > 0;
    }
}
