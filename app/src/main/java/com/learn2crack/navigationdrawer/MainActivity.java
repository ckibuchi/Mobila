package com.learn2crack.navigationdrawer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.learn2crack.navigationdrawer.Utils.WebClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
     RippleBackground rippleBackground;//=(RippleBackground)findViewById(R.id.content);
    ImageView imageView;
     FloatingActionButton fab;
    JSONObject data = new JSONObject();
    RequestParams params = new RequestParams();
    private static MainActivity mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initNavigationDrawer();
        initFab();
       TheBigButton();


    }

    public void initFab()
    {
          fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Click action
                /*Intent intent = new Intent(MainActivity.this, GenerateQRCodeActivity.class);
                startActivity(intent);*/


                createTicket();
                try {
                    String result = new WebClient().execute(data.toString(), "tickets", "saveorupdate").get();
                    Log.d("RESULTS",result);
                }
                catch(Exception e)
                {
                    showSnack(e.getMessage(),2);
                }



            }
        });
    }
    public void TheBigButton()
    {
        rippleBackground=(RippleBackground)findViewById(R.id.content);
        imageView=(ImageView)findViewById(R.id.centerImage);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();


            }
        });
    }
    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id){
                    case R.id.home:
                        Toast.makeText(getApplicationContext(),"Home",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.settings:
                        Toast.makeText(getApplicationContext(),"Settings",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.trash:
                        Toast.makeText(getApplicationContext(),"Trash",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.logout:
                        finish();

                }
                return true;
            }
        });
        View header = navigationView.getHeaderView(0);
        TextView tv_email = (TextView)header.findViewById(R.id.tv_email);
        tv_email.setText("chriskibuchics@gmail.com");
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }


    private void startScan() {
        /**
         * Build a new MaterialBarcodeScanner
         */
        params=new RequestParams();
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(MainActivity.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(final Barcode barcode) {
                        prepareTicketSearch(barcode.rawValue);
                        rippleBackground.startRippleAnimation();
                        try {

                            String result = new WebClient().execute(data.toString(), "tickets", "getTicketDetails").get();
                            Log.d("RESULTS", result);

                            JSONObject results=new JSONObject(result);
                            if(results.has("status"))
                            {
                                Log.d("STATUS ",results.getJSONArray("status").getString(0));

                                if(results.getJSONArray("status").getString(0).equalsIgnoreCase("success")) {
                                    showSnack("Success!", 0);

                                    JSONObject ticket = new JSONObject(results.getJSONArray("ticket").getString(0));
                                    Log.d("ticket===>", ticket.toString());
                                    Intent intent=new Intent(MainActivity.this,PaymentActivity.class);
                                    startActivity(intent);
                                }
                                else
                                {

                                    showSnack(results.getJSONArray("message").getString(0), 2);
                                }

                            }
                            else
                            {
                                showSnack("Error occured!",2);
                            }


                            // showSnack("Ticket: "+ticket.getString("scancode"),1);
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                            showSnack(e.getMessage(),2);
                        }
                        rippleBackground.stopRippleAnimation();
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }



    public void showSnack(String message,final int status)
    {
        Snackbar snackBar=  Snackbar.make(fab, message, Snackbar.LENGTH_LONG).setAction("Click", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Snackbar.make(fab, "This is Child Snackbar", Snackbar.LENGTH_LONG).show();
            }
        });
       if(status==0) {
        snackBar.getView().setBackgroundColor(Color.GREEN);
    }
         else if(status==1) {
            snackBar.getView().setBackgroundColor(Color.RED);
        }
        else
        {
            snackBar.getView().setBackgroundColor(Color.YELLOW);
            TextView textView = (TextView) snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.BLACK);
        }
        snackBar.show();

    }


    public static synchronized MainActivity getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    public void createTicket()
    {
        try {
            JSONObject objectData = new JSONObject();
            data.put("ticket", objectData);

            objectData.put("siteId", 1);
            objectData.put("status", "IN");
        }
        catch(Exception e)
        {}
    }
    public void prepareTicketSearch(String scancode)
    {
        try {
            JSONObject objectData = new JSONObject();
            data.put("ticket", objectData);

            objectData.put("siteId", 1);
            objectData.put("scancode",scancode);
        }
        catch(Exception e)
        {}
    }
}
