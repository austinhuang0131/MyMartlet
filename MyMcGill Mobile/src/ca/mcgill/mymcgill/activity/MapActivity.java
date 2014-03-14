package ca.mcgill.mymcgill.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;
import ca.mcgill.mymcgill.activity.drawer.DrawerAdapter;
import ca.mcgill.mymcgill.activity.drawer.DrawerFragmentActivity;

/**
 * Created by Ryan Singzon on 14/03/14.
 */
public class MapActivity extends DrawerActivity {

    @SuppressLint("NewApi")
     @Override
     public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_map);
        mDrawerAdapter = new DrawerAdapter(this, DrawerAdapter.MAP_POSITION);
        super.onCreate(savedInstanceState);

    }
}
