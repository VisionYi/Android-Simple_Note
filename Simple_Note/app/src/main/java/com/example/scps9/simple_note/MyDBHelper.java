package com.example.scps9.simple_note;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by scps9 on 2015/12/24.
 */
public class MyDBHelper extends SQLiteOpenHelper {
    //public Item_DAO itemDAO;

    // 資料庫名稱
    public static final String DATABASE_NAME = "My_Database";
    // 資料庫版本，資料結構改變的時候要更改這個數字，下一版就+1
    public static final int VERSION = 1;

    //參數1: Context物件。
    //參數2: 資料庫名稱（資料庫檔案名稱），其儲存在『\data\data\<套件名稱>\資料庫\』目錄。
    //參數3: 建立Cursor物件，預設null。
    //參數4: 版本整數值，從1開始。
    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null,VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 建立應用程式需要的表格
        // Item_DAO.CREATE_TABLE需要是 public 全域變數
        db.execSQL(Item_DAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 刪除原有的表格
        db.execSQL("DROP TABLE IF EXISTS" + Item_DAO.TABLE_NAME);
        // 呼叫onCreate建立新版的表格
        onCreate(db);
    }
}
