import java.util.ArrayList;

/**
 * Created by v1ar on 13.01.15.
 */
public class FireWall implements Observable{
    final public static int PORT = 0x10;
    final public static int IP = 0x11;

    public ArrayList<Observer> observers;

    public FilterService filterService;

    public boolean isstarted;


    public FireWall() {
        isstarted = false;
        observers = new ArrayList<>();
    }


    public int StartFilter() {
        if (!isstarted) {
            filterService = new FilterService();
            filterService.start();
            isstarted = true;
            return 0;
        }
        return -1;
    }

    public int StopFilter() {
        if (isstarted) {
            filterService.interrupt();
            isstarted = false;
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
