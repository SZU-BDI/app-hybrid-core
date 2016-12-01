package info.cmptech.printwrapper;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * NOTES: the wrapper from Service to the PrinterWorkerThread
 */

public class PrinterWorkService extends Service {
    private static final String LOGTAG = "PrinterWorkService";
    private static PrinterWorkThread _priterWorkThread = null;

    //    public PrinterWorkService() {
//        super();
//    }
    @Override
    public void onCreate() {
        mHandler = new MHandler(this);
        _priterWorkThread = new PrinterWorkThread(mHandler);
        _priterWorkThread.start();
        Log.v(LOGTAG, " PrinterWorkService onCreate !!!!!!!!!!!!!!!!!!");
    }

    //TMP solution to fix the old buggy codes...
    public static PrinterWorkThread getPrinterThread() {
        if (_priterWorkThread == null) {
            Log.v(LOGTAG, "_priterWorkThread null ??????!!!!!!!");
        }
        return _priterWorkThread;
    }

    private static Handler mHandler = null;

    private static List<Handler> targetsHandler = new ArrayList<Handler>(5);

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = Message.obtain();
        msg.what = PrinterUtils.MSG_ALLTHREAD_READY;
        notifyHandlers(msg);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.v(LOGTAG, "onDestroy ????????????");
        if (_priterWorkThread != null) {
            _priterWorkThread.disconnectBt();
            _priterWorkThread.disconnectBle();
            _priterWorkThread.disconnectNet();
            _priterWorkThread.disconnectUsb();
            _priterWorkThread.quit();
            _priterWorkThread = null;
        }
    }

    static class MHandler extends Handler {

        WeakReference<PrinterWorkService> mService;

        MHandler(PrinterWorkService service) {
            mService = new WeakReference<PrinterWorkService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            notifyHandlers(msg);
        }
    }

    public static void addHandler(Handler handler) {
        if (!targetsHandler.contains(handler)) {
            targetsHandler.add(handler);
        }
    }

    public static void delHandler(Handler handler) {
        if (targetsHandler.contains(handler)) {
            targetsHandler.remove(handler);
        }
    }

    public static void notifyHandlers(Message msg) {
        for (int i = 0; i < targetsHandler.size(); i++) {
            Message message = Message.obtain(msg);
            targetsHandler.get(i).sendMessage(message);
        }
    }

}
