package com.example.sqliteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private EditText editTextKey, editTextValue, editSearchKey;
    private TestOpenHelper helper;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextKey = findViewById(R.id.edit_text_key);
        editTextValue = findViewById(R.id.edit_text_value);
        editSearchKey = findViewById(R.id.search_form);

        textView = findViewById(R.id.text_view);

        Button insertButton = findViewById(R.id.button_insert);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(helper == null){
                    helper = new TestOpenHelper(getApplicationContext());
                }

                if(db == null){
                    db = helper.getWritableDatabase();
                }

                String key = editTextKey.getText().toString();
                String value = editTextValue.getText().toString();

                // 価格は整数を想定
                insertData(db, key, Integer.valueOf(value));
            }
        });

        Button readButton = findViewById(R.id.button_read);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readData();
            }
        });

        //全データ削除
        Button allDeleteButton= findViewById(R.id.alldeletebutton);
        allDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.delete("testdb", null, null);
            }
        });

        //データの検索
        Button searchBtn = findViewById(R.id.searchbutton);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TextViewのクリア
                textView.setText("SEARCH RESULTS:\n");
                //検索文字列の取得
                String str = editSearchKey.getText().toString();

                //検索
                Cursor c = db.query("testdb", new String[]{"company", "stockprice"}, "company=?", new String[]{str}, null, null, null);
                boolean mov = c.moveToFirst();
                while(mov){
                    textView.append(c.getInt(0)+": "+c.getString(1)+"\n");
                    mov = c.moveToNext();
                }
                c.close();
            }
        });


        //データの更新
        Button updateBtn = findViewById(R.id.updatebutton);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //nameとageを取得
                String company = editTextKey.getText().toString();
                String price = editTextValue.getText().toString();

                //ContentValuesに値を入れる
                ContentValues val = new ContentValues();
                val.put("stockprice", price);

                //データベースを更新
                db.update("testdb", val, "company=?", new String[]{company});
            }
        });


    }

    //データ表示
    private void readData(){
        if(helper == null){
            helper = new TestOpenHelper(getApplicationContext());
        }

        if(db == null){
            db = helper.getReadableDatabase();
        }
        Log.d("debug","**********Cursor");

        Cursor cursor = db.query(
                "testdb",
                new String[] { "company", "stockprice" },
                null,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        StringBuilder sbuilder = new StringBuilder();

        for (int i = 0; i < cursor.getCount(); i++) {
            sbuilder.append(cursor.getString(0));
            sbuilder.append(": ");
            sbuilder.append(cursor.getInt(1));
            sbuilder.append("\n");
            cursor.moveToNext();
        }

        // 忘れずに！
        cursor.close();

        Log.d("debug","**********"+sbuilder.toString());
        textView.setText(sbuilder.toString());
    }

    private void insertData(SQLiteDatabase db, String com, int price){

        ContentValues values = new ContentValues();
        values.put("company", com);
        values.put("stockprice", price);

        db.insert("testdb", null, values);
    }
}