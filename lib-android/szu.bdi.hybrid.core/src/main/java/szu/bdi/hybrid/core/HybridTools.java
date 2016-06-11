package szu.bdi.hybrid.core;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HybridTools {
    final private static String LOGTAG = "HybridTools";

    private static Context _appContext = null;

    //IMPORTANT !!!: in the app entry, set HybridTools.setAppContext(getApplicationContext());
    public static void setAppContext(Context appContext) {
        _appContext = appContext;
    }

    public static Context getAppContext() {
//        if (_appContext == null) {
//            throw new Exception("_appContext is null");
//        }
        return _appContext;
    }

    static MediaPlayer mp = new MediaPlayer();

    public static boolean flagAppWorking = false;//暂时用这个方法让back service先跳过

    public static void play_Greeting() {
        Log.d(LOGTAG, "play_Greeting()");

        new Thread(new Runnable() {
            @Override
            public void run() {
                mp.reset();
                try {

                    //TODO 因为时间关系，现在没有使用本地文件、资源文件、网络文件，而是使用了系统setting的值。
                    //暂时可以先用，但emulator中是没有声音的，之后要解决 从上述几种位置播放声音的方法。
                    Uri uu = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//                    Log.d(LOGTAG, "Uri=" + uu.toString());
                    mp.setDataSource(getAppContext(), uu);
                    mp.setOnPreparedListener(preparedListener);
                    //mp.prepareAsync();
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

//    public static void play_Warning_once() {
//        Log.d(LOGTAG, "play_warning()");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mp.reset();
//                try {
//                    Uri uu = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                    Log.d(LOGTAG, "Uri=" + uu.toString());
//                    mp.setDataSource(_appContext, uu);
//                    mp.setOnPreparedListener(preparedListenerOnce);
//                    mp.prepareAsync();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (IllegalStateException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }).start();
//    }

    public static void play_Warning() {
        Log.d(LOGTAG, "play_Warning()");
        new Thread(new Runnable() {
            @Override
            public void run() {
                mp.reset();
                try {
                    Uri uu = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//                    Log.d(LOGTAG, "Uri=" + uu.toString());
                    mp.setDataSource(getAppContext(), uu);
                    mp.setOnPreparedListener(preparedListener);
                    //mp.prepareAsync();
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public static void play_Notify() {
        Log.d(LOGTAG, "play_Notify()");
        new Thread(new Runnable() {
            @Override
            public void run() {
                mp.reset();
                try {
                    Uri uu = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                    Log.d(LOGTAG, "Uri=" + uu.toString());
                    mp.setDataSource(getAppContext(), uu);
                    mp.setOnPreparedListener(preparedListenerOnce);
                    //mp.prepareAsync();
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    static MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            long start_time = System.currentTimeMillis();
            mp.start();
            while (System.currentTimeMillis() - start_time < 4000) {
                if (!mp.isPlaying())
                    mp.start();
            }
            if (mp.isPlaying())
                mp.stop();
        }
    };

    static MediaPlayer.OnPreparedListener preparedListenerOnce = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            long start_time = System.currentTimeMillis();
            mp.start();
//            while (System.currentTimeMillis() - start_time < 1000) {
//                if (!mp.isPlaying())
//                    mp.start();
//            }
//            if (mp.isPlaying())
//                mp.stop();
        }
    };

    public static boolean isEmptyString(String s) {
        if (s == null || "".equals(s)) return true;
        if ("null".equals(s)) return true;//这个bug fix是不对的，只是暂时的，原因是因为 json包的 optString()
        // 返回字串的"null”,因为未有时间研究，所以先临时这样处理
        return false;
    }

    public static void quickShowMsgMain(String msg) {
        quickShowMsg(getAppContext(), msg);
    }

    public static void quickShowMsg(Context mContext, String msg) {
        //@ref http://blog.csdn.net/droid_zhlu/article/details/7685084
        //A toast is a view containing a quick little message for the user.  The toast class helps you create and show those.
        try {
            Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String system(String cmd) {
        String s = "";

        try {
            Process localProcess = Runtime.getRuntime().exec(cmd);

            localProcess.waitFor();//阻塞...
            String line = null;

            BufferedReader in = new BufferedReader(new InputStreamReader(localProcess.getErrorStream()));
            while ((line = in.readLine()) != null) {
                s += line + "\n";
            }
            BufferedReader in2 = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
            while ((line = in2.readLine()) != null) {
                s += line + "\n";
            }
//            localProcess.destroy();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String system(String[] cmd) {
        String s = "";

        try {
            Process localProcess = Runtime.getRuntime().exec(cmd);

            localProcess.waitFor();//阻塞...
            String line = null;

            BufferedReader in = new BufferedReader(new InputStreamReader(localProcess.getErrorStream()));
            while ((line = in.readLine()) != null) {
                s += line + "\n";
            }
            BufferedReader in2 = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
            while ((line = in2.readLine()) != null) {
                s += line + "\n";
            }
//            localProcess.destroy();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static void trySystemReboot() {
        String s = "";

//        s = system("su -v");//OK
//        s = system(new String[]{"su", "-c", "ls"});//OK
        //s = system(new String[]{"su", "-c", "ls -l /system/bin/reboot"});//OK
        //s = system(new String[]{"su", "-c", "reboot"});//FAIL at HTC at program but
        // ok at adb shell
//        s += system(new String[]{"su", "-c", "\"echo 'b' > /proc/sysrq-trigger\""});//ok at adb, but fail at program
//        s = s + system(new String[]{"su", "-c", "echo 'reboot' > /dev/rb.sh"});
//        s = s + system(new String[]{"su", "-c", "sh /dev/rb.sh"});

        try {
            //执行 su -c sh 进入 shell:
            Process localProcess = Runtime.getRuntime().exec(new String[]{"su", "-c", "sh"});

            //在shell环境做各种 reboot尝试
            DataOutputStream localDataOutputStream = new DataOutputStream(localProcess.getOutputStream());
            localDataOutputStream.writeBytes("su -c reboot\n");
            localDataOutputStream.flush();
//            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.writeBytes("su -c 'echo b > /proc/sysrq-trigger'\n");//HTC...
            localDataOutputStream.flush();
//            localDataOutputStream.close();
            localProcess.waitFor();
//            localProcess.destroy();
            s = s + "fail reboot? maybe check ROOT ...";
        } catch (Exception e) {
            s = s + "fail reboot. Pls check ROOT !!!";
            e.printStackTrace();
        }
        if (!isEmptyString(s)) {
            quickShowMsgMain(s);
        }
        Log.d(LOGTAG, "trySystemReboot s=" + s);
    }

//        try {
//            String cmd = "su -c reboot";
//            cmd = "su -c ls -al /system/bin/reboot";
////            Process localProcess = Runtime.getRuntime().exec(new String[]{"su", "-c", "/system/bin/reboot"});
//            Process localProcess = Runtime.getRuntime().exec(new String[]{"su", "-c", "/system/bin/reboot"});
//            localProcess.waitFor();
//            BufferedReader in = new BufferedReader(
//                    new InputStreamReader(localProcess.getInputStream()));
//            String line = null;
//            String s = "";
//            while ((line = in.readLine()) != null) {
//                s += line + "\n";
//            }
//            localProcess.destroy();
//            Log.v(LOGTAG, "s=" + s);

//            cmd = "su -c \"/system/bin/reboot\"";
//            cmd = "su -c \"am broadcast -a android.intent.action.ACTION_REBOOT\"";
//            cmd = "sh -c \"am broadcast -a android.intent.action.ACTION_REBOOT\"";
//            cmd = "am start -a android.intent.action.VIEW -d http://www.baidu.com/";//work....
//            cmd = "su -c \"am start -a android.intent.action.REBOOT\"";
//            Runtime.getRuntime().exec(cmd);

//            sleep(1000);

//            Process localProcess = Runtime.getRuntime().exec("su");
//            DataOutputStream localDataOutputStream = new DataOutputStream(localProcess.getOutputStream());
//            localDataOutputStream.writeBytes("reboot\n");
//            localDataOutputStream.writeBytes("exit\n");
//            localDataOutputStream.flush();
//            localDataOutputStream.close();
//            localProcess.waitFor();
//            localProcess.destroy();
//            Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot -p"});
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            PowerManager pManager = (PowerManager) _appContext.getSystemService(Context.POWER_SERVICE);
//            pManager.reboot("");
//        } catch (Exception e) {
//            Log.e(LOGTAG, e.toString(), e);
//        }
//        try {
//            PowerManager pManager = (PowerManager) _appContext.getSystemService(Context.POWER_SERVICE);
//            //重启到fastboot模式
//            pManager.reboot("");
//
////            //获得ServiceManager类
////            Class ServiceManager = Class
////                    .forName("android.os.ServiceManager");
////
////            //获得ServiceManager的getService方法
////
////            Method getService = ServiceManager.getMethod("getService", java.lang.String.class);
////
////            //调用getService获取RemoteService
////            Object oRemoteService = getService.invoke(null, Context.POWER_SERVICE);
////
////            //获得IPowerManager.Stub类
////            Class cStub = Class
////                    .forName("android.os.IPowerManager$Stub");
////            //获得asInterface方法
////            Method asInterface = cStub.getMethod("asInterface", android.os.IBinder.class);
////            //调用asInterface方法获取IPowerManager对象
////            Object oIPowerManager = asInterface.invoke(null, oRemoteService);
////            //获得shutdown()方法
////            Method shutdown = oIPowerManager.getClass().getMethod("shutdown", boolean.class, boolean.class);
////            //调用shutdown()方法
////            shutdown.invoke(oIPowerManager, false, true);
//
//        } catch (Exception e) {
//            Log.e(LOGTAG, e.toString(), e);
//        }
//        try {
//            Intent i = new Intent(Intent.ACTION_REBOOT);
//            i.putExtra("nowait", 1);
//            i.putExtra("interval", 1);
//            i.putExtra("window", 0);
//            _appContext.sendBroadcast(i);
//        } catch (Exception e) {
//            Log.e(LOGTAG, e.toString(), e);
//        }
//        try {
//            PowerManager pManager = (PowerManager) _appContext.getSystemService(Context.POWER_SERVICE);
//            pManager.reboot("");//fast book
//        } catch (Exception e) {
//            Log.e(LOGTAG, e.toString(), e);
//        }
//        Intent i = new Intent(Intent.ACTION_REBOOT);
//        i.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        _appContext.sendBroadcast(i);

    //TEST OK
//        //想测试一下函数体内动了 jsonObject的里面的值，会不会改变（测试是引用还是复制）
//    public static void testJSONObject(JSONObject _jsonObject) {
//        try {
//            _jsonObject.put("test2", "222");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Log.d(LOGTAG, "testJSONObject()" + _jsonObject.toString());
//    }

    /*public static String post(String url, Map<String, String> paramsMap) {
        CloseableHttpClient client = HttpClients.createDefault();
        String responseText = "";
        CloseableHttpResponse response = null;
        try {
            HttpPost method = new HttpPost(url);
            if (paramsMap != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
                    paramList.add(pair);
                }
                method.setEntity(new UrlEncodedFormEntity(paramList, ENCODING));
            }
            response = client.execute(method);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseText = EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return responseText;
    }*/
    public static String webPost(String url, String post_s) {
        String return_s = null;

        HttpClient httpClient = new DefaultHttpClient();
        //据说新版本是
        // HttpClient httpClient = HttpClientBuilder.create().build();
//            CloseableHttpClient httpClient;
//            httpClient = HttpClientBuilder.create()
//                    .setDefaultConnectionConfig(config)
//                    .build();

        // Connect Timeout
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 12000);
        // Socket Timeout
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 12000);

        //创建一个HttpPost对象以发送post请求

        HttpPost httpPost = new HttpPost(url);

        Log.v(LOGTAG, "To Post : " + url + "\n" + post_s);

        try {
            StringEntity se = new StringEntity(post_s);
            httpPost.setEntity(se);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            return_s = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
//            return_s = ex.getClass().getName() + "," + ex.getMessage();
            return_s = ex.getMessage();
            if (isEmptyString(return_s)) {
                return_s = "" + ex.getClass().getName();
            }
        }
        return return_s;
    }

    //Wrap the raw webPost
    public static JSONObject apiPost(String url, JSONObject jo) {
        String return_s = null;
        try {
            String post_s = jo.toString();
            return_s = webPost(url, post_s);
        } catch (Exception ex) {
            return_s = ex.getMessage();
            if (isEmptyString(return_s)) {
                return_s = "" + ex.getClass().getName();
            }
            ex.printStackTrace();
        }
        try {
//            String post_s = jo.toString();
//            return_s = webPost(url, null, post_s);
            if (return_s != null && return_s != "")
                return new JSONObject(return_s);
        } catch (Exception ex) {
//            Log.d(LOGTAG, "Wrong return =" + return_s);
////            return_s = ex.getClass().getName() + "," + ex.getMessage();
//            return_s = ex.getClass().getName() + "," + ex.getMessage();
//            return_s = ex.getMessage();
//            if (isEmptyString(return_s)) {
//                return_s = "" + ex.getClass().getName();
//            }
//            ex.printStackTrace();
        }
        JSONObject rt = new JSONObject();
        try {
            rt.put("STS", "KO");
            rt.put("errmsg", "" + return_s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rt;
    }

    public static void quit(boolean playMedia) {
        flagAppWorking = false;//wlll affect the bg service

        quickShowMsgMain("Quiting...");
        Log.d(LOGTAG, "quit " + playMedia);

        //TODO How to close all activities

        //TEST FAIL
        //put finish(); at that class
//        http://stackoverflow.com/questions/6330260/finish-all-previous-activities
//        Intent intent = new Intent(_appContext, CloseActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("EXIT", true);
//        _appContext.startActivity(intent);

//        if (playMedia) {
//            try {
//                mp.reset();
//                Uri uu = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
////                    Log.d(LOGTAG, "Uri=" + uu.toString());
//                mp.setDataSource(_appContext, uu);
//                mp.setOnPreparedListener(preparedListener);
//                //mp.prepareAsync();
//                mp.prepare();
//            } catch (IllegalStateException | IOException e) {
//                playMedia = false;
//                e.printStackTrace();
//            }
//        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                KillAppSelf();
            }
        }, (playMedia) ? 5000 : 3000);
    }

    //NOTES:  may need to run in main thread...
    public static void KillAppSelf() {
        int pid = android.os.Process.myPid();
        Log.d(LOGTAG, "kill and quit pid=" + pid);

        android.os.Process.killProcess(pid);
        System.exit(0);
    }

    //TODO parseFromFormat
    public static String isoDateTime() {
        String time_s = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
        return time_s;
    }

    public static String assetFile2Str(Context c, String urlStr) {
        InputStream in = null;
        try {
            in = c.getAssets().open(urlStr);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            StringBuilder sb = new StringBuilder();
            do {
                line = bufferedReader.readLine();
                if (line != null && !line.matches("^\\s*\\/\\/.*")) {
                    sb.append(line);
                }
            } while (line != null);

            bufferedReader.close();
            in.close();

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
                in = null;
            }
        }
        return null;
    }

    public static boolean isEmpty(Object o) {
        if (o == null) return true;
        return false;
    }
}

//run backgroup service
//        Intent bg = new Intent(_ctx, BackService.class);
//        this.startService(bg);

