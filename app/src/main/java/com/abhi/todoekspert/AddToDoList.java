package com.abhi.todoekspert;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;


public class AddToDoList extends AppCompatActivity {


    public static final String TODO = "todo";
    private Button button;
    private EditText contentEditText;
    private CheckBox contentCheckBox;
    private ToDoApplication.LoginManager loginManager;
    private TodoDao todoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setProgressBarIndeterminate(true);
        setContentView(R.layout.activity_add_to_do_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        button=(Button)findViewById(R.id.button_add);
        contentEditText=(EditText)findViewById(R.id.edittext_todo);
        contentCheckBox=(CheckBox)findViewById(R.id.checkbox_todo);
        loginManager = ((ToDoApplication) getApplication()).getLoginManager();
        todoDao = ((ToDoApplication) getApplication()).getToDoDao();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ifDone =contentCheckBox.isChecked();
                String content=contentEditText.getText().toString();
                Todo todo=new Todo();
                todo.content=content;
                todo.done=ifDone;
                todo.userId=loginManager.getUserId();
                AsyncTask<Todo,Void,JSONObject> asyncTask=new AsyncTask<Todo, Void, JSONObject>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        setProgressBarVisibility(true);
                    }

                    @Override
                    protected JSONObject doInBackground(Todo...params) {
                        Todo todoToSave=params[0] ;
                        String token=loginManager.getSessionToken();
                        try {
                            String result=HttpUtils.postTodo(todoToSave.toJsonString(), token);
                            return new JSONObject(result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(JSONObject object) {
                        super.onPostExecute(object);
                        setProgressBarVisibility(false);
                        if(object==null){

                        }
                        else if(object.has("error")){
                            Toast.makeText(getApplicationContext(), "Error"+object.optString("error"), Toast.LENGTH_SHORT).show();
                        }
                        else
                        try {
                            Todo todoFromServer=Todo.fromJsonObject(object);
                            todoDao.insertOrUpdate(todoFromServer);
                            setResult(RESULT_OK);
                            finish();
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                };

                asyncTask.execute(todo);


            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
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

}
