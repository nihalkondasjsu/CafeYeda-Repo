package com.nihalkonda.cafe.model;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nihalkonda.cafe.R;
import com.nihalkonda.cafe.utils.Server;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.MyViewHolder> {
private ArrayList<MyPlace> mDataset;
private RecyclerItemClicked itemClicked;
public Activity activity;
// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public static class MyViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    public LinearLayout l;
    public MyViewHolder(LinearLayout v) {
        super(v);
        l = v;
    }

    public void bind(final Activity activity,final MyPlace place, RecyclerItemClicked itemClicked, int index) {

        ((TextView)l.findViewById(R.id.place_name)).setText(place.getName());
        ((TextView)l.findViewById(R.id.place_rating)).setText(place.getRating()+"");
        ((TextView)l.findViewById(R.id.place_rating_count)).setText(place.getRatingCount()+"");
        ((TextView)l.findViewById(R.id.place_price_range)).setText(place.getPriceRange()+"");
        ((TextView)l.findViewById(R.id.place_address)).setText(place.getFormattedAddress());
        Picasso.get().load(Server.getInstance().getPlaceImage(activity,place.getPhotoReference())).into(((ImageView)l.findViewById(R.id.place_image)));
        //https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU&key=AIzaSyApTSRZpE54zD5XAf6MuVO966rE1HkFxh8
        l.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                itemClicked.itemClicked(place,index);
            }
        });
    }
}

    // Provide a suitable constructor (depends on the kind of dataset)
    public PlacesAdapter(Activity activity,ArrayList<MyPlace> myDataset, RecyclerItemClicked itemClicked) {
        mDataset = myDataset;
        this.itemClicked=itemClicked;
        this.activity = activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PlacesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        LinearLayout l = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_item, parent, false);
        MyViewHolder vh = new MyViewHolder(l);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        MyPlace place = mDataset.get(position);

        holder.bind(activity,place,itemClicked,position);
        //https://www.google.com/maps/place/?q=place_id:
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}