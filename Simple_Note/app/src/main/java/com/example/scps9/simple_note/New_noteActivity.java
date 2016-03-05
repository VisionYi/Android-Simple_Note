package com.example.scps9.simple_note;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Interface.finalName_NoteListView;

import java.util.Date;

public class New_noteActivity extends AppCompatActivity implements finalName_NoteListView {
    private Other_way Other = new Other_way();

    private Toolbar toolbar_note;
    private EditText note_context, note_title ,note_picture_ex;
    private TextView note_modifyDate;
    private ImageView picture ;
    private FloatingActionButton fab;

    private String  titleText,contentText ,picture_ex;
    private Note_item item;
    private final int CAMERA = 1;        //打開照相機
    private final int SELECT_FILE = 2;   //選擇圖片庫

    private Bitmap bitmap;
    private boolean isPicture =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        set_views();
        initial();
        click_short_Listener();

    }

    public void set_views(){
        note_context = (EditText)findViewById(R.id.editText_content);
        note_title =   (EditText)findViewById(R.id.editText_title);
        note_picture_ex = (EditText)findViewById(R.id.editText_picture_ex);
        note_modifyDate =(TextView)findViewById(R.id.textView_modifyDate);
        picture = (ImageView)findViewById(R.id.imageView_picture);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.conf_picture);
        fab = (FloatingActionButton) findViewById(R.id.fab_note);

        toolbar_note = (Toolbar) findViewById(R.id.toolbar_note_1);
        setSupportActionBar(toolbar_note);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void initial(){
        Intent intent = getIntent();

        // 修改記事本
        if(intent.getIntExtra("requestCode",0)==NOTE_MODIFY){

            item=(Note_item) intent.getSerializableExtra("myItem");

            note_title.setText(item.getTitle());
            note_context.setText(item.getContent());
            note_modifyDate.setText("ModifyDate: "+ item.getDayTime_modify());
            note_picture_ex.setText(item.getPicture_ex());

            bitmap = BitmapFactory.decodeByteArray(item.getPicture(), 0, item.getPicture().length);      //從byte轉 bitmap
            picture.setImageBitmap(bitmap);
        }
        else{  //新增筆記本
            item = new Note_item();
            item.setDatetime(new Date().getTime());
            //item.setScreenshot(Other.Bitmap_to_Bytes(bitmap));

            note_modifyDate.setText("Today: " + item.getDate());
            picture.setImageBitmap(bitmap);
        }
    }

    public void click_short_Listener(){
        toolbar_note.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSave();
//                Intent intent = new Intent(New_noteActivity.this,MainActivity.class);
//                startActivity(intent);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentCamera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intentCamera, CAMERA);
            }
        });
    }

    public void setSave(){
        Toast.makeText(this, "已儲存完成", Toast.LENGTH_SHORT).show();
        Intent result = getIntent();

        //result.getAction().equals("com.example.EDIT")
        if(isPicture || result.getIntExtra("requestCode",0)==NOTE_MODIFY) {
            //壓縮過的檔案bitmap
            item.setPicture(Other.Bitmap_to_Bytes(bitmap));
        }else
            item.setPicture(Other.Bitmap_to_Bytes(BitmapFactory.decodeResource(getResources(), R.drawable.conf_listview_image)));

        //bitmap.recycle();
        titleText = note_title.getText().toString();
        contentText = note_context.getText().toString();
        picture_ex = note_picture_ex.getText().toString();

        item.setLastModify(new Date().getTime());
        item.setTitle(titleText);
        item.setContent(contentText);
        item.setPicture_ex(picture_ex);

        result.putExtra("myItem", item);
        setResult(NOTE, result);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA ) {

            Bundle bundle = data.getExtras();
            bitmap = (Bitmap)bundle.get("data");
            picture.setImageBitmap(bitmap);
            isPicture = true;

        } else
        if (requestCode == SELECT_FILE ) {
            Uri uri = data.getData();
            picture.setImageURI(uri);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        toolbar_note.setNavigationIcon(R.drawable.ic_toolbar_back_24p);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item1){
        switch (item1.getItemId()){
            case R.id.item_saveNote:
                setSave();

                break;
//            case R.id.item_addPicture:
//                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
//                break;
        }
        return true;
    }

}

