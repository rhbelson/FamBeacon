package org.altbeacon.beaconreference;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
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



public class HomeActivity extends Activity implements BeaconConsumer,RangeNotifier {
    private static final String TAG = "BeaconScanActivity";
    private RegionBootstrap regionBootstrap;
    private BeaconManager mBeaconManager;
    private HashMap<String, Beacon> trackedBeacons = new HashMap<String, Beacon>();
    private HashMap<String, Date> trackedBeaconDates = new HashMap<String, Date>();
    private boolean mRangingCallbackTimerActive = false;
    private Date mLastRangingCallbackDate = null;
    private android.os.Handler mHandler = null;
    public ArrayList<Beacon> sortedBeacons;
    public Boolean enteredregion=false;
    public Boolean exitedregion=false;


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
        Region region = new Region("all-beacons-region", null, null, null);
        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.setRangeNotifier(this);
//        mBeaconManager.setMonitorNotifier(new MonitorNotifier() {
//            @Override
//            public void didEnterRegion(Region region) {
//                Log.i(TAG, "I just saw an beacon for the first time!");
//            }
//
//            @Override
//            public void didExitRegion(Region region) {
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        beaconexited();
//                    }
//                });
//            }
//
//            @Override
//            public void didDetermineStateForRegion(int state, Region region) {
//                Log.i(TAG, "I have just switched from seeing/not seeing beacons: " + state);
//            }
//        });
//
//        try {
//            mBeaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
//        } catch (RemoteException e) {    }
    }


    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
            if (beacons.size()>0) {
                Log.d("TAG", "OBSC:didRangeBeaconsInRegion"+String.valueOf(beacons.size()));
                for (Beacon beacon:beacons) {
                    if (String.valueOf(beacon.getId1()) == beacon_uuid && String.valueOf(beacon.getId2()) == beacon_major && String.valueOf(beacon.getId3()) == beacon_minor) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                beaconentered();
                            }
                        });
                    }
                    if (enteredregion==false) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                //This should really be beaconfound() but for testing purposes I'm leaving beaconentered() there
                                beaconentered();
                            }
                        });
                        sendSMS();
                        enteredregion=true;
                    }
                }
            }
            if (beacons.size()==0) {
                Log.d(TAG,"OBSC: ZERO BEACONS");
                //Notification
                if (exitedregion==false) {
                    beaconexitedregion();
                    new CountDownTimer(10000, 1000) {
                        //Add onClick capability that turns off timer, and add automatic email/text msg in onFinish()
                        public void onTick(long millisUntilFinished) {
                            //Do nothing
                        }

                        public void onFinish() {
                            Toast.makeText(HomeActivity.this, "Beacon exited region 10 seconds ago", Toast.LENGTH_LONG).show();
                        }
                    }.start();
                    exitedregion=true;
                }
                }
    }



    public void beaconfound() {
        Log.d(TAG, "OBSC:Beacon Found");
        Toast.makeText(HomeActivity.this,"A beacon has been spotted! If you have not yet configured your beacon, please do so by visiting the settings page.",Toast.LENGTH_LONG).show();
    }


    public void beaconentered() {
        Log.d(TAG, "OBSC:Beacon entered region");
        BeaconDetectedDialogFragment myFragment = new BeaconDetectedDialogFragment();
        myFragment.show(getFragmentManager(), "theDialog");
        }




    public void beaconexitedregion() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.fambeacon_icon)
                        .setContentTitle("Your beacon exited region")
                        .setContentText("Are you aware that your beacon has exited region. Please confirm you have seen this");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, HomeActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(HomeActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

    public void sendSMS() {
        String messageToSend = "Test";
        String number = "3018019811";
        SmsManager.getDefault().sendTextMessage(number, null, messageToSend, null,null);
    }




}


