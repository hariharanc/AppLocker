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

	public AppListAdapter(Context ctx, ArrayList<AppDetails> mArrayAppList) {

		this.ctx = ctx;
		mAppDetails = mArrayAppList;

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

	@SuppressLint({ "ResourceAsColor", "NewApi" })
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
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AppDetails appDetails = (AppDetails) getItem(position);
		if (appDetails.getAppLockStates().equalsIgnoreCase("1")) {
			holder.txtTitle.setText(appDetails.getAppName());
			holder.mImgIcon.setImageBitmap(StringToBitMap(appDetails
					.getAppIconBitMap()));
			//holder.txtTitle.setTextColor(ctx.getColor(R.color.ColorPrimary));
		} else {
			holder.txtTitle.setText(appDetails.getAppName());
			holder.mImgIcon.setImageBitmap(StringToBitMap(appDetails
					.getAppIconBitMap()));
			//holder.txtTitle.setTextColor(ctx.getColor(Color.BLACK));
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

	/* private view holder class */
	private class ViewHolder {
		TextView txtTitle;
		ImageView mImgIcon;

	}

}
