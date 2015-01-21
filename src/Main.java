import javax.swing.*;
import java.awt.*;


public class Main {
    public static FireWallMainForm mainForm;

    public static SingleFireWall fireWall;


    public static void main(String[] args) {

        // Windows style for interface
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e1) {
            e1.printStackTrace();
        }

        fireWall = SingleFireWall.getInstance();
        Notification notification = new Notification();
        notification.registerIn(fireWall);

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                mainForm = new FireWallMainForm();
            }
        });



    }

}