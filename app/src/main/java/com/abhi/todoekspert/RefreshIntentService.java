package com.abhi.todoekspert;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by SONY on 8/10/2016.
 */
public class RefreshIntentService extends IntentService {
    private static final String TAG = RefreshIntentService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param // Used to name the worker thread, important only for debugging.
     */
    public RefreshIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        ToDoApplication.LoginManager loginManager = ((ToDoApplication) getApplication()).getLoginManager();
        String result = null;
        try {
            result = HttpUtils.getTodos(loginManager.getSessionToken());
            JSONObject jsonObject = null;
            jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            List<Todo> todos = Todo.fromJsonArray(jsonArray);
            TodoDao todoDao = ((ToDoApplication) getApplication()).getToDoDao();
            long newestTimestamp=todoDao.getLatestCreatedAtTime(((ToDoApplication) getApplication()).getLoginManager().getUserId());
            int newItems=0;
            if (todos != null) {

                for (Todo todo : todos) {

                    Log.d(TAG, todo.toString());
                    todoDao.insertOrUpdate(todo);
                    if(todo.createdAt.getTime()>newestTimestamp)newItems++;
                }
                if(newItems>0)
                    showNotifications(newItems);
            }
            Intent broadcastIntent=new Intent("com.abhi.todoekspert.REFRESH_ACTION");
            sendBroadcast(broadcastIntent);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void showNotifications(int newItems) {
        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder=new Notification.Builder(getApplicationContext());
        builder.setContentTitle("New Todos Have Arrived!");
        builder.setContentText("New Items :" + newItems);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setAutoCancel(true);
        Intent intent=new Intent(getApplicationContext(),ListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification=builder.build();
        notificationManager.notify(1,notification);
    }
}