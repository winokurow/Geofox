package org.games.geofox;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.games.geofox.common.CommonUtils;

import static android.widget.Toast.*;


public class NewGameActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_game);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_game, menu);
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
     / Open Activity 'New Game' after click on the button 'Fox'.
     /
     */
    public void gotoMainActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     / Start new game.
     /
     */
    public void doStartGame(View view) {
        boolean isError = false;
        EditText gamenameEditText = (EditText) findViewById(R.id.gameName);
        String gamename = gamenameEditText.getText().toString();
        if (gamename.isEmpty()) {
            gamenameEditText.setError("You must fill this field.");
            isError = true;
        }
        if (!(CommonUtils.isAlpha(gamename))) {
            gamenameEditText.setError("This field must have only latin letters and numbers.");
            isError = true;
        }



        EditText passwordEditText = (EditText) findViewById(R.id.gamePassword);
        String password = passwordEditText.getText().toString();
        if (password.isEmpty()) {
            passwordEditText.setError("You must fill this field.");
            isError = true;
        }

        if (!(CommonUtils.isAlpha(password))) {
            passwordEditText.setError("This field must have only latin letters and numbers.");
            isError = true;
        }


        EditText usernameEditText = (EditText) findViewById(R.id.username);
        String username = usernameEditText.getText().toString();
        if (username.isEmpty()) {
            usernameEditText.setError("You must fill this field.");
            isError = true;
        }
        if (!(CommonUtils.isAlpha(username))) {
            usernameEditText.setError("This field must have only latin letters and numbers.");
            isError = true;
        }


        if (!(isError)) makeText(this, "Wait for game is created", LENGTH_SHORT).show();


    }
}
