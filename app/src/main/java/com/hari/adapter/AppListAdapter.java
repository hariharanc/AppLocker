package com.hari.adapter;

import java.util.ArrayList;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hari.locker.R;
import com.hari.util.AppDetails;

@SuppressLint("ResourceAsColor")
public class AppListAdapter extends BaseAdapter {

    private Context ctx;
    private ArrayList<AppDetails> mAppDetails;
    private ArrayList<String> mArrAppDetails;

    public AppListAdapter(Context ctx, ArrayList<AppDetails> mArrayAppList, ArrayList<String> mArrAppDetails) {

        this.ctx = ctx;
        mAppDetails = mArrayAppList;
        this.mArrAppDetails = mArrAppDetails;

    }

    @Override
    public int getCount() {

        return mAppDetails.size();
    }

    @Override
    public Object getItem(int position) {

        return mAppDetails.get(position);
    }

    @Override
    public long getItemId(int position) {

        return mAppDetails.indexOf(getItem(position));
    }

    @SuppressLint({"ResourceAsColor", "NewApi"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) ctx
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.app_list_item, null);
            holder = new ViewHolder();

            holder.txtTitle = (TextView) convertView
                    .findViewById(R.id.txt_appName);
            holder.mImgIcon = (ImageView) convertView
                    .findViewById(R.id.img_icon);
            holder.mImgLock = (ImageView) convertView.findViewById(R.id.img_lock);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AppDetails appDetails = (AppDetails) getItem(position);
        if (mArrAppDetails.contains(appDetails.getAppName())) {
            holder.txtTitle.setText(appDetails.getAppName());
            holder.mImgIcon.setImageBitmap(StringToBitMap(appDetails
                    .getAppIconBitMap()));
            holder.txtTitle.setTextColor(ContextCompat.getColor(ctx, R.color.app_locked));
            holder.mImgLock.setImageResource(R.drawable.lock_outline);
        } else {
            holder.txtTitle.setText(appDetails.getAppName());
            holder.mImgIcon.setImageBitmap(StringToBitMap(appDetails
                    .getAppIconBitMap()));
            holder.txtTitle.setTextColor(Color.BLACK);
            holder.mImgLock.setImageResource(R.drawable.lock_open_outline);
        }


        return convertView;
    }

    public Bitmap StringToBitMap(String image) {
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void passUpdatedList(ArrayList<String> mArrAppDetails) {

        this.mArrAppDetails = mArrAppDetails;
    }


    /* private view holder class */
    private class ViewHolder {
        TextView txtTitle;
        ImageView mImgIcon, mImgLock;

    }

}
