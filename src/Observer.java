/**
 * Created by v1ar on 15.01.15.
 */
public interface Observer {

    public void update(int direction, String address, String port);
    public void registerIn(Observable o);
}
