import java.util.ArrayList;
import java.util.List;

/**
 * Created by v1ar on 13.01.15.
 */

// 1. Code style (mixing camelCase and other styles);
// 2. FireWall can be singleton 
// 3. -1 and 0 are not so good error reasons
// 4. Observable can be abstract class

public class SingleFireWall implements Observable {
    final public static int PORT = 0x10;
    final public static int IP = 0x11;

    private FilterService filterService;
    public boolean isStarted;

    private List<Observer> observers;
    private static SingleFireWall instance;

    public static SingleFireWall getInstance() {
        return (instance == null)? new SingleFireWall() : instance;
    }


    private SingleFireWall() {
        isStarted = false;
        observers = new ArrayList<>();
    }


    public int startFilter() {
        if (!isStarted) {
            filterService = new FilterService();
            filterService.start();
            isStarted = true;
            return 0;
        }
        return -1;
    }

    public int stopFilter() {
        if (isStarted) {
            filterService.interrupt();
            isStarted = false;
            return 0;
        }
        return  -1;
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(int direction, int type, String body) {
        for (Observer o: observers) {
            try {
                o.update(direction, type, body);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
