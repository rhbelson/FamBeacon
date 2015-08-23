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
import android.widget.Button;
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
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.w3c.dom.Text;

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
    private RegionBootstrap regionBootstrap;
    private BeaconManager mBeaconManager;
    private HashMap<String, Beacon> trackedBeacons = new HashMap<String, Beacon>();
    private HashMap<String, Date> trackedBeaconDates = new HashMap<String, Date>();
    private boolean mRangingCallbackTimerActive = false;
    private Date mLastRangingCallbackDate = null;
    private android.os.Handler mHandler = null;
    public ArrayList<Beacon> sortedBeacons;

    String beacon_name;
    String beacon_uuid;
    String beacon_major;
    String beacon_minor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Region region = new Region("com.example.myapp.boostrapRegion", null, null, null);
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mBeaconManager.bind(this);
        Log.d(TAG, "Reached onCreate");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            beacon_name = extras.getString("name");
            beacon_uuid = extras.getString("uuid");
            beacon_major = extras.getString("major");
            beacon_minor = extras.getString("minor");
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
        int id = item.getItemId();
        if (id == R.id.action_home) {
            Intent i = new Intent(this, HomeActivity.class);
            this.startActivity(i);
            return true;
        }
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            this.startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getBeaconKey(Beacon beacon) {
        return beacon.getId1() + "," + beacon.getId2() + "," + beacon.getId3();
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.d(TAG, "OBSC: Reached onBeaconServiceConnect");
        mBeaconManager.setRangeNotifier(new RangeNotifier() {

            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Log.d(TAG, "OBSC: Reached didRangeBeaconsInRegion" + String.valueOf(beacons.size()));
                if (beacons.size() > 0) {
                    for (Beacon beacon : beacons) {//Beacon x: beacons
                        trackedBeacons.put(getBeaconKey(beacon), beacon);
                        trackedBeaconDates.put(getBeaconKey(beacon), new Date());
                    }
                    runOnUiThread(
                            new Runnable() {
                                public void run() {
                                    beaconfound();
                                }
                            });
                }
            }
        });
        mBeaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");
                runOnUiThread(
                        new Runnable() {
                            public void run() {
                                beaconentered();
                            }
                        });
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







    public void beaconfound() {
        sortedBeacons = new ArrayList<Beacon>(trackedBeacons.values());
        Collections.sort(sortedBeacons, new Comparator<Beacon>() {
            @Override
            public int compare(Beacon beacon1, Beacon beacon2) {
                int result = beacon1.getId1().compareTo(beacon2.getId1());
                if (result == 0) {
                    result = beacon1.getId2().compareTo(beacon2.getId2());
                }
                if (result == 0) {
                    result = beacon1.getId3().compareTo(beacon2.getId3());
                }
                return result;
            }
        });

            if (sortedBeacons.size()>0) {
                Log.d(TAG, "OBSC:Beacon Found");
                TextView home_default_text=(TextView)findViewById(R.id.home_default_text);
                home_default_text.setVisibility(View.INVISIBLE);
                Toast.makeText(HomeActivity.this,"A beacon has been spotted! If you have not yet configured your beacon, please do so by visiting the settings page",Toast.LENGTH_LONG).show();
            }

}

        public void beaconentered() {
            for (Beacon beacon : sortedBeacons) {
                if (String.valueOf(beacon.getId1()) == beacon_uuid && String.valueOf(beacon.getId2()) == beacon_major && String.valueOf(beacon.getId3()) == beacon_minor) {
                    Log.d(TAG, "OBSC:Beacon entered region");
                    BeaconDetectedDialogFragment myFragment = new BeaconDetectedDialogFragment();
                    myFragment.show(getFragmentManager(), "theDialog");
                }
            }
        }
}


