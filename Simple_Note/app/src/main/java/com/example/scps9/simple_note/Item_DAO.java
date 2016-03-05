package com.example.scps9.simple_note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by scps9 on 2015/12/25.
 *
 * 我的Item的 Data Access Object
 */
public class Item_DAO {
    public MyDBHelper myDBHelper;

    // 表格名稱
    public static final String TABLE_NAME = "item";
    // 編號欄位名稱
    public static final String KEY_ID = "_id";

    public static final String DATETIME = "datetime";               //型態為 long
    public static final String LASTMODIFY = "lastModify";           //型態為 long
    public static final String STARSTYLE = "starStyle";             //型態為 int
    public static final String TITLE = "title";                     //型態為 string
    public static final String CONTENT = "content";                 //型態為 string
    public static final String LABEL = "label";                     //型態為 string
    public static final String PICTURE = "picture";                 //型態為 byte[]

    public static final String PICTURE_EX = "picture_ex";           //型態為 string

    //宣告上面的變數 建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" + KEY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    DATETIME + " INTEGER, " +
                    LASTMODIFY + " INTEGER, " +
                    STARSTYLE + " INTEGER, " +
                    TITLE + " TEXT, " +
                    CONTENT + " TEXT, " +
                    LABEL + " TEXT," +
                    PICTURE + " BLOB,"+
                    PICTURE_EX + " TEXT)";

    private SQLiteDatabase db;

    // 開啟可讀寫資料庫
    public Item_DAO(Context context) {
        // 呼叫資料庫
        if ( db == null || !db.isOpen() ){
            myDBHelper= new MyDBHelper(context);
            db = myDBHelper.getWritableDatabase();
        }
    }

    // 關閉資料庫
    public void close() {
        db.close();
    }

    // 新增指定的物件
    public Note_item insert(Note_item item){
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(DATETIME, item.getDatetime());
        cv.put(LASTMODIFY, item.getLastModify());
        cv.put(STARSTYLE, item.getStarStyle());
        cv.put(TITLE, item.getTitle());
        cv.put(CONTENT, item.getContent());
        cv.put(LABEL, item.getLabel());
        cv.put(PICTURE, item.getPicture());
        cv.put(PICTURE_EX, item.getPicture_ex());

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        item.setId(id);
        return item;
    }
    // 更新指定的物件
    public boolean update(Note_item item){
        ContentValues cv = new ContentValues();

        cv.put(DATETIME, item.getDatetime());
        cv.put(LASTMODIFY, item.getLastModify());
        cv.put(STARSTYLE, item.getStarStyle());
        cv.put(TITLE, item.getTitle());
        cv.put(CONTENT, item.getContent());
        cv.put(LABEL, item.getLabel());
        cv.put(PICTURE, item.getPicture());
        cv.put(PICTURE_EX, item.getPicture_ex());

        // 設定修改資料編號,格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + item.getId();

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;   //db.update()成功回傳資料比數,失敗則回傳-1
    }

    // 刪除指定的物件
    public boolean delete(Note_item item){
        String where = KEY_ID + "=" + item.getId();

        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where, null) > 0;      //db.delete()成功回傳資料比數,失敗則回傳-1
    }

    // 把Cursor目前的資料包裝為物件
    public Note_item getRecord(Cursor cursor) {
        Note_item result = new Note_item();

        result.setId(cursor.getLong(0));
        result.setDatetime(cursor.getLong(1));
        result.setLastModify(cursor.getLong(2));
        result.setStarStyle(cursor.getInt(3));
        result.setTitle(cursor.getString(4));
        result.setContent(cursor.getString(5));
        result.setLabel(cursor.getString(6));
        result.setPicture(cursor.getBlob(7));
        result.setPicture_ex(cursor.getString(8));

        return result;
    }

    public List<Note_item> get_sortTitle(){
        List<Note_item> result = new ArrayList<>();

        Cursor cur = db.rawQuery("select * from " + TABLE_NAME + " order by " + TITLE + " , " + DATETIME + " desc;", null);
        while(cur.moveToNext()){
            result.add(getRecord(cur));
        }
        cur.close();
        return result;
    }

    public List<Note_item> get_sortDate(){
        List<Note_item> result = new ArrayList<>();

        Cursor cur = db.rawQuery("select * from " +TABLE_NAME+" order by "+DATETIME +" desc,"+ TITLE +";",null);
        while(cur.moveToNext()){
            result.add(getRecord(cur));
        }
        cur.close();
        return result;
    }
    public List<Note_item> get_sortLabel(){
        List<Note_item> result = new ArrayList<>();

        Cursor cur = db.rawQuery("select * from " +TABLE_NAME+" order by "+LABEL +" , "+ DATETIME + " desc;",null);
        while(cur.moveToNext()){
            result.add(getRecord(cur));
        }
        cur.close();
        return result;
    }
    public List<Note_item> get_search_title (String text){
        List<Note_item> result = new ArrayList<>();

        Cursor cur = db.rawQuery("select * from " + TABLE_NAME + " where " + TITLE + " like '%" + text + "%' or " +
                LABEL + " like '" + text + "%' order by " + TITLE + " , " + LABEL + " , " + DATETIME + " desc;", null);
        while(cur.moveToNext()){
            result.add(getRecord(cur));
        }
        cur.close();
        return result;
    }
    // 取得資料數量
    public int getAll_Count() {
        int result = 0;
        Cursor cursor = db.rawQuery("select count(*) from " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }



    // 讀取所有記事本資料 List<Note_item>
    public List<Note_item> getAll() {
        List<Note_item> result = new ArrayList<>();

        // 執行查詢
        //query(String table, String[] columns, String selection,String[] selectionArgs, String groupBy, String having,String orderBy, String limit)
        //表格名稱 (table)、欄位名稱 (columns)、查詢條件 (selection)、查詢條件的值 (selectionArgs)、欄位群組 (groupBy)、排序方式 (orderBy) 以及回傳資料的筆數限制 (limit)
        Cursor cur = db.query(TABLE_NAME, null, null, null, null, null, null, null);

        //moveToNext()表示移動到下筆資料,有資料回傳true,無資料了回傳false
        while (cur.moveToNext()) {
            result.add(getRecord(cur));
        }
        cur.close();
        return result;
    }

    // 取得指定的資料物件 Note_item
    public Note_item get_Item(long id) {
        Note_item item = null;
        String where = KEY_ID + "=" + id;
        Cursor c_result = db.query(TABLE_NAME, null, where, null, null, null, null, null);

        if (c_result.moveToFirst()) {
            item = getRecord(c_result);
        }
        c_result.close();
        return item;
    }
}
