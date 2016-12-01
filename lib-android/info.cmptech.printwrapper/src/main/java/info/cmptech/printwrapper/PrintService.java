package info.cmptech.printwrapper;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

//TODO this class is in a very mass.... should improve later...

public class PrintService extends Service {

    private static Handler mHandler = null;

    private boolean showLogo = false;

    public static int result_logo = -1;
    public static int result_text = -1;
    public static int result_QR = -1;
    public static int result_BR = -1;
    public static int result_header = -1;

    public static byte[] byteArraysToBytes(byte[][] data) {

        int length = 0;
        for (int i = 0; i < data.length; i++)
            length += data[i].length;
        byte[] send = new byte[length];
        int k = 0;
        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < data[i].length; j++)
                send[k++] = data[i][j];
        return send;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String value = intent.getStringExtra("value");
        final String printer_type = intent.getStringExtra("printer_type");
        Log.v("PrintService", "onStartCommand value=" + value);
        mHandler = new MHandler(this);
        PrinterWorkService.addHandler(mHandler);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
//                if ("inner".equals(printer_type)) {
//                    onPrintInnerPrinter(value);
//                } else {
//                    onPrint(value);
//                }
                doPrint(value);
            }
        }).start();
        return START_NOT_STICKY;
    }

    public void addSpace() {
        String text123 = "\r\n\r\n\r\n\t";
        byte header123[] = null;
        byte strbuf123[] = null;

        header123 = new byte[]{0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x39,
                0x01};
        try {
            strbuf123 = text123.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte buffer123[] = byteArraysToBytes(new byte[][]{
                header123, strbuf123});
        Bundle data123 = new Bundle();
        data123.putByteArray(PrinterUtils.BYTESPARA1, buffer123);
        data123.putInt(PrinterUtils.INTPARA1, 0);
        data123.putInt(PrinterUtils.INTPARA2, buffer123.length);
        PrinterWorkService.getPrinterThread().handleCmd(PrinterUtils.CMD_WRITE, data123);
        //PrinterWorkService.printerWorkThread.handleCmd(PrinterUtils.CMD_POS_WRITE, data123);
    }

    //keep for stub, cleanup later
    private void onPrintDeprecated(String s) {
        if (s.equals(""))
            return;
        Boolean logo = false;
        String header1 = "";
        String header_fontsize = "";
        String body = "";
        String body_fontsieze = "";
        String qr_content = "";
        String barcode = "";
        JSONObject jsonObject = null;

        int charset = 0, codepage = 0;
        String encoding = "UTF-8";
        byte[] addBytes = new byte[0];

        try {
            jsonObject = new JSONObject(s);
            logo = jsonObject.optBoolean("logo");
            showLogo = logo;
            header1 = jsonObject.optString("header");
            header_fontsize = jsonObject.optString("header_fontsize");
            body = jsonObject.optString("body");
            body_fontsieze = jsonObject.optString("body_fontsieze");
            qr_content = jsonObject.optString("qr_content");
            barcode = jsonObject.optString("barcode");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (PrinterWorkService.getPrinterThread().isConnected()) {

//            if (showLogo) {
//
//                ImageReadWrite obj = new ImageReadWrite();
//                Bitmap logoImage = obj.ReadImage();
//
//                //  Bitmap mBitmap = getImageFromAssetsFile("btn_application_center_press.png");
//                int nPaperWidth = 384;
//                if (logoImage != null) {
//                    Bundle data = new Bundle();
//                    // data.putParcelable(PrinterUtils.OBJECT1, mBitmap);
//                    data.putParcelable(PrinterUtils.PARCE1, logoImage);
//                    data.putInt(PrinterUtils.INTPARA1, nPaperWidth);
//                    data.putInt(PrinterUtils.INTPARA2, 0);
//                    PrinterWorkService.printerWorkThread.handleCmd(PrinterUtils.CMD_POS_PRINTPICTURE, data);
//                }
//            }

            addSpace();

            if (!header1.equals("") && header1 != null) {
                String text1 = "\t\t" + header1;

                byte header12[] = null;
                byte strbuf12[] = null;

                header12 = new byte[]{0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x39,
                        0x01};
                try {
                    strbuf12 = text1.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                byte buffer1[] = byteArraysToBytes(new byte[][]{
                        header12, strbuf12});

                //buffer1[2] = ((byte)(0x8 | buffer1[2]));
                Bundle data1 = new Bundle();
                data1.putByteArray(PrinterUtils.BYTESPARA1, buffer1);
                data1.putInt(PrinterUtils.INTPARA1, 0);
                data1.putInt(PrinterUtils.INTPARA2, buffer1.length);
                PrinterWorkService.getPrinterThread().handleCmd(PrinterUtils.CMD_POS_WRITE, data1);

                addSpace();

            }

            if (!body.equals("") && body != null) {
                String text = body;
                byte header[] = null;
                byte strbuf[] = null;

                header = new byte[]{0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x39,
                        0x01};
                try {
                    strbuf = text.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                byte buffer[] = byteArraysToBytes(new byte[][]{
                        header, strbuf});
                Bundle data = new Bundle();
                data.putByteArray(PrinterUtils.BYTESPARA1, buffer);
                data.putInt(PrinterUtils.INTPARA1, 0);
                data.putInt(PrinterUtils.INTPARA2, buffer.length);
                PrinterWorkService.getPrinterThread().handleCmd(PrinterUtils.CMD_POS_WRITE, data);


     /*       String text =body;
        //    byte header[] = null;
            byte strbuf[] = null;

       //     header = new byte[] { 0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x39,
          //          0x01 };
            try {
                strbuf = text.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
         //   byte buffer[] = DataUtils.byteArraysToBytes(new byte[][]{
         //           header, strbuf});

            Bundle data = new Bundle();
            data.putString(PrinterUtils.STRPARA1, text);
            data.putString(PrinterUtils.STRPARA2, "UTF-8");
            data.putInt(PrinterUtils.INTPARA1, 0);
            data.putInt(PrinterUtils.INTPARA2, 7);
            data.putInt(PrinterUtils.INTPARA3, 5);
            data.putInt(PrinterUtils.INTPARA4, 2);
            data.putInt(PrinterUtils.INTPARA5, 1);


            PrinterWorkService.printerWorkThread.handleCmd(PrinterUtils.CMD_POS_STEXTOUT, data);
*/

                addSpace();

            }

            if (null != qr_content && !qr_content.equals("")) {

                Bundle data_QR = new Bundle();
                data_QR.putString(PrinterUtils.STRPARA1, qr_content);
                data_QR.putInt(PrinterUtils.INTPARA1, 6);
                data_QR.putInt(PrinterUtils.INTPARA2, 4);
                data_QR.putInt(PrinterUtils.INTPARA3, 4);
                PrinterWorkService.getPrinterThread().handleCmd(
                        PrinterUtils.CMD_POS_SETQRCODE, data_QR);

                addSpace();

            }

            int nBarcodetype = 7, nStartOrgx = 0, nBarcodeWidth = 1,
                    nBarcodeHeight = 3, nBarcodeFontType = 0, nBarcodeFontPosition = 2;
            int nOrgx = nStartOrgx * 12;
            int nType = 0x41 + nBarcodetype;
            int nWidthX = nBarcodeWidth + 2;
            int nHeight = (nBarcodeHeight + 1) * 24;
            int nHriFontType = nBarcodeFontType;
            int nHriFontPosition = nBarcodeFontPosition;

            if (!barcode.equals("") && barcode != null)

            {
                Bundle data_Barcode = new Bundle();
                data_Barcode.putString(PrinterUtils.STRPARA1, barcode);
                data_Barcode.putInt(PrinterUtils.INTPARA1, nOrgx);
                data_Barcode.putInt(PrinterUtils.INTPARA2, nType);
                data_Barcode.putInt(PrinterUtils.INTPARA3, nWidthX);
                data_Barcode.putInt(PrinterUtils.INTPARA4, nHeight);
                data_Barcode.putInt(PrinterUtils.INTPARA5, nHriFontType);
                data_Barcode.putInt(PrinterUtils.INTPARA6, nHriFontPosition);
                PrinterWorkService.getPrinterThread().handleCmd(PrinterUtils.CMD_POS_SETBARCODE,
                        data_Barcode);

                addSpace();
            }

        } else {
            Toast.makeText(this, PrinterUtils.toast_notconnect,
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void doPrint(String s) {
        Log.v("onPrintInnerPrinter", s);

        if (s.equals("")) return;
        Boolean logo = false;
        String header1 = "";
        String header_fontsize = "";
        String body = "";
        String body_fontsieze = "";
        String qr_content = "";
        String barcode = "";
        JSONObject jsonObject = new JSONObject();

        int charset = 0, codepage = 0;
        String encoding = "UTF-8";
        byte[] addBytes = new byte[0];

        try {
            jsonObject = new JSONObject(s);
            logo = jsonObject.optBoolean("logo");
            showLogo = logo;
            header1 = jsonObject.optString("header");
            header_fontsize = jsonObject.optString("header_fontsize");
            body = jsonObject.optString("body");
            body_fontsieze = jsonObject.optString("body_fontsieze");
            qr_content = jsonObject.optString("qr_content");
            barcode = jsonObject.optString("barcode");
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        if (PrinterWorkService.printerWorkThread.isConnected()) {

//        if (showLogo) {
//
//            ImageReadWrite obj = new ImageReadWrite();
//            Bitmap logoImage = obj.ReadImage();
//
//            //  Bitmap mBitmap = getImageFromAssetsFile("btn_application_center_press.png");
//            int nPaperWidth = 384;
//            if (logoImage != null) {
//                Bundle data = new Bundle();
//                // data.putParcelable(PrinterUtils.OBJECT1, mBitmap);
//                data.putParcelable(PrinterUtils.PARCE1, logoImage);
//                data.putInt(PrinterUtils.INTPARA1, nPaperWidth);
//                data.putInt(PrinterUtils.INTPARA2, 0);
//                PrinterWorkService.printerWorkThread.handleCmd(PrinterUtils.CMD_POS_PRINTPICTURE, data);
//            }
//        }

        // addSpace();

        if (!header1.equals("") && header1 != null) {
            String str = header1;
            byte[] tmp1 = {0x1b, 0x40, 0x0A};
            //byte[] tmp2 = {0x1d, 0x21, 0x11};//宽高加倍//{0x1b, 0x21, 0x01};
            byte[] tmp2 = {0x1b, 0x45, 0x01, 0x1d, 0x21, 0x11};//bold + bigger
//                byte[] tmp3 = {0x0A, 0x0A, 0x0A, 0x0A};
            byte[] tmp3 = {0x0A, 0x0A};//two new lines.
            byte[] buf = byteArraysToBytes(new byte[][]{
                    tmp1, tmp2, str.getBytes(), tmp3});
            //if (PrinterWorkService.printerWorkThread.isConnected()) {
            Bundle data = new Bundle();
            data.putByteArray(PrinterUtils.BYTESPARA1, buf);
            data.putInt(PrinterUtils.INTPARA1, 0);
            data.putInt(PrinterUtils.INTPARA2, buf.length);
            PrinterWorkService.getPrinterThread().handleCmd(PrinterUtils.CMD_WRITE, data);
            //}

            //  addSpace();

            // add native bt files and print from there
        }

        if (!body.equals("") && body != null) {
            String str = body;
            byte[] tmp3 = {0x0A, 0x0A, 0x0A, 0x0A};
//                byte[] buf = DataUtils.byteArraysToBytes(new byte[][]{
//                        str.getBytes(), tmp3});
//                byte[] tmp1 = {0x1d, 0x21, 0x00};//back to normal font.
            byte[] tmp1 = {0x1b, 0x45, 0x00, 0x1d, 0x21, 0x00};//unbold + normal size
            byte[] buf = byteArraysToBytes(new byte[][]{tmp1,
                    str.getBytes(), tmp3});
            //if (PrinterWorkService.printerWorkThread.isConnected()) {
            Bundle data = new Bundle();
            data.putByteArray(PrinterUtils.BYTESPARA1, buf);
            data.putInt(PrinterUtils.INTPARA1, 0);
            data.putInt(PrinterUtils.INTPARA2, buf.length);
            PrinterWorkService.getPrinterThread().handleCmd(PrinterUtils.CMD_WRITE, data);
            //}

            //addSpace();

        }

        if (null != qr_content && !qr_content.equals("")) {
            Bundle data_QR = new Bundle();
            data_QR.putString(PrinterUtils.STRPARA1, qr_content);
            data_QR.putInt(PrinterUtils.INTPARA1, 6);
            data_QR.putInt(PrinterUtils.INTPARA2, 4);
            data_QR.putInt(PrinterUtils.INTPARA3, 4);
            PrinterWorkService.getPrinterThread().handleCmd(
                    PrinterUtils.CMD_POS_SETQRCODE, data_QR
            );
            addSpace();

        }


/*
            int nBarcodetype=7, nStartOrgx=0, nBarcodeWidth = 1,
                    nBarcodeHeight = 3, nBarcodeFontType=0, nBarcodeFontPosition = 2;
            int nOrgx = nStartOrgx * 12;
            int nType = 0x41 + nBarcodetype;
            int nWidthX = nBarcodeWidth + 2;
            int nHeight = (nBarcodeHeight + 1) * 24;
            int nHriFontType = nBarcodeFontType;
            int nHriFontPosition = nBarcodeFontPosition;



            if(!barcode.equals("") && barcode!=null)
           // barcode="0123456789012";
            {
                Bundle data_Barcode = new Bundle();
                data_Barcode.putString(PrinterUtils.STRPARA1, barcode);
                data_Barcode.putInt(PrinterUtils.INTPARA1, nOrgx);
                data_Barcode.putInt(PrinterUtils.INTPARA2, nType);
                data_Barcode.putInt(PrinterUtils.INTPARA3, nWidthX);
                data_Barcode.putInt(PrinterUtils.INTPARA4, nHeight);
                data_Barcode.putInt(PrinterUtils.INTPARA5, nHriFontType);
                data_Barcode.putInt(PrinterUtils.INTPARA6, nHriFontPosition);
                PrinterWorkService.printerWorkThread.handleCmd(PrinterUtils.CMD_POS_SETBARCODE,
                        data_Barcode);


//                addSpace();

            }
*/

//        } else {
//            Toast.makeText(this, PrinterUtils.toast_notconnect,
//                    Toast.LENGTH_SHORT).show();
//        }

    }

    static class MHandler extends Handler {

        WeakReference<PrintService> mActivity;

        MHandler(PrintService activity) {
            mActivity = new WeakReference<PrintService>(activity);
        }

        public synchronized void print_result_back() {

            if (result_text == 0 || result_QR == 0 || result_BR == 0 || result_logo == 0 || result_header == 0) {

                Bundle data = new Bundle();
                data.putString("result", "ko");
                PrinterWorkService.getPrinterThread().handleCmd(PrinterUtils.MSG_WORKTHREAD_HANDLER_PRINT_CALLBACK,
                        data);
                result_header = -1;
                result_logo = -1;
                result_text = -1;
                result_QR = -1;
                result_BR = -1;
            } else if (result_text == 1 && result_QR == 1 && result_BR == 1 && result_logo == 1 && result_header == 1) {
                PrinterWorkService.getPrinterThread().pos.POS_FeedLine();
                PrinterWorkService.getPrinterThread().pos.POS_FeedLine();
                PrinterWorkService.getPrinterThread().pos.POS_FeedLine();
                Bundle data = new Bundle();
                data.putString("result", "ok");
                PrinterWorkService.getPrinterThread().handleCmd(PrinterUtils.MSG_WORKTHREAD_HANDLER_PRINT_CALLBACK,
                        data);
                result_header = -1;
                result_logo = -1;
                result_text = -1;
                result_QR = -1;
                result_BR = -1;
            }
        }

        @Override
        public void handleMessage(Message msg) {
            PrintService theActivity = mActivity.get();
            switch (msg.what) {
                case PrinterUtils.CMD_POS_STEXTOUTRESULT: {
                    result_header = msg.arg1;
                    print_result_back();
                    break;
                }
                case PrinterUtils.CMD_POS_WRITERESULT: {
                    result_text = msg.arg1;
                    print_result_back();
                    break;
                }
                case PrinterUtils.CMD_POS_SETBARCODERESULT: {
                    result_BR = msg.arg1;
                    print_result_back();
                    break;
                }
                case PrinterUtils.CMD_POS_PRINTPICTURERESULT: {
                    result_logo = msg.arg1;
                    print_result_back();
                    break;
                }
                case PrinterUtils.CMD_POS_SETQRCODERESULT:
                case PrinterUtils.CMD_EPSON_SETQRCODERESULT: {
                    result_QR = msg.arg1;
                    print_result_back();
                    break;
                }

            }
        }
    }

    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PrinterWorkService.delHandler(mHandler);
        mHandler = null;
    }

    public static class ImageReadWrite {

        public void SaveImage(Bitmap finalBitmap) {

            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/saved_images");
            myDir.mkdirs();
            //Random generator = new Random();
            //int n = 10000;
            //n = generator.nextInt(n);
            String fname = "LogoImage.jpg";
            File file = new File(myDir, fname);
            if (file.exists()) file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public Bitmap ReadImage() {
            try {
                String root = Environment.getExternalStorageDirectory().toString();
                Bitmap bitmap = BitmapFactory.decodeFile(root + "/saved_images/LogoImage.jpg");
                return bitmap;
            } catch (Exception e) {
                return null;
            }

        }
    }
}
