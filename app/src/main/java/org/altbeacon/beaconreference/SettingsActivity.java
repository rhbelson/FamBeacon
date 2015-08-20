package org.altbeacon.beaconreference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class SettingsActivity extends Activity {
    String name;
    String uuid;
    String major;
    String minor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        EditText mn=(EditText)findViewById(R.id.meaningfulname);
        EditText u16b=(EditText)findViewById(R.id.uuid16byte);
        EditText maj=(EditText)findViewById(R.id.major);
        EditText min=(EditText)findViewById(R.id.minor);
        name=String.valueOf(mn.getText());
        uuid=String.valueOf(u16b.getText());
        major=String.valueOf(maj.getText());
        minor=String.valueOf(min.getText());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_done) {
            Intent i=new Intent(this,HomeActivity.class);
            i.putExtra("name",name);
            i.putExtra("uuid",uuid);
            i.putExtra("major",major);
            i.putExtra("minor",minor);
            this.startActivity(i);
            return true;
        }

        if (id == R.id.action_info) {
            Intent i=new Intent(this,SettingsActivity.class);
            this.startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
