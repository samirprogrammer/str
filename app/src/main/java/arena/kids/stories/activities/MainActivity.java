package arena.kids.stories.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Looper;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import arena.kids.stories.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.graphics.Rect;
import android.widget.TextView;
import android.widget.Toast;

import arena.kids.stories.adapter.CatMainPageAdapter;
import arena.kids.stories.adapter.activity_main_grid_more_apps;
import arena.kids.stories.adapter.activity_main_grid_story;
import arena.kids.stories.adapter.activity_main_grid_story2;
import arena.kids.stories.utils.Utils;


import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.common.collect.ImmutableList;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements
        PurchasesUpdatedListener {
    public static String[] prgmNameList;
    private static Activity activity;
    public static String[] prgmPics;
    public static String[] prgmStoryList;
    public static String[] prgmIndex;
    InterstitialAd mInterstitialAd;
    private static AdView mAdMobAdView;
    private GridView gridView;
    private String userId;
    private static final String TAG = MainActivity.class.getSimpleName();
    private SimpleCursorAdapter mAdapter;
    private String[] items;
    private String[] itemsid;
    private String[] storylist;
    private BillingClient mBillingClient;
    private long mLastPurchaseClickTime = 0;
    int adUpgrade;
    private List<String> mSkuList = new ArrayList<>();
    private List<SkuDetails> mSkuDetailsList = new ArrayList<>();
    private void setScreen() {

        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        // Transparent bars
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setNavigationBarContrastEnforced(false); // ✅ no forced dark overlay
        }
        View root = findViewById(R.id.rlNew);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            Insets navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars());

            int systemSBHeight = getStatusBarHeight();

            // ✅ Take whichever is smaller → avoids emulator exaggeration
            int topInset = Math.min(statusBars.top, systemSBHeight);

            v.setPadding(
                    v.getPaddingLeft(),
                    statusBars.top,
                    v.getPaddingRight(),
                    navBars.bottom
            );

            return insets;
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

        try {
            FirebaseApp.initializeApp(this);
        } catch (Exception e) {
        }
        Utils.onActivityCreateSetTheme(this);
        //kids.stories.utils.Utils.changeToTheme(this,R.style.AppTheme2);
        //getApplicationContext().setTheme(R.style.AppTheme2);
        setContentView(R.layout.activity_main);
        adContainerView = findViewById(R.id.ad_view_container);
        setScreen();
        redirect();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        items = getResources().getStringArray(R.array.titles);
        itemsid = getResources().getStringArray(R.array.id);
        storylist = getResources().getStringArray(R.array.storylist);
        //mAdMobAdView = (AdView) findViewById(R.id.ad_view);
        handleIntent(getIntent());
        activity = this;


        LinearLayout rl = (LinearLayout) findViewById(R.id.rlNew);
        //rl.setBackgroundColor(Color.LTGRAY);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SpannableString s = new SpannableString("Story Time!");
        s.setSpan(new kids.stories.utils.TypefaceSpan(this, "outfit_regular.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new RelativeSizeSpan(1.0f), 0, s.length(), 0);
        //s.setSpan(new ForegroundColorSpan(0xEEEEEEEE), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setElevation(0);
        // getSupportActionBar().setHideOnContentScrollEnabled(true);

        prgmNameList = getResources().getStringArray(R.array.storylist);
        prgmPics = getResources().getStringArray(R.array.storylistPics);

        RecyclerView recyclerView2 = (RecyclerView) findViewById(R.id.grdCat);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        CatMainPageAdapter mAdapter2 = new CatMainPageAdapter(MainActivity.this, prgmNameList, prgmPics);
        recyclerView2.setAdapter(mAdapter2);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2, RecyclerView.HORIZONTAL, false);
        recyclerView2.setLayoutManager(mLayoutManager);

        getStoriesByCat("Rhymes");
        RecyclerView main_grid_story = (RecyclerView) findViewById(R.id.grdStory);
        main_grid_story.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        activity_main_grid_story mAdapter3 = new activity_main_grid_story(MainActivity.this, prgmStoryList, prgmIndex, "Rhymes");
        main_grid_story.setAdapter(mAdapter3);

        getStoriesByCat("Panch");
        RecyclerView grdStoryPanch = (RecyclerView) findViewById(R.id.grdStoryPanch);
        grdStoryPanch.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        activity_main_grid_story mAdapter4 = new activity_main_grid_story(MainActivity.this, prgmStoryList, prgmIndex, "Rhymes");
        grdStoryPanch.setAdapter(mAdapter4);

        getStoriesByCat("Classic");
        RecyclerView grdStoryClassic = (RecyclerView) findViewById(R.id.grdStoryClassic);
        grdStoryClassic.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        activity_main_grid_story2 mAdapter5 = new activity_main_grid_story2(MainActivity.this, prgmStoryList, prgmIndex, "Rhymes");
        grdStoryClassic.setAdapter(mAdapter5);

        getStoriesByCat("Fairy");
        RecyclerView grdStoryFairy = (RecyclerView) findViewById(R.id.grdStoryFairy);
        grdStoryFairy.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        activity_main_grid_story mAdapter6 = new activity_main_grid_story(MainActivity.this, prgmStoryList, prgmIndex, "Rhymes");
        grdStoryFairy.setAdapter(mAdapter6);

        prgmStoryList = getResources().getStringArray(R.array.moreapps);
        RecyclerView grdMoreApps = (RecyclerView) findViewById(R.id.grdMoreApps);
        grdMoreApps.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        activity_main_grid_more_apps mAdapter7 = new activity_main_grid_more_apps(MainActivity.this, prgmStoryList, prgmStoryList, "Rhymes");
        grdMoreApps.setAdapter(mAdapter7);

        getStoriesByCat("Moral");
        RecyclerView grdStoryMoral = (RecyclerView) findViewById(R.id.grdStoryMoral);
        grdStoryMoral.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        activity_main_grid_story mAdapter8 = new activity_main_grid_story(MainActivity.this, prgmStoryList, prgmIndex, "Rhymes");
        grdStoryMoral.setAdapter(mAdapter8);

        getStoriesByCat("Animal");
        RecyclerView grdAnimals = (RecyclerView) findViewById(R.id.grdAnimals);
        grdAnimals.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        activity_main_grid_story mAdapter9 = new activity_main_grid_story(MainActivity.this, prgmStoryList, prgmIndex, "Animals");
        grdAnimals.setAdapter(mAdapter9);


        String[] sOfTheDay = getResources().getStringArray(R.array.storyoftheday);
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(sOfTheDay.length);
        System.out.println("StoryOfTheDay" + sOfTheDay[index]);
        ImageView catThumb = findViewById(R.id.catThumb);
        ImageView imgTheme = findViewById(R.id.imgTheme);
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);

        if (sharedPref.getInt("theme", 0) == 0) {
            imgTheme.setImageResource(R.drawable.lights);
        } else {
            imgTheme.setImageResource(R.drawable.nights);
        }

        String uri = "@drawable/a" + sOfTheDay[index];
        int imageResource = getApplicationContext().getResources().getIdentifier(uri, null, getApplicationContext().getPackageName());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(25));
        Glide.with(getApplicationContext())
                .load(imageResource).signature(new ObjectKey("56"))
                .into(catThumb);

        catThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                //if(pgName.contains("Picto"))
                //    i = new Intent(context, arena.kids.stories.activities.StoryPic.class);
                // else
                i = new Intent(getApplicationContext(), arena.kids.stories.activities.Story.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("pos", sOfTheDay[index]);
                i.putExtra("maxi", sOfTheDay);
                i.putExtra("pgname", sOfTheDay);
                i.putExtra("posorg", sOfTheDay[index]);
                //Log.e("positioncatstory", String.valueOf(position) +"--"+prgIndexN[position] +pgNameN);
                startActivity(i);
            }
        });

        imgTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);

                int myTheme = 0;
                if (sharedPref.getInt("theme", 0) == 1)
                    myTheme = 0;
                else
                    myTheme = 1;


                SharedPreferences.Editor e = sharedPref.edit();
                e.putInt("theme", myTheme);
                //e.clear();
                e.apply();
                e.commit();

                Utils.changeToTheme(MainActivity.this   , myTheme);
            }
        });


        int permissionState = ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS);
        // If the permission is not granted, request it.
        if (permissionState == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        //gridView = (GridView) findViewById(R.id.grid_view);
        // gv=(GridView) findViewById(R.id.gridView1);
        // gridView.setAdapter(new CustomerAdapter(this, prgmNameList, prgmPics, prgmNameList, prgmNameList));
        // ViewGroup.LayoutParams layoutParams = gridView.getLayoutParams();
        //layoutParams.height = Utils.getScreenHeight(getApplicationContext())-624; //this is in pixels
        //gridView.setLayoutParams(layoutParams);

        adUpgrade = sharedPref.getInt("adUpgrade", 0);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, 120);
        SharedPreferences.Editor e = sharedPref.edit();
        e.putInt("adtimeTotal", 0);
        e.putLong("adtime", c.getTimeInMillis());
        e.apply();

        billingClient = BillingClient.newBuilder(getApplicationContext())
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        System.out.println("adUpgrade main" + adUpgrade);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout
                .LayoutParams.WRAP_CONTENT);
        if (adUpgrade == 0) {

            initializeAds();
            JSONObject consentObject = new JSONObject();

        } else {
            layoutParams.bottomMargin = 0;
            //adLay.setVisibility(View.GONE);
            //mButtonNoAd.setVisibility(View.GONE);
            //mAdMobAdView.setVisibility(View.GONE);
        }

        final String[] from = new String[]{"cityName", "img"};
        final int[] to = new int[]{R.id.txt233, R.id.story2};
        mAdapter = new SimpleCursorAdapter(this,
                R.layout.item,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.story2) {
                    // Get the byte array from the database.
                    ImageView iconImageView = (ImageView) view;
                    // iconImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));


                    String uri = "@drawable/a" + cursor.getString(0);
                    Log.e("PicSug", uri);
                    int imageResource = getApplicationContext().getResources().getIdentifier(uri, null, getApplicationContext().getPackageName());
                    Glide.with(getApplicationContext())
                            .load(imageResource).signature(new ObjectKey("56"))
                            .into(iconImageView);
                    return true;
                } else {  // Process the rest of the adapter with default settings.
                    TextView txtPc = (TextView) view;
                    /*Typeface face = Typeface.createFromAsset(getAssets(),
                            "font/angella.otf");*/
                    Typeface face = ResourcesCompat.getFont(getApplicationContext(), R.font.angella);
                    // viewHolder.item.setTypeface(Typeface.sans-serif-black, Typeface.NORMAL);
                    txtPc.setTypeface(face);
                    txtPc.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            getResources().getDimension(R.dimen.result_font_small));
                    return false;
                }
            }
        });

        mSkuList.add("no_ad_upgrade");
        setupBillingClient();
    }

    public void gotoCategory(View V) {
        System.out.println("OnClickCat" + V.toString());
        Intent i = new Intent(getApplicationContext(), arena.kids.stories.activities.CatActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        String cat = "Panchtantra Stories";
        if (V.toString().toLowerCase().contains("panch"))
            cat = "Panchtantra Stories";
        else if (V.toString().toLowerCase().contains("fairy"))
            cat = "Fairytales";
        else if (V.toString().toLowerCase().contains("moral"))
            cat = "Moral";
        else if (V.toString().toLowerCase().contains("classic"))
            cat = "Classic";
        else if (V.toString().toLowerCase().contains("animal"))
            cat = "Animal";
        else if (V.toString().toLowerCase().contains("fairy"))
            cat = "Fairytales";
        else if (V.toString().toLowerCase().contains("rhym"))
            cat = "Rhymes";

        i.putExtra("pgname", cat);
        i.putExtra("pgurl", cat);
        i.putExtra("newID", cat);
        getApplicationContext().startActivity(i);
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
        String[] prgmStoryList2 = new String[0];
        String[] prgmIndex2 = new String[0];
        prgmStoryList = getResources().getStringArray(R.array.titles);
        prgmIndex = getResources().getStringArray(R.array.id);

        for (int i = 0; i <= prgmStoryList.length - 1; i++) {
            Log.i("prgmIndexii" + String.valueOf(i), String.valueOf(prgmStoryList[i]));
        }
        for (int i = 0; i <= prgmIndex.length - 1; i++) {
            Log.i("prgmIndexii" + String.valueOf(i), String.valueOf(prgmIndex[i]));
        }

        if (pgname.contains("Fav")) {

        } else {
            prgmStoryList2 = new String[len];
            prgmIndex2 = new String[len];
            int k = startindex;
            //Log.e("40111",String.valueOf(prgmIndex[118]));
            for (int i = 0; i <= len - 1; i++) {
                prgmStoryList2[i] = prgmStoryList[k];
                prgmIndex2[i] = prgmIndex[k];
                Log.i("indexi" + String.valueOf(i), String.valueOf(prgmStoryList2[i]));
                Log.i("indexi" + String.valueOf(i), String.valueOf(prgmIndex2[i]));
                k++;
            }
        }
        prgmStoryList = prgmStoryList2;
        prgmIndex = prgmIndex2;
        Log.i("Lengh", String.valueOf(prgmIndex2.length));
    }

    private FrameLayout adContainerView;
    private AdView adView;

    private void initializeAds() {
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("EBB40DF168323087C9461BE093AC4ED0")).build();
        MobileAds.initialize(activity,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                    }
                });
        MobileAds.setRequestConfiguration(configuration);

        if (adUpgrade == 0) {

            // Step 1 - Create an AdView and set the ad unit ID on it.
            adView = new AdView(this);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                    Log.e("ADMOB", "Failed: " + adError.getMessage());
                }

                @Override
                public void onAdLoaded() {
                    Log.d("ADMOB", "Ad loaded successfully!");
                }
            });
            adView.setAdUnitId(getString(R.string.admob_banner_id_main));
            adContainerView.addView(adView);

            loadBanner();
        } else
            adContainerView.setVisibility(View.GONE);

    }

    private void loadBanner() {


        AdRequest adRequest =
                new AdRequest.Builder()
                        .build();

        AdSize adSize = getBannerAdSize();
        // Step 4 - Set the adaptive ad size on the ad view.
        adView.setAdSize(adSize);


        // Step 5 - Start loading the ad in the background.
        adView.loadAd(adRequest);
    }

    private static AdSize getBannerAdSize() {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity.getApplicationContext(), adWidth);
    }

    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            // To be implemented in a later section.
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
            } else {
                // Handle any other error codes.
            }
        }
    };
    private BillingClient billingClient;

    private void setupBillingClient() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    getAvailableProducts();
                    System.out.println("Billing5 Getting Puchaselist");
                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
                    billingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder()
                                    .setProductType(BillingClient.ProductType.INAPP)
                                    .build(),
                            new PurchasesResponseListener() {
                                public void onQueryPurchasesResponse(BillingResult billingResult, List purchases) {
                                    System.out.println("Billing5 Puchaselist" + purchases.toString());
                                    if (purchases.size() == 0) {

                                        Log.e(TAG, "billing nack prod");
                                        SharedPreferences.Editor e = sharedPref.edit();
                                        e.putInt("adUpgrade", 0);
                                        e.apply();
                                    } else {
                                        Log.e(TAG, "billing ack prod");
                                        SharedPreferences.Editor e = sharedPref.edit();
                                        e.putInt("adUpgrade", 1);
                                        e.apply();
                                    }
                                }
                            }
                    );
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });


    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            System.out.println("billing purchases " + purchases);
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else {
            // displayError(R.string.inapp_purchase_problem, billingResult.getResponseCode());
            System.out.println("billing error " + billingResult.getResponseCode());
        }
    }

    private void handlePurchase(Purchase purchase) {
        System.out.println("billing purchase state " + purchase.getPurchaseState());

        System.out.println("billing Ack response purchase.getPurchaseState()" + purchase.getPurchaseState());
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            System.out.println("billing Ack response isAcknowledged()" + purchase.isAcknowledged());
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                //billingClient.acknowledgePurchase(acknowledgePurchaseParams, onAcknowledgePurchaseResponse);
                //AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
                billingClient.acknowledgePurchase(
                        acknowledgePurchaseParams,
                        new AcknowledgePurchaseResponseListener() {
                            @Override
                            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                                System.out.println("billing Ack response billingResult.getResponseCode()" + billingResult.getResponseCode());
                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                                    System.out.println("billing Ack response billingResult.getResponseCode()inside" + billingResult.getResponseCode());
                                    SharedPreferences sharedPref2 = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
                                    SharedPreferences.Editor e = sharedPref2.edit();
                                    e.putInt("adUpgrade", 1);
                                    e.putLong("adUpgradeTime", purchase.getPurchaseTime());
                                    e.putString("adUpgradeOrderID", purchase.getOrderId());
                                    e.apply();
                                    //Toast.makeText(MainActivity.this, "Consumed111111!", Toast.LENGTH_LONG).show();

                                    finish();
                                    startActivity(getIntent());
                                    Utils.showToast(MainActivity.this, "Hurray! No more ads! Please restart the app in case ads still appears!");
                                    //Toast.makeText(MainActivity.this, "Consumed!", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                );
            }
        }
    }


    public void purchase(String sku) {
        // Mis-clicking prevention, using threshold of 3 seconds
        try {
            ImmutableList productDetailsParamsList =
                    ImmutableList.of(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                    // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                    .setProductDetails(pd)
                                    // to get an offer token, call ProductDetails.getSubscriptionOfferDetails()
                                    // for a list of offers that are available to the user
                                    //.setOfferToken(selectedOfferToken)
                                    .build()
                    );

            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build();
            BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
        } catch (Exception e) {
            Toast msg = Toast.makeText(MainActivity.this, "Oops! Something went wrong with Billing! Please try after some time", Toast.LENGTH_LONG);
            FirebaseCrashlytics.getInstance().recordException(e);
        }

    }

    public ProductDetails pd;

    private void getAvailableProducts() {
        Log.e(TAG, "billing5 -getAvailableProducts");
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId("no_ad_upgrade")
                                                .setProductType(BillingClient.ProductType.INAPP)
                                                .build()))
                        .build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult,
                                                         List<ProductDetails> productDetailsList) {
                        try {
                            Log.e(TAG, "billing5 -getAvailableProducts" + productDetailsList.get(0));
                            pd = productDetailsList.get(0);
                            // check billingResult
                            // process returned productDetailsList
                        } catch (Exception e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                        }
                    }
                }
        );

    }


    @Override
    protected void onResume() {

        if (searchView != null) {
            Log.d(TAG, "onResume: Search");
            searchView.setQuery("", false);
            searchView.clearFocus();
            searchView.onActionViewCollapsed();
        }
        super.onResume();
    }

    public int getNewHeight() {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        Log.i("height-Screen", String.valueOf(dpHeight));
        Log.i("width-Screen", String.valueOf(dpWidth));

        int newHeight = 32;
           /*
        if (dpHeight > 720)
            newHeight = 90;
        else if (dpHeight <= 720 && dpHeight > 400)*/
        newHeight = 50;

        newHeight = 60;
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newHeight, r.getDisplayMetrics());
        Log.i("screen-pixel", String.valueOf(Math.round(px)));

        return Math.round(px);

    }

    public static int convertDpToPixels(float dp, Context context) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }

    static SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        //menu.add(1, 1, 1, "Share").setIcon(R.drawable.ic_share_white_24dp_2).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(2, 2, 2, "Favourites").setIcon(R.drawable.star).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        //menu.add(3, 3, 3, "Famous Nursery Rhymes - Audio").setIcon(R.drawable.ic_stars_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        //menu.add(4, 4, 4, "Our More Apps!").setIcon(R.drawable.ic_stars_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        //menu.add(2, 2, 2, "Favourites").setActionView("android.widget.SearchView").setIcon(R.drawable.ic_stars_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW|MenuItem.SHOW_AS_ACTION_IF_ROOM);

        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu_main, menu);
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setSuggestionsAdapter(mAdapter);
        searchView.setSubmitButtonEnabled(false);
        searchView.setFocusable(false);
        searchView.setIconifiedByDefault(false);


        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                // Your code here
                String[] prgmIndex = getResources().getStringArray(R.array.id);
                Log.d(TAG, "onSuggestionClick: " + c.getString(0));
                // Toast msg = Toast.makeText(MainActivity.this, "gusgg"+position +"text"+searchView.getQuery().toString(), Toast.LENGTH_LONG);
                //msg.show();


                Intent i = new Intent(getApplicationContext(), arena.kids.stories.activities.Story.class);
                i.putExtra("pos", c.getString(0));
                i.putExtra("maxi", prgmIndex);
                searchView.setQuery("", false);
                searchView.clearFocus();
                searchView.setIconified(true);
                searchView.onActionViewCollapsed();

                startActivity(i);
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                // Your code here
                return true;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                Log.d(TAG, "onFocusChange: " + hasFocus);
                if (hasFocus) {

                } else {
                }
            }
        });
        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "onFocusChange2: " + hasFocus);
                if (hasFocus) {
                    //got focus

                } else {

                    //lost focus
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                populateAdapter(s);
                hideKeyboardFrom(getApplicationContext(), searchView);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                populateAdapter(s);
                return false;
            }
        });

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
        MenuItem bedMenuItem = menu.findItem(R.id.menu_theme);
        int myTheme = 0;
        if (sharedPref.getInt("theme", 0) == 0) {
            bedMenuItem.setTitle("Dark Theme");
            myTheme = 1;
        } else {
            bedMenuItem.setTitle("Light Theme");
            myTheme = 0;
        }

        return true;
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    static MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "cityName", "img"});

    private void populateAdapter(String query) {
        int j = 0;
        c = new MatrixCursor(new String[]{BaseColumns._ID, "cityName", "img"});
        for (int i = 0; i < items.length; i++) {

            if (items[i].toLowerCase().contains(query.toLowerCase()) && query.length() > 2) {
                c.addRow(new Object[]{itemsid[i], items[i], ""});
                j++;
            }
            if (j >= 9)
                break;
        }

        if (j == 0 && query.length() > 2) {
            if (Arrays.asList(storylist).toString().toLowerCase().contains(query.toLowerCase())) {
                System.out.println("Storylist matching" + query.toLowerCase());
                // true
            } else {
                System.out.println("Storylist notmatching" + query.toLowerCase());
            }
        }


        mAdapter.changeCursor(c);

    }

    /*@Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }*/

    private void handleIntent(Intent intent) {
        String query = intent.getStringExtra(SearchManager.QUERY);
        //Toast msg = Toast.makeText(MainActivity.this, query, Toast.LENGTH_LONG);
        //msg.show();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            //use the query to search
        }
    }

    private Menu menu;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = df.format(c.getTime());

        switch (item.getItemId()) {
            case 1:


                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, "English Stories - " + Uri.parse("http://play.google.com/store/apps/details?id="
                        + getPackageName()));

                startActivity(Intent.createChooser(share, "Share Me"));

                return true;
            case 2:
                // write your code here
                Intent i = new Intent(getApplicationContext(), arena.kids.stories.activities.CatActivity.class);
                i.putExtra("pgname", "Favourites");
                startActivity(i);
                //Toast msg = Toast.makeText(MainActivity.this, "Menu 1", Toast.LENGTH_LONG);
                //msg.show();
                return true;
            /*case R.id.action_stories:
                // write your code here
                final String appPackageName4 = "arena.audio.english.stories"; // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName4)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName4)));
                }
                return true;
            case R.id.action_video:
                // write your code here
                final String appPackageName45 = "arena.kids.video"; // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName45)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName45)));
                }
                return true;
            case R.id.menu_famous:
                // write your code here
                final String appPackageName = "arena.kids.rhymes"; // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return true;*/
            case R.id.menu_theme:
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);

                int myTheme = 0;
                if (sharedPref.getInt("theme", 0) == 1)
                    myTheme = 0;
                else
                    myTheme = 1;


                SharedPreferences.Editor e = sharedPref.edit();
                e.putInt("theme", myTheme);
                //e.clear();
                e.apply();
                e.commit();

                Utils.changeToTheme(this, myTheme);
                return true;
            case R.id.action_rate:
                // write your code here
                final String appPackageName2 = getPackageName();// from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName2)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName2)));
                }
                return true;
            case R.id.action_feedback:
                // write your code here
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                /* Fill it with Data */
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"wearenadevelopers@gmail.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "User Feedback - English Stories");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Submit your Issue/Feedback : ");
                //emailIntent.setType("message/rfc82");
                /* Send it off to the Activity-Chooser */
                startActivity(Intent.createChooser(emailIntent, "Submit Issue/Feedback..."));

                return true;
            case R.id.action_no_ads:
                // write your code here
                System.out.println("billing click ");
                purchase("no_ad_upgrade");
                return true;
            case R.id.menu_famous_apps:
                // write your code here
                final String appPackageName3 = "Arena Developer"; // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:" + appPackageName3)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=" + appPackageName3)));
                }
                return true;
            case android.R.id.home:
                sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);


                //Set<String> set = mSharedPreferences.sharedPref("fav", null);

                SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
                String formattedDate2 = df2.format(c.getTime());
                String rte = sharedPref.getString("rate", formattedDate2);
                boolean dateflag = false;
                //if (!rte.contains("First") && !rte.contains("No")) {
                String dtStart = rte;
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    Date date = format.parse(dtStart);
                    System.out.println(date);
                    System.out.println("compare" + format.format(c.getTime()) + "-" + dtStart);
                    System.out.println("Comparedate" + c.getTime().compareTo(date));
                    if (c.getTime().compareTo(date) > 0) {
                        System.out.println("Comparedate" + new Date().equals(date));
                        dateflag = true;
                    }
                } catch (ParseException ee) {
                    FirebaseCrashlytics.getInstance().recordException(ee);
                    ee.printStackTrace();
                }
                // }


                Log.e("rate get - ", String.valueOf(rte));
                int rtetotal = sharedPref.getInt("adtimeTotal3", 0);
                Log.e("PageView Total - ", String.valueOf(rtetotal));


                if (rtetotal >= 4 && !rte.contains("No") && dateflag) {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom2);
                    } else {
                        builder = new AlertDialog.Builder(this);
                    }
                    SpannableString s = new SpannableString("Rate our app");
                    s.setSpan(new kids.stories.utils.TypefaceSpan(this, "angella.otf"), 0, s.length(),
                            Spannable.SPAN_PARAGRAPH);
                    s.setSpan(new RelativeSizeSpan(1.4f), 0, s.length(), 0);

                    SpannableString ss = new SpannableString("If you like this app, would you mind taking a moment to rate it? It wont take more than a minute, Thanks for your support!");
                    ss.setSpan(new kids.stories.utils.TypefaceSpan(this, "angella.otf"), 0, ss.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss.setSpan(new RelativeSizeSpan(1.0f), 0, ss.length(), 0);
                    ss.setSpan(new ForegroundColorSpan(Color.rgb(21, 101, 192)), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    s.setSpan(new ForegroundColorSpan(Color.rgb(0, 157, 214)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                    SpannableString sss = new SpannableString("RATE NOW");
                    sss.setSpan(new kids.stories.utils.TypefaceSpan(this, "angella.otf"), 0, sss.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sss.setSpan(new RelativeSizeSpan(0.8f), 0, sss.length(), 0);
                    sss.setSpan(new ForegroundColorSpan(Color.rgb(21, 101, 192)), 0, sss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    SpannableString s1 = new SpannableString("REMIND LATER");
                    s1.setSpan(new kids.stories.utils.TypefaceSpan(this, "angella.otf"), 0, s1.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    s1.setSpan(new RelativeSizeSpan(0.8f), 0, s1.length(), 0);
                    s1.setSpan(new ForegroundColorSpan(Color.rgb(21, 101, 192)), 0, s1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    SpannableString s2 = new SpannableString("NO THANKS");
                    s2.setSpan(new kids.stories.utils.TypefaceSpan(this, "angella.otf"), 0, s2.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    s2.setSpan(new RelativeSizeSpan(0.8f), 0, s2.length(), 0);
                    s2.setSpan(new ForegroundColorSpan(Color.rgb(21, 101, 192)), 0, s2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                    Rect displayRectangle = new Rect();
                    Window window = this.getWindow();

                    window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
                    builder.setTitle(s)
                            .setMessage(ss)
                            .setNeutralButton(sss, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                    try {
                                        startActivity(goToMarket);
                                    } catch (ActivityNotFoundException e) {
                                        startActivity(new Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("http://play.google.com/store/apps/details?id="
                                                        + getPackageName())));
                                    }

                                    dialog.dismiss();

                                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);

                                    Calendar c = Calendar.getInstance();
                                    System.out.println("Current time => " + c.getTime());
                                    c.add(Calendar.DAY_OF_YEAR, 30);
                                    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                                    String formattedDate = df.format(c.getTime());

                                    Log.e("rate date - ", String.valueOf(formattedDate));
                                    SharedPreferences.Editor e = sharedPref.edit();
                                    e.putString("rate", formattedDate);
                                    //e.clear();
                                    e.apply();
                                    e.commit();
                                    finish();
                                }
                            })
                            .setPositiveButton(s2, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);

                                    Calendar c = Calendar.getInstance();
                                    System.out.println("Current time => " + c.getTime());
                                    c.add(Calendar.DAY_OF_YEAR, 15);
                                    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                                    String formattedDate = df.format(c.getTime());

                                    Log.e("rate date - ", String.valueOf(formattedDate));
                                    SharedPreferences.Editor e = sharedPref.edit();
                                    e.putString("rate", formattedDate);
                                    //e.clear();
                                    e.apply();
                                    e.commit();
                                    finish();
                                }
                            })
                            .setNegativeButton(s1, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
                                    Calendar c = Calendar.getInstance();
                                    System.out.println("Current time => " + c.getTime());
                                    c.add(Calendar.DAY_OF_YEAR, 2);
                                    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                                    String formattedDate = df.format(c.getTime());

                                    Log.e("rate date - ", String.valueOf(formattedDate));
                                    SharedPreferences.Editor e = sharedPref.edit();
                                    e.putString("rate", formattedDate);
                                    //e.clear();
                                    e.apply();
                                    e.commit();
                                    finish();
                                }
                            })
                            .setIcon(R.drawable.elephant)
                            .show().getWindow().setLayout((int) (displayRectangle.width() *
                                    0.9f), (int) (displayRectangle.height() * 0.53f));
                } else
                    super.onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = df.format(c.getTime());


        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
        //Set<String> set = mSharedPreferences.sharedPref("fav", null);

        SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate2 = df2.format(c.getTime());
        String rte = sharedPref.getString("rate", formattedDate2);
        boolean dateflag = false;
        //if (!rte.contains("First") && !rte.contains("No")) {
        String dtStart = rte;
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date date = format.parse(dtStart);
            System.out.println(date);
            System.out.println("compare" + format.format(c.getTime()) + "-" + dtStart);
            System.out.println("Comparedate" + c.getTime().compareTo(date));
            if (c.getTime().compareTo(date) > 0) {
                System.out.println("Comparedate" + new Date().equals(date));
                dateflag = true;
            }
        } catch (ParseException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        // }


        Log.e("rate get - ", String.valueOf(rte));
        int rtetotal = sharedPref.getInt("adtimeTotal3", 0);
        Log.e("PageView Total - ", String.valueOf(rtetotal));


        if (rtetotal >= 4 && !rte.contains("No") && dateflag) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom2);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            SpannableString s = new SpannableString("Rate our app");
            s.setSpan(new kids.stories.utils.TypefaceSpan(this, "angella.otf"), 0, s.length(),
                    Spannable.SPAN_PARAGRAPH);
            s.setSpan(new RelativeSizeSpan(1.4f), 0, s.length(), 0);

            SpannableString ss = new SpannableString("If you like this app, would you mind taking a moment to rate it? It wont take more than a minute, Thanks for your support!");
            ss.setSpan(new kids.stories.utils.TypefaceSpan(this, "angella.otf"), 0, ss.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new RelativeSizeSpan(1.0f), 0, ss.length(), 0);
            ss.setSpan(new ForegroundColorSpan(Color.rgb(21, 101, 192)), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            s.setSpan(new ForegroundColorSpan(Color.rgb(0, 157, 214)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString sss = new SpannableString("RATE NOW");
            sss.setSpan(new kids.stories.utils.TypefaceSpan(this, "angella.otf"), 0, sss.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sss.setSpan(new RelativeSizeSpan(0.8f), 0, sss.length(), 0);
            sss.setSpan(new ForegroundColorSpan(Color.rgb(21, 101, 192)), 0, sss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString s1 = new SpannableString("REMIND LATER");
            s1.setSpan(new kids.stories.utils.TypefaceSpan(this, "angella.otf"), 0, s1.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            s1.setSpan(new RelativeSizeSpan(0.8f), 0, s1.length(), 0);
            s1.setSpan(new ForegroundColorSpan(Color.rgb(21, 101, 192)), 0, s1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            SpannableString s2 = new SpannableString("NO THANKS");
            s2.setSpan(new kids.stories.utils.TypefaceSpan(this, "angella.otf"), 0, s2.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            s2.setSpan(new RelativeSizeSpan(0.8f), 0, s2.length(), 0);
            s2.setSpan(new ForegroundColorSpan(Color.rgb(21, 101, 192)), 0, s2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            Rect displayRectangle = new Rect();
            Window window = this.getWindow();

            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
            builder.setTitle(s)
                    .setMessage(ss)
                    .setPositiveButton(sss, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = Uri.parse("market://details?id=" + getPackageName());
                            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                            try {
                                startActivity(goToMarket);
                            } catch (ActivityNotFoundException e) {
                                startActivity(new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("http://play.google.com/store/apps/details?id="
                                                + getPackageName())));
                            }

                            dialog.dismiss();

                            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);

                            Calendar c = Calendar.getInstance();
                            System.out.println("Current time => " + c.getTime());
                            c.add(Calendar.DAY_OF_YEAR, 30);
                            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                            String formattedDate = df.format(c.getTime());

                            Log.e("rate date - ", String.valueOf(formattedDate));
                            SharedPreferences.Editor e = sharedPref.edit();
                            e.putString("rate", formattedDate);
                            //e.clear();
                            e.apply();
                            e.commit();
                            finish();
                        }
                    })
                    .setNeutralButton(s2, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);


                            Calendar c = Calendar.getInstance();
                            System.out.println("Current time => " + c.getTime());
                            c.add(Calendar.DAY_OF_YEAR, 15);
                            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                            String formattedDate = df.format(c.getTime());

                            Log.e("rate date - ", String.valueOf(formattedDate));
                            SharedPreferences.Editor e = sharedPref.edit();
                            e.putString("rate", formattedDate);
                            //e.clear();
                            e.apply();
                            e.commit();
                            finish();
                        }
                    })
                    .setNegativeButton(s1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);

                            Calendar c = Calendar.getInstance();
                            System.out.println("Current time => " + c.getTime());
                            c.add(Calendar.DAY_OF_YEAR, 2);
                            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                            String formattedDate = df.format(c.getTime());

                            Log.e("rate date - ", String.valueOf(formattedDate));
                            SharedPreferences.Editor e = sharedPref.edit();
                            e.putString("rate", formattedDate);
                            //e.clear();
                            e.apply();
                            e.commit();
                            finish();
                        }
                    })
                    .setIcon(R.drawable.elephant)
                    .show().getWindow().setLayout((int) (displayRectangle.width() *
                            0.9f), (int) (displayRectangle.height() * 0.53f));
        } else
            super.onBackPressed();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();


    }

    private View.OnClickListener onClickListener(final Class<?> c) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, c);
                startActivity(i);
            }
        };
    }

    protected void setData(ArrayList<String> stringArrayList) {
        stringArrayList = new ArrayList<>();

        for (int i = 0; i < 1; i++) {
            stringArrayList.add("Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item Item ");
        }
    }

    private void redirect() {
        if (getIntent().getExtras() != null) {
            Intent mainIntent = new Intent(MainActivity.this, arena.kids.stories.activities.MainActivity.class);
            System.out.println("notificationRedirect");
            String[] prgmIndex = getResources().getStringArray(R.array.id);
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                Log.d("NotificationStory", "Key: " + key + " Value: " + value);
                if (getIntent().getExtras().getString("pos") != null) {
                    mainIntent = new Intent(MainActivity.this, arena.kids.stories.activities.Story.class);
                    mainIntent.putExtra("pos", getIntent().getExtras().getString("pos"));
                    mainIntent.putExtra("maxi", prgmIndex);
                    mainIntent.putExtra("not", "yes");
                    MainActivity.this.startActivity(mainIntent);
                }
                if (getIntent().getExtras().getString("cat") != null) {
                    mainIntent = new Intent(MainActivity.this, arena.kids.stories.activities.CatActivity.class);
                    mainIntent.putExtra("pgname", getIntent().getExtras().getString("cat"));
                    mainIntent.putExtra("not", "yes");
                    MainActivity.this.startActivity(mainIntent);
                }
                /*if (getIntent().getExtras().getString("play") != null) {
                    Log.d("StoryPlay", "KeyPlay: " + key + " Value: " + getIntent().getExtras().getString("play"));
                    // mainIntent = new Intent(SplashActivity.this, arena.kids.stories.activities.CatActivity.class);
                    //mainIntent.putExtra("pgname", getIntent().getExtras().getString("cat"));


                    Intent intent;

                    final String appPackageName = getIntent().getExtras().getString("play"); // getPackageName()
                    try {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                    }
                    int notificaionId = 1;
                    PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
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
                }*/
            }
        }

    }
}
