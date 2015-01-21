package windivert;

import com.sun.jna.Library;
import com.sun.jna.Native;


public class Header {
    protected Ws2_32 ws2_32;

    private int versionProtocol;
    public Header networkProtocol;

    
    public  Header () {
        ws2_32 = Ws2_32.INSTANCE;
    }
    
    public Header(byte [] packetBytes) {
        versionProtocol = ((int)packetBytes[0] & 0xFF) >>> 4; // one byte of header is type
        switch(versionProtocol) {
            case 4: // IPv4
                networkProtocol = new HeaderIPv4(packetBytes);
                break;
            case 6: // IPv6
                networkProtocol = new HeaderIPv6(packetBytes);
                break;
            default:
                versionProtocol = 0;
                networkProtocol = null;
        }
    }

    public int getVersionProtocol() {
        return versionProtocol;
    }

    public Header getNetworkProtocol() {
        return networkProtocol;
    }

    public long concatBytes (byte ... bytes) {
        long res = 0;
        for (byte bt : bytes) {
            res <<= 8;
            res |= (int)bt & 0xFF;
        }
        return res;
    }

    protected interface Ws2_32 extends Library {
        Ws2_32 INSTANCE = (Ws2_32) Native.loadLibrary("ws2_32", Ws2_32.class);

        public short ntohs(short netshort );
        public int ntohl (int netlong);
        public String inet_ntoa(int in_addr);
    }

    
}
