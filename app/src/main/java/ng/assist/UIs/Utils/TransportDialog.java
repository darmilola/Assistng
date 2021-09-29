package ng.assist.UIs.Utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import ng.assist.R;

public class TransportDialog {

    private Dialog transportDialog;
    private Context mContext;
    private TextView route,fare;
    private EditText phoneInput;
    private MaterialButton bookNow;
    private OnDialogActionClickListener dialogActionClickListener;

    public interface OnDialogActionClickListener{
        void bookClicked(String phonenumber);
    }

    public void setDialogActionClickListener(OnDialogActionClickListener dialogActionClickListener) {
        this.dialogActionClickListener = dialogActionClickListener;
    }

    public TransportDialog(Context context, String from, String to, String tFare){
        this.mContext = context;
        transportDialog = new Dialog(context);
        transportDialog.setContentView(R.layout.book_transport_dialog_layout);
        route = transportDialog.findViewById(R.id.transport_dialog_route);
        fare = transportDialog.findViewById(R.id.transport_dialog_fare);
        phoneInput = transportDialog.findViewById(R.id.transport_dialog_phone);
        bookNow = transportDialog.findViewById(R.id.transport_dialog_booknow);

        route.setText(from+" - "+to);
        fare.setText(tFare);

        bookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(phoneInput.getText().toString())){
                    phoneInput.setError("Required");
                }
                else {
                     dialogActionClickListener.bookClicked(phoneInput.getText().toString());
                     transportDialog.dismiss();
                }
            }
        });
    }

    public void ShowTransportDialog(){
          transportDialog.show();
    }
}
