package org.games.geofox;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.games.geofox.common.CommonUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class NewGameActivity extends AppCompatActivity {

    private Context context;
    private String version;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;

        setContentView(R.layout.activity_new_game);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_new_game, menu);
//        return true;
//    }

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
        String gamename = gamenameEditText.getText().toString().trim();
        if (gamename.isEmpty()) {
            gamenameEditText.setError("You must fill this field.");
            isError = true;
        }
        if (!(CommonUtils.isAlpha(gamename))) {
            gamenameEditText.setError("This field must have only latin letters and numbers.");
            isError = true;
        }



        EditText passwordEditText = (EditText) findViewById(R.id.gamePassword);
        String password = passwordEditText.getText().toString().trim();
        if (password.isEmpty()) {
            passwordEditText.setError("You must fill this field.");
            isError = true;
        }

        if (!(CommonUtils.isAlpha(password))) {
            passwordEditText.setError("This field must have only latin letters and numbers.");
            isError = true;
        }


        EditText usernameEditText = (EditText) findViewById(R.id.username);
        String username = usernameEditText.getText().toString().trim();
        if (username.isEmpty()) {
            usernameEditText.setError("You must fill this field.");
            isError = true;
        }
        if (!(CommonUtils.isAlpha(username))) {
            usernameEditText.setError("This field must have only latin letters and numbers.");
            isError = true;
        }


        if (!(isError))
        {
            findViewById(R.id.typ).setEnabled(false);
            gamenameEditText.setError(null);
            usernameEditText.setError(null);
            passwordEditText.setError(null);

            gamenameEditText.setEnabled(false);
            passwordEditText.setEnabled(false);
            usernameEditText.setEnabled(false);
            post("1", gamename, password, username);

            findViewById(R.id.okButton).setEnabled(false);
            findViewById(R.id.cancelButton).setEnabled(false);
        }



    }

    /**
     * post - Send create new game request
     *
     * @param typ - game type (now it is always 1)
     * @param name - game name
     * @param password - game password
     * @param username - fox username
     */
    private void post(String typ, String name, String password, String username){

        Properties prop = new Properties();
        AssetManager assetManager = getAssets();
        try {
        InputStream inputStream =
                assetManager.open("main.properties");
            prop.load(inputStream);
        } catch (IOException e) {
            Log.d("abd", "Error: " + e.getMessage());
        }
            url = prop.getProperty("service.url");
            version = prop.getProperty("service.version");

            JSONObject obj = new JSONObject();
            JSONObject obj1 = new JSONObject();
            try {
                obj.put("gametyp", typ);
                obj.put("gameExist", "0");
                obj.put("name", name);
                obj.put("password", password);
                obj.put("user", username);
                obj1.put("game",obj );

            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestQueue queue = Volley.newRequestQueue(this.context);
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT, url, obj1, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Toast toast = Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG);
                                toast.show();
                                findViewById(R.id.typ).setEnabled(true);
                                findViewById(R.id.gameName).setEnabled(true);
                                findViewById(R.id.gamePassword).setEnabled(true);
                                findViewById(R.id.username).setEnabled(true);
                                findViewById(R.id.okButton).setEnabled(true);
                                findViewById(R.id.cancelButton).setEnabled(true);

                                Intent intent = new Intent(context, MapsActivity.class);
                                intent.putExtra("version", version);
                                intent.putExtra("url", url);
                                try {
                                    intent.putExtra("sessionid", response.getString("gameid"));
                                    intent.putExtra("serviceinterval", response.getInt("serviceinterval"));
                                    intent.putExtra("servicefirstrun", response.getInt("servicefirstrun"));
                                    intent.putExtra("gamelength", response.getInt("gamelength"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                startActivity(intent);
                            }
                }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error.networkResponse  != null) {
                                    if (error.networkResponse.headers.get("error") == null) {
                                        Toast toast = Toast.makeText(getApplicationContext(), "Unexpected error. Please contact the game support.", Toast.LENGTH_LONG);
                                        toast.show();
                                    } else {
                                        String errorString = error.networkResponse.headers.get("error");
                                        int id = getResources().getIdentifier(errorString, "string", getPackageName());
                                        String value = id == 0 ? "" : (String) getResources().getText(id);


                                        EditText gamenameEditText = (EditText) findViewById(R.id.gameName);
                                        EditText usernameEditText = (EditText) findViewById(R.id.username);
                                        EditText passwordEditText = (EditText) findViewById(R.id.gamePassword);
                                        switch (errorString) {
                                            case "ERROR_ERR1":
                                                usernameEditText.setError(value);
                                                break;
                                            case "ERROR_ERR2":
                                                gamenameEditText.setError(value);
                                                break;
                                            case "ERROR_ERR3":
                                                gamenameEditText.setError(value);
                                                passwordEditText.setError(value);
                                                break;
                                            default:
                                                Toast toast = Toast.makeText(getApplicationContext(),value, Toast.LENGTH_LONG);
                                                toast.show();
                                        }
                                    }
                                    Log.d("abd", "Error: " + error
                                            + ">>" + error.networkResponse.statusCode
                                            + ">>" + error.getCause()
                                            + ">>" + error.getMessage());
                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Unexpected error. Conection Timeout. Please contact the game support.", Toast.LENGTH_LONG);
                                    toast.show();
                                    Log.d("abd", "Unexpected error. Conection Timeout. ");

                                }


                                findViewById(R.id.typ).setEnabled(true);
                                findViewById(R.id.gameName).setEnabled(true);
                                findViewById(R.id.gamePassword).setEnabled(true);
                                findViewById(R.id.username).setEnabled(true);
                                findViewById(R.id.okButton).setEnabled(true);
                                findViewById(R.id.cancelButton).setEnabled(true);
                            }


                        }

                ){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("version", version);
                    return params;
                }};
            queue.add(jsObjRequest);

    }
}
