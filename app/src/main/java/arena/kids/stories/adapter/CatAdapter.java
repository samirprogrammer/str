package kids.stories.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;

import arena.kids.stories.R;

public class CatAdapter extends BaseAdapter {

    String[] result;
    String[] Urls;
    Context context;
    String[] imageId;
    String[] newsId;
    String pgName;
    private static LayoutInflater inflater = null;
    private Activity _activity;

    public CatAdapter(Activity activity, String[] prgmNameList, String[] prgmImages, String[] prgmNameListUrl, String[] _newsId, String _pgName) {
        // TODO Auto-generated constructor stub

        this._activity = activity;
        result = prgmNameList;
        Urls = prgmNameListUrl;
        pgName=_pgName;
        context = activity;
        imageId = prgmImages;
        newsId = _newsId;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView tv;
        ImageView img;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.activity_cat_grid_view_detail, null);
        holder.tv = (TextView) rowView.findViewById(R.id.textview2);
        Typeface face = ResourcesCompat.getFont(context, R.font.angella);
        // viewHolder.item.setTypeface(Typeface.sans-serif-black, Typeface.NORMAL);
        holder.tv.setTypeface(face);
        /*holder.tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                _activity.getResources().getDimension(R.dimen.result_font_small_26));*/

        SharedPreferences sharedPref = context.getSharedPreferences("Shared", MODE_PRIVATE);
        if (sharedPref.getInt("theme", 0) == 0)
            holder.tv.setTextColor(context.getResources().getColor(R.color.cardview_dark_background));
        else
            holder.tv.setTextColor(context.getResources().getColor(R.color.primaryBk));
        //holder.tv.setTextColor(R.color.white);
        holder.img = (ImageView) rowView.findViewById(R.id.flag);
        holder.tv.setText(result[position]);

        //holder.img.setImageResource(imageId[position]);

        //String uri = "@drawable/a3";
        String uri = "@drawable/a" + newsId[position];
        Log.e("Pic","@drawable/a" + newsId[position]);
        int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());

        //imageview= (ImageView)findViewById(R.id.imageView);
        Drawable res = context.getResources().getDrawable(imageResource);

        try {
             //holder.img.setImageResource(imageResource);
            Glide.with(context)
                    .load(imageResource).signature(new ObjectKey("56"))
                    .into(holder.img) ;
        } catch (Exception e) {
            uri = "@drawable/a3";
            imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());

            Glide.with(context)
                    .load(imageResource).signature(new ObjectKey("56"))
                    .into(holder.img);

        }
        rowView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //oast.makeText(context, "You Clicked " + result[position], Toast.LENGTH_LONG).show();

                Intent i;
                //if(pgName.contains("Picto"))
                    //    i = new Intent(context, arena.kids.stories.activities.StoryPic.class);
               // else
                    i = new Intent(context, arena.kids.stories.activities.Story.class);

                i.putExtra("pos", newsId[position]);
                i.putExtra("maxi", newsId);
                i.putExtra("pgname", pgName);
                i.putExtra("posorg", String.valueOf(position) );
                Log.e("positioncatstory", String.valueOf(position) +"--"+newsId[position] +pgName);
                context.startActivity(i);
            }
        });

        return rowView;
    }

}
