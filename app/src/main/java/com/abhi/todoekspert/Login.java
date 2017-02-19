package com.abhi.todoekspert;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private EditText usernameEditText,passwordEditText;
    private Button button,loginButton;
    public static final String SESSION_TOKEN="sessionToken",OBJECT_ID="objectId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        button= (Button) findViewById(R.id.button_login);
        usernameEditText= (EditText) findViewById(R.id.edittext_username);
        passwordEditText= (EditText) findViewById(R.id.edittext_password);
        button.setOnClickListener(this);
        loginButton=(Button)findViewById(R.id.button_register);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Sign Up", Toast.LENGTH_SHORT).show();
                Uri uriurl= Uri.parse("http://todoekspert.parseapp.com/");
                Intent launchBrowser=new Intent(Intent.ACTION_VIEW,uriurl);
                startActivity(launchBrowser);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
      String username=usernameEditText.getText().toString();
        String password=passwordEditText.getText().toString();
        boolean isError=false;
        if(TextUtils.isEmpty(username))
        {
            isError=true;
            usernameEditText.setError("Username is required! :/");
        }
       else if(TextUtils.isEmpty(password))
        {
            isError=true;
            usernameEditText.setError("Password is required! :/");
        }
        else
        {
            login(username, password);
        }


    }

    private void login(final String username, final String password) {
        AsyncTask<String, Integer, JSONObject> asyncTask = new AsyncTask<String, Integer, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... params) {
                String username=params[0];
                String password=params[1];
                try {
                    String result=HttpUtils.getLogin(username,password);
                    return new JSONObject(result);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                button.setText(String.valueOf(values[0]));
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                super.onPostExecute(result);
                button= (Button) findViewById(R.id.button_login);
                button.setEnabled(true);
                if(result==null)
                    Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                else if(result.has("error"))
                    Toast.makeText(getApplicationContext(), "Error: "+result.optString("error"), Toast.LENGTH_SHORT).show();
                else {
                    String sessionToken=result.optString(SESSION_TOKEN);
                    String userId=result.optString(OBJECT_ID);
                    ((ToDoApplication)getApplication()).getLoginManager().saveLogin(sessionToken,userId);
                    Toast.makeText(getApplicationContext(), "LoginSuccesful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                button.setEnabled(false);
            }

        };
        asyncTask.execute(username,password);


    }

}
