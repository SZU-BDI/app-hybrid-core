package info.cmptech.printwrapper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConnectBTPairedActivity extends Activity implements
        OnItemClickListener {

    private ProgressDialog dialog;

    public static final String ICON = "ICON";
    public static final String PRINTERNAME = "PRINTERNAME";
    public static final String PRINTERMAC = "PRINTERMAC";
    private static List<Map<String, Object>> boundedPrinters;

    private static Handler mHandler = null;
    private static String TAG = "ConnectBTMacActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectbtpaired);

        dialog = new ProgressDialog(this);
        boundedPrinters = getBoundedPrinters();
        ListView listView;
        listView = (ListView) findViewById(R.id.listViewSettingConnect);
        listView.setAdapter(new SimpleAdapter(this, boundedPrinters,
                R.layout.list_item_printernameandmac, new String[]{ICON,
                PRINTERNAME, PRINTERMAC}, new int[]{
                R.id.btListItemPrinterIcon, R.id.tvListItemPrinterName,
                R.id.tvListItemPrinterMac}));
        listView.setOnItemClickListener(this);

        mHandler = new MHandler(this);
        PrinterWorkService.addHandler(mHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PrinterWorkService.delHandler(mHandler);
        mHandler = null;
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long id) {
        // TODO Auto-generated method stub

        String address = (String) boundedPrinters.get(position).get(PRINTERMAC);
        // AppHelper.saveSetting(ConnectBTPairedActivity.this, "PrinterModel",  "PrinterModel", (String) boundedPrinters.get(position).get(PRINTERNAME));

        //PreferenceManager.getSharedPreferences(getApplicationContext()).edit().putString("PrinterModel", (String) boundedPrinters.get(position).get(PRINTERNAME)).commit();

        SharedPreferences sharedPref = getSharedPreferences("FileName", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("PrinterModel", (String) boundedPrinters.get(position).get(PRINTERNAME));
        //prefEditor.commit();
        prefEditor.apply();

//        SharedPreferences sharedPref = getSharedPreferences("FileName", MODE_PRIVATE);
        String PrinterModel = sharedPref.getString("PrinterModel", "unknown");

        Log.i(TAG, "handler = ConnectedBTPaired, Modelname is  = " + PrinterModel);

        dialog.setMessage(PrinterUtils.toast_connecting + " " + address);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        PrinterWorkService.getPrinterThread().connectBt(address);
    }

    private List<Map<String, Object>> getBoundedPrinters() {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            return list;
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                .getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a
                // ListView
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(ICON, android.R.drawable.stat_sys_data_bluetooth);
                // Toast.makeText(this,
                // ""+device.getBluetoothClass().getMajorDeviceClass(),
                // Toast.LENGTH_LONG).show();
                map.put(PRINTERNAME, device.getName());
                map.put(PRINTERMAC, device.getAddress());
                list.add(map);
            }
        }
        return list;
    }

    static class MHandler extends Handler {

        WeakReference<ConnectBTPairedActivity> mActivity;

        MHandler(ConnectBTPairedActivity activity) {
            mActivity = new WeakReference<ConnectBTPairedActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ConnectBTPairedActivity theActivity = mActivity.get();
            switch (msg.what) {

                case PrinterUtils.MSG_WORKTHREAD_SEND_CONNECTBTRESULT: {
                    int result = msg.arg1;
                    Toast.makeText(
                            theActivity,
                            (result == 1) ? PrinterUtils.toast_fail
                                    : PrinterUtils.toast_fail, Toast.LENGTH_SHORT).show();
                    theActivity.dialog.cancel();
                    if (result == 1) {
                        mActivity.get().setResult(1);
                        mActivity.get().finish();
                    } else {
//                        AppHelper.connected_Printer_Model = "";//clear
//                        AppHelper.latest_printer_model = "";//clear
                    }
                    break;
                }

            }
        }
    }

}

