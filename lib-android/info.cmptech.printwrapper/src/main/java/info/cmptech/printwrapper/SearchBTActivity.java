package info.cmptech.printwrapper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class SearchBTActivity extends Activity implements OnClickListener {
    final private static String LOGTAG = (((new Throwable()).getStackTrace())[0]).getClassName();

    private LinearLayout linearlayoutdevices;
    private ProgressBar progressBarSearchStatus;
    private ProgressDialog dialog;

    private BroadcastReceiver broadcastReceiver = null;
    private IntentFilter intentFilter = null;

    private static Handler mHandler = null;

    public static void WaitMs(long ms) {
//            long time = System.currentTimeMillis();
//            while (System.currentTimeMillis() - time < ms) ;
        try {
            Thread.currentThread().sleep(ms, 1);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchbt);

        Button btSearch = (Button) findViewById(R.id.buttonSearch);
        btSearch.setOnClickListener(this);
        btSearch.setText(R.string.button_bt_search_Printer);
        progressBarSearchStatus = (ProgressBar) findViewById(R.id.progressBarSearchStatus);
        linearlayoutdevices = (LinearLayout) findViewById(R.id.linearlayoutdevices);
        dialog = new ProgressDialog(this);

        initBroadcast();
        mHandler = new MHandler(this);
        PrinterWorkService.addHandler(mHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PrinterWorkService.delHandler(mHandler);
        mHandler = null;
        uninitBroadcast();
    }

    public void onClick(View arg0) {
        do {
            if (arg0.getId() == R.id.buttonSearch) {

                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (null == adapter) {
                    Log.v(LOGTAG, "Not Found BluetoothAdapter");
                    Toast.makeText(this, "Not Found Bluetooth Device !!!", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                if (!adapter.isEnabled()) {
                    try {
                        adapter.enable();
                    } catch (Throwable ex) {
                        Toast.makeText(this, "Failed to Enable Bluetooth! " + ex.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                    for (int i = 0; i < 5; i++) {
                        try {
                            if (!adapter.isEnabled())
                                Thread.currentThread().sleep(1);
                        } catch (InterruptedException ex) {
                            Toast.makeText(this, "InterruptedException when sleep " + ex.getMessage(), Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }
                    }
                    if (adapter.enable()) {
                        Log.v(LOGTAG, "Done Enable BluetoothAdapter !");
                    } else {
                        Toast.makeText(this, "Failed to Enable Bluetooth!", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                //disconnect first
                PrinterWorkService.getPrinterThread().disconnectBt();
                WaitMs(10);

                //stop previous discovery if any
                adapter.cancelDiscovery();

                //clean views
                linearlayoutdevices.removeAllViews();
                WaitMs(10);

                //
                adapter.startDiscovery();

            }
        } while (false);
    }

    public static Map<String, String> map_name_address = new HashMap<String, String>();

    private void initBroadcast() {
        broadcastReceiver = new BroadcastReceiver() {

            public static final String LOGTAG = "SearchBT_Recv";

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.v(LOGTAG, "initBroadcast onReceive() intent = " + intent.toString());
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // TODO Auto-generated method stub
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    if (device == null) return;

                    final String address = device.getAddress();
                    String printerName = device.getName();

                    if (printerName == null)
                        printerName = "BT";
                    else if (printerName.equals(address))
                        printerName = "BT";
                    map_name_address.put(address, printerName);

                    Log.v(LOGTAG, "initBroadcast onReceive() printerName/address = " + printerName + "/" + address);

                    Button button = new Button(context);
                    button.setText(printerName + " - " + address);
                    button.setGravity(Gravity.CENTER_VERTICAL
                            | Gravity.LEFT);
                    button.setOnClickListener(new OnClickListener() {

                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                            PrinterWorkService.getPrinterThread().disconnectBt();

                            dialog.setMessage(PrinterUtils.toast_connecting + " "
                                    + address);
                            dialog.setIndeterminate(true);
                            dialog.setCancelable(false);
                            dialog.show();
                            PrinterWorkService.getPrinterThread().connectBt(address);
                        }
                    });
                    button.getBackground().setAlpha(100);
                    linearlayoutdevices.addView(button);
                } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {

                    if (device == null) return;
                    String address = device.getAddress();
                    String printerName = map_name_address.get(address);

                    Log.v(LOGTAG, "initBroadcast onReceive() printerName/address = " + printerName + "/" + address);
                    Log.v(LOGTAG, "ACTION_ACL_CONNECTED = " + printerName);
                    PrinterUtils.connected_Printer_Model = printerName;
                    PrinterUtils.connected_Printer_Address = address;
                    PrinterUtils.latest_printer_model = printerName;//for _get_printer_model

                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    if (device == null) return;
                    String address = device.getAddress();
                    String printerName = device.getName();
                    Log.v(LOGTAG, "initBroadcast onReceive() printerName/address = " + printerName + "/" + address);
                    PrinterUtils.connected_Printer_Model = "";
                    PrinterUtils.connected_Printer_Address = "";
                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED
                        .equals(action)) {
                    progressBarSearchStatus.setIndeterminate(true);
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                        .equals(action)) {
                    progressBarSearchStatus.setIndeterminate(false);
                }

            }

        };
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void uninitBroadcast() {
        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
    }

    static class MHandler extends Handler {

        WeakReference<SearchBTActivity> mActivity;

        MHandler(SearchBTActivity activity) {
            mActivity = new WeakReference<SearchBTActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SearchBTActivity theActivity = mActivity.get();
            switch (msg.what) {
                case PrinterUtils.MSG_WORKTHREAD_SEND_CONNECTBTRESULT: {
                    int result = msg.arg1;
                    Toast.makeText(
                            theActivity,
                            (result == 1) ? PrinterUtils.toast_success
                                    : PrinterUtils.toast_fail, Toast.LENGTH_SHORT).show();
                    Log.v(LOGTAG, "Connect Result: " + result);
                    theActivity.dialog.cancel();
                    if (1 == result) {
                        PrintTest();
                        mActivity.get().setResult(1);
                        mActivity.get().finish();
                    } else {
                        PrinterUtils.connected_Printer_Model = "";//clear
                        PrinterUtils.connected_Printer_Address = "";
                        PrinterUtils.latest_printer_model = "";//clear
                    }
                    break;
                }

            }
        }

        void PrintTest() {
            String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ\n0123456789\n";
//            String str = "PRINTER IS WORKING.\n";
            byte[] tmp1 = {0x1b, 0x40, (byte) 0xB2, (byte) 0xE2, (byte) 0xCA,
                    (byte) 0xD4, (byte) 0xD2, (byte) 0xB3, 0x0A};
            byte[] tmp2 = {0x1b, 0x21, 0x01};
            byte[] tmp3 = {0x0A, 0x0A, 0x0A, 0x0A};
            byte[] buf = PrintService.byteArraysToBytes(new byte[][]{tmp1,
                    str.getBytes(), tmp2, str.getBytes(), tmp3});
            if (PrinterWorkService.getPrinterThread().isConnected()) {
                Bundle data = new Bundle();
                data.putByteArray(PrinterUtils.BYTESPARA1, buf);
                data.putInt(PrinterUtils.INTPARA1, 0);
                data.putInt(PrinterUtils.INTPARA2, buf.length);
                PrinterWorkService.getPrinterThread().handleCmd(PrinterUtils.CMD_WRITE, data);
            } else {
                Toast.makeText(mActivity.get(), PrinterUtils.toast_notconnect,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
