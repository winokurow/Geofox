package org.games.geofox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.games.geofox.geofox.service.ServiceGPS;

public class EndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        Intent intent = getIntent();
        int status = intent.getIntExtra("status", 1);
        if (status==40) {
            TextView statusText = (TextView) findViewById(R.id.statustext);
            statusText.setText("The Fox is caught.");
        }
        if (status==50) {
            TextView statusText = (TextView) findViewById(R.id.statustext);
            statusText.setText("The Fox has won.");
        }
        if (status==60) {
            TextView statusText = (TextView) findViewById(R.id.statustext);
            statusText.setText("The Fox is lost by timeout.");
        }
        if (status==70) {
            TextView statusText = (TextView) findViewById(R.id.statustext);
            statusText.setText("The hunters is lost by timeout.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_end, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     / Start new game.
     /
     */
    public void doEndGame(View view) {
        Intent intent2 = new Intent(this, ServiceGPS.class);
        this.stopService(intent2);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
