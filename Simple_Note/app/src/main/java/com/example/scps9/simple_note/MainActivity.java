package com.example.scps9.simple_note;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Interface.finalName_NoteListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements finalName_NoteListView {
    private Other_way Other = new Other_way();
    private Item_DAO itemDAO;                   //資料庫的使用方法與指令

    private TextView choice_number;
    private Menu changeMenu;
    private FloatingActionButton fab;
    private LinearLayout linearLayout_number;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    private MenuItem searchItem;
    private SearchView search;
    private ItemAdapter itemAdapter;
    private ListViewCompat listView_notes;

    private boolean long_listener=false , selectClick=false ,is_search=false ,search_to_select=false;
    private int choice_count;
    private int sortWay;
    private final int changeStar=1 , changeTitle=2 ,changeLabel =3 ,changeDatetime=4;
    private final int sort_date=5 , sort_label=6 , sort_title=7 , sort_search=8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        define_views();
        initial();
        click_short_Listener();
        click_long_Listener();
    }

    public void define_views(){
        listView_notes = (ListViewCompat)findViewById(R.id.listView_all_notes);
        linearLayout_number=(LinearLayout)findViewById(R.id.Linear_toolbar);
        choice_number=(TextView)findViewById(R.id.textView_number);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void initial(){
        //region ListView 基本作法
//        String[] title = getResources().getStringArray(R.array.Notes_title);
//        ArrayList<HashMap<String,Object>> mylist = new ArrayList<HashMap<String,Object>>();

//        for(int i=0;i<title.length;i++){
//            HashMap<String,Object> it = new HashMap<String,Object>();
//            it.put("picture",R.drawable.conf_picture);
//            it.put("title",title[i]);
//            it.put("time", "2015-12-112");
//            it.put("label","!!筆記本!!!");
//            it.put("star",R.drawable.star_red_32);
//            mylist.add(it);
//        }
//        SimpleAdapter adapter= new SimpleAdapter(this,mylist,R.layout.listview_note,
//                new String[]{"picture","title","time","label","star"},
//                new int[]{R.id.image_list_screenshot,R.id.textView_list_title,R.id.textView_list_date,R.id.textView_list_label,R.id.image_list_star});
//        listView_notes.setAdapter(adapter);
        //endregion

        //設置那3橫槓槓
        toggle = new ActionBarDrawerToggle(this, drawer,toolbar, R.string.drawer_open, R.string.drawer_close);

        //DrawerLayout裡的導航鍵 NavigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                drawer.closeDrawer(GravityCompat.START);            //也等於 drawer.closeDrawers();
                return false;
            }
        });

/**------------------------------------------------------------------------------------------------------------------------------------------------------**/
        //開啟資料庫
        itemDAO = new Item_DAO(this);       // 也等於 Item_ADO(getApplicationContext())
        itemAdapter = new ItemAdapter(this);

        choice_count=0;
        sortWay = sort_date;
        List<Note_item> list_item = itemDAO.get_sortDate();       //資料庫載入 ,照時間排序
        for(int i=0 ; i< list_item.size() ; i++){
            itemAdapter.add(list_item.get(i) ,TYPE_DATE);
        }
        itemAdapter.notifyDataSetChanged();
        list_item.clear();

        listView_notes.setAdapter(itemAdapter);
        listView_notes.setEmptyView(findViewById(R.id.textView_empty_text));

    }

    public void click_short_Listener(){

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this ,New_noteActivity.class);
                intent.putExtra("requestCode",NOTE_ADD);
                startActivityForResult(intent, NOTE_ADD);
            }
        });

        listView_notes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // 參數1是使用者操作的ListView物件
            // 參數2是使用者選擇的項目
            // 參數3是使用者選擇的項目編號，第一個是0
            // 參數4在這裡沒有用途
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!long_listener) {
                    Note_item item = itemAdapter.getItem(position);

                    if (selectClick) {

                        item.setSelect(!item.getSelect());
                        itemAdapter.set(position, item);
                        itemAdapter.notifyDataSetChanged();

                        if (item.getSelect()) choice_count++;
                        else choice_count--;
                        choice_number.setText(String.valueOf(choice_count));

                    } else
                    {
                        if (item.getTitle().compareTo("_noTitle_") == 0) item.setTitle("");

                        Intent intent = new Intent(MainActivity.this ,New_noteActivity.class);
                        // 傳送現在ListView的position
                        intent.putExtra("position", position);
                        intent.putExtra("myItem", item);
                        intent.putExtra("requestCode",NOTE_MODIFY);
                        startActivityForResult(intent, NOTE_MODIFY);
                    }

                } else long_listener = false;
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectClick) {
                    selectClick = false;
                    //changeMenu.clear();
                    //onCreateOptionsMenu(changeMenu);
                    invalidateOptionsMenu();
                } else
                    drawer.openDrawer(GravityCompat.START);
            }
        });
    }

    public void click_long_Listener(){

        listView_notes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                long_listener = true;

                search_to_select = is_search;
                if (is_search) changeMenu.findItem(R.id.item_search).collapseActionView();

                selectClick = true;
                changeMenu.clear();
                onCreateOptionsMenu(changeMenu);
                //invalidateOptionsMenu();

                Note_item item = itemAdapter.getItem(position);
                item.setSelect(true);
                itemAdapter.set(position, item);
                itemAdapter.notifyDataSetChanged();

                choice_count = 1;
                choice_number.setText(String.valueOf(choice_count));

                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        itemDAO = new Item_DAO(this);    //開啟資料庫

        switch (resultCode){
            case NOTE:
                Note_item item = (Note_item)data.getSerializableExtra("myItem");

                if(TextUtils.isEmpty( item.getTitle() ) ){
                    item.setTitle("_noTitle_");
                }

                if(requestCode==NOTE_ADD) {
                    item.setLabel("筆記本");
                    item.setStarStyle(STAR_NO);

                    item = itemDAO.insert(item);            //加入資料庫, 設置itemId與資料庫的_id相同
                    sort_All(sortWay,null);                 //加入list item
                }
                else
                if(requestCode==NOTE_MODIFY){

                    int position = data.getIntExtra("position", -1);           //取得之前所點到listView的position
                    if(position!=-1)
                    {
                        itemDAO.update(item);               //更新資料庫
                        itemAdapter.set(position, item);    //更新list item
                    }
                }
                itemAdapter.notifyDataSetChanged();

                break;
        }
    }

    @Override
    public void onBackPressed(){            //當按下返回鍵觸發
        if(selectClick){
            selectClick = false;
            //changeMenu.clear();
            //onCreateOptionsMenu(changeMenu);
            invalidateOptionsMenu();
        }else
        if( drawer.isDrawerOpen(GravityCompat.START) )
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    protected void onRestart(){
        //開啟資料庫
        itemDAO = new Item_DAO(this);
        super.onRestart();
    }

    @Override
    protected void onStop(){
        itemDAO.close();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(selectClick)
        {
            getMenuInflater().inflate(R.menu.menu_select, menu);
            linearLayout_number.setVisibility(View.VISIBLE);
            getSupportActionBar().setDisplayShowTitleEnabled(false);                //取消actionbar標題
            toolbar.setNavigationIcon(R.drawable.check_done_24);

            fab.setVisibility(View.INVISIBLE);
            itemAdapter.setSelectLook(true, false);

            menu.findItem(R.id.select_allChoice).setVisible(!search_to_select);
            menu.findItem(R.id.select_allCancel).setVisible(!search_to_select);
        } else
        {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            linearLayout_number.setVisibility(View.INVISIBLE);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            toolbar.setNavigationIcon(null);

            fab.setVisibility(View.VISIBLE);
            itemAdapter.setSelectLook(false, false);

            drawer.setDrawerListener(toggle);                               //使用ActionBarDrawerToggle
            toggle.syncState();

            search_to_select = false;
            sort_All(sortWay, null);

            if(sortWay==sort_label)
                menu.findItem(R.id.sort_label).setChecked(true);
            else
            if(sortWay==sort_title)
                menu.findItem(R.id.sort_title).setChecked(true);
            else
                menu.findItem(R.id.sort_datetime).setChecked(true);

            function_search(menu);
        }
        changeMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item){
        switch (item.getItemId()) {
            case R.id.item_select :                                     //主Main的menu item
                choice_count=0;
                choice_number.setText(String.valueOf(choice_count));

                search_to_select = is_search;
                if(is_search)   changeMenu.findItem(R.id.item_search).collapseActionView();

                selectClick=true;
                //changeMenu.clear();
                //onCreateOptionsMenu(changeMenu);
                invalidateOptionsMenu();

                break;
//            case R.id.item_changeColor:
//                break;
            case R.id.sort_datetime:
                item.setChecked(true);

                sortWay = sort_date;
                sort_All(sortWay,null);
                break;

            case R.id.sort_label:
                item.setChecked(true);

                sortWay = sort_label;
                sort_All(sortWay,null);
                break;

            case R.id.sort_title:
                item.setChecked(true);

                sortWay = sort_title;
                sort_All(sortWay,null);
                break;


            case R.id.item_mark_star:                                           //select的menu
                changeMenu.findItem(R.id.noStar).setVisible(choice_count>0);
                changeMenu.findItem(R.id.redStar).setVisible(choice_count>0);
                changeMenu.findItem(R.id.blueStar).setVisible(choice_count>0);
                changeMenu.findItem(R.id.goldStar).setVisible(choice_count>0);

                if(choice_count==0){
                   // changeMenu.setGroupVisible(R.id.other,false);
                    Toast.makeText(this, "未選擇項目", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.noStar:
                set_change(changeStar, STAR_NO, null ,0);
                break;

            case R.id.redStar:
                set_change(changeStar, STAR_RED, null ,0);
                break;

            case R.id.goldStar:
                set_change(changeStar, STAR_GOLD, null ,0);
                break;

            case R.id.blueStar:
                set_change(changeStar, STAR_BLUE, null ,0);
                break;

            case R.id.item_delete:
                if(choice_count==0)
                    Toast.makeText(this, "未選擇項目", Toast.LENGTH_SHORT).show();
                else
                {
                    AlertDialog.Builder builder= new AlertDialog.Builder(this);
                    builder.setTitle("提示").setMessage("已選取 "+choice_count+" 個 , 確定刪除嗎?");

                    builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Note_item item;

                            for(int i=itemAdapter.getCount()-1; i>=0 ;i--){                         //還不知道為什麼是反過來選???
                                 item = itemAdapter.getItem(i);

                                if(item.getSelect()){
                                    itemDAO.delete(item);               //刪除資料庫裡的記事本
                                    itemAdapter.remove(i);
                                }
                            }
                            itemAdapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this,"已刪除 "+String.valueOf(choice_count)+" 項目", Toast.LENGTH_SHORT).show();

                            choice_count=0;
                            choice_number.setText(String.valueOf(choice_count));
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
                break;

            case R.id.change_title:
                if(choice_count!=1 )
                    Toast.makeText(this, "請選擇單一項",Toast.LENGTH_SHORT).show();
                else
                {
                    AlertDialog.Builder builder= new AlertDialog.Builder(this);
                    builder.setTitle("請輸入標題 : ");
                    final EditText Title = new EditText(MainActivity.this);
                    builder.setView(Title);

                    Note_item it;
                    //設置原來的標題內容
                    for(int i=0;i<itemAdapter.getCount();i++) {
                        it = itemAdapter.getItem(i);
                        if(it.getSelect()){
                            Title.setText(it.getTitle());
                            break;
                        }
                    }
                    builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            set_change(changeTitle, 0, Title.getText().toString() ,0);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.create().show();
                }
                break;

            case R.id.change_label:
                if(choice_count==0 )
                    Toast.makeText(this, "未選擇項目",Toast.LENGTH_SHORT).show();
                else
                {
                    final String items[] ={"筆記本" ,"考試本" ,"自訂本"};
                    AlertDialog.Builder builder= new AlertDialog.Builder(this);
                    builder.setTitle("請選擇 標籤~");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            set_change(changeLabel, 0, items[which] ,0);
                        }
                    });

                    builder.create().show();
                }
                break;

            case R.id.change_datetime:
                if(choice_count!=1 )
                    Toast.makeText(this, "請選擇單一項",Toast.LENGTH_SHORT).show();
                else
                {
                    final Calendar calendar = Calendar.getInstance();           //設置今天的日期
                    int Year = calendar.get(Calendar.YEAR);
                    int Month = calendar.get(Calendar.MONTH);
                    int Day = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datepicker =new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            calendar.set(year,month,day,10,10,10);
                            Date dt =calendar.getTime();

                            set_change(changeDatetime ,0 ,null ,dt.getTime());
                        }
                    },Year,Month,Day);

                    datepicker.show();
                }
                break;

            case R.id.select_allChoice:
                itemAdapter.setSelectLook(true , true);

                choice_count = itemDAO.getAll_Count();                  //從資料庫撈出項目總數
                choice_number.setText(String.valueOf(choice_count));
                break;
            case R.id.select_allCancel:
                itemAdapter.setSelectLook(true , false);

                choice_count=0;
                choice_number.setText(String.valueOf(choice_count));
                break;
        }
        return true;
    }

    //使用搜尋功能
    public void function_search(Menu menu){
        searchItem = menu.findItem(R.id.item_search);
        search = (SearchView) MenuItemCompat.getActionView(searchItem);
        search.setIconifiedByDefault(true);
        search.setSubmitButtonEnabled(true);
        search.setQueryHint("Search Here");
        //search.setSuggestionsAdapter();

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {                //點擊查詢案件時觸發
                Toast.makeText(MainActivity.this, "Searching for: " + query + "...", Toast.LENGTH_SHORT).show();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {              //輸入文字時觸發
                if(is_search)   sort_All(sort_search, newText);
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {                              //觸發開起searchView
                findViewById(R.id.imageView_empty_picture).setVisibility(View.INVISIBLE);
                fab.setVisibility(View.INVISIBLE);
                changeMenu.findItem(R.id.item_sort).setVisible(false);
                is_search = true;

                sort_All(sort_title, null);
                return true;
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {                            //觸發關閉searchView
                findViewById(R.id.imageView_empty_picture).setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                changeMenu.findItem(R.id.item_sort).setVisible(true);
                is_search = false;

                if (!search_to_select)  sort_All(sortWay, null);
                return true;
            }
        });
    }

    public void set_change(int set ,int star , String string , long date){
        Note_item item ;

        for(int i=0; i < itemAdapter.getCount();i++)
        {
            item = itemAdapter.getItem(i);
            if(item.getSelect())
            {
                switch (set){
                    case changeStar:
                        item.setStarStyle(star);
                        break;
                    case changeTitle:
                        item.setTitle(string);
                        break;
                    case changeLabel:
                        item.setLabel(string);
                        break;
                    case changeDatetime:
                        item.setDatetime(date);
                        break;
                }
                itemAdapter.set(i, item);
                itemDAO.update(item);                               //更新資料庫

                if(set==changeTitle || set==changeDatetime) break;
            }
        }
        itemAdapter.notifyDataSetChanged();
    }

    public void sort_All(int sortWay ,String search_text){

        List<Note_item> list_item = new ArrayList<>();
        itemAdapter.removeClear();
        int type;

        switch (sortWay){
            case sort_date :
                list_item = itemDAO.get_sortDate();
                type = TYPE_DATE;
                break;
            case sort_label :
                list_item = itemDAO.get_sortLabel();
                type = TYPE_LABEL;
                break;
            case sort_title :
                list_item = itemDAO.get_sortTitle();
                type = TYPE_CONTENT;
                break;
            case sort_search:
                list_item = itemDAO.get_search_title(search_text);
                type = TYPE_CONTENT;
                break;
            default:    type=TYPE_CONTENT;
        }
        for(int i=0 ; i<list_item.size() ; i++) {
            itemAdapter.add(list_item.get(i) , type);
        }

        itemAdapter.notifyDataSetChanged();
        list_item.clear();
    }

}
