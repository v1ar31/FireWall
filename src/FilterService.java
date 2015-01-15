import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by v1ar on 14.01.15.
 */
public class FilterService extends Thread implements Observable{
    public boolean isStarted = false;
    final static int MAXBUF = 65528;
    private WinDivertLibrary lib;
    private WinNT.HANDLE handle;

    private ArrayList<Observer> observers;

    public FilterService(){
        observers = new ArrayList<>();
    }

    public void init (WinDivertLibrary lib, WinNT.HANDLE handle) {
        this.lib = lib;
        this.handle = handle;
    }
    public void run() {
        isStarted = true;

        // основа для пакета - MAXBUF байтов
        byte[] packetBytes  = new byte[MAXBUF];

        // возвращаемые параметры для WinDivertRecv
        WinDivertLibrary.WINDIVERT_ADDRESS addr = new WinDivertLibrary.WINDIVERT_ADDRESS();
        IntByReference packet_len = new IntByReference();

        while (true) {
            if(!interrupted()) {

                // Read a matching packet.
                if (!lib.WinDivertRecv(handle, packetBytes, MAXBUF, addr, packet_len)) {
                    System.out.println("warning: failed to read packet"
                            + Integer.toHexString(Native.getLastError()));
                    continue;
                }

                Header packet = new Header(packetBytes);
                if (packet.name.compareTo("IPv4") == 0) {
                    Header.HeaderIPv4 ipv4 = (Header.HeaderIPv4)packet.struct_packet;

                    if (blockIPListContains(ipv4.SourceIPAddress, ipv4.DestinationIPAddress)) {
                        /*System.out.print("Block  ");
                        System.out.printf("%n ver = %d, ihl = %d, protocol = 0x%02x, srs = %s, dst = %s  %n",
                                ipv4.Version, ipv4.IHL,
                                ipv4.Protocol, ipv4.SourceIPAddress,
                                ipv4.DestinationIPAddress);*/
                        notifyObservers();
                        continue;
                    }

                    // TCP, UDP
                    if (ipv4.Protocol == 0x11 || ipv4.Protocol == 0x06) {

                        if (blockPortListContains(Integer.toString(ipv4.SourcePort), Integer.toString(ipv4.DestinationPort))) {
                            /*System.out.print("Block  ");
                            System.out.printf("srcprt = %d, dstprt = %d %n", ipv4.SourcePort, ipv4.DestinationPort);*/
                            notifyObservers();
                            continue;
                        }
                    }
                }

                if (packet.name.compareTo("IPv6") == 0){
                    // pass
                }

                if (!lib.WinDivertSend(handle, packetBytes, packet_len.getValue(), addr, 0)) {
                    System.out.println("warning: failed to reinject packet (%d)\n"
                            + Integer.toHexString(Native.getLastError()));
                }

            } else {
                //System.out.println("break");
                break;
            }
        }

        isStarted = false;
    }

    public boolean blockIPListContains (String source, String dest)  {
        ResultSet resSet;
        int indexName = 0;
        synchronized (Main.db.statement) {
            try {
                resSet = Main.db.statement.executeQuery("SELECT COUNT(1) from ips where exists (select null from ips where ip in ('" + source + "', '" + dest + "'))");

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
                resSet = Main.db.statement.executeQuery("SELECT COUNT(1) from ports where exists (select null from ports where port in ('" + source + "', '" + dest + "'))");

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

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer o: observers) {
            o.update();
        }
    }
}
