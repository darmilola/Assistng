package ng.assist.UIs;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.GroceryDisplayAdapter;
import ng.assist.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroceryFastFoods extends Fragment {


    RecyclerView recyclerView;
    View view;
    GroceryDisplayAdapter groceryDisplayAdapter;
    ArrayList<String> groceryList = new ArrayList<>();

    public GroceryFastFoods() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_grocery_fast_foods, container, false);
         initView();
         return  view;
    }

    private void initView(){
        recyclerView = view.findViewById(R.id.grocery_recyclerview);

        for(int i = 0; i < 20; i++){
            groceryList.add("");
        }
        groceryDisplayAdapter = new GroceryDisplayAdapter(groceryList,getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(groceryDisplayAdapter);
    }

}
