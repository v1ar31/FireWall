import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


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
        notification.incomingPackets.clear();
    }


    @Test
    public void testUpdateWithDublicatePacket() throws Exception {
        notification.update(0, "127.0.0.1", "21");
        notification.update(0, "127.0.0.1", "21");

        assertEquals(notification.incomingPackets.size(), 1);
    }

    @Test
    public void testUpdateWithEqualPacket() throws Exception {
        notification.update(0, "1.1.1.1", "127");
        notification.update(1, "1.1.1.1", "127");

        assertEquals(notification.incomingPackets.size(), 1);
    }

    @Test
    public void testUpdateWithUnEqualPacket() throws Exception {
        notification.update(0, "127.1.1.1", null);
        notification.update(0, null, "228");

        assertEquals(notification.incomingPackets.size(), 2);
    }

    @Test(expected = NotificationException.class)
    public void testUpdateWithNullParams() throws NotificationException {
        notification.update(0, null, null);
    }


}
