package windivert;

public abstract class FilterService extends Thread {
    final private int IPV4 = 4;
    final private int IPV6 = 6;

    private WinDivertDriver divertDriver;

    public FilterService() {
        divertDriver = new WinDivertDriver();
    }

		// 1. Packaga parsing for specific ip version can be in separate method { FIX }
		// 2. Main form is forbidden on this level       { FIX }

    public void run() {
        boolean isStarted;
        try {
            divertDriver.openWinDivert();
            isStarted = true;
        } catch (WinDivertException e) {
            e.printStackTrace();
            isStarted = false;
        }

        while (isStarted && !interrupted()) {
            try {
                Packet packet = divertDriver.receivePacket();

                Header header = new Header(packet.packetBytes);
                if (header.getVersionProtocol() == IPV4) {
                    if (filteredIPv4Header((HeaderIPv4) header.getNetworkProtocol(), packet.addr.Direction)) {
                        continue; //
                    }
                }

                if (header.getVersionProtocol() == IPV6) {
                   if (filteredIPv6Header((HeaderIPv6) header.getNetworkProtocol(), packet.addr.Direction)) {
                       continue;
                   }
                }

                divertDriver.sendPacket(packet);
            } catch (WinDivertException e) {
                //e.printStackTrace();
            }
        }

        try {
            divertDriver.closeWinDivert();
        } catch (WinDivertException e) {
            e.printStackTrace();
        }
    }

    public abstract boolean filteredIPv4Header (HeaderIPv4 ipv4Header, int direction);
    public abstract boolean filteredIPv6Header (HeaderIPv6 ipv6Header, int direction);


}
