package ng.assist.UIs.Utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import org.w3c.dom.Text;

import java.util.ArrayList;

import ng.assist.Grocery;
import ng.assist.R;

public class CheckoutDialog {

    private Dialog checkoutDialog;
    private Context mContext;
    private EditText address,phone,landmark,lga;
    private MaterialButton checkOut;
    private TextView state;
    private OnDialogActionClickListener dialogActionClickListener;
    private ListDialog listDialog;
    ArrayList<String> locationList = new ArrayList<>();

    public interface OnDialogActionClickListener{
        void checkOutClicked(String phone, String address,String landmark, String state, String lga);
    }

    public void setDialogActionClickListener(OnDialogActionClickListener dialogActionClickListener) {
        this.dialogActionClickListener = dialogActionClickListener;
    }

    public CheckoutDialog(Context context){
        populateLocation();
        this.mContext = context;
        checkoutDialog = new Dialog(context);
        checkoutDialog.setContentView(R.layout.checkout_dialog_layout);
        address = checkoutDialog.findViewById(R.id.checkout_dialog_delivery_address);
        phone = checkoutDialog.findViewById(R.id.checkout_dialog_delivery_phone);
        checkOut = checkoutDialog.findViewById(R.id.check_out_dialog_button);
        landmark = checkoutDialog.findViewById(R.id.checkout_dialog_closest_landmark);
        state = checkoutDialog.findViewById(R.id.checkout_dialog_state);
        lga = checkoutDialog.findViewById(R.id.checkout_dialog_lga);


        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(address.getText().toString().trim())){
                    address.setError("Required");
                }
                if(TextUtils.isEmpty(phone.getText().toString().trim())){
                    phone.setError("Required");
                }
                if(TextUtils.isEmpty(landmark.getText().toString().trim())){
                    landmark.setError("Required");
                }
                if(TextUtils.isEmpty(state.getText().toString().trim())){
                    state.setError("Required");
                }
                if(TextUtils.isEmpty(lga.getText().toString().trim())){
                    lga.setError("Required");
                }
                else {
                    dialogActionClickListener.checkOutClicked(phone.getText().toString().trim(),address.getText().toString().trim(),landmark.getText().toString().trim(),state.getText().toString().trim(),lga.getText().toString().trim());
                    checkoutDialog.dismiss();
                }
            }
        });

        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog = new ListDialog(locationList, context);
                listDialog.showListDialog();
                listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
                    @Override
                    public void onItemClicked(String city) {
                        state.setText(city);
                    }
                });

            }
        });
    }

    public void ShowCheckoutDialog(){
        checkoutDialog.show();
    }

    private void populateLocation(){
        locationList.add("Lagos");
        locationList.add("Abuja");
        locationList.add("Kano");

        String[] stateList = {"Abia","Adamawa","Akwa Ibom","Anambra","Bauchi","Bayelsa","Benue","Borno","Cross River","Delta","Ebonyi","Edo","Ekiti","Enugu","Gombe","Imo","Jigawa","Kaduna","Kano","Katsina","Kebbi"
                ,"Kogi","Kwara","Lagos","Nasarawa","Niger","Ogun","Ondo","Osun","Oyo","Plateau","Rivers","Sokoto","Taraba","Yobe","Zamfara"};

        for (String city : stateList) {
            locationList.add(city);
        }

    }
}
