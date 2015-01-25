import windivert.FilterService;

import java.sql.SQLException;
import java.util.ArrayList;


// 1. Code style (mixing camelCase and other styles); { FIX }
// 2. FireWall can be singleton { FIX }
// 3. -1 and 0 are not so good error reasons { FIX }
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

    public void startFilter() throws FireWallException {
        if (isStarted()) {
            throw new FireWallException("it is already launched");
        }
        try {
            filterService = new FireWallFilterService();
            filterService.start();
            setStarted(true);
        } catch (SQLException | ClassNotFoundException e) {
            throw new FireWallException("it wasn't succeeded to create a filter");
        }
    }

    public void stopFilter() throws FireWallException {
        if (!isStarted()) {
            throw new FireWallException("it is not launched");
        }
        filterService.interrupt();
        setStarted(false);
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
    public void notifyObservers(int direction, String address, String port) throws NotificationException {
        for (Observer o: observers) {
            o.update(direction, address, port);
        }
    }

}
