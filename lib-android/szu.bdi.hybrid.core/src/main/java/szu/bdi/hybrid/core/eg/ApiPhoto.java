package szu.bdi.hybrid.core.eg;

import android.content.Intent;

import info.cmptech.JSO;
import szu.bdi.hybrid.core.HybridApi;
import szu.bdi.hybrid.core.HybridCallback;
import szu.bdi.hybrid.core.HybridHandler;
import szu.bdi.hybrid.core.HybridTools;
import szu.bdi.hybrid.core.HybridUi;
import szu.bdi.hybrid.core.HybridUiCallback;


public class ApiPhoto extends HybridApi {
    //    private void captureImage(Request req) {
//        boolean needExternalStoragePermission =
//                !PermissionHelper.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
//
//        boolean needCameraPermission = cameraPermissionInManifest &&
//                !PermissionHelper.hasPermission(this, Manifest.permission.CAMERA);
//
//        if (needExternalStoragePermission || needCameraPermission) {
//            if (needExternalStoragePermission && needCameraPermission) {
//                PermissionHelper.requestPermissions(this, req.requestCode, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA});
//            } else if (needExternalStoragePermission) {
//                PermissionHelper.requestPermission(this, req.requestCode, Manifest.permission.READ_EXTERNAL_STORAGE);
//            } else {
//                PermissionHelper.requestPermission(this, req.requestCode, Manifest.permission.CAMERA);
//            }
//        } else {
//            // Save the number of images currently on disk for later
//            this.numPics = queryImgDB(whichContentStore()).getCount();
//
//            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//
//            // Specify file so that large image is captured and returned
//            File photo=new File(getTempDirectoryPath(),"Capture.jpg");
//            try{
//                // the ACTION_IMAGE_CAPTURE is run under different credentials and has to be granted write permissions
//                createWritableFile(photo);
//            }catch(IOException ex){
//                pendingRequests.resolveWithFailure(req,createErrorObject(CAPTURE_INTERNAL_ERR,ex.toString()));
//                return;
//            }
//            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,Uri.fromFile(photo));
//
//            this.cordova.startActivityForResult((CordovaPlugin)this,intent,req.requestCode);
//        }
//    }
    public HybridHandler getHandler() {
        return new HybridHandler() {

            @Override
            public void handler(JSO jso, final HybridCallback cbFunc) {
                final HybridUi ui = getCallerUi();

                HybridTools.startUi("UiPhoto", jso.toString(true), ui, new HybridUiCallback() {
                    @Override
                    public void onCallBack(final HybridUi ui) {

                        //bind "close"
                        ui.on("close", new HybridCallback() {
//                            @Override
//                            public void onCallBack(String cbStr) {
//                                //fwd to above handler
//                                onCallBack(JSO.s2o(cbStr));
//                            }

                            @Override
                            public void onCallBack(JSO jso) {
                                //manually handle finishing the ui
                                ui.finish();
                                //back to api caller
                                cbFunc.onCallBack(jso);
                            }
                        });
                    }

                });//startUi()
//                HybridUi caller = getCallerUi();
//                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                caller.startActivityForResult(intent, 1);
//                jso.setChild("STS", JSO.s2o("TODO"));
//                //jso.setChild("pong", JSO.s2o("" + (new Date()).getTime()));
//                cbFunc.onCallBack(jso);
            }
        };
    }
}
