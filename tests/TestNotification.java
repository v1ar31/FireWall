import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Created by v1ar on 17.01.15.
 */
public class TestNotification {
    public static Notification notification;

    @BeforeClass
    public static void init() {
        notification = new Notification();
    }

    @AfterClass
    public static void exit() {
        notification = null;
    }
/*
    @After
    public void after() {
        notification.incomingPackets.clear();
    }


    @Test
    public void testUpdateWithDublicatePacket() throws Exception {
        notification.update(0,FireWallFilterService.IP, "127.0.0.1");
        notification.update(0,FireWallFilterService.IP, "127.0.0.1");

        assertEquals(notification.incomingPackets.size(), 1);
    }

    @Test
    public void testUpdateWithEqualPacket() throws Exception {
        notification.update(0,FireWallFilterService.IP, "127");
        notification.update(0,FireWallFilterService.PORT, "127");

        assertEquals(notification.incomingPackets.size(), 1);
    }

    @Test
    public void testUpdateWithUnEqualPacket() throws Exception {
        notification.update(0,FireWallFilterService.IP, "127.1.1.1");
        notification.update(0,FireWallFilterService.PORT, "228");

        assertEquals(notification.incomingPackets.size(), 2);
    }
*/

}
