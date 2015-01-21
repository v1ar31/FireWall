package windivert;

import com.sun.jna.Native;


public abstract class FilterService extends Thread {
    final private int IPV4 = 4;
    final private int IPV6 = 6;

    private boolean isStarted;

    private WinDivertDriver divertDriver;

    public FilterService() {
        divertDriver = new WinDivertDriver();
    }

		// 1. Packaga parsing for specific ip version can be in separate method { FIX }
		// 2. Main form is forbidden on this level       { FIX }

    public void run() {
        if (divertDriver.openWinDivert() != 0) {                                                    // try - catch
            isStarted = false;
            System.out.println("Error in run FilterServece ");
        } else {
            isStarted = true;
        }

        while (isStarted) {
            if(!interrupted()) {

                Packet packet = divertDriver.receivePacket();                                       // try - catch
                if (packet == null) {
                    System.out.println("warning: failed to read packet"
                            + Integer.toHexString(Native.getLastError()));
                    continue;
                }

                Header header = new Header(packet.packetBytes);
                if (header.getVersionProtocol() == IPV4) {
                    if (filteredIPv4Header((HeaderIPv4) header.getNetworkProtocol(), packet.addr.Direction)) {
                        continue;
                    }
                }

                if (header.getVersionProtocol() == IPV6){
                   if (filteredIPv6Header((HeaderIPv6) header.getNetworkProtocol(), packet.addr.Direction)) {
                       continue;
                   }
                }

                if (divertDriver.sendPacket(packet) != 0) {
                    System.out.println("warning: failed to send packet"
                            + Integer.toHexString(Native.getLastError()));
                }

            } else {                                                                              // try - catch
                divertDriver.closeWinDivert();
                isStarted = false;
            }
        }

    }

    public abstract boolean filteredIPv4Header (HeaderIPv4 ipv4Header, int direction);
    public abstract boolean filteredIPv6Header (HeaderIPv6 ipv6Header, int direction);


}
