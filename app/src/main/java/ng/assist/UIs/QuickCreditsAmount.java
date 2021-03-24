package ng.assist.UIs;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import ng.assist.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.tiper.MaterialSpinner;


public class QuickCreditsAmount extends Fragment {


    View view;
    MaterialSpinner materialSpinner;
    ArrayAdapter<CharSequence> adapter;
    public QuickCreditsAmount() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_quick_credits_amount, container, false);
        initView();
        return  view;
    }

    private void initView(){
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.quick_credit_loan_amount, android.R.layout.simple_spinner_item);
        materialSpinner = view.findViewById(R.id.quick_credits_amount);
        materialSpinner.setAdapter(adapter);
    }
}
