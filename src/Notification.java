import javafx.util.Pair;

import java.util.ArrayDeque;


public class Notification implements Observer {
    private final String NEW_LINE = "<br>";

    public ArrayDeque<Pair<String, String>> incomingPackets;

    Notification () {
        incomingPackets = new ArrayDeque<>();
    }

    @Override
    public void update(int direction, String address, String port) throws NotificationException {

        if ((address == null) && (port == null)) {
            throw new NotificationException("null addresses and port");
        }

        Pair<String, String> packet = new Pair<>(address, port);
        if (!incomingPackets.contains(packet)) {
            String nameService = "";
            // тут можно будет определить имя сервиса, которое в БД
            new NotifierFrame("Блокировка Пакета", nameService + NEW_LINE + address + NEW_LINE + port);

            incomingPackets.push(packet);
        }
    }

    @Override
    public void registerIn(Observable o) {
        o.registerObserver(this);
    }

}
