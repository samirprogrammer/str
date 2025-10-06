package arena.kids.stories.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import arena.kids.stories.R;
import arena.kids.stories.activities.Story;

public class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.SlideViewHolder> {

    private final Context context;
    private final String[] slideNames; // e.g., "502", "512", etc.
    String[] prgmNameListNew;String[] prgIndexN;String pgNameN;
    public SlideAdapter(Context context,String[] prgmNameList, String[] slideNames) {
        this.context = context;
        this.slideNames = slideNames;
        prgmNameListNew=prgmNameList;
        prgIndexN=slideNames;pgNameN="";
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_slide, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        String name = slideNames[position];

        // Build drawable resource ID
        int resId = context.getResources().getIdentifier("a"+ name, "drawable", context.getPackageName());

        if (resId != 0) {
            // Glide with fade + rounded corners
            RequestOptions requestOptions = new RequestOptions()
                    .transforms(new CenterCrop(), new RoundedCorners(25));

            Glide.with(context)
                    .load(resId)
                    .apply(requestOptions)
                    .signature(new ObjectKey("56"))
                    .into(holder.slideImage);

            // Fade animation
            AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
            fadeIn.setDuration(500);
            fadeIn.setFillAfter(true);
            holder.slideImage.startAnimation(fadeIn);

            // Click listener
            holder.slideImage.setOnClickListener(v -> {
                Intent intent = new Intent(context, Story.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("pos", prgIndexN[position]);
                intent.putExtra("maxi", prgIndexN);
                intent.putExtra("pgname", "Moral");
                context.startActivity(intent);
            });

            /*holder.myView.setOnClickListener(new View.OnClickListener() {

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
            });*/

        } else {
            Log.e("SlideAdapter", "❌ Drawable not found for: " + name);
            holder.slideImage.setImageResource(R.drawable.ic_launcher);
        }
    }

    @Override
    public int getItemCount() {
        return slideNames.length;
    }

    static class SlideViewHolder extends RecyclerView.ViewHolder {
        ImageView slideImage;
        public SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            slideImage = itemView.findViewById(R.id.slideImage);
        }
    }
}
