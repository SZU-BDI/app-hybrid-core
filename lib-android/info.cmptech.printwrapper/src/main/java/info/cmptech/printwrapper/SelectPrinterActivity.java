package info.cmptech.printwrapper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectPrinterActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        Button btSearch = (Button) findViewById(R.id.search_btprinter);
        btSearch.setOnClickListener(this);
        btSearch.setText(R.string.button_bt_search_Printer);
        Button btConnect = (Button) findViewById(R.id.connect_coupled);
        btConnect.setOnClickListener(this);
        btConnect.setText(R.string.button_bt_connect_Printer);
        Button btDisconnect = (Button) findViewById(R.id.disconnect_btprinter);
        btDisconnect.setOnClickListener(this);
        btDisconnect.setText(R.string.button_bt_disconnect_Printer);
    }

    public void onClick(View arg0) {
        int i = arg0.getId();
        if (i == R.id.search_btprinter) {
            startActivityForResult(new Intent(this, SearchBTActivity.class), 2);

        } else if (i == R.id.connect_coupled) {
            startActivityForResult(new Intent(this, ConnectBTPairedActivity.class), 1);

        } else if (i == R.id.disconnect_btprinter) {
            PrinterWorkThread _thread = PrinterWorkService.getPrinterThread();
            if (null != _thread) {
                _thread.disconnectBle();
                _thread.disconnectBt();
                _thread.disconnectNet();
                _thread.disconnectUsb();
            }

        }
    }

    //TODO
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == 1) {
            finish();
        }
    }

    @Override
    protected void onStop() {
        Intent rtIntent = new Intent();
//        rtIntent.putExtra("rt", _last_rt_s);
        setResult(PrinterUtils.RESULT_SELECT_PRINTER_CB, rtIntent);
        super.onStop();
    }
}
