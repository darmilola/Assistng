package ng.assist.UIs;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import ng.assist.MainActivity;
import ng.assist.R;
import ng.assist.UIs.ViewModel.RecipientUserInfo;
import ng.assist.UIs.ViewModel.SendMoneyModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendMoneyRecepient extends Fragment {


    MaterialButton next;
    TextInputEditText userEmailField;
    View view;
    private String userEmail;
    onRecipientReady recipientReady;
    public SendMoneyRecepient() {
        // Required empty public constructor
    }

    public interface onRecipientReady{
        void onReady(RecipientUserInfo recipientUserInfo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
          view =  inflater.inflate(R.layout.fragment_send_money_recepient, container, false);
          initView();
          return  view;
    }

    private void initView(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userEmail = preferences.getString("userEmail","");
        next = view.findViewById(R.id.send_money_recipient_next);
        userEmailField = view.findViewById(R.id.send_money_recipient_email);
    }


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        recipientReady = (onRecipientReady) context;
    }

    @Override
    public void onResume() {
        super.onResume();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(userEmailField.getText().toString().trim())){
                    userEmailField.setError("Required");
                }
                else if(userEmailField.getText().toString().trim().equalsIgnoreCase(userEmail)){
                    Toast.makeText(getContext(), "Invalid Request", Toast.LENGTH_SHORT).show();
                }
                else{
                    SendMoneyModel sendMoneyModel = new SendMoneyModel(userEmailField.getText().toString().trim(),getContext());
                    sendMoneyModel.showUser();
                    sendMoneyModel.setShowUserListener(new SendMoneyModel.ShowUserListener() {
                        @Override
                        public void onSuccessful(RecipientUserInfo recipientUserInfo) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                            preferences.edit().putString("sendMoneyFirstname",recipientUserInfo.getUserFirstname()).apply();
                            preferences.edit().putString("sendMoneyLastname",recipientUserInfo.getUserLastname()).apply();
                            preferences.edit().putString("sendMoneyEmail",recipientUserInfo.getUserEmail()).apply();
                            preferences.edit().putString("sendMoneyImageUrl",recipientUserInfo.getUserImageUrl()).apply();
                            recipientReady.onReady(recipientUserInfo);
                        }
                        @Override
                        public void onFailure(String message) {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
