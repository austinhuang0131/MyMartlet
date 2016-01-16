/*
 * Copyright 2014-2016 Appvelopers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.appvelopers.mcgillmobile.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.net.MalformedURLException;
import java.net.URL;

import butterknife.Bind;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Homepage;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.storage.Clear;
import timber.log.Timber;

/**
 * Base class for all of the activities with the main navigation drawer
 * @author Julien Guerinet
 * @since 2.1.0
 */
public class DrawerActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * The drawer layout
     */
    @Bind(R.id.drawer_layout)
    protected DrawerLayout mDrawerLayout;
    /**
     * The navigation view
     */
    @Bind(R.id.drawer)
    protected NavigationView mDrawer;
    /**
     * The toggle for the drawer inside the action bar
     */
    private ActionBarDrawerToggle mDrawerToggle;
    /**
     * Callback manager used for Facebook
     */
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize the Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());
        //Set up the Facebook callback manager
        mCallbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Set up the toolbar
        setUpToolbar(false);

        //Set up the drawer
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, 0, 0);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setFocusableInTouchMode(false);
        mDrawer.setNavigationItemSelectedListener(this);

        //Show the BugDialog if there is one
        String parserBug = getIntent().getStringExtra(Constants.BUG);
        if(parserBug != null){
            DialogHelper.showBugDialog(this, parserBug.equals(Constants.TRANSCRIPT),
                    getIntent().getStringExtra(Constants.TERM));
        }
    }

    @Override
    public void onBackPressed() {
        //Open the menu if it's not open
        if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //For Facebook sharing
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Class activity;
        switch (item.getItemId()) {
            case R.id.facebook:
                shareOnFacebook();
                return true;
            case R.id.twitter:
                shareOnTwitter();
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                //Try to get one of the activities to open
                activity = Homepage.getActivity(item.getItemId());
                break;
        }

        if (activity == null) {
            //Nothing found, do nothing
            return false;
        }

        if (getClass().equals(activity)) {
            //Check if it's the currently opened activity and close the drawer if that's the case
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            //If not, load the page
            startActivity(new Intent(this, activity));
            finish();
        }
        return true;
    }

    /* HELPERS */

    /**
     * Logs the user out
     */
    private void logout() {
        //Confirm with the user
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout_dialog_title)
                .setMessage(R.string.logout_dialog_message)
                .setPositiveButton(R.string.logout_dialog_positive,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Analytics.get().sendEvent("Logout", "Clicked");
                                Clear.all();
                                //Go back to SplashActivity
                                startActivity(new Intent(DrawerActivity.this,
                                        SplashActivity.class));
                                finish();
                            }

                        })
                .setNegativeButton(R.string.logout_dialog_negative, null)
                .show();
    }

    /**
     * Shares the app on Facebook
     */
    private void shareOnFacebook() {
        Analytics.get().sendEvent("facebook", "attempt_post");

        //Set up all of the info
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle(getString(R.string.social_facebook_title, "Android"))
                .setContentDescription(getString(R.string.social_facebook_description_android))
                .setContentUrl(Uri.parse(getString(R.string.social_link_android)))
                .setImageUrl(Uri.parse(getString(R.string.social_facebook_image)))
                .build();

        //Show the dialog
        ShareDialog dialog = new ShareDialog(this);
        dialog.registerCallback(mCallbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                if (result.getPostId() != null) {
                    //Let the user know he posted successfully
                    Toast.makeText(DrawerActivity.this, R.string.social_post_success,
                            Toast.LENGTH_SHORT).show();
                    Analytics.get().sendEvent("facebook", "successful_post");
                } else {
                    Timber.i("Facebook post cancelled");
                }
            }

            @Override
            public void onCancel() {
                Timber.i("Facebook post cancelled");
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(DrawerActivity.this, R.string.social_post_failure,
                        Toast.LENGTH_SHORT).show();
                Timber.e(e, "Error posting to Facebook");
                Analytics.get().sendEvent("facebook", "failed_post");
            }
        });
        dialog.show(content);
    }

    /**
     * Shares the app on Twitter
     */
    private void shareOnTwitter() {
        try {
            new TweetComposer.Builder(this)
                    .text(getString(R.string.social_twitter_message_android, "Android"))
                    .url(new URL(getString(R.string.social_link_android)))
                    .show();
        } catch(MalformedURLException e) {
            Timber.e(e, "Twitter URL malformed");
        }
    }
}
