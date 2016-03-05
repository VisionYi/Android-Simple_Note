package com.example.scps9.simple_note;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by scps9 on 2015/12/21.
 */
public class Other_way {


    public  byte[] Bitmap_to_Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    public static Bitmap scalePicture(String filename, int maxWidth,int maxHeight) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filename, opts);
            int srcWidth = opts.outWidth;
            int srcHeight = opts.outHeight;
            int desWidth = 0;
            int desHeight = 0;
            // 缩放比例
            double ratio = 0.0;
            if (srcWidth > srcHeight) {
                ratio = srcWidth / maxWidth;
                desWidth = maxWidth;
                desHeight = (int) (srcHeight / ratio);
            } else {
                ratio = srcHeight / maxHeight;
                desHeight = maxHeight;
                desWidth = (int) (srcWidth / ratio);
            }
            // 设置输出宽度、高度
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inSampleSize = (int) (ratio) + 1;
            newOpts.inJustDecodeBounds = false;
            newOpts.outWidth = desWidth;
            newOpts.outHeight = desHeight;
            bitmap = BitmapFactory.decodeFile(filename, newOpts);

        } catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
    }


    public Bitmap compressImageSize(String path, int size ,Context context) throws IOException {
        // 取得圖片
        InputStream temp = context.getAssets().open(path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 這個參數代表，不為bitmap分配內存空間，只記錄一些該圖片的資訊（例如圖片大小），說白了就是為了內存優化
        options.inJustDecodeBounds = true;
        // 通過創建圖片的方式，取得options的內容（這裏就是利用了java的位址傳遞來賦值）
        BitmapFactory.decodeStream(temp, null, options);
        // 關閉流
        temp.close();


        // 生成壓縮的圖片
        int i = 0;
        Bitmap bitmap = null;
        while (true) {
            // 這一步是根據要設置的大小，使寬和高都能滿足
            if ((options.outWidth >> i <= size) && (options.outHeight >> i <= size)) {
                // 重新取得流，注意：這裏一定要再次加載，不能二次使用之前的流！
                temp = context.getAssets().open(path);
                // 這個參數表示 新生成的圖片為原始圖片的幾分之一。
                options.inSampleSize = (int) Math.pow(2.0D, i);
                // 這裏之前設置為了true，所以要改為false，否則就創建不出圖片
                options.inJustDecodeBounds = false;

                bitmap = BitmapFactory.decodeStream(temp, null, options);
                break;
            }
            i += 1;
        }
        return bitmap;
    }
}
