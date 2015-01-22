import java.util.List;


public abstract class Observable {

    protected List<Observer> observers;

    public abstract void registerObserver(Observer o) throws NullPointerException;
    public abstract void removeObserver(Observer o) throws NullPointerException;
    public abstract void notifyObservers(int direction, String address, String port) throws NotificationException;
}
