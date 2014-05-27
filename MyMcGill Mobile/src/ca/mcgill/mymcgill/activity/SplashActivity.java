package ca.mcgill.mymcgill.activity;

import android.content.Intent;
import android.os.Bundle;

import ca.mcgill.mymcgill.App;
import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.base.BaseActivity;
import ca.mcgill.mymcgill.object.ConnectionStatus;
import ca.mcgill.mymcgill.util.Clear;
import ca.mcgill.mymcgill.util.Connection;
import ca.mcgill.mymcgill.util.Constants;
import ca.mcgill.mymcgill.util.Load;

/**
 * Author: Julien
 * Date: 22/01/14, 7:34 PM
 */
public class SplashActivity extends BaseActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Get the username and password stored
        final String username = Load.loadUsername(this);
        final String password = Load.loadPassword(this);

        //If one of them is null, send the user to the LoginActivity
        if(username == null || password == null){
            //If we need to go back to the login, make sure to
            //delete anything with the previous user's info
            //Clear.clearAllInfo(this);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        //If not, try to log him in, and send him to the LoginActivity if there's a problem
        else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Set the username and password
                    Connection.getInstance().setUsername(username + SplashActivity.this.getResources().getString(R.string.login_email));
                    Connection.getInstance().setPassword(password);
                    ConnectionStatus connectionResult = Connection.getInstance().connectToMinerva(SplashActivity.this);
                    //Successful connection: ScheduleActivity
                    if(connectionResult == ConnectionStatus.CONNECTION_OK ||
                            connectionResult == ConnectionStatus.CONNECTION_NO_INTERNET){
                        startActivity(new Intent(SplashActivity.this, App.getHomePage().getHomePageClass()));
                        finish();
                    }
                    else{
                        Clear.clearAllInfo(SplashActivity.this);
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        intent.putExtra(Constants.CONNECTION_STATUS, connectionResult);
                        startActivity(intent);
                        finish();
                    }
                }
            }).start();
        }
    }
}