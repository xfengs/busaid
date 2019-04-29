package com.arcsoft.sdk_demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.guo.android_extend.widget.ExtImageView;
import com.guo.android_extend.widget.HListView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class StudentActivity extends Activity  {

    private HListView mHFaceView;
    private RegViewAdapter mRegViewAdapter;
    // Layout Views
    private TextView mTitle;
    private TextView mTestView;
    private EditText mEditText;

    private RecyclerView mRecyclerView;
    private List<String> mDatas;
    private ItemAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FaceDB.FaceRegist face;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        mRegViewAdapter = new RegViewAdapter(this);
        mHFaceView = (HListView)findViewById(R.id.hFaceView);
        mHFaceView.setAdapter(mRegViewAdapter);
        mHFaceView.setOnItemClickListener(mRegViewAdapter);



    }

    class mainHolder {
        ExtImageView siv;
        TextView tv;
    }

    class RegViewAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{
        Context mContext;
        LayoutInflater mLInflater;

        public RegViewAdapter(Context c) {
            // TODO Auto-generated constructor stub
            mContext = c;
            mLInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return ((Application)mContext.getApplicationContext()).mFaceDB.mRegister.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            mainHolder holder = null;
            if (convertView != null) {
                holder = (mainHolder) convertView.getTag();
            } else {
                convertView = mLInflater.inflate(R.layout.item_sample, null);
                holder = new mainHolder();
                holder.siv = (ExtImageView) convertView.findViewById(R.id.imageView1);
                holder.tv = (TextView) convertView.findViewById(R.id.textView1);
                convertView.setTag(holder);
            }

            if (!((Application)mContext.getApplicationContext()).mFaceDB.mRegister.isEmpty()) {
                FaceDB.FaceRegist face = ((Application) mContext.getApplicationContext()).mFaceDB.mRegister.get(position);
                holder.tv.setText(face.mName);
                //holder.siv.setImageResource(R.mipmap.ic_launcher);
                convertView.setWillNotDraw(false);
            }

            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final String name = ((Application)mContext.getApplicationContext()).mFaceDB.mRegister.get(position).mName;
            final int count = ((Application)mContext.getApplicationContext()).mFaceDB.mRegister.get(position).mFaceList.size();
            new AlertDialog.Builder(StudentActivity.this)
                    .setTitle("删除注册名:" + name)
                    .setMessage("包含:" + count + "个注册人脸特征信息")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((Application)mContext.getApplicationContext()).mFaceDB.delete(name);
                            mRegViewAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

    }

    //网格布局
    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

        List<String> list;//存放数据
        Context context;

        public ItemAdapter(List<String> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public ItemAdapter.MyViewHolder  onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemAdapter.MyViewHolder holder = new ItemAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_sample, parent, false));
            return holder;
        }

        //在这里可以获得每个子项里面的控件的实例，比如这里的TextView,子项本身的实例是itemView，
// 在这里对获取对象进行操作
        //holder.itemView是子项视图的实例，holder.textView是子项内控件的实例
        //position是点击位置
        @Override
        public void onBindViewHolder(ItemAdapter.MyViewHolder holder, final int position) {
            //设置textView显示内容为list里的对应项
            holder.tv.setText(list.get(position));
            final String bmpname = ((Application)StudentActivity.this.getApplicationContext()).mFaceDB.mRegister.get(position).mName;
            String bmppath=((Application)StudentActivity.this.getApplicationContext()).mPath;
            try {
                FileInputStream fis = new FileInputStream(bmppath+"/"+bmpname+".jpg");
                Bitmap facebmp=BitmapFactory.decodeStream(fis);
                DisplayMetrics dm = getResources().getDisplayMetrics();
                Bitmap nfb=scaleBitmap(facebmp,dm.widthPixels/4,dm.widthPixels/4);
                holder.siv.setImageBitmap(nfb);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //子项的点击事件监听
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(context, "点击子项" + position, Toast.LENGTH_SHORT).show();
                    final String name = ((Application)StudentActivity.this.getApplicationContext()).mFaceDB.mRegister.get(position).mName;
                    final int count = ((Application)StudentActivity.this.getApplicationContext()).mFaceDB.mRegister.get(position).mFaceList.size();
                    new AlertDialog.Builder(StudentActivity.this)
                            .setTitle("删除注册名:" + name)
                            .setMessage("包含:" + count + "个注册人脸特征信息")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((Application)StudentActivity.this.getApplicationContext()).mFaceDB.delete(name);
                                    mAdapter.removeItem(position);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });
        }
        private Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
            if (origin == null) {
                return null;
            }
            int height = origin.getHeight();
            int width = origin.getWidth();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
            Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
            if (!origin.isRecycled()) {
                origin.recycle();
            }
            return newBM;
        }

        //要显示的子项数量
        @Override
        public int getItemCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        //这里定义的是子项的类，不要在这里直接对获取对象进行操作
        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView textView;
            ExtImageView siv;
            TextView tv;

            public MyViewHolder(View itemView) {
                super(itemView);
                siv = itemView.findViewById(R.id.imageView1);
                tv = itemView.findViewById(R.id.textView1);

            }
        }

        /*之下的方法都是为了方便操作，并不是必须的*/

        //在指定位置插入，原位置的向后移动一格
        public boolean addItem(int position, String msg) {
            if (position < list.size() && position >= 0) {
                list.add(position, msg);
                notifyItemInserted(position);
                return true;
            }
            return false;
        }

        //去除指定位置的子项
        public boolean removeItem(int position) {
            if (position < list.size() && position >= 0) {
                list.remove(position);
                notifyItemRemoved(position);
                return true;
            }
            return false;
        }

        //清空显示数据
        public void clearAll() {
            list.clear();
            notifyDataSetChanged();
        }
    }
}

