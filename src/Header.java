import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 *
 * @author v1ar
 */
public class Header {
    // c library
    private interface Ws2_32 extends Library {
        Ws2_32 INSTANCE = (Ws2_32) Native.loadLibrary("ws2_32", Ws2_32.class);

        public short htons( short hostshort );
        public short ntohs( short netshort );
        public int ntohl (int netlong);
        public int htonl (int hostlong);
        
        String inet_ntoa( int in_addr);
    }
    private Ws2_32 ws2_32 = Ws2_32.INSTANCE;   
    
    private long concatBytes (byte ... bytes) {
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
    public Header struct_packet;
    
    
    public class HeaderIPv4 extends Header {
        public byte Version;
        public byte IHL;
        public byte TOS;
        public int TotalLength;
        public int Identification;
        public byte Flags;
        public int FragmentOffset;
        public byte TTL;
        public byte Protocol;
        public short HeaderChecksum;
        private final int SourceIPAddressINT;
        private final int DestinationIPAddressINT;
        public String SourceIPAddress;
        public String DestinationIPAddress;
        public int Options;
        public int DestinationPort;
        public int SourcePort;
        
        
        public HeaderIPv4(byte [] pktBytes) {
            name = "IPv4";
            // ver_ihl byte
            Version = (byte) (((int)pktBytes[0] & 0xFF) >>> 4);
            IHL = (byte) ((int)pktBytes[0] & 0xF);
            // TOS byte
            TOS = pktBytes[1];
            // totallength 2byte
            TotalLength = ws2_32.ntohs((short) concatBytes(pktBytes[2],pktBytes[3])) & 0xFFFF;
            // idnt 2byte
            Identification = ws2_32.ntohs((short) concatBytes(pktBytes[4], pktBytes[5])) & 0xFFFF;
            // frags and offset 2byte
            FragmentOffset = ws2_32.ntohs((short) concatBytes(pktBytes[6], pktBytes[7])) & 0xFFFF;
            Flags = (byte)(FragmentOffset & 0xE000);
            FragmentOffset &= 0x1FFF;
            // ttl byte
            TTL = pktBytes[8];
            // protocol byte
            Protocol = pktBytes[9];
            HeaderChecksum = (short) concatBytes(pktBytes[10], pktBytes[11]);
            // ip addr 8bytes
            SourceIPAddressINT = (int) concatBytes(pktBytes[12], pktBytes[13], pktBytes[14], pktBytes[15]);
            DestinationIPAddressINT = (int) concatBytes(pktBytes[16], pktBytes[17], pktBytes[18], pktBytes[19]);
            SourceIPAddress = ws2_32.inet_ntoa(ws2_32.ntohl(SourceIPAddressINT));
            DestinationIPAddress = ws2_32.inet_ntoa(ws2_32.ntohl(DestinationIPAddressINT));
            // option
            Options = (IHL > 5)? (int) concatBytes(pktBytes[20], pktBytes[21], pktBytes[22], pktBytes[23]) : 0;
            int i = (IHL > 5)? 24 : 20;
                        
            // Ports
            if (Protocol == 0x11 || Protocol == 0x06) {
                SourcePort = (int) concatBytes(pktBytes[i], pktBytes[i+1]);
                DestinationPort = (int) concatBytes(pktBytes[i+2], pktBytes[i+3]);
            }
        }
    }
    
    public class HeaderIPv6 extends Header {
        public HeaderIPv6(byte [] pktBytes){
            name = "IPv6";
            
        }
    }
    
    public  Header () {
        
    }
    
    public Header(byte [] pktBytes) {
        version_packet = ((int)pktBytes[0] & 0xFF) >>> 4;
        switch(version_packet) {
            case 4: 
                name = "IPv4";
                struct_packet = new HeaderIPv4(pktBytes);
                break;
            case 6:
                name = "IPv6";
                struct_packet = new HeaderIPv6(pktBytes);
                break;
            default:
                name = "";
                struct_packet = null;
        }
    }
    
}
