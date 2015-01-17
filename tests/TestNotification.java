import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

    @After
    public void after() {
        notification.prevPkts.clear();
    }


    @Test
    public void testUpdateWithDublicatePacket() throws Exception {
        notification.update(0,FireWall.IP, "127.0.0.1");
        notification.update(0,FireWall.IP, "127.0.0.1");

        assertEquals(notification.prevPkts.size(), 1);
    }

    @Test
    public void testUpdateWithEqualPacket() throws Exception {
        notification.update(0,FireWall.IP, "127");
        notification.update(0,FireWall.PORT, "127");

        assertEquals(notification.prevPkts.size(), 1);
    }

    @Test
    public void testUpdateWithUnEqualPacket() throws Exception {
        notification.update(0,FireWall.IP, "127.1.1.1");
        notification.update(0,FireWall.PORT, "228");

        assertEquals(notification.prevPkts.size(), 2);
    }


}
