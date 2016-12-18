package szu.bdi.hybrid.core.eg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import info.cmptech.JSO;
import szu.bdi.hybrid.core.HybridTools;
import szu.bdi.hybrid.core.NativeUi;


public class UiPhoto extends NativeUi {
    final private static String LOGTAG = new Throwable().getStackTrace()[0].getClassName();

    final public static String REQUEST_CAMERA = "camera";
    final public static String REQUEST_ALBUM = "album";

    final public static int REQUEST_CODE_CAMERA = 1;
    final public static int REQUEST_CODE_ALBUM = 2;

    public void getImage() {
        Intent intent = new Intent();
        String title = "Select Photo";
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        this.startActivityForResult(
                Intent.createChooser(intent, new String(title)),
                REQUEST_CODE_ALBUM);
    }

    private static void createWritableFile(File file) throws IOException {
        file.createNewFile();
        file.setWritable(true, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String srcType = getUiData("from").asString();
        if (REQUEST_CAMERA.equals(srcType)) {
            this.callTakePicture();
        } else if (REQUEST_ALBUM.equals(srcType)) {
            this.getImage();
        } else {
            JSO rt = new JSO();
            rt.setChild("STS", "KO");
            rt.setChild("errmsg", "Need param 'from'");
            closeUi(rt);
        }
    }

    private void callTakePicture() {

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        // Specify file so that large image is captured and returned
        File photo = new File(HybridTools.getTempDirectoryPath(), "Capture.jpg");
        try {
            // the ACTION_IMAGE_CAPTURE is run under different credentials and has to be granted write permissions
            createWritableFile(photo);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//do top

        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        JSO jso = new JSO();

        if (this.RESULT_OK == resultCode) {
            String srcPhoto = null;
            if (this.REQUEST_CODE_ALBUM == requestCode) {
                if (null != intent) {
                    Uri uri = intent.getData();
                    srcPhoto = uri.toString();
                }
            } else if (this.REQUEST_CODE_CAMERA == requestCode) {
                srcPhoto = HybridTools.getTempDirectoryPath() + "/Capture.jpg";
            }

            String fileToUpload = null;
            InputStream fileStream = null;
            Bitmap image = null;
            try {
                if (srcPhoto.startsWith("content:")) {
                    Uri uuu = Uri.parse(srcPhoto);
                    fileStream = this.getContentResolver().openInputStream(uuu);
                } else if (srcPhoto.startsWith("file:///android_asset/")) {
                    Uri uuu = Uri.parse(srcPhoto);
                    String relativePath = srcPhoto.substring(15);
                    fileStream = this.getAssets().open(relativePath);
                } else {
                    fileStream = new FileInputStream(srcPhoto);
                }
                image = BitmapFactory.decodeStream(fileStream);
                int w = image.getWidth();
                int h = image.getHeight();
                jso.setChild("w", "" + w);
                jso.setChild("h", "" + h);

                //TODO 判断大小看要不要改比例；
                //TODO 把Bitmap输出成 Jpg
            } catch (Throwable ex) {
                ex.printStackTrace();
            } finally {
                if (fileStream != null) {
                    try {
                        fileStream.close();
                    } catch (IOException e) {
                        Log.v(LOGTAG, "Exception while closing file input stream.");
                    }
                }
            }
            if (HybridTools.isEmptyString(fileToUpload)) {
                jso.setChild("STS", "KO");
                jso.setChild("file", srcPhoto);
            } else {
                jso.setChild("STS", "OK");
                jso.setChild("file", fileToUpload);
            }
        } else if (this.RESULT_CANCELED == resultCode) {
            jso.setChild("STS", "CANCEL");
        } else {
            jso.setChild("STS", "KO");
            jso.setChild("code", "" + resultCode);
        }

        closeUi(jso);
//        // Get src and dest types from request code for a Camera Activity
//        int srcType = (requestCode / 16) - 1;
//        int destType = (requestCode % 16) - 1;
//
//        // If Camera Crop
//        if (requestCode >= CROP_CAMERA) {
//            if (resultCode == Activity.RESULT_OK) {
//
//                // Because of the inability to pass through multiple intents, this hack will allow us
//                // to pass arcane codes back.
//                destType = requestCode - CROP_CAMERA;
//                try {
//                    processResultFromCamera(destType, intent);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    LOG.e(LOG_TAG, "Unable to write to file");
//                }
//
//            }// If cancelled
//            else if (resultCode == Activity.RESULT_CANCELED) {
//                this.failPicture("Camera cancelled.");
//            }
//
//            // If something else
//            else {
//                this.failPicture("Did not complete!");
//            }
//        }
//        // If CAMERA
//        else if (srcType == CAMERA) {
//            // If image available
//            if (resultCode == Activity.RESULT_OK) {
//                try {
//                    if(this.allowEdit)
//                    {
//                        Uri tmpFile = Uri.fromFile(createCaptureFile(this.encodingType));
//                        performCrop(tmpFile, destType, intent);
//                    }
//                    else {
//                        this.processResultFromCamera(destType, intent);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    this.failPicture("Error capturing image.");
//                }
//            }
//
//            // If cancelled
//            else if (resultCode == Activity.RESULT_CANCELED) {
//                this.failPicture("Camera cancelled.");
//            }
//
//            // If something else
//            else {
//                this.failPicture("Did not complete!");
//            }
//        }
//        // If retrieving photo from library
//        else if ((srcType == PHOTOLIBRARY) || (srcType == SAVEDPHOTOALBUM)) {
//            if (resultCode == Activity.RESULT_OK && intent != null) {
//                final Intent i = intent;
//                final int finalDestType = destType;
//                cordova.getThreadPool().execute(new Runnable() {
//                    public void run() {
//                        processResultFromGallery(finalDestType, i);
//                    }
//                });
//            }
//            else if (resultCode == Activity.RESULT_CANCELED) {
//                this.failPicture("Selection cancelled.");
//            }
//            else {
//                this.failPicture("Selection did not complete!");
//            }
//        }
//        if (PrinterUtils.RESULT_SELECT_PRINTER_CB == resultCode) {
//            //wait some time for the thread write the model name
//            (new Handler()).postDelayed(new Runnable() {
//                public void run() {
//                    JSO jso = new JSO();
//                    String PrinterModel = PrinterUtils.connected_Printer_Model;
//
//                    jso.setChild("Model", PrinterModel);
//                    if (HybridTools.isEmptyString(PrinterModel)) {
//                        jso.setChild("Status", "OFF");
//                    } else {
//                        jso.setChild("Status", "ON");
//                    }
//                    closeUi(jso);
//                }
//            }, 500);
//        } else {
//            JSO jso = new JSO();
//            closeUi(jso);
//        }
    }
}
