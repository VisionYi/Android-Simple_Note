package com.example.scps9.simple_note;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Created by scps9 on 2015/12/17.
 */
public class Note_item implements Serializable {

    private long id;
    private long datetime;
    private long lastModify;
    private int starStyle;
    private String label;
    private String title;
    private String content;

    private boolean selected ;
    private byte[] Picture;     // 內容是bitmap 的點陣圖
    private String Picture_ex;  //圖片說明

    private int view_type;      //在ItemAdapter裡

    public Note_item(){
        title ="";
        content="";
        view_type = 0;        //在ItemAdapter裡是 一般的note view
    }

    public Note_item(long id, long date, String t ,String content  ,String label, int starStyle ,byte[] picture ,String picture_ex)
    {
        this.id = id;
        this.datetime = date;
        this.lastModify=date;
        this.title = t;
        this.content = content;
        this.label=label;
        this.starStyle=starStyle;
        this.Picture=picture;
        this.Picture_ex =picture_ex;

        view_type=0;           //在ItemAdapter裡是 一般的note view
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String get_Y_M_Date(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy年-MM月");
        return df.format(datetime);
    }
    public String getDate(){
        SimpleDateFormat df = new SimpleDateFormat("MM/dd");
        return df.format(datetime);
    }

    public String getDayTime_modify(){
        SimpleDateFormat df = new SimpleDateFormat("MM/dd hh:mm:ss a");
        return df.format(lastModify);
    }
    public long getDatetime(){
        return datetime;
    }

    public void setDatetime(long date){
        this.datetime = date;
    }

    public long getLastModify(){
        return lastModify;
    }

    public void setLastModify(long modify){
        this.lastModify = modify;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String t){
        this.title = t;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String con){
        this.content = con;
    }

    public String getLabel(){
        return label;
    }

    public void setLabel(String label){
        this.label = label;
    }

    public int getStarStyle(){
        return starStyle;
    }

    public void setStarStyle(int star){
        this.starStyle=star;
    }

    public byte[] getPicture() {
        return  Picture;
    }

    public void setPicture(byte[] picture) {
        this.Picture = picture;
    }

    public String getPicture_ex(){
        return Picture_ex;
    }

    public void setPicture_ex(String picture_ex){
        this.Picture_ex = picture_ex;
    }

    public boolean getSelect() {
        return selected;
    }

    public void setSelect(boolean select) {
        this.selected = select;
    }

    public int getView_type(){
        return view_type;
    }

    public void setView_type(int type ){
        this.view_type = type;
    }

}
