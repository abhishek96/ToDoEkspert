package com.abhi.todoekspert;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by SONY on 8/5/2016.
 */
public class TodoDao  {
    public static final String C_ID = "_id";
    public static final String C_CONTENT = "content";
    public static final String C_DONE = "done";
    public static final String C_USER_ID = "user_id";
    public static final String C_CREATED_AT = "created_at";
    public static final String C_UPDATED_AT = "updated_at";
    public static final String TABLE_NAME = "todos";
    public static final String DB_NAME = "todo.db";
    public static final int DB_VERSION = 1;
    private DBHelper dbHelper;



    public TodoDao(Context context){
        dbHelper=new DBHelper(context);
    }
    public void insertOrUpdate(Todo todo){
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(C_ID,todo.objectId);
        values.put(C_CONTENT,todo.content);
        values.put(C_DONE,todo.done);
        values.put(C_CREATED_AT,todo.createdAt.getTime());
        values.put(C_UPDATED_AT, todo.updatedAt.getTime());
        values.put(C_USER_ID,todo.userId);
        database.insertWithOnConflict(TABLE_NAME,null,values,SQLiteDatabase.CONFLICT_REPLACE);
    }
    public Cursor query(String userId,boolean sortAsc){
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        return db.query(TABLE_NAME,null,String.format("%s=?",C_USER_ID),new String[]{userId},null,null
                ,String.format("%s %s",C_UPDATED_AT,sortAsc?"ASC":"DESC"));

    }






    public static class DBHelper extends SQLiteOpenHelper{

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }
        public static final String TAG = DBHelper.class.getSimpleName();

        @Override
        public void onCreate(SQLiteDatabase db) {
        String sql=String.format("CREATE TABLE %s "+"(%s TEXT PRIMARY KEY NOT NULL,%s TEXT, %s INT, %s INT,%s INT,"+"%s TEXT)",TABLE_NAME,C_ID,C_CONTENT,C_DONE,C_CREATED_AT,C_UPDATED_AT,C_USER_ID);
            Log.d(TAG, "onCreate sql" + sql);
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s",TABLE_NAME));
            onCreate(db);
        }
    }
    public long getLatestCreatedAtTime(String userId){
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        try{
            Cursor cursor=db.query(TABLE_NAME,new String[] {String.format("max(%s)",C_CREATED_AT)},
                    String.format("%s=?",C_USER_ID),new String[]{userId},null,null,null);
            try {
                return cursor.moveToNext()?cursor.getLong(0):Long.MIN_VALUE;
            }finally {
                cursor.close();
            }
        }finally {
            db.close();
        }
    }
}
