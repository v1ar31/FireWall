package windivert;

import com.sun.jna.*;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

import java.util.Arrays;
import java.util.List;


public interface WinDivertLibrary extends Library {
	public static final String JNA_LIBRARY_NAME = "WinDivert";
	public static final NativeLibrary JNA_NATIVE_LIB = NativeLibrary
            .getInstance(WinDivertLibrary.JNA_LIBRARY_NAME);
	public static final WinDivertLibrary INSTANCE = (WinDivertLibrary)Native
            .loadLibrary(WinDivertLibrary.JNA_LIBRARY_NAME, WinDivertLibrary.class);

	public static final int WINDIVERT_DIRECTION_OUTBOUND = 0;
	public static final int WINDIVERT_DIRECTION_INBOUND = 1;


    public static interface WINDIVERT_LAYER {
        public static final int WINDIVERT_LAYER_NETWORK = 0;
        public static final int WINDIVERT_LAYER_NETWORK_FORWARD = 1;
    };


	public static class WINDIVERT_ADDRESS extends Structure {
		public int IfIdx;
		public int SubIfIdx;
		public byte Direction;

		public WINDIVERT_ADDRESS() {
			super();
		}

        @Override
		protected List<? > getFieldOrder() {
			return Arrays.asList("IfIdx", "SubIfIdx", "Direction");
		}

		public WINDIVERT_ADDRESS(int IfIdx, int SubIfIdx, byte Direction) {
			super();
			this.IfIdx = IfIdx;
			this.SubIfIdx = SubIfIdx;
			this.Direction = Direction;
		}

		public WINDIVERT_ADDRESS(Pointer peer) {
			super(peer);
            read();
		}
	}
	

	HANDLE WinDivertOpen(String filter, int layer, short priority, long flags);

	boolean WinDivertRecv(HANDLE handle, byte [] pPacket, int packetLen,
                          WINDIVERT_ADDRESS pAddr, IntByReference readLen);

	boolean WinDivertSend(HANDLE handle, byte [] pPacket, int packetLen,
                          WINDIVERT_ADDRESS pAddr, int writeLen);

	boolean DivertClose(HANDLE handle);


}

