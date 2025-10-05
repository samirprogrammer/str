package arena.kids.stories.utils;

import static android.content.Context.MODE_PRIVATE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatDelegate;

import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import arena.kids.stories.R;

public class Utils {

    private Context _context;

    // constructor
    public Utils(Context context) {
        this._context = context;
    }

    /*
     * Reading file paths from SDCard
     */
    private static int sTheme;

    public static void changeToTheme(Activity activity, int theme) {

        final SharedPreferences sharedPref = activity.getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);

        SharedPreferences.Editor e = sharedPref.edit();
        e.putInt("theme", theme);
        //e.clear();
        e.apply();
        e.commit();

        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }
    public static void showToast(Context cnx, String str) {
        LayoutInflater inflater = ((Activity) cnx).getLayoutInflater();
        View layout = inflater.inflate(R.layout.custome_toast,
                (ViewGroup) ((Activity) cnx).findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(str);

        Toast toast = new Toast(cnx);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        if (str.contains("download") || str.contains("Restart"))
            toast.setDuration(Toast.LENGTH_LONG);
        else
            toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
    /**
     * Set the theme of the activity, according to the configuration.
     */
    public static void onActivityCreateSetTheme(Activity activity) {
        System.out.println("themechange" + sTheme);
        sTheme = 0;
        final SharedPreferences sharedPref = activity.getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);

        if (sharedPref.getInt("theme", 0) == 1)
            sTheme = 1;


        System.out.println("DarkMode Setting " + AppCompatDelegate.MODE_NIGHT_YES  );
        if (isNightModeActive(activity.getApplicationContext())) {
            System.out.println("DarkMode Settings 25 " + AppCompatDelegate.MODE_NIGHT_YES);

            SharedPreferences.Editor e = sharedPref.edit();
            e.putInt("theme", 1);
            //e.clear();
            e.apply();
            e.commit();
            activity.setTheme(R.style.AppThemeDark);
        }
        else {
            System.out.println("DarkMode Setting 3 " + AppCompatDelegate.MODE_NIGHT_YES);
            if (sTheme == 1)
                activity.setTheme(R.style.AppThemeDark);
            else
                activity.setTheme(R.style.AppTheme2);
        }
    }
    public static boolean isNightModeActive(Context context) {
        int defaultNightMode = AppCompatDelegate.getDefaultNightMode();
        if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            return true;
        }
        if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            return false;
        }

        int currentNightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                return false;
            case Configuration.UI_MODE_NIGHT_YES:
                return true;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                return false;
        }
        return false;
    }
    public ArrayList<String> getFilePaths(String folder) {
        ArrayList<String> filePaths = new ArrayList<String>();

        // check for directory

        // getting list of file paths
        String cntimg = "10";

        Log.e("cntimg " + folder, cntimg);
        // Check for count
        // if (listFiles.length > 0) {

        // loop through all files
        int k = 42;
        if (folder.equals("wedding"))
            k = 37;
        else if (folder.equals("foot"))
            k = 22;
        else if (folder.equals("rajasthani"))
            k = 9;
        else if (folder.equals("arabic"))
            k = 25;


        for (int i = 0; i <= k; i++) {
            if (i < 9)
                filePaths.add("mehndi" + folder + "0" + String.valueOf(i + 1));
            else
                filePaths.add("mehndi" + folder + String.valueOf(i + 1));

        }

        //Collections.reverse(filePaths);
        return filePaths;
    }


    public ArrayList<String> getFilePathsVid(String folder) {
        ArrayList<String> filePaths = new ArrayList<String>();

        // check for directory

        // getting list of file paths
        String cntimg = "10";

        try {
            // Create a URL for the desired page
            URL url = new URL("http://samirhosting-001-site1.htempurl.com/"
                    + folder + "/videos.txt");

            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    url.openStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                Log.e("linesyoutube", line);
                if (line.trim() == "")
                    break;
                String line2 = "https://img.youtube.com/vi/" + line + "/hqdefault.jpg";
                filePaths.add(line2);
            }
            in.close();
        } catch (MalformedURLException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        } catch (IOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        Collections.reverse(filePaths);
        return filePaths;
    }

    public static String getYoutubeVideoId(String youtubeUrl) {
        String video_id = "";
        Log.e("youtubeUrl", youtubeUrl);
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0
                && youtubeUrl.startsWith("https")) {
            Log.e("youtubeUrl", youtubeUrl);
            String expression = "^.*((youtu.be"
                    + "\\/)"
                    + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var
            // regExp
            // =
            // /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
            CharSequence input = youtubeUrl;
            Pattern pattern = Pattern.compile(expression,
                    Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                String groupIndex1 = matcher.group(7);
                video_id = groupIndex1;
                if (groupIndex1 != null && groupIndex1.length() == 11)
                    video_id = groupIndex1;
                else if (groupIndex1 != null && groupIndex1.length() == 10)
                    video_id = "v" + groupIndex1;
            }
        }
        Log.e("video_id", video_id);
        return video_id;
    }

    /*
     * Check supported file extensions
     *
     * @returns boolean
     */
    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
        }
    }


    /*
     * getting screen width
     */
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) _context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }

    public static int getScreenHeight(Context _context) {
        int columnHeight;
        WindowManager wm = (WindowManager) _context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnHeight = point.y;
        return columnHeight;
    }
}
