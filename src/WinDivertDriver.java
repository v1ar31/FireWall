import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

import static com.sun.jna.platform.win32.WinBase.INVALID_HANDLE_VALUE;

/**
 * Created by v1ar on 16.01.15.
 */
public class WinDivertDriver {

    private WinDivertLibrary lib;
    private WinNT.HANDLE handle;

    public WinDivertDriver() {
        lib = WinDivertLibrary.INSTANCE;
        handle = null;
    }

    public int openWinDivert() {
        handle = lib.WinDivertOpen("true", WinDivertLibrary.WINDIVERT_LAYER.WINDIVERT_LAYER_NETWORK, (short)0, 0);
        if (handle == INVALID_HANDLE_VALUE) {
            handle = null;
            return 1;
        }
        return 0;
    }

    public int closeWinDivert() {
        if (handle != null) {
            if(!lib.DivertClose(handle)) {
                handle = null;
                return 1;
            }
        }
        return 0;
    }

    public WinNT.HANDLE getHandle() {
        return handle;
    }

    public WinDivertLibrary getLib() {
        return lib;
    }



    static public class Packet {
        final static int MAXBUF = 65528;

        public byte[] packetBytes;
        WinDivertLibrary.WINDIVERT_ADDRESS addr;
        IntByReference packet_len;

        public Packet () {
            packetBytes = new byte[MAXBUF];
            packet_len = new IntByReference();
            addr = new WinDivertLibrary.WINDIVERT_ADDRESS();
        }

    }

    public  Packet recvPkt() {
        Packet recv = new Packet();
        if (!lib.WinDivertRecv(handle, recv.packetBytes, recv.MAXBUF, recv.addr, recv.packet_len)) {
            System.out.println("warning: failed to read packet"
                    + Integer.toHexString(Native.getLastError()));
            return null;
        }
        return recv;
    }

    public int sendPkt (Packet send) {
        if (!lib.WinDivertSend(handle, send.packetBytes, send.packet_len.getValue(), send.addr, 0)) {
            System.out.println("warning: failed to reinject packet (%d)\n"
                    + Integer.toHexString(Native.getLastError()));
            return 1;
        }
        return 0;
    }


}
