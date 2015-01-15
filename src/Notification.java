/**
 * Created by v1ar on 15.01.15.
 */
public class Notification implements Observer {
    @Override
    public void update() {
        new NotifierFrame("test", "caption<br>tt");
    }
}
