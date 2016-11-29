package info.cmptech.scanwrapper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
//import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.Vector;

public class MipcaActivityCapture extends Activity implements Callback {

    //    private static final float BEEP_VOLUME = 0.10f;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
//    private MediaPlayer mediaPlayer;
//    private boolean playBeep;
//    private boolean vibrate;
//    private int clicktime = 1;

    public static void quickShowMsg(Context mContext, String msg) {
        //@ref http://blog.csdn.net/droid_zhlu/article/details/7685084
        //A toast is a view containing a quick little message for the user.
        // The toast class helps you create and show those.
        try {
            Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_mipca_activity_capture);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.set_activity_tltle_style);
//		Button title_btn_back=(Button)findViewById(R.id.title_btn);

//		TextView title_TextView=(TextView)findViewById(R.id.title_textView);

//		title_TextView.setText(R.string.title_activity_scan_bitmap);
//		title_btn_back.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});

        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

//        playBeep = true;
//        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
//        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
//            playBeep = false;
//        }
//        //initBeepSound();
//        vibrate = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        finish();
//        /*if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
//            if((System.currentTimeMillis()-exitTime) > 2000){
//                Toast.makeText(getApplicationContext(), getText(R.string.WebActivity_finishi), Toast.LENGTH_SHORT).show();
//                exitTime = System.currentTimeMillis();
//            } else {
//                finish();
//            }
//            return true;
//        }*/
//        return true;
//    }
//public static void quickShowMsgMain(String msg) {
//    quickShowMsg(getAppContext(), msg);
//}

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        //playBeepSoundAndVibrate();
        String resultString = result.getText();

        //TODO prompt user "OK?", if user NO, than start again, if YES, continue return....

        CameraManager.get().stopPreview();
        if (null == resultString || resultString.equals("")) {
            quickShowMsg(MipcaActivityCapture.this, "Failed Scan");
//            Toast.makeText(MipcaActivityCapture.this, "Failed Scan" //R.string.bitmap_scan_failed
//                    , Toast.LENGTH_SHORT).show();
        } else {

            Log.v("MipcaActivityCapture", " scan = " + resultString);

            Intent id = new Intent();
            id.putExtra("result", resultString);
            //MipcaActivityCapture.this.setResult(1, id);
            setResult(1, id);
        }
        //MipcaActivityCapture.this.finish();
        finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

//	private void initBeepSound() {
//		if (playBeep && mediaPlayer == null) {
//			// The volume on STREAM_SYSTEM is not adjustable, and users found it
//			// too loud,
//			// so we now play on the music stream.
//			setVolumeControlStream(AudioManager.STREAM_MUSIC);
//			mediaPlayer = new MediaPlayer();
//			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//			mediaPlayer.setOnCompletionListener(beepListener);
//
//			AssetFileDescriptor file = getResources().openRawResourceFd(
//					R.raw.beep);
//			try {
//				mediaPlayer.setDataSource(file.getFileDescriptor(),
//						file.getStartOffset(), file.getLength());
//				file.close();
//				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
//				mediaPlayer.prepare();
//			} catch (IOException e) {
//				mediaPlayer = null;
//			}
//		}
//	}

//	private static final long VIBRATE_DURATION = 200L;
//
//	private void playBeepSoundAndVibrate() {
//		if (playBeep && mediaPlayer != null) {
//			mediaPlayer.start();
//		}
//		if (vibrate) {
//			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//			vibrator.vibrate(VIBRATE_DURATION);
//		}
//	}

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
//	private final OnCompletionListener beepListener = new OnCompletionListener() {
//		public void onCompletion(MediaPlayer mediaPlayer) {
//			mediaPlayer.seekTo(0);
//		}
//	};

//	public void checkNetWork() {
//		boolean flag = false;
//		flag=A();
//		if (flag&&(!AppHelper.getsid().equals("")))
//		{
//			Log.d("sd", "---flag---" + flag);
//			Log.d("sd", "---flag---" + AppHelper.getsid());
//			/*openUrlAtNewActivity(Uri.parse(url+ ApiTestThread._sid), mclass,text);*/
//
//		} else {
//			if (!flag)
//			{ Toast.makeText(MipcaActivityCapture.this, R.string.HttpUtil_NoInternet_ToastText, Toast.LENGTH_SHORT).show();}
//			else if (AppHelper.getsid().equals("")){
//				AlertDialog.Builder bulider = new AlertDialog.Builder(this);
//				bulider.setMessage(R.string.Fragments_NoScanResult_ToastText);
//				bulider.setCancelable(false);
//				bulider.setPositiveButton(getText(R.string.Login_dialog_sure).toString(), new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						finish();
//					}
//				}).show();
//			}
//
//			if (clicktime==1){
//				clicktime++;
//
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//
//						while ((!A())&&(AppHelper.getsid().equals(""))){ Log.d("sd","——————————————————————网络连接测试中———————未连接———");}
//						String filename = "LoginDataStore";
//						String field = "Username";
//						String username = AppHelper.getSavedSetting(MipcaActivityCapture.this, filename, field);
//						String filename2 = "LoginDataStore";
//						String field2 = "Password";
//						String password = AppHelper.getSavedSetting(MipcaActivityCapture.this, filename2, field2);
//						String m = MD5.toMD5(username);
//						String rp = DES.decryptDES(password, m);
//						new ApiTestThread().SwitchOfApiStatus(LoginActivity.MobileLoginFlag, MipcaActivityCapture.this, username, rp);
//					}
//				}).start();
//			}
//		}
//
//	}

//	public boolean A(){
//		boolean flag=false;
//		ConnectivityManager cwjManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//
//		if (cwjManager.getActiveNetworkInfo() != null)
//			flag = cwjManager.getActiveNetworkInfo().isAvailable();
//		return flag;
//
//	}

    /**
     * This class handles all the messaging which comprises the state machine for capture.
     */
    public static final class CaptureActivityHandler extends Handler {

        private static final String TAG = CaptureActivityHandler.class.getSimpleName();

        private final MipcaActivityCapture activity;
        private final DecodeThread decodeThread;
        private State state;

        public CaptureActivityHandler(MipcaActivityCapture activity, Vector<BarcodeFormat> decodeFormats,
                                      String characterSet) {
            this.activity = activity;
            decodeThread = new DecodeThread(activity, decodeFormats, characterSet, new ViewfinderResultPointCallback(activity.getViewfinderView()));
            decodeThread.start();
            state = State.SUCCESS;
            // Start ourselves capturing previews and decoding.
            CameraManager.get().startPreview();
            restartPreviewAndDecode();
        }

        @Override
        public void handleMessage(Message message) {
            Log.v(TAG, "CaptureActivityHandler handleMessage " + message.toString());
            if (message.what == R.id.auto_focus) {//Log.d(TAG, "Got auto-focus message");
                // When one auto focus pass finishes, start another. This is the closest thing to
                // continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.
                if (state == State.PREVIEW) {
                    CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
                }

            } else if (message.what == R.id.restart_preview) {
                Log.d(TAG, "Got restart preview message");
                restartPreviewAndDecode();

            } else if (message.what == R.id.decode_succeeded) {
                Log.d(TAG, "Got decode succeeded message");
                state = State.SUCCESS;
                Bundle bundle = message.getData();

                /***********************************************************************/
                Bitmap barcode = bundle == null ? null : (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);

                activity.handleDecode((Result) message.obj, barcode);

            } else if (message.what == R.id.decode_failed) {// We're decoding as fast as possible, so when one decode fails, start another.
                state = State.PREVIEW;
                CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);

            } else if (message.what == R.id.return_scan_result) {
                Log.d(TAG, "Got return scan result message");
                activity.setResult(RESULT_OK, (Intent) message.obj);
                activity.finish();

            } else if (message.what == R.id.launch_product_query) {
                Log.d(TAG, "Got product query message");
                String url = (String) message.obj;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                activity.startActivity(intent);

            }
        }

        public void quitSynchronously() {
            state = State.DONE;
            CameraManager.get().stopPreview();
            Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
            quit.sendToTarget();
            try {
                decodeThread.join();
            } catch (InterruptedException e) {
                // continue
            }

            // Be absolutely sure we don't send any queued up messages
            removeMessages(R.id.decode_succeeded);
            removeMessages(R.id.decode_failed);
        }

        private void restartPreviewAndDecode() {
            if (state == State.SUCCESS) {
                state = State.PREVIEW;
                CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
                activity.drawViewfinder();
            }
        }

        private enum State {
            PREVIEW,
            SUCCESS,
            DONE
        }

    }
}