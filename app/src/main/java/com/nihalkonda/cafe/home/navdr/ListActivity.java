package com.nihalkonda.cafe.home.navdr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nihalkonda.cafe.R;
import com.nihalkonda.cafe.model.MyPlace;
import com.nihalkonda.cafe.model.PlaceManager;
import com.nihalkonda.cafe.model.PlacesAdapter;
import com.nihalkonda.cafe.model.RecyclerItemClicked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class ListActivity extends BaseNavigationActivity {


    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<MyPlace> placesList;
    private RecyclerItemClicked itemClickOperation;

    private PopupMenu.OnMenuItemClickListener menuItemClickListener;

    int sortingStyle=0;

    private ArrayList<String> menuOptions = new ArrayList<String>(Arrays.asList(
            "Rating HTL", "Rating LTH", "Rating Count HTL", "Rating Count LTH", "Price Range HTL", "Price Range LTH"
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.content_list);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
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
                    Toast.makeText(ListActivity.this,"Please Install Chrome",Toast.LENGTH_LONG).show();
                }
            }
        };

        placesList = PlaceManager.getInstance().getPlaceList();
        sortPlaceList();

        menuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sortingStyle = menuOptions.indexOf(item.getTitle());
                sortPlaceList();
                return false;
            }
        };


        setFloatingButton(
                getResources().getDrawable(android.R.drawable.ic_menu_sort_by_size),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu menu = new PopupMenu(ListActivity.this,view);
                        for(String menuOpt:menuOptions)
                            menu.getMenu().add(menuOpt);
                        menu.setOnMenuItemClickListener(menuItemClickListener);
                        menu.show();
                    }
                }
        );
    }

    private void sortPlaceList() {
        Collections.sort(placesList, new Comparator<MyPlace>() {
            @Override
            public int compare(MyPlace p1, MyPlace p2) {
                switch (sortingStyle){
                    case 0:
                        return Double.compare(p2.getRating(),p1.getRating());
                    case 1:
                        return Double.compare(p1.getRating(),p2.getRating());
                    case 2:
                        return Integer.compare(p2.getRatingCount(),p1.getRatingCount());
                    case 3:
                        return Integer.compare(p1.getRatingCount(),p2.getRatingCount());
                    case 4:
                        return Integer.compare(p2.getPriceRange(),p1.getPriceRange());
                    case 5:
                        return Integer.compare(p1.getPriceRange(),p2.getPriceRange());
                    default:
                        return p1.getName().compareTo(p2.getName());
                }
            }
        });
        mAdapter = new PlacesAdapter(this,placesList,itemClickOperation);
        recyclerView.setAdapter(mAdapter);
    }


}