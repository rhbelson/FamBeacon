package org.altbeacon.beaconreference;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.lang.String;
import java.util.HashMap;
import java.util.List;



public class HomeActivity extends Activity implements BeaconConsumer {
    private static final String TAG = "BeaconScanActivity";
    private static final int LOST_BEACON_DISPLAY_MILISECONDS = 5000;
    private RegionBootstrap regionBootstrap;
    private BeaconManager mBeaconManager;
    private HashMap<String, Beacon> trackedBeacons = new HashMap<String, Beacon>();
    private HashMap<String, Date> trackedBeaconDates = new HashMap<String, Date>();
    private String emailtext;
    private String newregion;
    private String newuuid;
    private boolean mRangingCallbackTimerActive = false;
    private Date mLastRangingCallbackDate = null;
    private android.os.Handler mHandler = null;

    String beacon_name;
    String beacon_uuid;
    String beacon_major;
    String beacon_minor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            beacon_name=extras.getString("name");
            beacon_uuid=extras.getString("uuid");
            beacon_major=extras.getString("major");
            beacon_minor=extras.getString("minor");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBeaconManager.unbind(this);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Prevent Going back");
        //Prevent Going Back
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBeaconManager.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        if (id == R.id.action_home) {
            Intent i=new Intent(this,HomeActivity.class);
            this.startActivity(i);
            return true;
        }

        if (id == R.id.action_settings) {
            Intent i=new Intent(this,SettingsActivity.class);
            this.startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");

            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+state);
            }
        });
        }
    }





