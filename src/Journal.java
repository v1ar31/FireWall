/**
 * Created by v1ar on 16.01.15.
 */
public class Journal implements Observer {

    @Override
    public void update(int direction, int type, String body) {
        if (type == SingleFireWall.IP) {
            Main.journalFrame.textArea1.append("Заблокирован IP: ");
        } else {
            Main.journalFrame.textArea1.append("Эаблокирован Порт: ");
        }
        Main.journalFrame.textArea1.append(body);
        if (direction == WinDivertLibrary.WINDIVERT_DIRECTION_OUTBOUND) {
            Main.journalFrame.textArea1.append(" - >> OUT");
        } else {
            Main.journalFrame.textArea1.append(" << - IN");
        }
        Main.journalFrame.textArea1.append("\n");
    }
}
