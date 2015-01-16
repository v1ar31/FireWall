import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by v1ar on 15.01.15.
 */
public class NotifierFrame extends JDialog {
    final public String FW_IMAGE = "/images/firewall.jpg";

    Dimension scrSize;
    Insets toolHeight;

    String header;
    String text;

    final URL imageFW;

    JLabel headingLabel;
    JButton closeButton;
    JLabel messageLabel;


    public NotifierFrame (String header, String text){
        this.header = header;
        this.text = text;
        imageFW = Main.class.getResource(FW_IMAGE);

        setSize(220, 100);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        scrSize = Toolkit.getDefaultToolkit().getScreenSize();// size of the screen
        toolHeight = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());// height of the task bar
        setLocation(scrSize.width, scrSize.height);
        setAlwaysOnTop(false);

        createComponents();

        setLayout(new GridBagLayout());
        packFrame();

        setVisible(true);

        slideIN();
        slideOUT();
    }

    public void slideIN() {
        new Thread () {
            float hop = 3;
            public void run() {
                while (hop > 1) {
                    hop -= 0.5;
                    setLocation((int) (scrSize.width - getWidth()/hop), scrSize.height - toolHeight.bottom - getHeight());
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    public void slideOUT() {
        new Thread(){
            @Override
            public void run() {
                float hop = 1;
                try {
                    Thread.sleep(4000); // time after which pop up will be disappeared.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (hop < 3) {
                    hop += 0.5;
                    setLocation((int) (scrSize.width - getWidth()/hop), scrSize.height - toolHeight.bottom - getHeight());
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                dispose();
            }
        }.start();
    }

    private void createComponents() {
        headingLabel = new JLabel(header);
        try {
            headingLabel.setIcon(new ImageIcon(imageFW));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        headingLabel.setOpaque(false);

        closeButton = new JButton("X");
        closeButton.setMargin(new Insets(1, 4, 1, 4));
        closeButton.setFocusable(false);

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        messageLabel = new JLabel("<HtMl>"+text);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 12));
    }

    private void packFrame() {
        AtomicReference<GridBagConstraints> constraints = new AtomicReference<>(new GridBagConstraints());
        constraints.get().gridx = 0;
        constraints.get().gridy = 0;
        constraints.get().weightx = 1.0f;
        constraints.get().weighty = 1.0f;
        constraints.get().insets = new Insets(5, 5, 5, 5);
        constraints.get().fill = GridBagConstraints.BOTH;

        add(headingLabel, constraints.get());

        constraints.get().gridx++;
        constraints.get().weightx = 0f;
        constraints.get().weighty = 0f;
        constraints.get().fill = GridBagConstraints.NONE;
        constraints.get().anchor = GridBagConstraints.NORTH;

        add(closeButton, constraints.get());

        constraints.get().gridx = 0;
        constraints.get().gridy++;
        constraints.get().weightx = 1.0f;
        constraints.get().weighty = 1.0f;
        constraints.get().insets = new Insets(5, 5, 5, 5);
        constraints.get().fill = GridBagConstraints.BOTH;

        add(messageLabel, constraints.get());

    }

}
