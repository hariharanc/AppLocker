package com.hari.locker;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hari.adapter.AppListAdapter;
import com.hari.dbhelper.DatabaseHandler;
import com.hari.pin.PinActivity;
import com.hari.pin.SetPinActivity;
import com.hari.util.AppDetails;

public class MainActivity extends AppCompatActivity implements
        OnItemClickListener {
    private ListView mListView;
    private Toolbar toolbar;
    private AppListAdapter mApListAdapter;
    private ArrayList<AppDetails> mArrayAppList;
    private DatabaseHandler mDatabaseHandler;
    private ArrayList<String> mArrAppDetails;
    private ProgressBar mProgressBar;
    private SharedPreferences mPreferences;
    private Editor mEditor;
    private Boolean isBackPressed =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //android back press dialog to exit the application
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        mPreferences = getSharedPreferences("hariPref",
                Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();


        mDatabaseHandler = new DatabaseHandler(this);
        mArrayAppList = new ArrayList<AppDetails>();
        //this async class background thread execution.
        new AppDataAysnc().execute();

        try {
            Intent serviceIntent = new Intent(this, LockerService.class);
            // Start service
            this.startService(serviceIntent);
        } catch (Exception e) {
            Log.e(MainActivity.class.toString(), "MainActivity Exception is"
                    + e.getMessage());
        }
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.invalidateOptionsMenu();

    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    private void initViews() {
        mListView = (ListView) findViewById(R.id.list_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        Log.i(MainActivity.class.toString(), "MainActivity size Array"
                + mArrayAppList.size());
        mListView.setOnItemClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuResetPin = menu.findItem(R.id.action_reset_pin);
        MenuItem menuSetPin = menu.findItem(R.id.action_set_pin);

        Log.i(MainActivity.class.toString(), "MainActivity get pin" + mPreferences.getString("myPin", ""));

        if ((mPreferences.getString("myPin", "") != null) &&
                (mPreferences.getString("myPin", "").equalsIgnoreCase(""))) {
            menuSetPin.setVisible(true);
            menuResetPin.setVisible(false);

        } else {
            menuSetPin.setVisible(false);
            menuResetPin.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_reset_pin:
                isBackPressed= true;
                Intent i = new Intent(MainActivity.this, SetPinActivity.class);
                i.putExtra("pinOption", "resetpin");
                startActivity(i);
                return true;

            case R.id.action_set_pin:
                isBackPressed= true;
                Intent pinIntent = new Intent(MainActivity.this, SetPinActivity.class);
                pinIntent.putExtra("pinOption", "setpin");
                startActivity(pinIntent);
                return true;

            case R.id.action_refresh:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            final int position, long id) {
        Log.i(MainActivity.class.toString(), "MainActivity Pack Name"
                + mArrayAppList.get(position).getAppPackName());
        if ((mPreferences.getString("myPin", "") != null) &&
                (mPreferences.getString("myPin", "").equalsIgnoreCase(""))) {
            isBackPressed= true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                                .setTitle("Alert")
                                .setMessage("Set Your Pin First!!!")
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent pinIntent = new Intent(MainActivity.this, SetPinActivity.class);
                                        pinIntent.putExtra("pinOption", "setpin");
                                        startActivity(pinIntent);

                                    }
                                }).create().show();
                    }
                }
            });

        } else {
            isBackPressed= true;
            final String[] items;
            if (mArrAppDetails.contains(mArrayAppList.get(position).getAppName())) {
                items = new String[]{"Unlock"};
            } else {
                items = new String[]{"Lock", "Unlock"};
            }


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select your option");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].toString().equalsIgnoreCase("Lock")) {
                        mDatabaseHandler.insertAppLock(mArrayAppList.get(position)
                                .getAppName(), mArrayAppList.get(position)
                                .getAppPackName(), mArrayAppList.get(position)
                                .getAppIconBitMap(), "1");
                        mArrAppDetails.clear();
                        mArrAppDetails = mDatabaseHandler.getAllLockApp();

                        Log.i(MainActivity.class.toString(), "MainActivity Insert App Data Array Size" + mArrAppDetails.size());
                        upDateListView();
                    } else {
                        String value = mDatabaseHandler
                                .delLockApp(mArrayAppList.get(position)
                                        .getAppPackName());
                        if (Integer.parseInt(value) > 0) {
                            mArrAppDetails.remove(mArrayAppList.get(position)
                                    .getAppName());
                            upDateListView();
                        }
                    }
                    isBackPressed= false;
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }


    }

    private void upDateListView() {
        mApListAdapter.passUpdatedList(mArrAppDetails);
        mApListAdapter.notifyDataSetChanged();
        mListView.invalidateViews();
        isBackPressed= true;


    }


    /***
     * The main purpose of this class to maintaon the seperated thread process to get the installed app
     * name and images it converted drawable into bitmap conversion
     */
    public class AppDataAysnc extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            mArrAppDetails = mDatabaseHandler.getAllLockApp();

            Log.i(MainActivity.class.toString(), "MainActivity App Lock Size" + mArrAppDetails.size());
            final PackageManager pm = getPackageManager();
            // get a list of installed apps.
            List<ApplicationInfo> packages = pm
                    .getInstalledApplications(PackageManager.GET_META_DATA);

            for (ApplicationInfo packageInfo : packages) {
                String appName = packageInfo.loadLabel(
                        getApplicationContext().getPackageManager()).toString();
                Log.i(MainActivity.class.toString(), "MainActivity Application Name is :"
                        + appName);
                String processName = packageInfo.processName;
                Log.i(MainActivity.class.toString(), "Process Name is :"
                        + processName);
                Drawable app_icon = packageInfo
                        .loadIcon(getApplicationContext().getPackageManager());


                AppDetails appDetails = new AppDetails(appName, processName,
                        BitMapToString(drawableToBitmap(app_icon)), "0");

                mArrayAppList.add(appDetails);


            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressBar.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mApListAdapter = new AppListAdapter(getApplicationContext(),
                    mArrayAppList, mArrAppDetails);
            mListView.setAdapter(mApListAdapter);
        }

    }


    /**
     * @param drawable is an drawable object
     *                 This method  used to convert the drawble object into bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0
                || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * @param bitmap is an Bitmap object
     * @see this method used to convert the bitmap into string conversion
     */
    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] arr = baos.toByteArray();
        String result = Base64.encodeToString(arr, Base64.DEFAULT);
        return result;
    }

    @Override
    public void onBackPressed() {
        Log.i(MainActivity.class.toString(), "MainActivity onBackPressed");
        isBackPressed=true;
        isBackPressed(true);
    }

    private void isBackPressed(boolean b) {
        Log.i(MainActivity.class.toString(), "MainActivity isBackPressed"+b);

        if (b) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Exit Application?");
            alertDialogBuilder
                    .setMessage("Click Yes To Exit!")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    isBackPressed = false;
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("EXIT", true);
                                    startActivity(intent);
                                }
                            })

                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            isBackPressed = false;
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            finish();
        }
    }


    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.i(MainActivity.class.toString(), "MainActivity Focus changed !");

        if (!hasFocus) {
            Log.i(MainActivity.class.toString(), "MainActivity Lost Focus!");
            if(isBackPressed == false) {
                isBackPressed(false);
            }
//            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//            sendBroadcast(closeDialog);

        }
    }

}
