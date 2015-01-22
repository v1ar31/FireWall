import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestFireWallFilterService {
    public static FireWallFilterService filter;


    @BeforeClass
    public static void init() {
        filter = new FireWallFilterService();
    }

    @AfterClass
    public static void exit() {
        filter = null;
    }

    @Test
    public void testBlockListContainsWithoutRecord() {
        boolean ret = filter.blockListContains("","");

        assertEquals(ret, false);
    }
}
