package cn.hydewww.xposeddemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String CREATE_SWITCH = "create table Switch ("
            + "id integer primary key autoincrement, "
            + "state integer)";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SWITCH);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVers, int newVers) {
        db.execSQL("drop table if exists Switch");
    }

}
