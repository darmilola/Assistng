package ng.assist.UIs.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import ng.assist.R;

public class AccomodationRefundDialog {

    private Dialog refundDialog;
    private Context mContext;
    private OnDialogActionClickListener dialogActionClickListener;
    private MaterialButton cancel;
    private MaterialButton send;
    private MaterialButton attachEvidence;
    private TextView evidenceTitle;
    private EditText reason;
    private String evidenceUrl = "";

    public interface OnDialogActionClickListener{
        void onActionClicked(String message, String evidenceUrl);
        void onEvidenceClicked();
    }

    public void setDialogActionClickListener(OnDialogActionClickListener dialogActionClickListener) {
        this.dialogActionClickListener = dialogActionClickListener;
    }

    public void setEvidenceUrl(String evidenceUrl) {
        this.evidenceUrl = evidenceUrl;
    }

    public void setEvidenceTitle(String evidenceTitle) {
        this.evidenceTitle.setText(evidenceTitle);
    }

    public AccomodationRefundDialog(Context context){
        this.mContext = context;
        refundDialog = new Dialog(context);
        refundDialog.setContentView(R.layout.accomodation_request_refund_dialog);
        attachEvidence = refundDialog.findViewById(R.id.input_dialog_attach_evidence);
        evidenceTitle = refundDialog.findViewById(R.id.input_dialog_attachment_title);
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
                    dialogActionClickListener.onActionClicked(reason.getText().toString(),evidenceUrl);
                    refundDialog.dismiss();
                }
            }
        });

        attachEvidence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dialogActionClickListener.onEvidenceClicked();
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


