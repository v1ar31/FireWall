package windivert;

import com.sun.jna.platform.win32.WinNT;

import static com.sun.jna.platform.win32.WinBase.INVALID_HANDLE_VALUE;


public class WinDivertDriver {
    private WinDivertLibrary lib;
    private WinNT.HANDLE handle;

    public WinDivertDriver() {
        lib = WinDivertLibrary.INSTANCE;
        handle = null;
    }

    public void openWinDivert() throws WinDivertException {
        handle = lib.WinDivertOpen("true", WinDivertLibrary.WINDIVERT_LAYER.WINDIVERT_LAYER_NETWORK, (short)0, 0);
        if (handle == INVALID_HANDLE_VALUE) {
            handle = null;
            throw new WinDivertException("Cannot open WinDivertDriver");
        }
    }

    public void closeWinDivert() throws WinDivertException {
        if (handle != null) {
            if(!lib.DivertClose(handle)) {
                handle = null;
                throw new WinDivertException("Cannot close WinDivertDriver");
            }
        }
    }

    public Packet receivePacket() throws WinDivertException {
        Packet recv = new Packet();
        if (!lib.WinDivertRecv(handle, recv.packetBytes, recv.MAXBUF, recv.addr, recv.packetLen)) {
            throw new WinDivertException("failed to read packet in WinDivertDriver");
        }
        return recv;
    }

    public void sendPacket(Packet send) throws WinDivertException {
        if (!lib.WinDivertSend(handle, send.packetBytes, send.packetLen.getValue(), send.addr, 0)) {
            throw new WinDivertException("failed to send packet in WinDiverDriver");
        }
    }


}
