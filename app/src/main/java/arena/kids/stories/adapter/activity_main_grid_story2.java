package arena.kids.stories.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import java.util.List;

import arena.kids.stories.R;


public class activity_main_grid_story2 extends RecyclerView.Adapter<activity_main_grid_story2.ViewHolder> {

    private List<Integer> mViewColors;
    private List<String> mAnimals;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context cnx;
    Activity act;
    public SharedPreferences p;
    String[] prgmNameListNew;
    String[] prgIndexN;String pgNameN;
    // data is passed into the constructor
    public activity_main_grid_story2(Activity act2, String[] prgmNameList, String[] prgIndex, String pgName) {
        act = act2;
        this.mInflater = LayoutInflater.from(act.getApplicationContext());
        cnx = act.getApplicationContext();
        prgmNameListNew=prgmNameList;
        prgIndexN=prgIndex;pgNameN=pgName;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_grid_main_story_2, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


//        holder.myView.setBackgroundColor(color);
        holder.txtCatTitle.setText(prgmNameListNew[position]);
        if(prgmNameListNew[position].length()>=15)
            holder.txtCatTitle.setText(prgmNameListNew[position].substring(0,14)+"..");

        String uri = "@drawable/a" + prgIndexN[position];
        Log.e("Pic","@drawable/a" + prgIndexN[position]);
        int imageResource = cnx.getResources().getIdentifier(uri, null, cnx.getPackageName());

        //imageview= (ImageView)findViewById(R.id.imageView);
        Drawable res = cnx.getResources().getDrawable(imageResource);

        try {
            //holder.img.setImageResource(imageResource);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(25));
            Glide.with(cnx)
                    .load(imageResource).apply(requestOptions).signature(new ObjectKey("56"))
                    .into(holder.catThumb) ;
        } catch (Exception e) {
            uri = "@drawable/a3";
            imageResource = cnx.getResources().getIdentifier(uri, null, cnx.getPackageName());

            Glide.with(cnx)
                    .load(imageResource).signature(new ObjectKey("56"))
                    .into(holder.catThumb);

        }

        holder.myView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //oast.makeText(context, "You Clicked " + result[position], Toast.LENGTH_LONG).show();

                Intent i;
                //if(pgName.contains("Picto"))
                //    i = new Intent(context, arena.kids.stories.activities.StoryPic.class);
                // else
                i = new Intent(cnx, arena.kids.stories.activities.Story.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("pos", prgIndexN[position]);
                i.putExtra("maxi", prgIndexN);
                i.putExtra("pgname", pgNameN);
                i.putExtra("posorg", String.valueOf(position) );
                Log.e("positioncatstory", String.valueOf(position) +"--"+prgIndexN[position] +pgNameN);
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

        ViewHolder(View itemView) {
            super(itemView);
            //myView = itemView.findViewById(R.id.t);
            myView=itemView;
            txtCatTitle = itemView.findViewById(R.id.txtCatTitle);
            catThumb = itemView.findViewById(R.id.catThumb);
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