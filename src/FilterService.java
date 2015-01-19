import com.sun.jna.Native;

/**
 * Created by v1ar on 14.01.15.
 */
public abstract class FilterService extends Thread {
    public boolean isStarted = false;

    public WinDivertDriver windivdr;

    public FilterService() {
        windivdr = new WinDivertDriver();
    }

		// 1. Packaga parsing for specific ip version can be in separate method { FIX }
		// 2. Main form is forbidden on this level       { FIX }

    public void run() {
        if (windivdr.openWinDivert() != 0) {
            isStarted = false;
            System.out.println("Error in run FilterServece ");
        } else {
            isStarted = true;
        }

        while (isStarted) {
            if(!interrupted()) {

                WinDivertDriver.Packet packet = windivdr.recvPkt();
                if (packet == null) {
                    System.out.println("warning: failed to read packet"
                            + Integer.toHexString(Native.getLastError()));
                    continue;
                }

                Header header = new Header(packet.packetBytes);
                if (header.name.compareTo("IPv4") == 0) {
                    if (filteredIPv4Header(packet)) {
                        continue;
                    }
                }

                if (header.name.compareTo("IPv6") == 0){
                   if (filteredIPv6Header(packet)) {
                       continue;
                   }
                }

                if (windivdr.sendPkt(packet) != 0) {
                    System.out.println("warning: failed to send packet"
                            + Integer.toHexString(Native.getLastError()));
                }

            } else {
                windivdr.closeWinDivert();
                isStarted = false;
            }
        }

    }

    public abstract boolean filteredIPv4Header (WinDivertDriver.Packet ipv4Packet);
    public abstract boolean filteredIPv6Header (WinDivertDriver.Packet ipv6Packet);


}
