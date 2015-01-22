import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestSingleFireWall {

    public static SingleFireWall fireWall;

    @BeforeClass
    public static void init() {
        fireWall = SingleFireWall.getInstance();
    }

    @AfterClass
    public static void exit() {
        fireWall = null;
    }

    @Test(expected = FireWallException.class)
    public void testRepeatedOpening() throws FireWallException {
        fireWall.startFilter();
        fireWall.startFilter();
        fireWall.stopFilter();
    }

    @Test(expected = FireWallException.class)
    public void testRepeatedClosing() throws FireWallException {
        fireWall.startFilter();
        fireWall.stopFilter();
        fireWall.stopFilter();
    }
}
