import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by v1ar on 15.01.15.
 */
public class NotifierFrame extends JFrame {
    final public String FW_IMAGE = "/images/firewall.jpg";

    Dimension scrSize;
    Insets toolHeight;

    public NotifierFrame (String header, String text){
        setSize(220, 100);
        setLayout(new GridBagLayout());

        AtomicReference<GridBagConstraints> constraints = new AtomicReference<>(new GridBagConstraints());
        constraints.get().gridx = 0;
        constraints.get().gridy = 0;
        constraints.get().weightx = 1.0f;
        constraints.get().weighty = 1.0f;
        constraints.get().insets = new Insets(5, 5, 5, 5);
        constraints.get().fill = GridBagConstraints.BOTH;

        final URL imageFW = Main.class.getResource(FW_IMAGE);
        JLabel headingLabel = new JLabel(header);
        try {
            headingLabel.setIcon(new ImageIcon(imageFW)); // --- use image icon you want to be as heading image.
        } catch (NullPointerException ex) {

        }
        headingLabel.setOpaque(false);
        add(headingLabel, constraints.get());

        constraints.get().gridx++;
        constraints.get().weightx = 0f;
        constraints.get().weighty = 0f;
        constraints.get().fill = GridBagConstraints.NONE;
        constraints.get().anchor = GridBagConstraints.NORTH;

        JButton cloesButton = new JButton("X");
        cloesButton.setMargin(new Insets(1, 4, 1, 4));
        cloesButton.setFocusable(false);
        add(cloesButton, constraints.get());
        cloesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        constraints.get().gridx = 0;
        constraints.get().gridy++;
        constraints.get().weightx = 1.0f;
        constraints.get().weighty = 1.0f;
        constraints.get().insets = new Insets(5, 5, 5, 5);
        constraints.get().fill = GridBagConstraints.BOTH;

        JLabel messageLabel = new JLabel("<HtMl>"+text);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 12));
        add(messageLabel, constraints.get());

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setUndecorated(true);

        scrSize = Toolkit.getDefaultToolkit().getScreenSize();// size of the screen
        toolHeight = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());// height of the task bar

        setLocation(scrSize.width, scrSize.height);
        setVisible(true);

        //setType(Type.UTILITY);
        setAlwaysOnTop(false);

        new Thread () {
            float hop = 3;
            public void run() {
                while (hop > 1) {
                    hop -= 0.5;
                    setLocation((int) (scrSize.width - (int)getWidth()/hop), scrSize.height - toolHeight.bottom - getHeight());
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();


        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(2000); // time after which pop up will be disappeared.
                    float hop = 1;
                    while (hop < 3) {
                        hop += 0.5;
                        setLocation((int) (scrSize.width - (int)getWidth()/hop), scrSize.height - toolHeight.bottom - getHeight());
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    dispose();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }
}
