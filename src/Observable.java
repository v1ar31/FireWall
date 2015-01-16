/**
 * Created by v1ar on 15.01.15.
 */
public interface Observable {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers(int direction, int type, String body);
}
