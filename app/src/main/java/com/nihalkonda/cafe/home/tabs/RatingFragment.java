package com.nihalkonda.cafe.home.tabs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.nihalkonda.cafe.R;
import com.nihalkonda.cafe.model.MyPlace;
import com.nihalkonda.cafe.model.PlacesAdapter;
import com.nihalkonda.cafe.model.RecyclerItemClicked;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class RatingFragment extends Fragment implements UpdateLocationAndPlaces{

    TextView textView;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<MyPlace> placesList;
    private RecyclerItemClicked itemClickOperation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View main =  inflater.inflate(R.layout.fragment_rating, container, false);

        recyclerView = (RecyclerView) main.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);

        itemClickOperation = new RecyclerItemClicked() {
            @Override
            public void itemClicked(Object o, int index) {
                MyPlace myPlace = (MyPlace)o;
                Intent i=new Intent(Intent.ACTION_VIEW);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                String url = "https://www.google.com/maps/place/"+myPlace.getName()+"/@"+myPlace.getLocation().latitude+","+myPlace.getLocation().longitude+",17z/";
                url = "https://www.google.com/maps/place/?q=place_id:"+myPlace.getPlaceId();
                System.out.println(url);
                i.setData(Uri.parse(url));


                try {
                    i.setPackage("com.android.chrome");
                    startActivity(i);
                }catch (Exception e){
                    Toast.makeText(getActivity(),"Please Install Chrome",Toast.LENGTH_LONG).show();
                }
            }
        };

        // specify an adapter (see also next example)


        return main;
    }

    @Override
    public void newLocation(LatLng newLocation) {

    }

    @Override
    public void newPlacesList(ArrayList<MyPlace> newPlacesList) {
        placesList = newPlacesList;
        Collections.sort(placesList, new Comparator<MyPlace>() {
            @Override
            public int compare(MyPlace p1, MyPlace p2) {
                return Double.compare(p2.getRating(),p1.getRating());
            }
        });
        mAdapter = new PlacesAdapter(getActivity(),placesList,itemClickOperation);
        recyclerView.setAdapter(mAdapter);
    }
}