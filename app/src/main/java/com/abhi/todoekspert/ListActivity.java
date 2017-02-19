package com.abhi.todoekspert;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 123;
    private ToDoApplication.LoginManager loginManager;
    ListView list;
    private static final String TAG=ListActivity.class.getSimpleName();
    private String result="hellooo";
    public SimpleCursorAdapter simpleCursorAdapter;
    //TodoDao todoDao=((ToDoApplication)getApplication()).getToDoDao();

    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TodoDao todoDao=((ToDoApplication)getApplication()).getToDoDao();
            Cursor cursor=todoDao.query(loginManager.getUserId(),false);
            simpleCursorAdapter.changeCursor(cursor);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter("com.abhi,todoekspert.ACTION_REFRESH"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ToDoApplication todoApplication=(ToDoApplication)getApplication();
        loginManager=todoApplication.getLoginManager();
        if(loginManager.isUserNotLogged())
        {
            goToLogin();
            return;
        }
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        list = (ListView)findViewById(R.id.list);
        registerForContextMenu(list);


        String[] from=new String[]{TodoDao.C_CONTENT,TodoDao.C_DONE};
        CheckBox checkbox= (CheckBox) findViewById(R.id.item_cbox);
        int[] to=new int[]{R.id.item_cbox,R.id.item_cbox};
        simpleCursorAdapter=new SimpleCursorAdapter(getApplicationContext(),R.layout.content_main,null,from,to,0);
        SimpleCursorAdapter.ViewBinder viewBinder= new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int i) {
                if(i==cursor.getColumnIndex(TodoDao.C_DONE)){
                    CheckBox checkBox= (CheckBox) view;
                    int value=cursor.getInt(i);
                    checkBox.setChecked(value>0);
                    return true;
                }
                return false;
            }
        };
        simpleCursorAdapter.setViewBinder(viewBinder);
        list.setAdapter(simpleCursorAdapter);
        //refreshCursor();
        TodoDao todoDao=((ToDoApplication)getApplication()).getToDoDao();
        Cursor cursor=todoDao.query(loginManager.getUserId(),false);
        simpleCursorAdapter.changeCursor(cursor);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),"Delete",Toast.LENGTH_SHORT).show();
                return false;
            }
        });


    }
   // private void refreshCursor(){
     //   Cursor cursor=todoDao.query(loginManager.getUserId(),false);
       // simpleCursorAdapter.changeCursor(cursor);
    //}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //if(v.getId()==R.id.list)
        //Toast.makeText(getApplicationContext(),"Delete",Toast.LENGTH_SHORT).show();
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_longpress,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            loginManager.logout();
            goToLogin();
            return true;
        }
        else if(id==R.id.action_add) {
            Intent intent=new Intent(getApplicationContext(),AddToDoList.class);
            startActivityForResult(intent, REQUEST_CODE);
            return true;

        }
        else if(id==R.id.action_refresh){
            AsyncTask<Void,Void,List<Todo>> asyncTask=new AsyncTask<Void, Void, List<Todo>>() {


                @Override
                protected List<Todo> doInBackground(Void... params) {

                    return null;
                }

                @Override
                protected void onPostExecute(List<Todo> todos) {
                    super.onPostExecute(todos);
                         TodoDao todoDao = ((ToDoApplication) getApplication()).getToDoDao();
                        Cursor cursor=todoDao.query(loginManager.getUserId(),false);
                        simpleCursorAdapter.changeCursor(cursor);
                        Intent intent=new Intent(getApplicationContext(),RefreshIntentService.class);
                        startService(intent);
                        //refreshCursor();


                }
            };
            asyncTask.execute();
        }

        return super.onOptionsItemSelected(item);
    }
    public void goToLogin(){
        finish();
        Intent intent=new Intent(getApplicationContext(),Login.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE)
        if(resultCode==RESULT_OK) {

            //refreshCursor();
        }
        else if(resultCode==RESULT_CANCELED)
            Toast.makeText(getApplicationContext(),"Not Added",Toast.LENGTH_SHORT).show();
    }



}
