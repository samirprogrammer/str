package arena.kids.stories.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.signature.ObjectKey;

import java.util.List;

import arena.kids.stories.R;


public class CatMainPageAdapter extends RecyclerView.Adapter<CatMainPageAdapter.ViewHolder> {

    private List<Integer> mViewColors;
    private List<String> mAnimals;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context cnx;
    Activity act;
    public SharedPreferences p;
    String[] prgmNameListNew;
    String[] prgImagesN;

    // data is passed into the constructor
    public CatMainPageAdapter(Activity act2, String[] prgmNameList, String[] prgImages) {
        act = act2;
        this.mInflater = LayoutInflater.from(act.getApplicationContext());
        cnx = act.getApplicationContext();
        prgmNameListNew = prgmNameList;
        prgImagesN = prgImages;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_grid_cat_main_page, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        SharedPreferences sharedPref = cnx.getSharedPreferences("Shared", MODE_PRIVATE);

        if (sharedPref.getInt("theme", 0) == 0) {
          //  ColorStateList color = ColorStateList.valueOf(cnx.getResources().getColor(R.color.pink));
             holder.cardCat.setCardBackgroundColor(cnx.getResources().getColor(R.color.pink));
        } else {
            holder.cardCat.setCardBackgroundColor(cnx.getResources().getColor(R.color.cardview_dark_background));
        }

//        holder.myView.setBackgroundColor(color);
        holder.txtCatTitle.setText(prgmNameListNew[position]);
        String uri = "@drawable/" + prgImagesN[position];
        Log.e("Pic", "@drawable/" + prgImagesN[position]);
        int imageResource = cnx.getResources().getIdentifier(uri, null, cnx.getPackageName());

        //imageview= (ImageView)findViewById(R.id.imageView);
        //Drawable res = context.getResources().getDrawable(imageResource);
        try {
            // holder.img.setImageResource(imageResource);

            Glide.with(cnx)
                    .load(imageResource)
                    // .diskCacheStrategy(DiskCacheStrategy.ALL).apply(requestOptions)
                    .signature(new ObjectKey("49")).format(DecodeFormat.PREFER_ARGB_8888)
                    .into(holder.catThumb);

        } catch (Exception e) {
            uri = "@drawable/a3";
            imageResource = cnx.getResources().getIdentifier(uri, null, cnx.getPackageName());

            //imageview= (ImageView)findViewById(R.id.imageView);

            //Drawable res = context.getResources().getDrawable(imageResource);
            // holder.img.setImageResource(imageResource);
            Glide.with(cnx)
                    .load(imageResource).signature(new ObjectKey("56"))
                    .into(holder.catThumb);
        }

        holder.myView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //oast.makeText(context, "You Clicked " + result[position], Toast.LENGTH_LONG).show();

                Intent i = new Intent(cnx, arena.kids.stories.activities.CatActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("pgname", prgmNameListNew[position]);
                i.putExtra("pgurl", prgmNameListNew[position]);
                i.putExtra("newID", prgmNameListNew[position]);
                cnx.startActivity(i);
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return prgmNameListNew.length;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View myView;
        TextView txtCatTitle;
        ImageView catThumb;
        CardView cardCat;

        ViewHolder(View itemView) {
            super(itemView);
            //myView = itemView.findViewById(R.id.t);
            myView = itemView;
            txtCatTitle = itemView.findViewById(R.id.txtCatTitle);
            catThumb = itemView.findViewById(R.id.catThumb);
            cardCat = itemView.findViewById(R.id.cardCatMain);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mAnimals.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}