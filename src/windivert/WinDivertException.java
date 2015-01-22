package windivert;


public class WinDivertException extends Exception {

    public WinDivertException(String message) {
        super(message);
    }

    public WinDivertException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
