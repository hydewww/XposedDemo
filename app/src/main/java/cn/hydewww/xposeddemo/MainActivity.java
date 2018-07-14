package cn.hydewww.xposeddemo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;

    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // db init
        dbHelper = new MyDatabaseHelper(this, "XposedDemo.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from Switch", null);
        if (cursor.moveToFirst()){
            state = cursor.getInt(cursor.getColumnIndex("state"));
        } else {
            db.execSQL("insert into Switch(state) values (?)", new String[] { "1" });
            state = 1;
        }
        cursor.close();
        // switch
        Switch sw = findViewById(R.id.sw);
        sw.setChecked(state == 1);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("update Switch set state = ?", new String[]{ isChecked ? "1" : "0" });
            }
        });
    }

}
