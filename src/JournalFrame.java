import javax.swing.*;
import java.awt.event.*;

public class JournalFrame extends JDialog implements Observer{
    private JPanel contentPane;
    private JButton buttonCancel;
    public JTextArea textArea1;

    public JournalFrame() {
        setTitle("Журнал заблокированных пакетов");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setVisible(false);
        setLocationRelativeTo(null);
        setSize(500, 400);

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onCancel() {
        setVisible(false);
    }

    @Override
    public void update(int direction, int type, String body) {
        if (type == SingleFireWall.IP) {
            textArea1.append("Заблокирован IP: ");
        } else {
            textArea1.append("Эаблокирован Порт: ");
        }
        textArea1.append(body);
        if (direction == WinDivertLibrary.WINDIVERT_DIRECTION_OUTBOUND) {
            textArea1.append(" - >> OUT");
        } else {
            textArea1.append(" << - IN");
        }
        textArea1.append("\n");
    }

    @Override
    public void registerIn(Observable o) {
        synchronized (o) {
            o.registerObserver(this);
        }
    }

}
