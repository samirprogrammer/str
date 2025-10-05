package arena.kids.stories.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;

import java.util.ArrayList;
import java.util.Objects;

import arena.kids.stories.R;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Activity activity;
    String[] result;
    ArrayList prgmDetails;
    Context context;
    String[] imageId;
    String[] newsId;
    String Story;
    String pgName;
    int pos;
    int posOrg;
    TextToSpeech tts;
    public int starth, endh;
    public int tempstart = 0;

    public RecyclerAdapter(Activity activity, ArrayList _prgmDetails, String story, String cat, int posOrgNew, int StartH, int EndH) {
        this.activity = activity;
        prgmDetails = _prgmDetails;
        context = activity;
        pgName = cat;
        posOrg = posOrgNew;
        Story = story;
        StartH = starth;
        EndH = endh;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        //inflate your layout and pass it to view holder
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.story_detail, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    private android.view.ActionMode actionMode;

    public Spanned getHtmlFormattedString(String value) {
        Spanned result = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                result = Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT);
            } else {
                result = Html.fromHtml(value);
            }

        } catch (Exception ex) {
        }
        return result;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder viewHolder, int position) {
        //System.out.println("storydetail:" + Story + "catname" + prgmDetails.get(pos) + "s" + pos + "s" + posOrg);
        if (position == pos) {

            viewHolder.item.setText("");

            // System.out.println("storydetail:" + Story + "catname" + pgName + "s" + pos + "s" + posOrg);
            //viewHolder.item.setText("aa");
            //viewHolder.item.setText("<b>sfsf</b>" + Html.fromHtml(Story) + "\n\n\n");
            viewHolder.item.setText(getHtmlFormattedString(Objects.requireNonNull(  Story.replaceAll("\\n", "<br/>"))));
            /*Typeface face = Typeface.createFromAsset(activity.getAssets(),
                    "font/angella.otf");*/
            Typeface face = ResourcesCompat.getFont(context, R.font.tenderness);
            SharedPreferences sharedPref = context.getSharedPreferences("Shared", MODE_PRIVATE);
            if (sharedPref.getInt("theme", 0) == 0)
                viewHolder.item.setTextColor(context.getResources().getColor(R.color.dark_3));
            else
                viewHolder.item.setTextColor(context.getResources().getColor(R.color.dark_4));

            viewHolder.item.setSelectAllOnFocus(false);
            // viewHolder.item.setTypeface(Typeface.sans-serif-black, Typeface.NORMAL);
            //whviewHolder.item.setBackgroundColor(Color.BLACK);
            //viewHolder.item.setTextColor(Color.BLACK);
            viewHolder.item.setTypeface(face);
            viewHolder.item.setLongClickable(true);
            viewHolder.item.setClickable(true);


            String fontSize = sharedPref.getString("fontSize", "3");
            int fSize = R.dimen.result_font22;
            if (fontSize.contains("1"))
                fSize = R.dimen.result_font11;
            else if (fontSize.contains("2"))
                fSize = R.dimen.result_font22;
            else if (fontSize.contains("3"))
                fSize = R.dimen.result_font33;
            else if (fontSize.contains("4"))
                fSize = R.dimen.result_font44;
            else if (fontSize.contains("5"))
                fSize = R.dimen.result_font55;

            viewHolder.item.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    activity.getResources().getDimension(fSize));

            this.activity.registerForContextMenu(viewHolder.item);


            return;
        }

    }

    private void ConvertTextToSpeech(String text) {
        // TODO Auto-generated method stub
        //String text = "Hi Sameer. What are you doing! Where are you going?";
        if (text == null || "".equals(text)) {
            text = "Content not available";
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        } else
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public int getItemCount() {
        return (null != prgmDetails ? prgmDetails.size() : 0);
    }

    /**
     * View holder to display each RecylerView item
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {
        private TextView item;
        private ImageView itemImg;
        private ScrollView sCroll;

        public ViewHolder(View view) {
            super(view);
            item = (TextView) view.findViewById(R.id.textviews2);
            itemImg = (ImageView) view.findViewById(R.id.headerImg2);
            item.setMovementMethod(new ScrollingMovementMethod());
            //sCroll = (ScrollView) view.findViewById(R.id.sView);
        }
    }

    class MyActionModeCallBack implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    }
}