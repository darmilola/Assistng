package ng.assist.UIs;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.tiper.MaterialSpinner;

import ng.assist.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuickCreditsOccupation extends Fragment {


    View view;
    MaterialSpinner materialSpinner;
    ArrayAdapter<CharSequence> adapter;

    public QuickCreditsOccupation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_quick_credits_occupation, container, false);
        initView();
        return  view;

    }

    private void initView(){
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.quick_credits_type, android.R.layout.simple_spinner_item);
        materialSpinner = view.findViewById(R.id.quick_credits_occupation_spinner);
        materialSpinner.setAdapter(adapter);
    }

}
