package windivert;

import com.sun.jna.ptr.IntByReference;

public class Packet {
    public final int MAXBUF = 65528;

    public byte[] packetBytes;
    public WinDivertLibrary.WINDIVERT_ADDRESS addr;
    public IntByReference packetLen;

    public Packet () {
        packetBytes = new byte[MAXBUF];
        packetLen = new IntByReference();
        addr = new WinDivertLibrary.WINDIVERT_ADDRESS();
    }
}