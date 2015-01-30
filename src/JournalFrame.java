import javax.swing.*;
import java.awt.event.*;

public class JournalFrame extends JDialog implements Observer {
    private final String APPLICATION_NAME = "Журнал заблокированных пакетов";

    private JPanel contentPane;
    private JButton cancelBtn;
    private JTextArea textArea;
    private JButton saveToButton;

    public JournalFrame() {
        constructJournalFrame();
        addActionOnJournalFrame();
    }

    private void constructJournalFrame() {
        setTitle(APPLICATION_NAME);
        setContentPane(contentPane);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setModal(true);
        setVisible(false);
        setLocationRelativeTo(null);
        setSize(500, 400);
    }

    private void addActionOnJournalFrame() {
        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
    }

    private void onCancel() {
        setVisible(false);
    }

    @Override
    public void update(int direction, String address, String port) throws NotificationException {
        if ((address == null) && (port == null)) {
            throw new NotificationException("null addresses and port");
        }

        String tmp = "Заблокирован Пакет: ";
        tmp += address + ":" + port;
        tmp += (direction == FireWallFilterService.DIRECTION_OUTBOUND)? " - >> OUT"
                                                                      : " << - IN";
        tmp += "\n";
        textArea.append(tmp);
    }

    @Override
    public void registerIn(Observable o) {
        o.registerObserver(this);
    }

}
