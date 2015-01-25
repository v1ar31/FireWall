import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;


public class FireWallMainForm extends JFrame{
    private final String APPLICATION_NAME = "Фаерволл";
    private final String FW_WORK = "/images/work.png";
    private final String FW_STAY = "/images/stay.png";
    private final URL imageStay = FireWallMainForm.class.getResource(FW_STAY);
    private final URL imageWork = FireWallMainForm.class.getResource(FW_WORK);

    private JPanel mainPanel;
    private JButton blackListBtn;
    private JButton startBtn;
    private JButton stopBtn;
    private TrayIcon trayIcon;

    private PopupMenu trayMenu;
    private MenuItem blockListItem;
    private MenuItem startItem;
    private MenuItem stopItem;
    private MenuItem closeItem;

    private SingleFireWall fireWall;

    public FireWallBlockListForm dbFrame;
    public JournalFrame journalFrame;


    public FireWallMainForm() {
        super();
        fireWall = SingleFireWall.getInstance();

        try {
            constructTrayIcon();
        } catch (AWTException e) {
            constructMainForm();
            setVisible(true);
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                journalFrame = new JournalFrame();
                journalFrame.registerIn(fireWall);
            }
        });

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                dbFrame = new FireWallBlockListForm();
                dbFrame.display();
            }
        });
    }

    private void constructMainForm() {
        setResizable(false);
        setTitle(APPLICATION_NAME);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);

        addActionsOnMainForm();
    }

    private void addActionsOnMainForm() {
        blackListBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dbFrame.setVisible(true);
            }
        });
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    fireWall.startFilter();
                } catch (FireWallException e1) {
                    // it is already started
                }
            }
        });
        stopBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    fireWall.stopFilter();
                } catch (FireWallException e1) {
                    // it is not started
                }
            }
        });
    }

    public void constructTrayIcon() throws AWTException {
        if (!SystemTray.isSupported()) {
            throw new AWTException("SystemTray is not supported");
        }

        blockListItem = new MenuItem("Черный список");
        startItem = new MenuItem("Старт");
        stopItem = new MenuItem("Стоп");
        closeItem = new MenuItem("Закрыть");

        trayMenu = new PopupMenu();
        trayMenu.add(blockListItem);
        trayMenu.add(startItem);
        trayMenu.add(stopItem);
        trayMenu.add(closeItem);

        Image icon = Toolkit.getDefaultToolkit().getImage(imageStay);
        trayIcon = new TrayIcon(icon, APPLICATION_NAME, trayMenu);
        trayIcon.setImageAutoSize(true);

        SystemTray tray = SystemTray.getSystemTray();
        tray.add(trayIcon);

        trayIcon.displayMessage(APPLICATION_NAME, "Приложение готово к работе",
                TrayIcon.MessageType.INFO);

        addActionsOnTrayIcon();
    }

    private void addActionsOnTrayIcon() {
        trayIcon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                journalFrame.setVisible(true);
            }
        });
        closeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        stopItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    fireWall.stopFilter();
                    trayIcon.displayMessage(APPLICATION_NAME, "Приложение остановлено",
                            TrayIcon.MessageType.INFO);
                    trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(imageStay));
                } catch (FireWallException ex) {
                    trayIcon.displayMessage(APPLICATION_NAME, "Приложение не запущено",
                            TrayIcon.MessageType.INFO);
                }
            }
        });
        startItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 try {
                    fireWall.startFilter();
                    trayIcon.displayMessage(APPLICATION_NAME, "Приложение запущено",
                            TrayIcon.MessageType.INFO);
                    trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(imageWork));
                 } catch (FireWallException ex) {
                     trayIcon.displayMessage(APPLICATION_NAME, "Ошибка запуска",
                             TrayIcon.MessageType.INFO);
                 }
            }
        });
        blockListItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dbFrame.setVisible(true);
            }
        });
    }

}
