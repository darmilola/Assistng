package ng.assist.UIs.Utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import ng.assist.R;

public class CheckoutDialog {

    private Dialog checkoutDialog;
    private Context mContext;
    private EditText address,phone;
    private MaterialButton checkOut;
    private OnDialogActionClickListener dialogActionClickListener;

    public interface OnDialogActionClickListener{
        void checkOutClicked(String phone, String address);
    }

    public void setDialogActionClickListener(OnDialogActionClickListener dialogActionClickListener) {
        this.dialogActionClickListener = dialogActionClickListener;
    }

    public CheckoutDialog(Context context){
        this.mContext = context;
        checkoutDialog = new Dialog(context);
        checkoutDialog.setContentView(R.layout.checkout_dialog_layout);
        address = checkoutDialog.findViewById(R.id.checkout_dialog_delivery_address);
        phone = checkoutDialog.findViewById(R.id.checkout_dialog_delivery_phone);
        checkOut = checkoutDialog.findViewById(R.id.check_out_dialog_button);


        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(address.getText().toString().trim())){
                    address.setError("Required");
                }
                if(TextUtils.isEmpty(phone.getText().toString().trim())){
                    phone.setError("Required");
                }
                else {
                    dialogActionClickListener.checkOutClicked(phone.getText().toString().trim(),address.getText().toString().trim());
                    checkoutDialog.dismiss();
                }
            }
        });
    }

    public void ShowCheckoutDialog(){
        checkoutDialog.show();
    }
}
