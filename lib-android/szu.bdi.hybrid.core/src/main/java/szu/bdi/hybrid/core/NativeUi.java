package szu.bdi.hybrid.core;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

public class NativeUi extends HybridUi {
    final private static String LOGTAG = "NativeUi";

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOGTAG, ".onCreate()");
        Intent iin = getIntent();
        String s_uiData = iin.getStringExtra("uiData");

        initUiData(HybridTools.s2o(s_uiData));

        Log.v(LOGTAG, "whole data=" + wholeUiData());
        //N: FullScreen + top status, Y: Have Bar + top status, M: only bar - top status, F: full screen - top status
        String topbar = HybridTools.optString(getUiData("topbar"));

        switch (topbar) {
            case "F":
                //F: full screen w- top status
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case "M":
                //M: only top bar w- top status
                requestWindowFeature(Window.FEATURE_ACTION_BAR);
                this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case "N":
                //N: FullScreen w+ top status
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                break;
            case "Y":
            default:
                //Y: top bar w+ top status (default)
                requestWindowFeature(Window.FEATURE_ACTION_BAR);
                break;
        }

        setTitle("TODO setTitle()");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        Log.v(LOGTAG, "onBackPressed set Result 1");
        //{name: $name, address: adress}

        JSONObject o = new JSONObject();
        try {
            o.put("name", getUiData("name"));
            o.put("address", getUiData("address"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent rtIntent = new Intent();
        rtIntent.putExtra("rt", o.toString());
        setResult(1, rtIntent);//@ref onActivityResult()
        finish();
    }
}
