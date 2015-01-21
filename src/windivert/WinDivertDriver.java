package windivert;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT;

import static com.sun.jna.platform.win32.WinBase.INVALID_HANDLE_VALUE;


public class WinDivertDriver {
    private WinDivertLibrary lib;
    private WinNT.HANDLE handle;

    public WinDivertDriver() {
        lib = WinDivertLibrary.INSTANCE;
        handle = null;
    }

    public int openWinDivert() {                                                                                         // -->> add throws
        handle = lib.WinDivertOpen("true", WinDivertLibrary.WINDIVERT_LAYER.WINDIVERT_LAYER_NETWORK, (short)0, 0);
        if (handle == INVALID_HANDLE_VALUE) {
            handle = null;
            return 1;
        }
        return 0;
    }

    public int closeWinDivert() {                                                                                        // -->> add throws
        if (handle != null) {
            if(!lib.DivertClose(handle)) {
                handle = null;
                return 1;
            }
        }
        return 0;
    }

    public Packet receivePacket() {                                                                                           // -->> add throws
        Packet recv = new Packet();
        if (!lib.WinDivertRecv(handle, recv.packetBytes, recv.MAXBUF, recv.addr, recv.packetLen)) {
            System.out.println("warning: failed to read packet"
                    + Integer.toHexString(Native.getLastError()));
            return null;
        }
        return recv;
    }

    public int sendPacket(Packet send) {                                                                                   // -->> add throws
        if (!lib.WinDivertSend(handle, send.packetBytes, send.packetLen.getValue(), send.addr, 0)) {
            System.out.println("warning: failed to reinject packet (%d)\n"
                    + Integer.toHexString(Native.getLastError()));
            return 1;
        }
        return 0;
    }


}
