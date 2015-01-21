import javafx.util.Pair;

import java.util.ArrayDeque;

/**
 * Created by v1ar on 15.01.15.
 */
public class Notification implements Observer {
    private final String NEW_LINE = "<br>";

    public ArrayDeque incomingPackets;

    Notification () {
        incomingPackets = new ArrayDeque<>();
    }

    @Override
    public void update(int direction, String address, String port) {          // <<<-- сделать три режима оповщеиний (null, -), (-, null), (-, -)
        port = (port == null)? ""
                             : port;
        address = (address == null)? ""
                                   : address;
        Pair<String, String> packet = new Pair<String, String>(address, port);
        if (!incomingPackets.contains(packet)) {
            String nameService = "";
            // тут нужно определить имя сервиса, которое в БД
            new NotifierFrame("Блокировка Пакета", nameService + NEW_LINE + address + NEW_LINE + port);

            incomingPackets.push(packet);
        }
    }

    @Override
    public void registerIn(Observable o) {
        o.registerObserver(this);
    }
}
