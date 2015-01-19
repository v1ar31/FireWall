import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 *
 * @author v1ar
 */
public class Header {

    protected Ws2_32 ws2_32 = Ws2_32.INSTANCE;
    
    public long concatBytes (byte ... bytes) {
        long res = 0;
        for (byte bt : bytes) {
            res <<= 8;
            res |= (int)bt & 0xFF;
        }
        return res;
    }
    
    // pkt structure
    private int version_packet; // ipv4/ipv6 or unknown;
    public String name;
    public Header struct;

    
    public  Header () {
        
    }
    
    public Header(byte [] pktBytes) {
        version_packet = ((int)pktBytes[0] & 0xFF) >>> 4;
        switch(version_packet) {
            case 4: 
                name = "IPv4";
                struct = new HeaderIPv4(pktBytes);
                break;
            case 6:
                name = "IPv6";
                struct = new HeaderIPv6(pktBytes);
                break;
            default:
                name = "";
                struct = null;
        }
    }

    protected interface Ws2_32 extends Library {
        Ws2_32 INSTANCE = (Ws2_32) Native.loadLibrary("ws2_32", Ws2_32.class);

        public short htons( short hostshort );
        public short ntohs( short netshort );
        public int ntohl (int netlong);
        public int htonl (int hostlong);

        String inet_ntoa( int in_addr);
    }

    
}
