package com.hybrid.core;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 作者：Created by LYC on 2015/4/10.
 * The app is created for test.
 */
public class AppHelper {
    public  static final String Extra_openwebsite_key="com.hybrid.core";

    private static String TAG="sdfg";

    public static String postJSONtoInternet(JSONObject mjsonobject,String url1){
        try{
            StringEntity se = new StringEntity( mjsonobject.toString());
            HttpClient httpClient=new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,20000);
            HttpPost httpPost=new HttpPost(url1);
            httpPost.setEntity(se);
            HttpResponse httpResponse=httpClient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode()!=200){
                Log.d(TAG, "-----------------------!200_----------->>>>>>"+"--------------!200-----------------"+httpResponse.getStatusLine().getStatusCode()+"\n"+EntityUtils.toString(httpResponse.getEntity(),"UTF-8"));

            }else {
                String result= EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
                return result;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return null;

    }




    public static String posttoInternet(String url1){
        try{
            HttpClient httpClient=new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,20000);
            HttpGet request = new HttpGet(url1);
            HttpResponse httpResponse = httpClient.execute(request);
            if(httpResponse.getStatusLine().getStatusCode()!=200){
                Log.d(TAG, "-----------获取入口信息------------!200_----------->>>>>>"+"--------------!200-----------------"+httpResponse.getStatusLine().getStatusCode()+"sdfs"+url1);

            }else {
                String result= EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
                return result;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return null;
    }
/*
    public static String getApiEntry_json(String correct_url){

        String url=getCurrentApiEntryRoot()+correct_url+"?device=android&icon_size="+MyApplication.Metrics_level;

        return url;
    }


    public static String getApiEntry(String correct_url){

        String url=getCurrentApiEntryRoot()+correct_url+"?device=android&lang="+ MyApplication.lang_name+"&_s="+getsid();

        return url;
    }



    public static String getCurrentApiEntryRoot(){
        if (MyApplication.entry_perfect==null)
            return getDefaultEntryRoot();
        else {
            String url=MyApplication.entry_perfect;
            return url;
        }

    }


    public static String getDefaultEntryRoot(){

        return "http://devace.sinaapp.com/ace_mobile/"; //dev

       // return "http://58.96.171.154/ace_mobile/"; //live

    }


    public static String[] getinit_entry_array(){

     return MyApplication.init_entry_dev_array;


     //   return MyApplication.init_entry_live_array;
    }


    public static String getsid(){
        return MyApplication.sid;
    }


*/

    public static void SendWebsiteByIntent(Context mcontext,Uri url,Class mclass){
        boolean flag = false;
        ConnectivityManager cwjManager = (ConnectivityManager)mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager.getActiveNetworkInfo() != null)
            flag = cwjManager.getActiveNetworkInfo().isAvailable();
        if (flag){
            Intent intent = new Intent(mcontext,mclass);
            intent.setData(url);
            mcontext.startActivity(intent);}
        else {
            Toast.makeText(mcontext, R.string.Login_Network_Status_unavailable, Toast.LENGTH_LONG).show();
        }
    }

    public static void SendWebsiteByIntent_with_title(Context context,Uri url,Class mclass,String text){
        boolean flag = false;
        ConnectivityManager cwjManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager.getActiveNetworkInfo() != null)
            flag = cwjManager.getActiveNetworkInfo().isAvailable();
        if (flag){
            Intent intent = new Intent(context,mclass);
            intent.putExtra(Extra_openwebsite_key,text);
            intent.setData(url);
            context.startActivity(intent);}
        else {
            Toast.makeText(context, R.string.Login_Network_Status_unavailable, Toast.LENGTH_LONG).show();
        }

    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        return line;
    }

}
