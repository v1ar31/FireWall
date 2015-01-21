import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import windivert.Header;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by v1ar on 16.01.15.
 */
public class TestHeader {
    static Header header;
    static byte[] tst1;
    static byte[] tst2;
    static byte[] tst3;

    @BeforeClass
    public static void init() {
        header = new Header();
        tst1 = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        tst2 = new byte[]{};
        tst3 = null;
    }

    @AfterClass
    public static void after() {
        header = null;
        tst1 = null;
        tst2 = null;
    }

    @Test
    public void testConcatBytesWithTwoBytes() throws Exception {
        int concat = (int) header.concatBytes((byte)0xAA, (byte)0xBB);
        assertEquals(0xAABB, concat);
    }

    @Test
    public void testConcatBytesWithOneByte() throws Exception {
        int concat = (int) header.concatBytes((byte)0xAA);
        assertEquals(0xAA, concat);
    }

    @Test
    public void testConcatBytesWithZeroBytes() throws Exception {
        int concat = (int) header.concatBytes();
        assertEquals(0, concat);
    }

    @Test
    public void testConstructorHeaderWithSalt() throws Exception {
        Header header1 = new Header(tst1);
        assertNull(header1.networkProtocol);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testConstructorHeaderWithEmptyArray() throws Exception {
        Header header1 = new Header(tst2);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorHeaderWithNULL() throws Exception {
        Header header1 = new Header(tst3);
    }



}
