import java.util.List;

/**
 * Created by v1ar on 15.01.15.
 */
public abstract class Observable {

    protected List<Observer> observers;

    public abstract void registerObserver(Observer o) throws NullPointerException;
    public abstract void removeObserver(Observer o) throws NullPointerException;
    public abstract void notifyObservers(int direction, String address, String port) throws NullPointerException;
}
