import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by v1ar on 16.01.15.
 */
public class HeaderTest {
    private String hop1;
    private String hop2;

    @Before
    public void init() {
        this.hop1 = "privet";
        this.hop2 = "moloko";
    }

    @After
    public void after() {
        this.hop1 = "kak";
        this.hop2 = "dela";
    }

    @Test
    public void testOdin() {
        int a = 3;
        int b = 2;

        assertTrue((a+b) ==  5);
    }

    @Test
    public void test2() {
        int a = 3;
        int b = 2;

        assertFalse(a + b != 5);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void test3() {
        int[] arr = new int[5];

        arr[6] = 2;
    }

    @Test
    public void test4() {
        assertEquals(hop1 + " " + hop2, "privet moloko");
        assertNotNull(hop1);
        assertFalse(false);
        assertTrue(true);
    }


}
