package arena.kids.stories.activities;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.speech.tts.UtteranceProgressListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


import arena.kids.stories.R;

import arena.kids.stories.adapter.RecyclerAdapter;

public class Story extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private ArrayList<String> stringArrayList;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    public static String[] prgmNameList;
    public static String[] prgDetail;
    public static String[] prgIndex;
    public static String[] prgStoriesID;
    public static String[] prgStoriesTitle;
    static int position;
    static int orgpos;
    static int posnew;
    static int posTitle;
    static int max;
    static String pgName;
    private float x1, x2;
    static final int MIN_DISTANCE = 50;
    public Intent ii;
    public String StoryFull;
    private static AdView mAdMobAdView;
    private static InterstitialAd mInterstitialAd;
    public AdRequest adRequest;
    public int adUpgrade;
    public boolean speechButton = false;
    private TextToSpeech tts;
    public int posMain;
    public ScrollView scroll;

    private static String AD_UNIT_ID = "";
    private static final String TAG = "MyActivity";

    private InterstitialAd interstitialAd;
    private FrameLayout adContainerView;
    private AdView adView;

    @SuppressWarnings("ConstantConditions")
    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //kids.stories.utils.Utils.onActivityCreateSetTheme(this);
        final SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        View decorView = getWindow().getDecorView();

        //decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        Window w = getWindow();
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );

        }*/

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_paralax_toolbar);
        View root = findViewById(R.id.cor);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            Insets navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars());

            // int systemSBHeight = getStatusBarHeight();

            // ✅ Take whichever is smaller → avoids emulator exaggeration
            // int topInset = Math.min(statusBars.top, systemSBHeight);

            v.setPadding(
                    v.getPaddingLeft(),
                    0,
                    v.getPaddingRight(),
                    navBars.bottom
            );
            //return WindowInsetsCompat.CONSUMED;
            return insets;
        });
        AD_UNIT_ID = "ca-app-pub-4801686843982324/8423136100";
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("EBB40DF168323087C9461BE093AC4ED0")).build();

        adUpgrade = sharedPref.getInt("adUpgrade", 0);
        new Thread(
                () -> {
                    // Initialize the Google Mobile Ads SDK on a background thread.
                    MobileAds.initialize(getApplicationContext(), initializationStatus -> {
                    });
                    // Load an ad on the main thread.
                    runOnUiThread(
                            () -> {
                                if (adUpgrade == 0) {
                                    adContainerView = findViewById(R.id.ad_view_container);
                                    // Step 1 - Create an AdView and set the ad unit ID on it.
                                    adView = new AdView(this);
                                    adView.setAdUnitId("ca-app-pub-4801686843982324/9736217775");
                                    adContainerView.addView(adView);
                                    loadBanner();
                                    loadAd();
                                }
                                //loadRewAd();
                            });
                })
                .start();

        //tts = new TextToSpeech(this, this);
        //ConvertTextToSpeech();
        //scroll = (ScrollView)  findViewById(R.id.sView);
        ii = getIntent();
        position = Integer.parseInt(ii.getStringExtra("pos"));
        posnew = position;
        prgStoriesID = ii.getStringArrayExtra("maxi");
        pgName = ii.getStringExtra("pgname");
        Log.e("positionfirst", String.valueOf(position));
        posMain = position;
        prgmNameList = getResources().getStringArray(R.array.titles);
        //prgDetail = getResources().getStringArray(R.array.detail);

        if (position >= 100 && position <= 132)
            prgDetail = getResources().getStringArray(R.array.shorts);
        else if (position >= 1 && position <= 38)
            prgDetail = getResources().getStringArray(R.array.moral);
        else if (position >= 201 && position <= 235)
            prgDetail = getResources().getStringArray(R.array.panch);
        else if (position >= 301 && position <= 312)
            prgDetail = getResources().getStringArray(R.array.arabian);
        else if (position >= 401 && position <= 428)
            prgDetail = getResources().getStringArray(R.array.fairy);
        else if (position >= 501 && position <= 509)
            prgDetail = getResources().getStringArray(R.array.newclass);
        else if (position >= 510 && position <= 510) {
            prgDetail = getResources().getStringArray(R.array.newclassic);
            position = position - 9;
        } else if (position >= 511 && position <= 517) {
            prgDetail = getResources().getStringArray(R.array.newclassics);
            position = position - 10;
        }
        else if (position >= 601 && position <= 609)
            prgDetail = getResources().getStringArray(R.array.funny);
        else if (position >= 701 && position <= 717)
            prgDetail = getResources().getStringArray(R.array.bedtime);
        else if (position >= 100 && position <= 132)
            prgDetail = getResources().getStringArray(R.array.animal);
        else if (position >= 801 && position <= 817)
            prgDetail = getResources().getStringArray(R.array.christmas);
        else if (position >= 1001 && position <= 1020)
            prgDetail = getResources().getStringArray(R.array.rhythms);
        else if (position >= 1101 && position <= 1110)
            prgDetail = getResources().getStringArray(R.array.fable);
        else if (position >= 1201 && position <= 1210)
            prgDetail = getResources().getStringArray(R.array.inspire);
        else if (position >= 1301 && position <= 1310)
            prgDetail = getResources().getStringArray(R.array.motive);
        else if (position >= 1401 && position <= 1410)
            prgDetail = getResources().getStringArray(R.array.akbar);
        else if (position >= 1501 && position <= 1509)
            prgDetail = getResources().getStringArray(R.array.tenali);
        else if (position >= 1601 && position <= 1602)
            prgDetail = getResources().getStringArray(R.array.pic);

        position = (position - (100 * ((position + 50) / 100))) - 1;
        Log.e("positionn", String.valueOf(position));
        prgIndex = getResources().getStringArray(R.array.id);


        orgpos = posnew;
        posTitle = 1;
        prgStoriesTitle = getResources().getStringArray(R.array.id);
        for (int i = 0; i <= prgStoriesTitle.length - 1; i++) {
            Log.i("compareing post" + String.valueOf(prgStoriesTitle[i]), String.valueOf(posnew));
            if (Integer.parseInt(prgStoriesTitle[i]) == posnew) {
                posTitle = i;
                Log.i("orgposposTitle" + String.valueOf(i), String.valueOf(posTitle));
                break;
            }
        }
        for (int i = 0; i <= prgIndex.length - 1; i++) {
            //Log.i("compareing pos" + String.valueOf(prgIndex[i]), String.valueOf(position));
            if (Integer.parseInt(prgIndex[i]) == posnew) {
                posnew = i;
                Log.i("orgposposnew" + String.valueOf(i), String.valueOf(posnew));
                break;
            }
        }

        StoryFull = "?." + prgDetail[position];
        //System.out.println("storyfulll" + StoryFull);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

// Load your back icon
        Drawable upArrow = AppCompatResources.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow = upArrow.mutate();

            // Wrap inside a LayerDrawable so we can move it
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{upArrow});

            // Shift it: left, top, right, bottom (in pixels)
            int shiftLeft = 0;
            int shiftTop = 100;   // move down a bit
            int shiftRight = 0;
            int shiftBottom = 0;

            layerDrawable.setLayerInset(0, shiftLeft, shiftTop, shiftRight, shiftBottom);

            // Apply shifted arrow
            getSupportActionBar().setHomeAsUpIndicator(layerDrawable);
        }

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        //final int leftPadding = (int) getResources().getDimension(R.dimen._10sdp);

        collapsingToolbar.setTitle(prgmNameList[posTitle]);
        //collapsingToolbar.setexp(R.drawable.rounded_corners);
        //collapsingToolbar.setBackgroundResource(R.drawable.rounded_corners);

        toolbar.inflateMenu(R.menu.storymenu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                SharedPreferences.Editor e = sharedPref.edit();
                int ngt = sharedPref.getInt("night", 0);
                ngt = ngt == 0 ? 1 : 0;
                e.putInt("night", ngt);
                //e.clear();
                e.apply();
                e.commit();
                adapter.notifyDataSetChanged();
                return true;
            }
        });


        final Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.angella);
        collapsingToolbar.setCollapsedTitleTypeface(tf);

        collapsingToolbar.setExpandedTitleTypeface(tf);

        ImageView img = (ImageView) findViewById(R.id.header);
        String uri = "@drawable/a" + String.valueOf(orgpos);
        int imageResource = this.getResources().getIdentifier(uri, null, this.getPackageName());
        //Drawable res = this.getResources().getDrawable(imageResource);
        img.setImageResource(imageResource);

        Glide.with(getApplicationContext())
                .load(imageResource).signature(new ObjectKey("56"))
                .into(img);

        CoordinatorLayout cly = (CoordinatorLayout) findViewById(R.id.cor);
        if (sharedPref.getInt("theme", 0) == 0)
            cly.setBackgroundColor(getResources().getColor(R.color.white));
        else
            cly.setBackgroundColor(getResources().getColor(R.color.dark_3));

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);//

        setData(); //adding data to array list

        System.out.println("Sotry detail Main Pos" + posMain);
        adapter = new RecyclerAdapter(this, stringArrayList, prgDetail[position], pgName, posMain, 0, 0);
        recyclerView.setAdapter(adapter);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout
                .LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = getNewHeight();
        //recyclerView.setLayoutParams(layoutParams);

        FloatingActionButton myFabShare = (FloatingActionButton) findViewById(R.id.myFabShare);
        myFabShare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e("fabclick", "share");

                String msg = prgmNameList[posTitle];

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, msg + " " + Uri.parse("http://play.google.com/store/apps/details?id="
                        + getPackageName()));

                startActivity(Intent.createChooser(share, "Share Me"));


                //speakOut();
            }
        });

        FloatingActionButton myFabFav = (FloatingActionButton) findViewById(R.id.myFabFav);
        myFabFav.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //doMyThing();
                Log.e("fabclick", "fav");


                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);

                Set<String> set = sharedPref.getStringSet("fav", new HashSet<String>());

                Log.d("FavSetstore", "" + set);
                //Set<String> set = new HashSet<String>();
                set.add(String.valueOf(posnew));
                Log.e("Fav Adding Size - ", String.valueOf(set.size()));
                SharedPreferences.Editor e = sharedPref.edit();
                e.putStringSet("fav", set);
                //e.clear();
                e.apply();
                e.commit();
                Toast.makeText(getApplicationContext(), "Woooh! Story added to your Favourites!",
                        Toast.LENGTH_LONG).show();
            }
        });
        FloatingActionButton myFabFont = (FloatingActionButton) findViewById(R.id.myFabFont);
        myFabFont.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //doMyThing();
                Log.e("fabclick", "font");


                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
                String fontSize = sharedPref.getString("fontSize", "3");
                Log.d("FontSetstore", "" + fontSize);

                if (fontSize.contains("1"))
                    fontSize = "2";
                else if (fontSize.contains("2"))
                    fontSize = "3";
                else if (fontSize.contains("3"))
                    fontSize = "4";
                else if (fontSize.contains("4"))
                    fontSize = "5";
                else if (fontSize.contains("5"))
                    fontSize = "1";


                //Set<String> set = new HashSet<String>();
//                set.add(String.valueOf(posnew));
//                Log.e("Fav Adding Size - ", String.valueOf(set.size()));
                SharedPreferences.Editor e = sharedPref.edit();
                e.putString("fontSize", fontSize);
                //e.clear();
                e.apply();
                e.commit();

                adapter.notifyDataSetChanged();
                // Toast.makeText(getApplicationContext(), "Woooh! Story added to your Favourites!",
                //       Toast.LENGTH_LONG).show();
            }
        });

        FloatingActionButton myFabNext = (FloatingActionButton) findViewById(R.id.myFabNext);
        myFabNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //doMyThing();
                //Log.e("fabclick", String.valueOf(position) + "|" + String.valueOf(prgStoriesID.length));
                /*for (int i=0;i<=prgStoriesID.length-1;i++)
                {
                    Log.e("fabclick" +String.valueOf(orgpos), prgStoriesID[i] );
                }*/

                if (Arrays.asList(prgStoriesID).contains(String.valueOf(orgpos + 1))) {
                    // true
                    Intent i = new Intent(getApplicationContext(), Story.class);
                    i.putExtra("maxi", prgStoriesID);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("pos", String.valueOf(orgpos + 1));
                    startActivity(i);
                }

            }
        });

        FloatingActionButton myFabPrev = (FloatingActionButton) findViewById(R.id.myFabPrev);
        myFabPrev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                //e.commit();
                if (Arrays.asList(prgStoriesID).contains(String.valueOf(orgpos - 1))) {
                    Log.e("fabclick", "prev");
                    Intent i = new Intent(getApplicationContext(), Story.class);

                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("pos", String.valueOf(orgpos - 1));
                    i.putExtra("maxi", prgStoriesID);
                    startActivity(i);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //menu.add(1, 1, 1, "Night Mode").setIcon(R.drawable.ic_wb_sunny_black_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);


        return super.onCreateOptionsMenu(menu);
    }


    private void setData() {
        stringArrayList = new ArrayList<>();

        for (int i = 0; i <= 1; i++) {
            stringArrayList.add("It  ength, but could not lift the Ram. ");
        }
    }

    private void loadBanner() {
        // Create an ad request. Check your logcat output for the hashed device ID
        // to get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this
        // device."
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("EBB40DF168323087C9461BE093AC4ED0")).build();
        MobileAds.initialize(Story.this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                    }
                });
        Bundle extras = new Bundle();
        if (position % 5 == 0)
            extras.putString("collapsible", "bottom");
        MobileAds.setRequestConfiguration(configuration);
        AdRequest adRequest =
                new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras)
                        .build();

        //AdSize adSize = getBannerAdSize();
        // Step 4 - Set the adaptive ad size on the ad view.
        adView.setAdSize(getBannerAdSize());


        // Step 5 - Start loading the ad in the background.
        adView.loadAd(adRequest);
    }

    private AdSize getBannerAdSize() {
        Display display = Story.this.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(Story.this.getApplicationContext(), adWidth);
    }

    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                AD_UNIT_ID,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        Story.this.interstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        // Toast.makeText(Story.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        Story.this.interstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        Story.this.interstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showInterstitial();
                                //Do something after 100ms
                            }
                        }, 3000);

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        interstitialAd = null;

                        String error =
                                String.format(
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                        Log.e(TAG + "error ad", error);

                    }
                });
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        SharedPreferences sharedPref = Story.this.getSharedPreferences("Shared", MODE_PRIVATE);
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        long rte = sharedPref.getLong("adtime", 0);
        int rtetotal = sharedPref.getInt("adtimeTotal", 0);
        System.out.println("AdDateDiff" + rte + " |" + c.getTimeInMillis() + " |" + (rte - c.getTimeInMillis()) + "|total" + rtetotal);
        if (interstitialAd != null && adUpgrade == 0) {
            SharedPreferences.Editor e = sharedPref.edit();
            if ((rte - c.getTimeInMillis() <= 0 || rtetotal >= 4)) {
                // mInterstitialAd.show();

                c.add(Calendar.SECOND, 60);
                System.out.println("addatematch-showingads");
                e.putLong("adtime", c.getTimeInMillis());
                e.putInt("adtimeTotal", 0);
                e.putInt("firstTime", 0);
                //e.clear();
                //e.apply();
                e.commit();

                interstitialAd.show(this);
                loadAd();
            }
        } else {
            //Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();

        }
        SharedPreferences.Editor e = sharedPref.edit();
        rtetotal = sharedPref.getInt("adtimeTotal", 0);
        int rtetotal2 = sharedPref.getInt("adtimeTotal2", 0);
        rtetotal = rtetotal + 1;
        rtetotal2 = rtetotal2 + 1;
        e.putInt("adtimeTotal", rtetotal);
        e.putInt("adtimeTotal2", rtetotal2);
        //e.clear();
        e.apply();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub

        // if(tts != null){

        // tts.stop();
        //  tts.shutdown();
        // }
        super.onPause();
    }

    public int getNewHeight() {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        Log.i("height-Screen", String.valueOf(dpHeight));
        Log.i("width-Screen", String.valueOf(dpWidth));

        int newHeight = 32;
        if (dpHeight > 720)
            newHeight = 90;
        else if (dpHeight <= 720 && dpHeight > 400)
            newHeight = 50;


        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, r.getDisplayMetrics());
        Log.i("screen-pixel", String.valueOf(Math.round(px)));

        return Math.round(px);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        System.out.print("contextnew");
        menu.clear();
        super.onCreateContextMenu(menu, v, menuInfo);

        // do something here. To test, you could  Add a toast
    }


    @Override
    public void onBackPressed() {

        if (ii.getStringExtra("not") != null) {
            this.startActivity(new Intent(Story.this, arena.kids.stories.activities.MainActivity.class));
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // Left to Right swipe action
                    if (x2 > x1) {
                        if (Arrays.asList(prgStoriesID).contains(String.valueOf(orgpos - 1))) {
                            Log.e("fabclick", "prev");
                            Intent i = new Intent(getApplicationContext(), Story.class);

                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("pos", String.valueOf(orgpos - 1));
                            i.putExtra("maxi", prgStoriesID);
                            startActivity(i);
                        }
                        // Toast.makeText(this, "Left to Right swipe [Next]", Toast.LENGTH_SHORT).show ();
                    }

                    // Right to left swipe action
                    else {
                        if (Arrays.asList(prgStoriesID).contains(String.valueOf(orgpos + 1))) {
                            // true
                            Intent i = new Intent(getApplicationContext(), Story.class);
                            i.putExtra("maxi", prgStoriesID);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("pos", String.valueOf(orgpos + 1));
                            startActivity(i);
                        }


                        // Toast.makeText(this, "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show ();
                    }

                } else {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.UK);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Toast.makeText(getApplicationContext(), "Meanwhile, turtle (tortoise) continue to walk slowly, until he reaches the finish line. The overslept hare sees the turtle on the other side of the finish line. The turtle had beaten him in the race.w", Toast.LENGTH_SHORT).show();
            } else {
                // button.setEnabled(true);
            }

        } else {
            // Toast.makeText(getApplicationContext(), "Init failed", Toast.LENGTH_SHORT).show();
        }
    }

    static int lastPara = 0;
    static int startH = 0;

    private void speakOut() {


        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {

                final String keyword = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lastPara = Integer.parseInt(keyword.replace("Sam", "").toString());//String.valueOf( lastPara);
                        //Toast.makeText(getApplicationContext(), "Started" + keyword, Toast.LENGTH_SHORT).show();

                        String[] splitspeech = StoryFull.split("\\.");

                        //for (int i = 0; i < splitspeech.length; i++) {
                        // {

                        synchronized (recyclerView) {
                            if (splitspeech[lastPara].length() > 0) {
                                //Toast.makeText(getApplicationContext(), "Started" + splitspeech[lastPara], Toast.LENGTH_SHORT).show();
                                System.out.println("Started para" + keyword + splitspeech[lastPara + 1]);
                                adapter.starth = startH;
                                //adapter.starth = 2;
                                adapter.endh = splitspeech[lastPara + 1].length() + 2 + startH;
                                //adapter.endh = 5;
                                startH = startH + splitspeech[lastPara + 1].length() + 1;

                                // recyclerView.invalidate();
                                adapter.notifyDataSetChanged();
                            }

                        }
                        // }

                    }
                });
            }

            @Override
            public void onDone(String s) {
                final String keyword = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(getApplicationContext(), keyword + "Done ", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String s) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Toast.makeText(getApplicationContext(), "Error ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");

        String text = "Meanwhile, turtle (tortoise) continue to walk slowly, until he reaches the finish line. The overslept hare sees the turtle on the other side of the finish line. The turtle had beaten him in the race.";
        tts.setSpeechRate(0.9f);
        if (!tts.isSpeaking()) {
            // tts.speak(StoryFull, TextToSpeech.QUEUE_FLUSH, params, "Dummy String");

            HashMap<String, String> myHash = new HashMap<String, String>();
            //myHash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "Sam0");

            String[] splitspeech = StoryFull.split("\\.");

            for (int i = lastPara; i < splitspeech.length; i++) {

                if (i == 0) { // Use for the first splited text to flush on audio stream

                    tts.speak(splitspeech[i].toString().trim(), TextToSpeech.QUEUE_ADD, myHash);

                } else { // add the new test on previous then play the TTS

                    tts.speak(splitspeech[i].toString().trim(), TextToSpeech.QUEUE_ADD, myHash);
                }
                int k = i;
                tts.playSilentUtterance(750, TextToSpeech.QUEUE_ADD, "Sam" + k);
            }

        } else {

            tts.stop();
        }
    }

    @Override
    public void onDestroy() {
        System.out.println("destroying");
        if (tts != null) {
            tts.stop();
            lastPara = 0;
            startH = 0;
            tts.shutdown();
        }
        super.onDestroy();
    }
}
