import sqldb.DBAdapterSQLite;
import sqldb.FireWallBlockListForm;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Created by v1ar on 30.10.14.
 */
public class Main {
    public static FireWallMainForm form;
    public static FireWallBlockListForm dbform;

    public static SingleFireWall fireWall;
    public static DBAdapterSQLite db;

    public static JournalFrame journalFrame;
    public static void main(String[] args) {

        /**
         * Windows style for interface
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e1) {
            e1.printStackTrace();
        }


        try {
            db = new DBAdapterSQLite("jdbc:sqlite:resources/blockList.sqlite3");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        Journal journal = new Journal();
        Notification notification = new Notification();

        fireWall = SingleFireWall.getInstance();
        fireWall.registerObserver(notification);
        fireWall.registerObserver(journal);


        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                journalFrame = new JournalFrame();
            }
        });

        /**
         * Tray icon
         */
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                form = new FireWallMainForm();
            }
        });


        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                dbform = new FireWallBlockListForm(db);
                dbform.display();
            }
        });
    }

}