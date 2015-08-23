package org.altbeacon.beaconreference;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;


public class BeaconDetectedDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder theDialog = new AlertDialog.Builder(getActivity());
        theDialog.setTitle("Your Beacon has Entered Region");
        theDialog.setMessage("If you would like to continue monitoring this beacon, click OK. Otherwise, click CANCEL.");
        theDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
       @Override
       public void onClick(DialogInterface dialogInterface, int i) {
           Toast.makeText(getActivity(), "Beacon will continue to be monitored", Toast.LENGTH_SHORT).show();
           }
      });
      theDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            Toast.makeText(getActivity(), "Beacon will no longer be monitored", Toast.LENGTH_SHORT).show();
               }
           });

      return theDialog.create();
    }
}
