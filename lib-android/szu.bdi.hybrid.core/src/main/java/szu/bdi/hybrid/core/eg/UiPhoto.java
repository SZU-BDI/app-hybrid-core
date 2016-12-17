package szu.bdi.hybrid.core.eg;

import android.content.Intent;
import android.os.Bundle;

import info.cmptech.JSO;
import szu.bdi.hybrid.core.NativeUi;

public class UiPhoto extends NativeUi {
    final private static String LOGTAG = new Throwable().getStackTrace()[0].getClassName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //call the activity from printwrapper
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //intent.setClass(UiPhoto, android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//do top
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        JSO jso = new JSO();
        jso.setChild("code", "" + resultCode);
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
