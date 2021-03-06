package org.altbeacon.beaconreference;

import android.os.Bundle;
import android.os.RemoteException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

/**
 * 
 * @author dyoung
 * @author Matt Tyler
 */
public class MonitoringActivity extends Activity {
	protected static final String TAG = "MonitoringActivity";
	private BeaconManager beaconManager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_monitoring);
		verifyBluetooth();
		logToDisplay("Application just launched");
	}

	public void onRangingClicked(View view) {
		Intent myIntent = new Intent(this, RangingActivity.class);
		this.startActivity(myIntent);
	}

	@Override
	public void onResume() {
		super.onResume();
		((BeaconReferenceApplication) this.getApplicationContext()).setMonitoringActivity(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		((BeaconReferenceApplication) this.getApplicationContext()).setMonitoringActivity(null);
	}

	private void verifyBluetooth() {

		try {
			if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Bluetooth not enabled");
				builder.setMessage("Please enable bluetooth in settings and restart this application.");
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						finish();
						System.exit(0);
					}
				});
				builder.show();
			}
		} catch (RuntimeException e) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Bluetooth LE not available");
			builder.setMessage("Sorry, this device does not support Bluetooth LE.");
			builder.setPositiveButton(android.R.string.ok, null);
			builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					finish();
					System.exit(0);
				}

			});
			builder.show();

		}

	}

	public void logToDisplay(final String line) {
//    	runOnUiThread(new Runnable() {
//    	    public void run() {
//    	    	EditText editText = (EditText)MonitoringActivity.this
//    					.findViewById(R.id.monitoringText);
//       	    	editText.append(line+"\n");
//    	    }
//    	});
//    }

	}
}
