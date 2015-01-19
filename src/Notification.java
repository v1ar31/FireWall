import java.util.ArrayDeque;

/**
 * Created by v1ar on 15.01.15.
 */
public class Notification implements Observer {
    public ArrayDeque<String> prevPkts;

    Notification () {
        prevPkts = new ArrayDeque<>();
    }

    @Override
    public void update(int direction, int type, String body) {
        if (!prevPkts.contains(body)) {
            String nameService = "";
            // тут нужно определить имя сервиса, которое в БД
            if (type == SingleFireWall.IP) {
                new NotifierFrame("Блокировка IP", nameService + "<br> " + body);
            } else {
                new NotifierFrame("Блокировка Порта", nameService + "<br> " + body);
            }
            prevPkts.push(body);
        } else {
            String tmp = prevPkts.pop();
            if (tmp != null) {
                prevPkts.push(tmp);
            }
        }
    }

    @Override
    public void registerIn(Observable o) {
        o.registerObserver(this);
    }
}
