import com.sun.jna.*;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

import java.util.Arrays;
import java.util.List;


public interface WinDivertLibrary extends Library {
	public static final String JNA_LIBRARY_NAME = "WinDivert";
	public static final NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(WinDivertLibrary.JNA_LIBRARY_NAME);
	public static final WinDivertLibrary INSTANCE = (WinDivertLibrary)Native.loadLibrary(WinDivertLibrary.JNA_LIBRARY_NAME, WinDivertLibrary.class);
	
	public static interface WINDIVERT_LAYER {
		public static final int WINDIVERT_LAYER_NETWORK = 0;
		public static final int WINDIVERT_LAYER_NETWORK_FORWARD = 1;
	};
	
	public static interface WINDIVERT_PARAM {
		public static final int WINDIVERT_PARAM_QUEUE_LEN = 0;
		public static final int WINDIVERT_PARAM_QUEUE_TIME = 1;
	};
	
	
	
	public static final int DIVERT_HELPER_NO_UDP_CHECKSUM = (int)16;
	public static final int WINDIVERT_HELPER_NO_ICMPV6_CHECKSUM = (int)4;
	public static final int DIVERT_HELPER_NO_TCP_CHECKSUM = (int)8;
	public static final int DIVERT_HELPER_NO_IP_CHECKSUM = (int)1;
	public static final int WINDIVERT_DIRECTION_OUTBOUND = (int)0;
	public static final int DIVERT_FLAG_DROP = (int)2;
	public static final int WINDIVERT_FLAG_DROP = (int)2;
	public static final int WINDIVERT_FLAG_NO_CHECKSUM = (int)1024;
	public static final int DIVERT_FLAG_SNIFF = (int)1;
	public static final int DIVERT_HELPER_NO_ICMPV6_CHECKSUM = (int)4;
	public static final int DIVERT_DIRECTION_INBOUND = (int)1;
	public static final int WINDIVERT_HELPER_NO_ICMP_CHECKSUM = (int)2;
	public static final int WINDIVERT_DIRECTION_INBOUND = (int)1;
	public static final int DIVERT_DIRECTION_OUTBOUND = (int)0;
	public static final long WINDIVERT_FLAG_SNIFF = 1L;
	public static final int DIVERT_HELPER_NO_ICMP_CHECKSUM = (int)2;
	public static final int WINDIVERT_HELPER_NO_UDP_CHECKSUM = (int)16;
	public static final int WINDIVERT_HELPER_NO_IP_CHECKSUM = (int)1;
	public static final int WINDIVERT_HELPER_NO_TCP_CHECKSUM = (int)8;
	
	public static class WINDIVERT_ADDRESS extends Structure {
		/** Packet's interface index. */
		public int IfIdx;
		/** Packet's sub-interface index. */
		public int SubIfIdx;
		/** Packet's direction. */
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
	};
	
	public static class WINDIVERT_IPHDR extends Structure {
		public byte HdrLength;
                public byte Version;
		public byte TOS;
		public short Length;
		public short Id;
		public short FragOff0;
		public byte TTL;
		public byte Protocol;
		public short Checksum;
		public int SrcAddr;
		public int DstAddr;
		public WINDIVERT_IPHDR() {
			super();
		}
                @Override
		protected List<? > getFieldOrder() {
			return Arrays.asList(/*"HdrLength",*/ "Version", "TOS", "Length", "Id", "FragOff0", "TTL", "Protocol", "Checksum", "SrcAddr", "DstAddr");
		}
		public WINDIVERT_IPHDR(/*byte HdrLength,*/ byte Version, byte TOS, short Length, short Id, short FragOff0, byte TTL, byte Protocol, short Checksum, int SrcAddr, int DstAddr) {
			super();
                        //this.HdrLength = HdrLength;
			this.Version = Version;
			this.TOS = TOS;
			this.Length = Length;
			this.Id = Id;
			this.FragOff0 = FragOff0;
			this.TTL = TTL;
			this.Protocol = Protocol;
			this.Checksum = Checksum;
			this.SrcAddr = SrcAddr;
			this.DstAddr = DstAddr;
		}
		public WINDIVERT_IPHDR(Pointer peer) {
			super(peer);
                        read();
		}
	};
	public static class WINDIVERT_IPV6HDR extends Structure {
		public byte TrafficClass0_Version;
		public byte FlowLabel0_TrafficClass1;
		public short FlowLabel1;
		public short Length;
		public byte NextHdr;
		public byte HopLimit;
		public int[] SrcAddr = new int[4];
		public int[] DstAddr = new int[4];
		public WINDIVERT_IPV6HDR() {
			super();
		}
                @Override
		protected List<? > getFieldOrder() {
			return Arrays.asList("TrafficClass0_Version", "FlowLabel0_TrafficClass1", "FlowLabel1", "Length", 
				"NextHdr", "HopLimit", "SrcAddr", "DstAddr");
		}
		public WINDIVERT_IPV6HDR(byte TrafficClass0_Version, 
			byte FlowLabel0_TrafficClass1, short FlowLabel1, 
			short Length, byte NextHdr, byte HopLimit, 
			int SrcAddr[], int DstAddr[]) {
			super();
			this.TrafficClass0_Version = TrafficClass0_Version;
			this.FlowLabel0_TrafficClass1 = FlowLabel0_TrafficClass1;
			this.FlowLabel1 = FlowLabel1;
			this.Length = Length;
			this.NextHdr = NextHdr;
			this.HopLimit = HopLimit;
			if ((SrcAddr.length != this.SrcAddr.length)) 
				throw new IllegalArgumentException("Wrong array size !");
			this.SrcAddr = SrcAddr;
			if ((DstAddr.length != this.DstAddr.length)) 
				throw new IllegalArgumentException("Wrong array size !");
			this.DstAddr = DstAddr;
		}
		public WINDIVERT_IPV6HDR(Pointer peer) {
			super(peer);
                        read();
		}
	};
	public static class WINDIVERT_ICMPHDR extends Structure {
		public byte Type;
		public byte Code;
		public short Checksum;
		public int Body;
		public WINDIVERT_ICMPHDR() {
			super();
		}
                @Override
		protected List<? > getFieldOrder() {
			return Arrays.asList("Type", "Code", "Checksum", "Body");
		}
		public WINDIVERT_ICMPHDR(byte Type, byte Code, 
			short Checksum, int Body) {
			super();
			this.Type = Type;
			this.Code = Code;
			this.Checksum = Checksum;
			this.Body = Body;
		}
		public WINDIVERT_ICMPHDR(Pointer peer) {
			super(peer);
                        read();
		}
	};
	
	public static class WINDIVERT_ICMPV6HDR extends Structure {
		public byte Type;
		public byte Code;
		public short Checksum;
		public int Body;
		public WINDIVERT_ICMPV6HDR() {
			super();
		}
                @Override
		protected List<? > getFieldOrder() {
			return Arrays.asList("Type", "Code", "Checksum", "Body");
		}
		public WINDIVERT_ICMPV6HDR(byte Type, byte Code, 
			short Checksum, int Body) {
			super();
			this.Type = Type;
			this.Code = Code;
			this.Checksum = Checksum;
			this.Body = Body;
		}
		public WINDIVERT_ICMPV6HDR(Pointer peer) {
			super(peer);
                        read();
		}
	};
	
	public static class WINDIVERT_TCPHDR extends Structure {
		public short SrcPort;
		public short DstPort;
		public int SeqNum;
		public int AckNum;
		public short Reserved;
		public short Window;
		public short Checksum;
		public short UrgPtr;
		public WINDIVERT_TCPHDR() {
			super();
		}
                @Override
		protected List<? > getFieldOrder() {
			return Arrays.asList("SrcPort", "DstPort", "SeqNum", "AckNum", "Reserved", 
				"Window", "Checksum", "UrgPtr");
		}
		public WINDIVERT_TCPHDR(short SrcPort, short DstPort, 
			int SeqNum, int AckNum, short Reserved, short Window, 
			short Checksum, short UrgPtr) {
			super();
			this.SrcPort = SrcPort;
			this.DstPort = DstPort;
			this.SeqNum = SeqNum;
			this.AckNum = AckNum;
			this.Reserved = Reserved;
			this.Window = Window;
			this.Checksum = Checksum;
			this.UrgPtr = UrgPtr;
		}
		public WINDIVERT_TCPHDR(Pointer peer) {
			super(peer);
                        read();
		}
	};
	public static class WINDIVERT_UDPHDR extends Structure {
		public short SrcPort;
		public short DstPort;
		public short Length;
		public short Checksum;
		public WINDIVERT_UDPHDR() {
			super();
		}
                @Override
		protected List<? > getFieldOrder() {
			return Arrays.asList("SrcPort", "DstPort", "Length", "Checksum");
		}
		public WINDIVERT_UDPHDR(short SrcPort, short DstPort, short Length, short Checksum) {
			super();
			this.SrcPort = SrcPort;
			this.DstPort = DstPort;
			this.Length = Length;
			this.Checksum = Checksum;
		}
		public WINDIVERT_UDPHDR(Pointer peer) {
			super(peer);
                        read();
		}
	};
	
	
	HANDLE WinDivertOpen(String filter, int layer, short priority, long flags);
	boolean WinDivertRecv(HANDLE handle, byte [] pPacket, int packetLen, WinDivertLibrary.WINDIVERT_ADDRESS pAddr, IntByReference readLen);
	boolean WinDivertHelperParsePacket(byte [] pPacket, int packetLen, PointerByReference ppIpHdr, PointerByReference ppIpv6Hdr, PointerByReference ppIcmpHdr, PointerByReference ppIcmpv6Hdr, PointerByReference ppTcpHdr, PointerByReference ppUdpHdr, Pointer ppData, IntByReference pDataLen);
	boolean WinDivertSend(HANDLE handle, byte [] pPacket, int packetLen, WinDivertLibrary.WINDIVERT_ADDRESS pAddr, int writeLen);
	
        boolean WinDivertRecvEx(HANDLE handle, Pointer pPacket, int packetLen, long flags, WinDivertLibrary.WINDIVERT_ADDRESS pAddr, IntByReference readLen, WinBase.OVERLAPPED lpOverlapped);
	boolean WinDivertSendEx(HANDLE handle, Pointer pPacket, int packetLen, long flags, WinDivertLibrary.WINDIVERT_ADDRESS pAddr, IntByReference writeLen, WinBase.OVERLAPPED lpOverlapped);
	boolean WinDivertClose(HANDLE handle);
	boolean WinDivertSetParam(HANDLE handle, int param, long value);
	boolean WinDivertGetParam(HANDLE handle, int param, LongByReference pValue);
	boolean WinDivertHelperParseIPv4Address(String addrStr, IntByReference pAddr);
	boolean WinDivertHelperParseIPv6Address(String addrStr, IntByReference pAddr);
	int WinDivertHelperCalcChecksums(Pointer pPacket, int packetLen, long flags);
	HANDLE DivertOpen(String filter, WinDivertLibrary.WINDIVERT_LAYER layer, short priority, long flags);
	boolean DivertRecv(HANDLE handle, Pointer pPacket, int packetLen, WinDivertLibrary.WINDIVERT_ADDRESS pAddr, IntByReference readLen);
	boolean DivertSend(HANDLE handle, Pointer pPacket, int packetLen, WinDivertLibrary.WINDIVERT_ADDRESS pAddr, IntByReference writeLen);
	boolean DivertClose(HANDLE handle);
	boolean DivertSetParam(HANDLE handle, int param, long value);
	boolean DivertGetParam(HANDLE handle, int param, LongByReference pValue);
	boolean DivertHelperParsePacket(Pointer pPacket, int packetLen, PointerByReference ppIpHdr, PointerByReference ppIpv6Hdr, PointerByReference ppIcmpHdr, PointerByReference ppIcmpv6Hdr, PointerByReference ppTcpHdr, PointerByReference ppUdpHdr, Pointer ppData, IntByReference pDataLen);
	boolean DivertHelperParseIPv4Address(String addrStr, int pAddr);
	boolean DivertHelperParseIPv6Address(String addrStr, int pAddr);
	int DivertHelperCalcChecksums(Pointer pPacket, int packetLen, long flags);
	
	
}

