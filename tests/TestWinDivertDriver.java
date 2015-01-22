import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import windivert.Packet;
import windivert.WinDivertDriver;
import windivert.WinDivertException;

public class TestWinDivertDriver {
    public static WinDivertDriver winDivertDriver;

    @BeforeClass
    public static void init() {
        winDivertDriver = new WinDivertDriver();
        winDivertDriver.handle = new WinNT.HANDLE(new Pointer(0xFFFFF)); // emule openWinDriver
    }

    @AfterClass
    public static void exit() {
        winDivertDriver = null;
    }

    @Test(expected = WinDivertException.class)
    public void testCloseDriverWithoutOpen() throws WinDivertException {
        winDivertDriver.closeWinDivert();
    }

    @Test(expected = WinDivertException.class)
    public void testRecvPacketWithoutOpen() throws WinDivertException {
        winDivertDriver.receivePacket();
    }

    @Test(expected = WinDivertException.class)
    public void testSendPacketWithoutOpen() throws WinDivertException {
        winDivertDriver.sendPacket(new Packet());
    }
}
