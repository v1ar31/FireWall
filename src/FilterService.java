import com.sun.jna.Native;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by v1ar on 14.01.15.
 */
public class FilterService extends Thread {
    public boolean isStarted = false;

    public WinDivertDriver windivdr;

    public FilterService(){
        windivdr = new WinDivertDriver();
    }

		// 1. Packaga parsing for specific ip version can be in separate method
		// 2. Main form is forbidden on this level

    public void run() {
        if (windivdr.openWinDivert() != 0) {
            isStarted = false;
            System.out.println("Error in run FilterServece ");
        } else {
            isStarted = true;
        }

        while (isStarted) {
            if(!interrupted()) {

                // Read a matching packet.
                //WinDivertDriver.Packet recpacket = new WinDivertDriver.Packet();
                WinDivertDriver.Packet recpacket = windivdr.recvPkt();
                if (recpacket == null) {
                    System.out.println("warning: failed to read packet"
                            + Integer.toHexString(Native.getLastError()));
                    continue;
                }

                Header packet = new Header(recpacket.packetBytes);
                if (packet.name.compareTo("IPv4") == 0) {
                    Header.HeaderIPv4 ipv4 = (Header.HeaderIPv4)packet.struct_packet;

                    if (blockIPListContains(ipv4.SourceIPAddress, ipv4.DestinationIPAddress)) {
                        /*System.out.print("Block  ");
                        System.out.printf("%n ver = %d, ihl = %d, protocol = 0x%02x, srs = %s, dst = %s  %n",
                                ipv4.Version, ipv4.IHL,
                                ipv4.Protocol, ipv4.SourceIPAddress,
                                ipv4.DestinationIPAddress);*/
                        if (recpacket.addr.Direction == WinDivertLibrary.WINDIVERT_DIRECTION_OUTBOUND) {
                            Main.fireWall.notifyObservers(WinDivertLibrary.WINDIVERT_DIRECTION_OUTBOUND, FireWall.IP, ipv4.DestinationIPAddress);
                        } else {
                            Main.fireWall.notifyObservers(WinDivertLibrary.WINDIVERT_DIRECTION_INBOUND, FireWall.IP, ipv4.SourceIPAddress);
                        }
                        continue;
                    }

                    // TCP, UDP
										// ERROR: Hello magic numbers!
                    if (ipv4.Protocol == 0x11 || ipv4.Protocol == 0x06) {

                        if (blockPortListContains(Integer.toString(ipv4.SourcePort), Integer.toString(ipv4.DestinationPort))) {
                            //System.out.print("Block  ");
                            //System.out.printf("srcprt = %d, dstprt = %d %n", ipv4.SourcePort, ipv4.DestinationPort);

                            if (recpacket.addr.Direction == WinDivertLibrary.WINDIVERT_DIRECTION_OUTBOUND) {
                                Main.fireWall.notifyObservers(WinDivertLibrary.WINDIVERT_DIRECTION_OUTBOUND, FireWall.PORT,
                                        Integer.toString(ipv4.DestinationPort));
                            } else {
                                Main.fireWall.notifyObservers(WinDivertLibrary.WINDIVERT_DIRECTION_INBOUND, FireWall.PORT,
                                        Integer.toString(ipv4.SourcePort));
                            }
                            continue;
                        }
                    }
                }

                if (packet.name.compareTo("IPv6") == 0){
                    // pass
                }

                if (windivdr.sendPkt(recpacket) != 0) {
                    System.out.println("warning: failed to read packet"
                            + Integer.toHexString(Native.getLastError()));
                }

            } else {
                windivdr.closeWinDivert();
                isStarted = false;
            }
        }

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
