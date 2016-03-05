package com.example.scps9.simple_note;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.Interface.finalName_NoteListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by scps9 on 2015/12/18.
 */
public class ItemAdapter extends BaseAdapter implements finalName_NoteListView {

    private LayoutInflater inflater;
    private List<Note_item> list_items;  // 記事本資料List
    private boolean selectVisible = false ;

    final int VIEW_TYPE  = 3;

    public ItemAdapter(Context context ) {
        inflater = LayoutInflater.from(context);
        this.list_items = new ArrayList<>();
    }
    @Override
    public int getItemViewType(int position){
        Note_item item = getItem(position);

        if(item.getView_type()==TYPE_LABEL)
            return TYPE_LABEL;
        else
        if(item.getView_type()==TYPE_DATE)
            return TYPE_DATE;
        else
            return TYPE_CONTENT;
    }
    @Override
    public  int getViewTypeCount(){
        return VIEW_TYPE;
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public int getCount(){
        return list_items.size();
    }
    @Override
    public Note_item getItem(int position){
        return list_items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Note_item item = getItem(position);  // 讀取目前位置的記事物件
        // 建立項目畫面元件
        switch(getItemViewType(position)){
            case TYPE_CONTENT:
                ViewHolder1 holder1= new ViewHolder1();

                if (convertView == null) {
                    convertView=inflater.inflate(R.layout.listview_note, parent, false);

                    holder1.title = (TextView) convertView.findViewById(R.id.textView_list_title);
                    holder1.date = (TextView) convertView.findViewById(R.id.textView_list_date);
                    holder1.label = (TextView)convertView.findViewById(R.id.textView_list_label);
                    holder1.screenshot = (ImageView) convertView.findViewById(R.id.image_list_screenshot);
                    holder1.star =(ImageView) convertView.findViewById(R.id.image_list_star);
                    holder1.select =(ImageView) convertView.findViewById(R.id.image_list_select);
                    convertView.setTag(holder1);
                }
                else   holder1 = (ViewHolder1) convertView.getTag();

                setAll_listView(holder1,item);
                break;

            case TYPE_DATE:
                ViewHolder2 holder2= new ViewHolder2();

                if(convertView==null){
                    convertView=inflater.inflate(R.layout.listview_note_m, parent, false);

                    holder2.m_date = (TextView) convertView.findViewById(R.id.textView_list_m);
                    convertView.setTag(holder2);
                }
                else holder2 = (ViewHolder2) convertView.getTag();

                holder2.m_date.setText(item.get_Y_M_Date());
                convertView.setOnClickListener(null);
                convertView.setOnLongClickListener(null);

                break;
            case TYPE_LABEL:
                ViewHolder3 holder3= new ViewHolder3();

                if(convertView==null){
                    convertView=inflater.inflate(R.layout.listview_note_m, parent, false);

                    holder3.m_label = (TextView) convertView.findViewById(R.id.textView_list_m);
                    convertView.setTag(holder3);
                }
                else holder3 = (ViewHolder3) convertView.getTag();

                holder3.m_label.setText(item.getLabel());
                convertView.setOnClickListener(null);
                convertView.setOnLongClickListener(null);

                break;
        }
        return convertView;
    }

    class ViewHolder1{
        TextView   title, date  , label;
        ImageView  screenshot , star ,select;
    }
    class ViewHolder2{
        TextView   m_date;
    }
    class ViewHolder3{
        TextView   m_label;
    }

    private String no_title = "_noTitle_";             //預設無標題

    public void setAll_listView(ViewHolder1 Holder ,Note_item itemId){

        String title_s =itemId.getTitle();
        if(title_s.compareTo(no_title)==0){
            Holder.title.setTextColor(Color.GRAY);
        }else
            Holder.title.setTextColor(Color.parseColor("#555555"));

        Holder.title.setText(title_s);
        Holder.date.setText(itemId.getDate());
        Holder.label.setText(itemId.getLabel());
        Bitmap image= BitmapFactory.decodeByteArray(itemId.getPicture(), 0, itemId.getPicture().length);      //從byte轉 bitmap
        Holder.screenshot.setImageBitmap(image);

        switch (itemId.getStarStyle()) {
            case STAR_NO :
                Holder.star.setVisibility(View.INVISIBLE);
                break;
            case STAR_RED :
                Holder.star.setVisibility(View.VISIBLE);
                Holder.star.setImageResource(R.drawable.star_red_32);
                break;
            case STAR_GOLD :
                Holder.star.setVisibility(View.VISIBLE);
                Holder.star.setImageResource(R.drawable.star_gold_32);
                break;
            case STAR_BLUE :
                Holder.star.setVisibility(View.VISIBLE);
                Holder.star.setImageResource(R.drawable.star_blue_32);
                break;
        }

        if(selectVisible){
            Holder.select.setVisibility(View.VISIBLE);
            Holder.select.setImageResource(R.drawable.check_box_outline_32);
        }
        else Holder.select.setVisibility(View.GONE);


        if(itemId.getSelect() && selectVisible)
            Holder.select.setImageResource(R.drawable.check_box_32);
        else
        if(!itemId.getSelect() && selectVisible)
            Holder.select.setImageResource(R.drawable.check_box_outline_32);
    }

    private int Year =3000 ,Month=12 ;      //預設日期
    private String Label = "_";             //預設標籤

    // 加入list的item
    public void add(Note_item item ,int type){
        Calendar ca = Calendar.getInstance();       //偵測日期
        ca.setTime(new Date(item.getDatetime()) );  //偵測日期
        String label =item.getLabel();              //偵測標籤

        if(type==TYPE_DATE  && (Year > ca.get(Calendar.YEAR) || Month > ca.get(Calendar.MONTH)) ){
            Year = ca.get(Calendar.YEAR);
            Month = ca.get(Calendar.MONTH);

            Note_item m_Date = new Note_item();
            m_Date.setDatetime( item.getDatetime() );
            m_Date.setView_type(TYPE_DATE);
            list_items.add(m_Date);
        }
        else
        if(type==TYPE_LABEL  && label.compareTo(Label)!=0){
            Label = label;

            Note_item m_Label = new Note_item();
            m_Label.setLabel(item.getLabel());
            m_Label.setView_type(TYPE_LABEL);
            list_items.add(m_Label);
        }

        list_items.add(item);
    }

    // 刪除list的item
    public void remove(int position){
        list_items.remove(position);
    }

    // 更新list的item
    public void set(int index, Note_item item) {
        if (index >= 0 && index < list_items.size()) {
            list_items.set(index, item);
        }
    }

    public void removeClear(){
        list_items.clear();
        Year = 3000 ;
        Month = 12;
        Label = "_";
    }
    public void setSelectLook( boolean set , boolean selectAll){        //參數1為設置checkbox是否顯現,參數2為 設置是否全勾選
        selectVisible = set;
        for(int i=0; i < list_items.size() ;i++){
            Note_item item = getItem(i);                                //取得本身 ItemAdapter 的position的編號item
            item.setSelect(selectAll);
            list_items.set(i, item);
        }
        notifyDataSetChanged();
    }
}
