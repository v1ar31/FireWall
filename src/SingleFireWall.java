import windivert.FilterService;

import java.util.ArrayList;


// 1. Code style (mixing camelCase and other styles); { FIX }
// 2. FireWall can be singleton { FIX }
// 3. -1 and 0 are not so good error reasons
// 4. Observable can be abstract class () { FIX }

public class SingleFireWall extends Observable {
    private FilterService filterService;
    private boolean isStarted;

    private static volatile SingleFireWall instance; // volatile - for multi threads

    public static SingleFireWall getInstance() {
        if (instance == null) {
            synchronized (SingleFireWall.class) {
                if (instance == null) {
                    instance = new SingleFireWall();
                }
            }
        }
        return instance;
    }

    private SingleFireWall() {
        isStarted = false;
        observers = new ArrayList<>();
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }

    public int startFilter() {                                          // -->> add throws
        if (!isStarted()) {
            filterService = new FireWallFilterService();
            filterService.start();
            setStarted(true);
            return 0;
        }
        return -1;
    }

    public int stopFilter() {                                           // -->> add throws
        if (isStarted()) {
            filterService.interrupt();
            setStarted(false);
            return 0;
        }
        return  -1;
    }

    @Override
    public void registerObserver(Observer o) throws NullPointerException {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) throws NullPointerException {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(int direction, String address, String port) throws NullPointerException{
        for (Observer o: observers) {
            o.update(direction, address, port);
        }
    }

}
