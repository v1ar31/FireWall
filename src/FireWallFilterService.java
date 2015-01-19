import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by v1ar on 20.01.15.
 */
public class FireWallFilterService extends FilterService {

    public SingleFireWall fireWall;

    public FireWallFilterService() {
        super();
        fireWall = SingleFireWall.getInstance();
    }

    @Override
    public boolean filteredIPv4Header(WinDivertDriver.Packet ipv4Packet) {
        Header header = new Header(ipv4Packet.packetBytes);
        HeaderIPv4 ipv4Header = (HeaderIPv4)header.struct;

        if (blockIPListContains(ipv4Header.SourceIPAddress, ipv4Header.DestinationIPAddress)) {

            if (ipv4Packet.addr.Direction == WinDivertLibrary.WINDIVERT_DIRECTION_OUTBOUND) {
                fireWall.notifyObservers(WinDivertLibrary.WINDIVERT_DIRECTION_OUTBOUND, SingleFireWall.IP, ipv4Header.DestinationIPAddress);
            } else {
                fireWall.notifyObservers(WinDivertLibrary.WINDIVERT_DIRECTION_INBOUND, SingleFireWall.IP, ipv4Header.SourceIPAddress);
            }

            return true;
        }

        // TCP, UDP
        // ERROR: Hello magic numbers!
        if (ipv4Header.Protocol == 0x11 || ipv4Header.Protocol == 0x06) {

            if (blockPortListContains(Integer.toString(ipv4Header.SourcePort), Integer.toString(ipv4Header.DestinationPort))) {

                if (ipv4Packet.addr.Direction == WinDivertLibrary.WINDIVERT_DIRECTION_OUTBOUND) {
                    fireWall.notifyObservers(WinDivertLibrary.WINDIVERT_DIRECTION_OUTBOUND, SingleFireWall.PORT,
                            Integer.toString(ipv4Header.DestinationPort));
                } else {
                    fireWall.notifyObservers(WinDivertLibrary.WINDIVERT_DIRECTION_INBOUND, SingleFireWall.PORT,
                            Integer.toString(ipv4Header.SourcePort));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean filteredIPv6Header(WinDivertDriver.Packet ipv6Packet) {
        // pass
        return false;
    }


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
    }
}
