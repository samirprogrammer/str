package arena.kids.stories.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.WindowManager;

import arena.kids.stories.R;

public class SplashActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 0000;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
          getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        setContentView(R.layout.splash);

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPref.edit();
        int rtetotal = sharedPref.getInt("adtimeTotal3", 0);
        rtetotal=rtetotal+1;

        e.putInt("adtimeTotal3", rtetotal);
        e.apply();
        e.commit();

        System.out.println("I m opening for : " + rtetotal);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                //Intent mainIntent = new Intent(this, MainActivity.class);
                Intent mainIntent = new Intent(SplashActivity.this, arena.kids.stories.activities.MainActivity.class);

                if (getIntent().getExtras() != null) {
                    String[] prgmIndex = getResources().getStringArray(R.array.id);
                    for (String key : getIntent().getExtras().keySet()) {
                        String value = getIntent().getExtras().getString(key);
                        Log.d("Story", "Key: " + key + " Value: " + value);
                        if(getIntent().getExtras().getString("pos")!=null) {
                            mainIntent = new Intent(SplashActivity.this, arena.kids.stories.activities.Story.class);
                            mainIntent.putExtra("pos", getIntent().getExtras().getString("pos"));
                            mainIntent.putExtra("maxi", prgmIndex);
                            mainIntent.putExtra("not", "yes");
                        }
                        if(getIntent().getExtras().getString("cat")!=null) {
                            mainIntent = new Intent(SplashActivity.this, arena.kids.stories.activities.CatActivity.class);
                            mainIntent.putExtra("pgname", getIntent().getExtras().getString("cat"));
                            mainIntent.putExtra("not", "yes");
                        }
                        if(getIntent().getExtras().getString("play")!=null) {
                            Log.d("StoryPlay", "KeyPlay: " + key + " Value: " + getIntent().getExtras().getString("play"));
                           // mainIntent = new Intent(SplashActivity.this, arena.kids.stories.activities.CatActivity.class);
                            //mainIntent.putExtra("pgname", getIntent().getExtras().getString("cat"));


                            Intent intent;

                                final String appPackageName =  getIntent().getExtras().getString("play"); // getPackageName()
                                try {
                                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                                }
                                int notificaionId = 1;
                            PendingIntent pIntent = PendingIntent.getActivity(
                                    getApplicationContext(),
                                    0,
                                    intent,
                                    PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
                            );
                                NotificationCompat.BigTextStyle bigTextNotiStyle = null;
                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                int color = ContextCompat.getColor(getApplicationContext(), R.color.accentLight);
                                NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                                        //.setSmallIcon(R.drawable.ic_launcher)
                                        .setContentTitle("" + getIntent().getExtras().getString("desc"))
                                        .setContentText("" + getIntent().getExtras().getString("desc"))
                                        .setStyle(bigTextNotiStyle)
                                        .setAutoCancel(true)
                                        .setColor(color)
                                        .setContentIntent(pIntent)
                                        .setLights(Color.RED, 3000, 3000);
                                notificationManager.notify(notificaionId, mBuilder.build());

                            Uri uri = Uri.parse("market://details?id=" + getIntent().getExtras().getString("play"));
                            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                            try {
                                startActivity(goToMarket);
                            } catch (ActivityNotFoundException e) {
                                startActivity(new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("http://play.google.com/store/apps/details?id="
                                                + getIntent().getExtras().getString("play"))));
                            }
                        }
                    }
                }

                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
