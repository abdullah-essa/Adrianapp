package com.hizyaz.adrianapp.models;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB_Sqlit extends SQLiteOpenHelper {

    private static final String DBName = "mdata.db";

    public DB_Sqlit(Context context) {
        super(context, DBName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create Table myTable (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userid TEXT," +
                "username TEXT," +
                "fullname TEXT," +
                "email TEXT," +
                "contact TEXT," +
                "password TEXT," +
                "savedata TEXT)");

        ContentValues contentValues = new ContentValues();
        contentValues.put("userid", "null");
        contentValues.put("username", "null");
        contentValues.put("fullname", "null");
        contentValues.put("email", "null");
        contentValues.put("contact", "null");
        contentValues.put("password", "null");
        contentValues.put("savedata", "0");


        db.insert("myTable", null, contentValues);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS myTable");
        onCreate(db);
    }

    public String get_check() {
        String check = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select savedata from myTable", null);
//        Log.e("Cursor", res.);
        if (res != null && res.moveToFirst()) {
            check = res.getString(res.getColumnIndex("savedata"));
            res.close();
        }
//        res.moveToFirst();
//        check = res.getString(res.getColumnIndex("savedata"));
//        if(res != null)
//        {
//            if (res.moveToFirst())
//            {
//                check = res.getString(res.getColumnIndex("savedata"));
//            }
//        }
        return check;

    }

    /**
     * Return values for a single row with the specified id
     * @return All column values are stored as properties in the ContentValues object
     */
//    public Cursor get(String id) {
    public Cursor get() {

        SQLiteDatabase db = this.getReadableDatabase();
        if (db == null) {
            return null;
        }
//        ContentValues row = new ContentValues();
        Cursor cur = db.rawQuery("select username, fullname,email,contact from myTable where id = ?", new String[]{"1"});
//        if (cur.moveToNext()) {
//            row.put("username", cur.getString(0));
//            row.put("fullname", cur.getInt(1));
//            row.put("email", cur.getString(2));
//            row.put("contact", cur.getInt(3));
//        }
//        cur.close();
//        db.close();
        return cur;
    }
    public String get_userid() {
        String userid;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select userid from myTable", null);
        res.moveToFirst();
        userid = res.getString(res.getColumnIndex("userid"));
        return userid;
    }

    public String get_username() {
        String username;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select username from myTable", null);
        res.moveToFirst();
        username = res.getString(res.getColumnIndex("username"));
        return username;
    }

    public String get_password() {
        String password;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select password from myTable", null);
        res.moveToFirst();
        password = res.getString(res.getColumnIndex("password"));
        return password;
    }

    public Boolean update_data_register(String username, String fullname, String email, String contact, String check_save_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("fullname", fullname);
        contentValues.put("email", email);
        contentValues.put("contact", contact);
        contentValues.put("savedata", check_save_data);

        db.update("myTable", contentValues, "id = ?", new String[]{"1"});
        return true;
    }

    public Boolean update_data_login(String userid, String username, String password, String check_save_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userid", userid);
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("savedata", check_save_data);
        db.update("myTable", contentValues, "id = ?", new String[]{"1"});
        return true;
    }

    public Boolean update_email(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        db.update("myTable", contentValues, "id = ?", new String[]{"1"});
        return true;
    }

    public Boolean update_password(String password1) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("password", password1);
        db.update("myTable", contentValues, "id = ?", new String[]{"1"});
        return true;
    }

    public Boolean update_userid(String user_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userid", user_id);
        db.update("myTable", contentValues, "id = ?", new String[]{"1"});
        return true;
    }
}

