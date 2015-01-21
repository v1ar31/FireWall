package windivert;


public class HeaderIPv4 extends Header {
    public byte version;
    public byte ihl;
    public byte tos;
    public int totalLength;
    public int identification;
    public byte flags;
    public int fragmentOffset;
    public byte ttl;
    public byte protocol;
    public short headerChecksum;
    private final int sourceIPAddressINT;
    private final int destinationIPAddressINT;
    public String sourceIPAddress;
    public String destinationIPAddress;
    public int options;
    public int destinationPort;
    public int sourcePort;


    public HeaderIPv4(byte[] packetBytes) {
        // ver_ihl byte
        version = (byte) (((int)packetBytes[0] & 0xFF) >>> 4);
        ihl = (byte) ((int)packetBytes[0] & 0xF);

        // tos byte
        tos = packetBytes[1];

        // totallength 2byte
        totalLength = ws2_32.ntohs((short) concatBytes(packetBytes[2],packetBytes[3])) & 0xFFFF;

        // idnt 2byte
        identification = ws2_32.ntohs((short) concatBytes(packetBytes[4], packetBytes[5])) & 0xFFFF;

        // frags and offset 2byte
        fragmentOffset = ws2_32.ntohs((short) concatBytes(packetBytes[6], packetBytes[7])) & 0xFFFF;
        flags = (byte)(fragmentOffset & 0xE000);
        fragmentOffset &= 0x1FFF;

        // ttl byte
        ttl = packetBytes[8];

        // protocol byte
        protocol = packetBytes[9];
        headerChecksum = (short) concatBytes(packetBytes[10], packetBytes[11]);

        // ip addr 8bytes
        sourceIPAddressINT = (int) concatBytes(packetBytes[12], packetBytes[13], packetBytes[14], packetBytes[15]);
        destinationIPAddressINT = (int) concatBytes(packetBytes[16], packetBytes[17], packetBytes[18], packetBytes[19]);
        sourceIPAddress = ws2_32.inet_ntoa(ws2_32.ntohl(sourceIPAddressINT));
        destinationIPAddress = ws2_32.inet_ntoa(ws2_32.ntohl(destinationIPAddressINT));

        // option
        options = (ihl > 5)? (int) concatBytes(packetBytes[20], packetBytes[21], packetBytes[22], packetBytes[23]) : 0;
        int i = (ihl > 5)? 24 : 20;

        // Ports
        if (protocol == 0x11 || protocol == 0x06) {
            sourcePort = (int) concatBytes(packetBytes[i], packetBytes[i+1]);
            destinationPort = (int) concatBytes(packetBytes[i+2], packetBytes[i+3]);
        }
    }
}