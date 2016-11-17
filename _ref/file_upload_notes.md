        ///////////////////////////////////////
        // <input type=file> support:
//        // openFileChooser() is for pre KitKat and in KitKat mr1. For Kitkat, it's known broken...
//        //TODO copy some codes from Cordova to get the return result of the file pickup activity...
//
//        // For Lollipop, we use onShowFileChooser().
//        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
//            this.openFileChooser(uploadMsg, "*/*");
//        }
//
//        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
//            this.openFileChooser(uploadMsg, acceptType, null);
//        }
//
//        public void openFileChooser(final ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
////            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
////            intent.addCategory(Intent.CATEGORY_OPENABLE);
////            intent.setType("*/*");
////                parentEngine.cordova.startActivityForResult(new CordovaPlugin() {
////                    @Override
////                    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
////                        Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
////                        Log.d(LOG_TAG, "Receive file chooser URL: " + result);
////                        uploadMsg.onReceiveValue(result);
////                    }
////                }, intent, FILECHOOSER_RESULTCODE);
//        }
//
//        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//        public boolean onShowFileChooser(WebView webView, final ValueCallback<Uri[]> filePathsCallback, final WebChromeClient.FileChooserParams fileChooserParams) {
////            Intent intent = fileChooserParams.createIntent();
////                try {
////                    parentEngine.cordova.startActivityForResult(new CordovaPlugin() {
////                        @Override
////                        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
////                            Uri[] result = WebChromeClient.FileChooserParams.parseResult(resultCode, intent);
////                            Log.d(LOG_TAG, "Receive file chooser URL: " + result);
////                            filePathsCallback.onReceiveValue(result);
////                        }
////                    }, intent, FILECHOOSER_RESULTCODE);
////                } catch (ActivityNotFoundException e) {
////                    Log.w("No activity found to handle file chooser intent.", e);
////                    filePathsCallback.onReceiveValue(null);
////                }
//            return true;
//        }
        ///////////////////////////////////////


				
        //TODO hacking prompt...
        @Override
        public boolean onJsPrompt(WebView view, String origin, String message, String defaultValue, final JsPromptResult result) {
            result.confirm(defaultValue);
            // Unlike the @JavascriptInterface bridge, this method is always called on the UI thread.
//            String handledRet = parentEngine.bridge.promptOnJsPrompt(origin, message, defaultValue);
//            if (handledRet != null) {
//                result.confirm(handledRet);
//            } else {
//                dialogsHelper.showPrompt(message, defaultValue, new CordovaDialogsHelper.Result() {
//                    @Override
//                    public void gotResult(boolean success, String value) {
//                        if (success) {
//                            result.confirm(value);
//                        } else {
//                            result.cancel();
//                        }
//                    }
//                });
//            }
            return true;
        }
