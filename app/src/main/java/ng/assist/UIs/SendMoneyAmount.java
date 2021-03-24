package ng.assist.UIs;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ng.assist.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendMoneyAmount extends Fragment {


    public SendMoneyAmount() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_money_amount, container, false);
    }

}
