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
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.BaseColumns;
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
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import androidx.viewpager2.widget.ViewPager2;
import arena.kids.stories.R;
import arena.kids.stories.adapter.CatMainPageAdapter;
import arena.kids.stories.adapter.SlideAdapter;
import arena.kids.stories.adapter.activity_main_grid_more_apps;
import arena.kids.stories.adapter.activity_main_grid_story2;
import arena.kids.stories.adapter.activity_main_grid_story;
import arena.kids.stories.utils.Utils;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
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
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.common.collect.ImmutableList;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    public static String[] prgmNameList;
    private static Activity activity;
    public static String[] prgmPics;
    public static String[] prgmStoryList;
    public static String[] prgmIndex;
    private SlideAdapter adapter;
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
    private ViewPager2 viewPager;
    private TabLayout tabDots;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (adapter != null && adapter.getItemCount() > 0) {
                int next = (viewPager.getCurrentItem() + 1) % adapter.getItemCount();
                viewPager.setCurrentItem(next, true);
            }
        }
    };

    private void setScreen() {
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);

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
        setContentView(R.layout.activity_main);
        adContainerView = findViewById(R.id.ad_view_container);
        setScreen();
        redirect();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        items = getResources().getStringArray(R.array.titles);
        itemsid = getResources().getStringArray(R.array.id);
        storylist = getResources().getStringArray(R.array.storylist);
        activity = this;

        LinearLayout rl = (LinearLayout) findViewById(R.id.rlNew);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float screenWidthDp = metrics.widthPixels / metrics.density;

        float relativeSize;
        if (screenWidthDp < 360) {        // small phones
            relativeSize = 0.9f;
        } else if (screenWidthDp < 600) { // normal phones
            relativeSize = 1.0f;
        } else if (screenWidthDp < 720) { // large phones
            relativeSize = 1.2f;
        } else {                          // tablets
            relativeSize = 1.5f;
        }

        SpannableString s = new SpannableString("Story Time!");
        s.setSpan(new kids.stories.utils.TypefaceSpan(this, "outfit_regular.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new RelativeSizeSpan(relativeSize), 0, s.length(), 0);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setElevation(0);

        prgmNameList = getResources().getStringArray(R.array.storylist);
        prgmPics = getResources().getStringArray(R.array.storylistPics);

        RecyclerView recyclerView2 = (RecyclerView) findViewById(R.id.grdCat);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        CatMainPageAdapter mAdapter2 = new CatMainPageAdapter(MainActivity.this, prgmNameList, prgmPics);
        recyclerView2.setAdapter(mAdapter2);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2, RecyclerView.HORIZONTAL, false);
        recyclerView2.setLayoutManager(mLayoutManager);

        setupStoryRecyclerView(R.id.grdStory, "Rhymes", false, 0,
                category -> new activity_main_grid_story(this, prgmStoryList, prgmIndex, category));


        setupStoryRecyclerView(R.id.grdStoryPanch, "Panch", false, 0,
                category -> new activity_main_grid_story(this, prgmStoryList, prgmIndex, category));

        setupStoryRecyclerView(R.id.grdStoryClassic, "Classic", false, 0,
                category -> new activity_main_grid_story2(this, prgmStoryList, prgmIndex, category));

        setupStoryRecyclerView(R.id.grdStoryFairy, "Fairy", false, 0,
                category -> new activity_main_grid_story(this, prgmStoryList, prgmIndex, category));


        setupStoryRecyclerView(R.id.grdStoryMoral, "Moral", false, 0,
                category -> new activity_main_grid_story(this, prgmStoryList, prgmIndex, category));

        setupStoryRecyclerView(R.id.grdAnimals, "Animal", false, 0,
                category -> new activity_main_grid_story(this, prgmStoryList, prgmIndex, "Animals"));


        prgmStoryList = getResources().getStringArray(R.array.moreapps);
        RecyclerView grdMoreApps = (RecyclerView) findViewById(R.id.grdMoreApps);
        grdMoreApps.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        activity_main_grid_more_apps mAdapter7 = new activity_main_grid_more_apps(MainActivity.this, prgmStoryList, prgmStoryList, "Rhymes");
        grdMoreApps.setAdapter(mAdapter7);

        viewPager = findViewById(R.id.viewPager);
        tabDots = findViewById(R.id.tabDots);




        ImageView imgTheme = findViewById(R.id.imgTheme);
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);

        if (sharedPref.getInt("theme", 0) == 0) {
            imgTheme.setImageResource(R.drawable.lights);
        } else {
            imgTheme.setImageResource(R.drawable.nights);
        }



        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(25));


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
                e.apply();
                e.commit();

                Utils.changeToTheme(MainActivity.this, myTheme);
            }
        });

        int permissionState = ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS);
        if (permissionState == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        adUpgrade = sharedPref.getInt("adUpgrade", 0);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, 120);
        SharedPreferences.Editor e = sharedPref.edit();
        e.putInt("adtimeTotal", 0);
        e.putLong("adtime", c.getTimeInMillis());
        e.apply();


        System.out.println("adUpgrade main" + adUpgrade);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout
                .LayoutParams.WRAP_CONTENT);
        if (adUpgrade == 0) {
            initializeAds();
            JSONObject consentObject = new JSONObject();
        } else {
            layoutParams.bottomMargin = 0;
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
                    ImageView iconImageView = (ImageView) view;
                    String uri = "@drawable/a" + cursor.getString(0);
                    Log.e("PicSug", uri);
                    int imageResource = getApplicationContext().getResources().getIdentifier(uri, null, getApplicationContext().getPackageName());
                    Glide.with(getApplicationContext())
                            .load(imageResource).signature(new ObjectKey("56"))
                            .into(iconImageView);
                    return true;
                } else {
                    TextView txtPc = (TextView) view;
                    Typeface face = ResourcesCompat.getFont(getApplicationContext(), R.font.angella);
                    txtPc.setTypeface(face);
                    txtPc.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            getResources().getDimension(R.dimen.result_font_small));
                    return false;
                }
            }
        });
        setupCarasoul();
        mSkuList.add("no_ad_upgrade");
        setupBillingClient();
    }
    private  void setupCarasoul()
    {
        String[] sOfTheDay = getResources().getStringArray(R.array.storyoftheday);
        List<String> selectedImages = new ArrayList<>();
        List<Integer> usedIndexes = new ArrayList<>();
        Random random = new Random();

        int totalToPick = Math.min(5, sOfTheDay.length);
        while (selectedImages.size() < totalToPick) {
            int index = random.nextInt(sOfTheDay.length);
            if (!usedIndexes.contains(index)) {
                usedIndexes.add(index);
                selectedImages.add(sOfTheDay[index]);
            }
        }

        int[] selectedResIds = new int[selectedImages.size()];
        for (int i = 0; i < selectedImages.size(); i++) {
            selectedResIds[i] = getResources().getIdentifier(
                    selectedImages.get(i),
                    "drawable",
                    getPackageName()
            );

            Log.d("SlideSelection", "Picked image: " + selectedImages.get(i) + " → resId: " + selectedResIds[i]);
        }
        /*setupStoryRecyclerView(R.id.grdStory, "Rhymes", false, 0,
                category -> new activity_main_grid_story(this, prgmStoryList, prgmIndex, category));
*/
        adapter = new SlideAdapter(this, prgmStoryList,selectedImages.toArray(new String[0]));
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabDots, viewPager, (tab, position) -> {
            tab.setCustomView(R.layout.tab_unselected);
        }).attach();

        tabDots.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setCustomView(R.layout.tab_selected);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setCustomView(R.layout.tab_unselected);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        viewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                page.setTranslationX(-position * page.getWidth());
                if (position <= -1 || position >= 1) {
                    page.setAlpha(0f);
                } else if (position == 0) {
                    page.setAlpha(1f);
                } else {
                    page.setAlpha(1 - Math.abs(position));
                }
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 5000); // 5 sec per slide
            }
        });

        new TabLayoutMediator(tabDots, viewPager, (tab, position) -> {
            tab.setCustomView(R.layout.tab_unselected);
        }).attach();

        tabDots.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setCustomView(R.layout.tab_selected);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setCustomView(R.layout.tab_unselected);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        viewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                page.setTranslationX(-position * page.getWidth());

                if (position <= -1 || position >= 1) {
                    page.setAlpha(0f);
                } else if (position == 0) {
                    page.setAlpha(1f);
                } else {

                    page.setAlpha(1 - Math.abs(position));
                }
            }
        });


        sliderHandler.postDelayed(sliderRunnable, 5000);
    }
    private void setupStoryRecyclerView(int recyclerViewId, String category, boolean isGrid, int spanCount,
                                        Function<String, RecyclerView.Adapter> adapterProvider) {
        // Fetch stories
        getStoriesByCat(category);

        // Create adapter using the provided function
        RecyclerView.Adapter adapter = adapterProvider.apply(category);

        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(recyclerViewId);
        RecyclerView.LayoutManager layoutManager = isGrid
                ? new GridLayoutManager(this, spanCount, RecyclerView.HORIZONTAL, false)
                : new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
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
            adView.setAdUnitId("ca-app-pub-4801686843982324/9736217775");
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
        adView.setAdSize(adSize);
        adView.loadAd(adRequest);
    }

    private static AdSize getBannerAdSize() {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity.getApplicationContext(), adWidth);
    }

    private BillingClient billingClient;

    private void setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
                .setListener(this)
                .enablePendingPurchases(
                        PendingPurchasesParams.newBuilder()
                                .enableOneTimeProducts()
                                .build()
                )
                .build();

        Log.d(TAG, "BillingV8: Initializing BillingClient...");

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                Log.d(TAG, "BillingV8: Billing setup finished with response code: " + billingResult.getResponseCode());
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    getAvailableProducts();
                    queryExistingPurchases();
                } else {
                    Log.e(TAG, "BillingV8: Billing setup failed: " + billingResult.getDebugMessage());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.w(TAG, "BillingV8: Billing service disconnected");
            }
        });
    }

    private void getAvailableProducts() {
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(ImmutableList.of(
                        QueryProductDetailsParams.Product.newBuilder()
                                .setProductId("no_ad_upgrade")
                                .setProductType(BillingClient.ProductType.INAPP)
                                .build()
                ))
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, result) -> {
            Log.d(TAG, "BillingV8: ProductDetails query response: " + billingResult.getResponseCode());
            List<ProductDetails> productDetailsList = result.getProductDetailsList();
            if (productDetailsList != null && !productDetailsList.isEmpty()) {
                pd = productDetailsList.get(0);
                Log.i(TAG, "BillingV8: Available product loaded: " + pd.getName() + " | ID: " + pd.getProductId());
            } else {
                Log.w(TAG, "BillingV8: No product details found");
            }
        });
    }

    private void queryExistingPurchases() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);

        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                (billingResult, purchases) -> {
                    Log.d(TAG, "BillingV8: QueryPurchases response: " + billingResult.getResponseCode());

                    int adUpgradeValue = 0;

                    if (!purchases.isEmpty()) {
                        Log.i(TAG, "BillingV8: Existing purchases found: " + purchases.size());
                        for (Purchase purchase : purchases) {
                            Log.i(TAG, "BillingV8: Purchase: " + purchase.getProducts() + " | State: " + purchase.getPurchaseState());
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                adUpgradeValue = 1; // active purchase exists

                            }
                        }
                    } else {
                        Log.i(TAG, "BillingV8: No active purchases found");
                    }

                    sharedPref.edit().putInt("adUpgrade", adUpgradeValue).apply();
                    Log.d(TAG, "BillingV8: adUpgrade set to " + adUpgradeValue);
                }
        );
    }

    private void consumePurchaseImmediately(Purchase purchase, SharedPreferences sharedPref) {
        ConsumeParams params = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        billingClient.consumeAsync(params, (billingResult, purchaseToken) -> {
            Log.d(TAG, "BillingV8: ConsumeAsync response for token " + purchaseToken + ": " + billingResult.getResponseCode());
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                Log.i(TAG, "BillingV8: Purchase consumed successfully: " + purchase.getProducts());
                sharedPref.edit()
                        .putInt("adUpgrade", 1)
                        .putLong("adUpgradeTime", purchase.getPurchaseTime())
                        .putString("adUpgradeOrderID", purchase.getOrderId())
                        .putBoolean("purchase_consumed", true)
                        .apply();
                runOnUiThread(() -> Toast.makeText(this, "Purchase successful! Ads removed!", Toast.LENGTH_SHORT).show());
            } else {
                Log.e(TAG, "BillingV8: Failed to consume purchase: " + billingResult.getDebugMessage());
            }
        });
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
        Log.d(TAG, "BillingV8: onPurchasesUpdated response: " + billingResult.getResponseCode());
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                Log.i(TAG, "BillingV8: New purchase received: " + purchase.getProducts());
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.w(TAG, "BillingV8: User canceled purchase");
        } else {
            Log.e(TAG, "BillingV8: Purchase error: " + billingResult.getDebugMessage());
        }
    }

    private void handlePurchase(Purchase purchase) {
        String productId = purchase.getProducts().get(0);
        boolean isOneTime = productId.equals("no_ad_upgrade") || productId.equals("lifetime");

        if (isOneTime) {
            Log.d(TAG, "BillingV8: Handling one-time purchase: " + productId);

        } else {
            Log.d(TAG, "BillingV8: Handling subscription purchase: " + productId);
            if (!purchase.isAcknowledged()) {
                acknowledgePurchase(purchase);
            } else {
                Log.i(TAG, "BillingV8: Subscription already acknowledged: " + productId);
                updateAdUpgradeStatus(purchase);
            }
        }
    }

    private void acknowledgePurchase(Purchase purchase) {
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(params, billingResult -> {
            Log.d(TAG, "BillingV8: Acknowledge response: " + billingResult.getResponseCode());
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                Log.i(TAG, "BillingV8: Purchase acknowledged successfully: " + purchase.getProducts());
                updateAdUpgradeStatus(purchase);
                runOnUiThread(() -> Toast.makeText(this, "Hurray! No more ads!", Toast.LENGTH_SHORT).show());
                finish();
                startActivity(getIntent());
            } else {
                Log.e(TAG, "BillingV8: Failed to acknowledge purchase: " + billingResult.getDebugMessage());
            }
        });
    }

    private void updateAdUpgradeStatus(Purchase purchase) {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
        sharedPref.edit()
                .putInt("adUpgrade", 1)
                .putLong("adUpgradeTime", purchase.getPurchaseTime())
                .putString("adUpgradeOrderID", purchase.getOrderId())
                .apply();
        Log.i(TAG, "BillingV8: Ad upgrade status updated in SharedPreferences for: " + purchase.getProducts());
    }

    public void purchase() {
        if (pd == null) {
            Toast.makeText(this, "Product not loaded yet!", Toast.LENGTH_SHORT).show();
            return;
        }

        BillingFlowParams params = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                        ImmutableList.of(BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(pd)
                                .build())
                )
                .build();

        billingClient.launchBillingFlow(this, params);
    }

    public ProductDetails pd;

    @Override
    protected void onResume() {

        if (searchView != null) {
            Log.d(TAG, "onResume: Search");
            searchView.setQuery("", false);
            searchView.clearFocus();
            searchView.onActionViewCollapsed();
        }
        sliderHandler.postDelayed(sliderRunnable, 5000);
        super.onResume();
    }

    public int getNewHeight() {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        Log.i("height-Screen", String.valueOf(dpHeight));
        Log.i("width-Screen", String.valueOf(dpWidth));

        int newHeight = 32;
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
        menu.add(2, 2, 2, "Favourites").setIcon(R.drawable.star).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
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
                String[] prgmIndex = getResources().getStringArray(R.array.id);
                Log.d(TAG, "onSuggestionClick: " + c.getString(0));

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


                } else {

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

            } else {
                System.out.println("Storylist notmatching" + query.toLowerCase());
            }
        }

        mAdapter.changeCursor(c);
    }

    private void handleIntent(Intent intent) {
        String query = intent.getStringExtra(SearchManager.QUERY);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

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
                Intent i = new Intent(getApplicationContext(), arena.kids.stories.activities.CatActivity.class);
                i.putExtra("pgname", "Favourites");
                startActivity(i);
                return true;
            case R.id.menu_theme:
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);

                int myTheme = 0;
                if (sharedPref.getInt("theme", 0) == 1)
                    myTheme = 0;
                else
                    myTheme = 1;

                SharedPreferences.Editor e = sharedPref.edit();
                e.putInt("theme", myTheme);
                e.apply();
                e.commit();

                Utils.changeToTheme(this, myTheme);
                return true;
            case R.id.action_rate:
                final String appPackageName2 = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName2)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName2)));
                }
                return true;
            case R.id.action_feedback:
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"wearenadevelopers@gmail.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "User Feedback - English Stories");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Submit your Issue/Feedback : ");
                startActivity(Intent.createChooser(emailIntent, "Submit Issue/Feedback..."));

                return true;
            case R.id.action_no_ads:
                System.out.println("billing click ");
                purchase();
                return true;
            case R.id.menu_famous_apps:
                final String appPackageName3 = "Arena Developer";
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:" + appPackageName3)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=" + appPackageName3)));
                }
                return true;
            case android.R.id.home:
                sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);

                SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
                String formattedDate2 = df2.format(c.getTime());
                String rte = sharedPref.getString("rate", formattedDate2);
                boolean dateflag = false;
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

        SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate2 = df2.format(c.getTime());
        String rte = sharedPref.getString("rate", formattedDate2);
        boolean dateflag = false;
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
        if (billingClient != null) {
            billingClient.endConnection();
        }
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
            }
        }
    }
}