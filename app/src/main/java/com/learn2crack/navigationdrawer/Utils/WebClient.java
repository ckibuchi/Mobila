package com.learn2crack.navigationdrawer.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.learn2crack.navigationdrawer.MainActivity;
import com.learn2crack.navigationdrawer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Consts;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.params.ClientPNames;
import cz.msebera.android.httpclient.client.params.CookiePolicy;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.protocol.HttpContext;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by ckibuchi on 11/2/2016.
 */


public class WebClient  extends AsyncTask<String, Void, String> {

    // Required initialization

    private final HttpClient Client = new DefaultHttpClient();
    private String Content;
    private String ACTION;
    JSONObject data = null;//new JSONObject();
    private String Error = null;
    private ProgressDialog Dialog = new ProgressDialog(MainActivity.getInstance());

    String path=Constants.SERVER_URL;

    int sizeData = 0;

    protected void onPreExecute() {
        // NOTE: You can call UI Element here.

        //Start Progress Dialog (Message)

        Dialog.setMessage("Please wait..");
        Dialog.show();

    }

    // Call after onPreExecute method
    protected String doInBackground(String... params) {
       try{ HttpClient httpClient = new DefaultHttpClient();

          if(params.length>0)
          {   int i=0;
              for(String param:params)
              {

                  if(i==0)
                  {
                    data=new JSONObject(param);
                  }
                  else {
                      path += "/" + param;
                  }
                  i+=1;
              }

          }
           path+="/";

       HttpPost httpPost = new HttpPost(path);

        httpPost.setHeader("content-type", "application/json");



        StringEntity entity = new StringEntity(data.toString(), HTTP.UTF_8);
        httpPost.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPost);
           Log.d("RESPONSE: ",response.toString());
        Content = EntityUtils.toString(response.getEntity());
           Log.d("WebClient: ", Content);

    }
       catch(Exception e)
       {
           e.printStackTrace();
           Log.e("ERROR ",e.getMessage());
       }
        Dialog.dismiss();
        return Content;

    }

    protected String onPostExecute(Void unused) {
        // NOTE: You can call UI Element here.

        // Close progress dialog


       return Content;

        }
    }

