package com.face.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.arcsoft.sdk_demo.Application;
import com.arcsoft.sdk_demo.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.face.entity.Face;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.List;


class ViewHolder{      //当布局加载过后，保存获取到的控件信息。
    ImageView ivImage;
    TextView tvName;

}
public class FaceAdapter extends ArrayAdapter<Face> {

    private int resourceId;
    private int rot;
    private Context con;
    private ListView listView;
    private AsyncImageLoader asyncImageLoader;

    public FaceAdapter(Context context, int textViewResourceId,
                        List<Face> objects, int rotation, ListView lvFaces) {                         // 第一个参数是上下文环境，第二个参数是每一项的子布局，第三个参数是数据
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        rot=rotation;//获取子布局
        con=context;
        asyncImageLoader = new AsyncImageLoader();
        listView = lvFaces;
    }

    @Override         //getView方法在每个子项被滚动到屏幕内的时候都会被调用，每次都将布局重新加载一边
    public View getView(int position, View convertView, ViewGroup parent) {//第一个参数表示位置，第二个参数表示缓存布局，第三个表示绑定的view对象
        View view;
        int mode;
        ViewHolder viewHolder;                  //实例ViewHolder，当程序第一次运行，保存获取到的控件，提高效率
        if(convertView==null){
            viewHolder=new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(//convertView为空代表布局没有被加载过，即getView方法没有被调用过，需要创建
                    resourceId, null);          // 得到子布局，非固定的，和子布局id有关
            viewHolder.ivImage = (ImageView) view.findViewById(R.id.ivImage);//获取控件,只需要调用一遍，调用过后保存在ViewHolder中
            viewHolder.tvName = (TextView) view.findViewById(R.id.tvName);   //获取控件
            view.setTag(viewHolder);
        }else{
            view=convertView;           //convertView不为空代表布局被加载过，只需要将convertView的值取出即可
            viewHolder=(ViewHolder) view.getTag();
        }

        Face face = getItem(position);//实例指定位置的水果

        String bmppath=((Application)con.getApplicationContext()).mPath;
        String bmpfullname=face.getImageName();
        String bmpname=bmpfullname.split("_")[1];

        // Load the image and set it on the ImageView
        String imageUrl = bmppath+"/faces/"+bmpfullname+".jpg";
        //viewHolder.ivImage.setTag(imageUrl);

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);
        File file=new File(imageUrl);
        if(file.exists()) {
            Glide.with(con).load(file).apply(options).into(viewHolder.ivImage);
        }
        else
            Glide.with(con).load(R.drawable.default_avatar).apply(options).into(viewHolder.ivImage);
/*
        Bitmap cachedImage = asyncImageLoader.loadDrawable(imageUrl, new AsyncImageLoader.ImageCallback() {
            public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
                ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl);
                if (imageViewByTag != null) {
                    imageViewByTag.setImageBitmap(imageDrawable);
                }
            }
        });
        if (cachedImage == null) {
            //imageView.setImageResource(R.drawable.default_image);
        }else{
            viewHolder.ivImage.setRotation(0);
            viewHolder.ivImage.setImageBitmap(cachedImage);
        }*/

        String str;

        if(face.getMode()==0){
            viewHolder.tvName.setTextColor(Color.parseColor("#ff0000"));
            str=" 已乘车 " ;
        }
        else{
            viewHolder.tvName.setTextColor(Color.parseColor("#9AFF9A"));
            str=" 下车 " + face.getTime();
        }
        viewHolder.tvName.setText(bmpname + str );        //获得指定位置水果的名字
//
        return view;

    }
    public void notifyDataSetChanged(ListView listView, int position) {
        /**第一个可见的位置**/
       int firstVisiblePosition = listView.getFirstVisiblePosition();
        /**最后一个可见的位置**/
       // int lastVisiblePosition = listView.getLastVisiblePosition()+1;

        /**在看见范围内才更新，不可见的滑动后自动会调用getView方法更新**/
      //  if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            /**获取指定位置view对象**/
            View view = listView.getChildAt(position - firstVisiblePosition);
            getView(position, view, listView);
       // }
    }
}

class AsyncImageLoader {

    private HashMap<String, SoftReference<Bitmap>> imageCache;

    public AsyncImageLoader() {
        imageCache = new HashMap<String, SoftReference<Bitmap>>();
    }

    public Bitmap loadDrawable(final String imageUrl, final ImageCallback imageCallback) {
        if (imageCache.containsKey(imageUrl)) {
            SoftReference<Bitmap> softReference = imageCache.get(imageUrl);
            Bitmap facebmp = softReference.get();
            if (facebmp != null) {
                return facebmp;
            }
        }
        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);
            }
        };
        new Thread() {
            @Override
            public void run() {
                Bitmap facebmp = null;
                try {
                    facebmp = loadImageFromUrl(imageUrl);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                imageCache.put(imageUrl, new SoftReference<Bitmap>(facebmp));
                Message message = handler.obtainMessage(0, facebmp);
                handler.sendMessage(message);
            }
        }.start();
        return null;
    }

    public static Bitmap loadImageFromUrl(String url) throws FileNotFoundException {
        URL m;
        InputStream i = null;

            //m = new URL(url);
            //i = (InputStream) m.getContent();
        Bitmap facebmp = null;
        try{
            FileInputStream fis = new FileInputStream(url);
            facebmp =BitmapFactory.decodeStream(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //Drawable d = Drawable.createFromStream(i, "src");
        return facebmp;
    }

    public interface ImageCallback {
        public void imageLoaded(Bitmap imageDrawable, String imageUrl);
    }

}
