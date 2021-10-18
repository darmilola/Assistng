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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.sql.Timestamp;
import java.util.Date;

import androidx.room.Room;
import de.hdodenhof.circleimageview.CircleImageView;
import ng.assist.R;
import ng.assist.UIs.ViewModel.SendMoneyModel;
import ng.assist.UIs.ViewModel.TransactionDao;
import ng.assist.UIs.ViewModel.TransactionDatabase;
import ng.assist.UIs.ViewModel.Transactions;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendMoneyAmount extends Fragment {


    View view;
    CircleImageView profileImage;
    TextView userName;
    TextView userEmail;
    EditText amountToSend;
    MaterialButton sendButton;
    int walletBalance = 0;
    String payerEmail = "";
    String recipientEmail = "";
    SendMoneyListener sendMoneyListener;
    public SendMoneyAmount() {
        // Required empty public constructor
    }
    public interface SendMoneyListener{
        void onSendSuccess();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        sendMoneyListener = (SendMoneyListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_send_money_amount, container, false);
        initView();
        return view;
    }

    private void initView(){
        profileImage = view.findViewById(R.id.send_money_profile_image);
        userName = view.findViewById(R.id.send_money_name);
        userEmail = view.findViewById(R.id.send_money_email);
        sendButton = view.findViewById(R.id.send_money_send_button);
        amountToSend = view.findViewById(R.id.send_money_amount);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        walletBalance = Integer.parseInt(preferences.getString("walletBalance",""));

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(amountToSend.getText().toString().trim())){
                    amountToSend.setError("Required");
                }
                else if(walletBalance < Long.parseLong(amountToSend.getText().toString())){
                    Toast.makeText(getContext(), "Insufficient Balance", Toast.LENGTH_SHORT).show();
                }
                else{
                    SendMoneyModel sendMoneyModel = new SendMoneyModel(recipientEmail,payerEmail,amountToSend.getText().toString(),getContext());
                    sendMoneyModel.sendMoneyToUser();
                    sendMoneyModel.setSendMoneyListener(new SendMoneyModel.sendMoneyListener() {
                        @Override
                        public void onSuccessful() {
                            Date date = new Date();
                            Timestamp timestamp = new Timestamp(date.getTime());
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                            preferences.edit().putString("sendMoneyAmount",amountToSend.getText().toString()).apply();
                            sendMoneyListener.onSendSuccess();
                            insertBooking(0,6,"Send Money",timestamp.toString(),amountToSend.getText().toString(),"");
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

    @Override
    public void onResume() {
        super.onResume();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            String firstname = preferences.getString("sendMoneyFirstname","");
            String lastname = preferences.getString("sendMoneyLastname","");
            String email = preferences.getString("sendMoneyEmail","");
            this.recipientEmail = email;
            String imageUrl = preferences.getString("sendMoneyImageUrl","");
            payerEmail = preferences.getString("userEmail","");
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.profileplaceholder)
                    .error(R.drawable.profileplaceholder)
                    .into(profileImage);
            userName.setText(firstname+" "+lastname);
            userEmail.setText(email);
    }

    private void insertBooking(int id,int type, String title, String timestamp, String amount, String orderId){
        TransactionDatabase db = Room.databaseBuilder(getContext(),
                TransactionDatabase.class, "transactions").allowMainThreadQueries().build();
        Transactions transactions = new Transactions(id,type,title,timestamp,amount,orderId);
        TransactionDao transactionDao = db.transactionDao();
        transactionDao.insert(transactions);
    }


}
