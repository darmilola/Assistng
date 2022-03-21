package ng.assist.UIs.Utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.material.button.MaterialButton;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ng.assist.Grocery;
import ng.assist.R;

public class CheckOutPickupDialog {

    private Dialog checkoutDialog;
    private Context mContext;
    private EditText name,phone;
    private MaterialButton checkOut;
    private TextView date;
    private OnDialogActionClickListener dialogActionClickListener;
    private ListDialog listDialog;
    ArrayList<String> locationList = new ArrayList<>();

    public interface OnDialogActionClickListener{
        void checkOutClicked(String name, String phone,String date);
    }

    public void setDialogActionClickListener(OnDialogActionClickListener dialogActionClickListener) {
        this.dialogActionClickListener = dialogActionClickListener;
    }

    public CheckOutPickupDialog(Context context){
        populateLocation();
        this.mContext = context;
        checkoutDialog = new Dialog(context);
        checkoutDialog.setContentView(R.layout.checkout_dialog_shop_pickup);
        date = checkoutDialog.findViewById(R.id.checkout_dialog_pickup_date);
        name = checkoutDialog.findViewById(R.id.checkout_dialog_pickup_name);
        phone = checkoutDialog.findViewById(R.id.checkout_dialog_pickup_phone);
        checkOut = checkoutDialog.findViewById(R.id.check_out_dialog_button);

        date.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 checkoutDialog.cancel();
                 new SingleDateAndTimePickerDialog.Builder(context)
                         .displayMinutes(false)
                         .displayHours(false)
                         .displayDays(false)
                         .displayMonth(true)
                         .displayYears(true)
                         .displayDaysOfMonth(true)
                         .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                             @Override
                             public void onDisplayed(SingleDateAndTimePicker picker) {
                                 // Retrieve the SingleDateAndTimePicker
                             }
                         })
                         .title("Select Date")
                         .listener(new SingleDateAndTimePickerDialog.Listener() {
                             @Override
                             public void onDateSelected(Date mDate) {
                                    date.setText(mDate.toString());
                                    checkoutDialog.show();
                             }
                         }).display();
             }
         });

        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(isValidForm()){
                  String mName  = name.getText().toString().trim();
                  String mPhone = phone.getText().toString().trim();
                  String mDate = date.getText().toString().trim();

                  dialogActionClickListener.checkOutClicked(mName,mPhone,mDate);
               }
            }
        });



    }

    public void ShowCheckoutDialog(){
        checkoutDialog.show();
    }

    public void closeCheckoutDialog(){
        checkoutDialog.cancel();
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

    private boolean isValidForm(){
      boolean isValid  = true;

      if(name.getText().toString().trim().equalsIgnoreCase("")){
          isValid = false;
          name.setError("Required");
          return isValid;
      }

        if(date.getText().toString().trim().equalsIgnoreCase("")){
            isValid = false;
            date.setError("Required");
            return isValid;
        }

        if(phone.getText().toString().trim().equalsIgnoreCase("")){
            isValid = false;
            phone.setError("Required");
            return isValid;
        }

      return  isValid;
    }

}
