package arena.kids.stories.activities;

import static android.service.controls.ControlsProviderService.TAG;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import arena.kids.stories.R;

import arena.kids.stories.utils.Utils;
import kids.stories.adapter.CatAdapter;

public class CatActivity extends AppCompatActivity {
    public static String[] prgmNameList;
    public static String[] prgmIndex;
    private GridView gridView;
    InterstitialAd mInterstitialAd;
    private static AdView mAdMobAdView;
    Intent i;
    private static Activity activity;
    int adUpgrade;
    private FirebaseAnalytics mFirebaseAnalytics;
    private void setScreen() {

        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);

        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setNavigationBarContrastEnforced(false);
            window.setStatusBarContrastEnforced(false); // ✅ no forced dark overlay
        }
        View root = findViewById(R.id.rlNew);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            Insets navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars());

           // int systemSBHeight = getStatusBarHeight();

            // ✅ Take whichever is smaller → avoids emulator exaggeration
           // int topInset = Math.min(statusBars.top, systemSBHeight);

            v.setPadding(v.getPaddingLeft(), statusBars.top, v.getPaddingRight(), 0);

            return WindowInsetsCompat.CONSUMED;
            //return insets;
        });
    }
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_cat);
        setScreen();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // mAdMobAdView = (AdView) findViewById(R.id.ad_view);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        i = getIntent();
        String pgname = i.getStringExtra("pgname");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SpannableString s = new SpannableString(pgname);
        s.setSpan(new kids.stories.utils.TypefaceSpan(this, "angella.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new RelativeSizeSpan(1.0f), 0, s.length(), 0);
        getSupportActionBar().setTitle(s);
        activity = this;

        getStoriesByCat(pgname);
        //prgmNameList = getResources().getStringArray(R.array.titles);
        //prgmIndex = getResources().getStringArray(R.array.id);

        gridView = (GridView) findViewById(R.id.grid_view);
        // gv=(GridView) findViewById(R.id.gridView1);
        gridView.setAdapter(new CatAdapter(this, prgmNameList, prgmNameList, prgmNameList, prgmIndex, pgname));

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
        adUpgrade = sharedPref.getInt("adUpgrade", 0);

        System.out.println("adUpgrade cat" + adUpgrade);
        /*if (adUpgrade == 0) {
            AdView mAdView = (AdView) findViewById(R.id.ad_view);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("22606D3F1E8932B94418861535B62D90")
                    .addTestDevice("C1E8777CF5A4E669CCF47AE9B9D33FB1").build();
            mAdView.loadAd(adRequest);
        }*/
        MobileAds.initialize(this, initializationStatus -> {});
        if (adUpgrade == 0) {
            initializeAds();

            // Test: Check container visibility after delay
            new Handler().postDelayed(() -> {
                Log.d("AdMob", "Post-delay check - Container visible: " +
                        (adContainerView != null && adContainerView.getVisibility() == View.VISIBLE));
                if (adContainerView != null) {
                    Log.d("AdMob", "Container height: " + adContainerView.getHeight());
                }
            }, 2000);
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout
                .LayoutParams.WRAP_CONTENT);
        if (adUpgrade == 0)
            layoutParams.bottomMargin = getNewHeight();
        else
            layoutParams.bottomMargin = 0;
        // layoutParams.bottomMargin = getNewHeight();
       // gridView.setLayoutParams(layoutParams);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle params = new Bundle();
        String pg=pgname.toLowerCase().replace(" ","_").replace("story","").replace("stories","");
        params.putString(pg, pgname);
        mFirebaseAnalytics.logEvent(pg, params);

      /*  FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.d(TAG, "FirebaseToken" + token);
                        //Toast.makeText(CatActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });*/

       /* runOnUiThread(
                () -> {
                    Bundle params1 = new Bundle();
                    params1 = new Bundle();
                    params1.putString("cat_clicked", pgname);
                    mFirebaseAnalytics.logEvent("cat_clicked", params1);
                });*/

    }

    public int getNewHeight() {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        Log.i("height-Screen", String.valueOf(dpHeight));
        Log.i("width-Screen", String.valueOf(dpWidth));

        int newHeight = 0;
       /* if (dpHeight > 720)
            newHeight = 90;
        else if (dpHeight <= 720 && dpHeight > 400)
            newHeight = 50;*/

        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newHeight, r.getDisplayMetrics());
        Log.i("screen-pixel", String.valueOf(Math.round(px)));

        return Math.round(px);

    }

    private FrameLayout adContainerView;
    private AdView adView;

    private void initializeAds() {
        MobileAds.initialize(activity, initializationStatus -> {
            Log.d("AdMob", "✅ Mobile Ads initialized");
        });

        RequestConfiguration configuration =
                new RequestConfiguration.Builder()
                        .setTestDeviceIds(Arrays.asList(
                                "EBB40DF168323087C9461BE093AC4ED0",
                                "22606D3F1E8932B94418861535B62D90",
                                "C1E8777CF5A4E669CCF47AE9B9D33FB1",
                                AdRequest.DEVICE_ID_EMULATOR
                        ))
                        .build();
        MobileAds.setRequestConfiguration(configuration);

        adContainerView = findViewById(R.id.ad_view_container);

        // CRITICAL: Set container to VISIBLE and proper height
        adContainerView.setVisibility(View.VISIBLE);

        // Set minimum height for container
        //int minHeightInPx = (int) (80 * getResources().getDisplayMetrics().density);
        //adContainerView.setMinimumHeight(minHeightInPx);

        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-4801686843982324/9736217775"); // Test ID first

        // CRITICAL: Set ad view to VISIBLE immediately
        adView.setVisibility(View.VISIBLE);

        FrameLayout.LayoutParams adParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        adParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        adView.setLayoutParams(adParams);

        adContainerView.addView(adView);

        // Load banner after short delay to ensure layout is ready
        new Handler().postDelayed(() -> {
            loadBanner();
        }, 300);
    }

    private void loadBanner() {
        Log.d("AdMob", "=== loadBanner() called ===");

        // FORCE VISIBILITY - This is the key fix
        adContainerView.setVisibility(View.VISIBLE);
        adView.setVisibility(View.VISIBLE);

        // Use standard banner size first for testing
        AdSize adSize = getBannerAdSize(); // Use standard 320x50 banner first

        Log.d("AdMob", "Using AdSize: " + adSize);

        adView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d("AdMob", "✅✅✅ BANNER LOADED SUCCESSFULLY");

                // CRITICAL: Force visibility and layout
                runOnUiThread(() -> {
                    adContainerView.setVisibility(View.VISIBLE);
                    adView.setVisibility(View.VISIBLE);

                    // Change colors for visual confirmation
                   // adContainerView.setBackgroundColor(Color.GREEN);
                    //adView.setBackgroundColor(Color.YELLOW);

                    // Force layout update
                    adContainerView.requestLayout();
                    adView.requestLayout();

                    updateAdMargin(true);

                    // Log final dimensions after layout
                    adContainerView.post(() -> {
                        Log.d("AdMob", "After layout - Container: " +
                                adContainerView.getWidth() + "x" + adContainerView.getHeight());
                        Log.d("AdMob", "After layout - AdView: " +
                                adView.getWidth() + "x" + adView.getHeight());
                    });
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                Log.e("AdMob", "❌❌❌ BANNER FAILED: " + adError.toString());
                runOnUiThread(() -> {
                    updateAdMargin(false);
                });
            }

            @Override
            public void onAdOpened() {
                Log.d("AdMob", "Ad opened");
            }

            @Override
            public void onAdImpression() {
                Log.d("AdMob", "💰 Ad impression recorded");
            }
        });

        Log.d("AdMob", "Starting ad load...");
        adView.loadAd(adRequest);
    }

    private void updateAdMargin(boolean adVisible) {
        runOnUiThread(() -> {
            if (adUpgrade != 0) {
                adContainerView.setVisibility(View.GONE);
                return;
            }

            if (adVisible) {
                // Calculate proper margin based on ad size
                int margin = (int) (60 * getResources().getDisplayMetrics().density); // ~60dp

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) gridView.getLayoutParams();
                if (params == null) {
                    params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                }
                params.bottomMargin = margin;
                gridView.setLayoutParams(params);

                Log.d("AdMob", "Set bottom margin: " + margin + "px");
            } else {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) gridView.getLayoutParams();
                if (params != null) {
                    params.bottomMargin = 0;
                    gridView.setLayoutParams(params);
                }
            }
        });
    }
    private AdSize getBannerAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(getApplicationContext(), adWidth);
    }


    private View.OnClickListener onClickListener(final Class<?> c) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CatActivity.this, c);
                startActivity(i);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, "Share").setIcon(R.drawable.ic_share_white_24dp_2).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        //menu.add(2, 2, 2, "Favourites").setIcon(R.drawable.ic_stars_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    public void getStoriesByCat(String pgname) {
        int startindex = 0;
        int lastIndex = 37;
        if (pgname.contains("Short")) {
            startindex = 38; //101
            lastIndex = 69; //132
        } else if (pgname.contains("Panch")) {
            startindex = 70; //201
            lastIndex = 104; //235
        } else if (pgname.contains("Arabian")) {
            startindex = 105; //301
            lastIndex = 116; //312
        } else if (pgname.contains("Fairy")) {
            startindex = 117; //401
            lastIndex = 144; //428
        } else if (pgname.contains("Classic")) {
            startindex = 145; //501
            lastIndex = 161; //514
        } else if (pgname.contains("Funny")) {
            startindex = 162; //601
            lastIndex = 170; //609
        } else if (pgname.contains("Bed")) {
            startindex = 171; //701
            lastIndex = 187; //717
        } else if (pgname.contains("Animal")) {
            startindex = 5; //301
            lastIndex = 25; //312
        } else if (pgname.contains("Chri")) {
            startindex = 188; //801
            lastIndex = 204; //312
        } else if (pgname.contains("Rhy")) {
            startindex = 205; //801
            lastIndex = 224; //312
        } else if (pgname.contains("Fab")) {
            startindex = 225; //801
            lastIndex = 234; //312
        } else if (pgname.contains("Ins")) {
            startindex = 235; //801
            lastIndex = 243; //312
        } else if (pgname.contains("Mot")) {
            startindex = 244; //801
            lastIndex = 253; //312
        } else if (pgname.contains("Akbar")) {
            startindex = 254; //801
            lastIndex = 263; //312
        } else if (pgname.contains("Tenali")) {
            startindex = 264; //801
            lastIndex = 272; //312
        } else if (pgname.contains("Picto")) {
            startindex = 273; //801
            lastIndex = 275; //312
        }


        int len = (lastIndex + 1) - startindex;
        String[] prgmNameList2 = new String[0];
        String[] prgmIndex2 = new String[0];
        prgmNameList = getResources().getStringArray(R.array.titles);
        prgmIndex = getResources().getStringArray(R.array.id);

        for (int i = 0; i <= prgmNameList.length - 1; i++) {
            Log.i("prgmIndexii" + String.valueOf(i), String.valueOf(prgmNameList[i]));
        }
        for (int i = 0; i <= prgmIndex.length - 1; i++) {
            Log.i("prgmIndexii" + String.valueOf(i), String.valueOf(prgmIndex[i]));
        }
        Set<String> _setFromPrefs;
        if (pgname.contains("Fav")) {
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);

            //Set<String> set = mSharedPreferences.sharedPref("fav", null);

            //Set<String> set = mSharedPreferences.sharedPref("fav", null);
            Set<String> set = sharedPref.getStringSet("fav", new HashSet<String>());
            _setFromPrefs = new HashSet<>(set);
            Object[] array = set.toArray();
            if (_setFromPrefs != null) {
                prgmNameList2 = new String[array.length];
                prgmIndex2 = new String[array.length];

                for (int i = 0; i <= array.length - 1; i++) {
                    int ind = Integer.parseInt(String.valueOf(array[i]));
                    Log.i("indexfav" + array[i], String.valueOf(array[i]));
                    Log.i("indexi" + array[i], String.valueOf(prgmNameList[ind]));
                    Log.i("indexi" + String.valueOf(i), String.valueOf(prgmIndex[ind]));

                    prgmNameList2[i] = String.valueOf(prgmNameList[ind]);
                    prgmIndex2[i] = String.valueOf(prgmIndex[ind]);

                }

            }
            if (array.length <= 0) {
                Toast msg = Toast.makeText(this, "Oops! There is no story in your Favourites!", Toast.LENGTH_LONG);
                msg.show();
            }
        } else {
            prgmNameList2 = new String[len];
            prgmIndex2 = new String[len];
            int k = startindex;
            //Log.e("40111",String.valueOf(prgmIndex[118]));
            for (int i = 0; i <= len - 1; i++) {
                prgmNameList2[i] = prgmNameList[k];
                prgmIndex2[i] = prgmIndex[k];
                Log.i("indexi" + String.valueOf(i), String.valueOf(prgmNameList2[i]));
                Log.i("indexi" + String.valueOf(i), String.valueOf(prgmIndex2[i]));
                k++;
            }
        }
        prgmNameList = prgmNameList2;
        prgmIndex = prgmIndex2;
        Log.i("Lengh", String.valueOf(prgmIndex2.length));
    }

    protected void setData(ArrayList<String> stringArrayList) {
        stringArrayList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            stringArrayList.add("Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item ");
        }
    }


    @Override
    public void onBackPressed() {

        if (i.getStringExtra("not") != null) {
            this.startActivity(new Intent(arena.kids.stories.activities.CatActivity.this, arena.kids.stories.activities.MainActivity.class));
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:


                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, "English Stories - " + Uri.parse("http://play.google.com/store/apps/details?id="
                        + getPackageName()));

                startActivity(Intent.createChooser(share, "Share Me"));

                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
