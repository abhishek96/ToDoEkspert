package com.abhi.todoekspert;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * Created by SONY on 7/15/2016.
 */
public class ToDoApplication extends Application {
    public LoginManager getLoginManager() {
        return loginManager;
    }

    private LoginManager loginManager;
    private TodoDao todoDao;

    @Override
    public void onCreate() {
        super.onCreate();
        loginManager=new LoginManager();
        todoDao=new TodoDao(getApplicationContext());
    }
    public TodoDao getToDoDao() {
        return todoDao;
    }

    class LoginManager {
        private SharedPreferences sharedPreferences;

        public String getUserId() {
            return userId;
        }

        private String userId;
        private String sessionToken;
        public static final String SESSION_TOKEN="sessionToken",OBJECT_ID="objectId";
        public String getSessionToken() {
            return sessionToken;
        }



        public LoginManager(){
            sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            this.sessionToken=sharedPreferences.getString(SESSION_TOKEN,null);
            this.userId=sharedPreferences.getString(OBJECT_ID,null);
        }
        public boolean isUserNotLogged(){
            return TextUtils.isEmpty(sessionToken)||TextUtils.isEmpty(userId);
        }
        public void saveLogin(String sessionToken,String userId){
            this.sessionToken=sessionToken;
            this.userId=userId;
            SharedPreferences.Editor edit=sharedPreferences.edit();
            edit.putString(SESSION_TOKEN,sessionToken);
            edit.putString(OBJECT_ID,userId);
            edit.apply();
        }
        public void logout(){
            sessionToken=null;
            userId=null;
            SharedPreferences.Editor edit=sharedPreferences.edit();
            edit.remove(SESSION_TOKEN);
            edit.remove(OBJECT_ID);
            edit.apply();
        }
    }
}
