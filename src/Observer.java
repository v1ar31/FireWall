
public interface Observer {

    public void update(int direction, String address, String port) throws NotificationException;
    public void registerIn(Observable o);
}
