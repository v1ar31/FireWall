import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT;

import static com.sun.jna.platform.win32.W32Errors.ERROR_INVALID_PARAMETER;
import static com.sun.jna.platform.win32.WinBase.INVALID_HANDLE_VALUE;

/**
 * Created by v1ar on 13.01.15.
 */
public class FireWall {
    public WinDivertLibrary lib;
    public WinNT.HANDLE handle;

    public FilterService filterService;

    public boolean openedFilter;
    public boolean isstarted;


    public FireWall() {
        //observers = new ArrayList<>();
        lib = WinDivertLibrary.INSTANCE;
        openedFilter = false;
        isstarted = false;
        filterService = new FilterService();
    }

    public int OpenFilter () {
        if (!openedFilter) {
            handle = lib.WinDivertOpen("true", WinDivertLibrary.WINDIVERT_LAYER.WINDIVERT_LAYER_NETWORK, (short)0, 0);

            if (handle == INVALID_HANDLE_VALUE) {
                if (Native.getLastError() == ERROR_INVALID_PARAMETER) {
                    return -2;
                } else {
                    return -3;
                }
            }

            openedFilter = true;
        }

        return 0;
    }

    public int CloseFilter() {
        if (openedFilter) {
            if(!lib.DivertClose(handle)) {
                return -1;
            }
            openedFilter = false;
        }
        return 0;
    }

    public int StartFilter() {
        if (!isstarted) {
            OpenFilter();
            filterService.init(lib, handle);
            filterService.start();
            isstarted = true;
            return 0;
        }
        return -1;
    }

    public int StopFilter() {
        if (isstarted) {
            filterService.interrupt();
            CloseFilter();
            isstarted = false;
            return 0;
        }
        return  -1;
    }

    public void addObserver (Observer o) {
        if (openedFilter) {
            filterService.removeObserver(o);
        }
    }

    public void delObserver (Observer o) {
        if (openedFilter) {
            filterService.removeObserver(o);
        }
    }
}
