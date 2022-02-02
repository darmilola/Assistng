package ng.assist.UIs.Utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import ng.assist.R;

public class RefundDialog {

    private Dialog refundDialog;
    private Context mContext;
    private OnDialogActionClickListener dialogActionClickListener;
    private MaterialButton cancel;
    private MaterialButton send;
    private EditText reason;

    public interface OnDialogActionClickListener{
        void onActionClicked(String text);
    }

    public void setDialogActionClickListener(OnDialogActionClickListener dialogActionClickListener) {
        this.dialogActionClickListener = dialogActionClickListener;
    }

    public RefundDialog(Context context){
        this.mContext = context;
        refundDialog = new Dialog(context);
        refundDialog.setContentView(R.layout.request_refund_dialog_layout);
        cancel = refundDialog.findViewById(R.id.input_dialog_cancel);
        send = refundDialog.findViewById(R.id.input_dialog_send);
        reason = refundDialog.findViewById(R.id.input_dialog_type_here);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(reason.getText().toString())){
                    reason.setError("Required");
                }
                else {
                    dialogActionClickListener.onActionClicked(reason.getText().toString());
                    refundDialog.dismiss();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refundDialog.cancel();
            }
        });
    }

    public void ShowRefundDialog(){
        refundDialog.show();
    }

    public void CloseRefundDialog(){
        refundDialog.cancel();
    }
}
