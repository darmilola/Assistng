package ng.assist.UIs.Utils;

import android.app.Dialog;
import android.content.Context;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import ng.assist.R;


public class InputDialog {

    private Dialog inputDialog;
    private Context mContext;
    private TextView dialogTitle;
    private EditText typeHere;
    private OnDialogActionClickListener dialogActionClickListener;
    private MaterialButton cancel,save;

    public interface OnDialogActionClickListener{
        void saveClicked(String text);
        void cancelClicked();
    }

    public void setDialogActionClickListener(OnDialogActionClickListener dialogActionClickListener) {
        this.dialogActionClickListener = dialogActionClickListener;
    }

    public InputDialog(Context context, String title){
        inputDialog = new Dialog(context);
        inputDialog.setCancelable(false);
        this.mContext = context;
        inputDialog.setContentView(R.layout.input_dialog_layout);
        dialogTitle = inputDialog.findViewById(R.id.input_dialog_title);
        typeHere = inputDialog.findViewById(R.id.input_dialog_type_here);
        cancel = inputDialog.findViewById(R.id.input_dialog_cancel);
        save = inputDialog.findViewById(R.id.input_dialog_save);
        dialogTitle.setText(title);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogActionClickListener.cancelClicked();
                inputDialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(typeHere.getText().toString().trim())){
                    typeHere.setError("Required");
                }
                else{
                    dialogActionClickListener.saveClicked(typeHere.getText().toString());
                    inputDialog.dismiss();
                }
            }
        });
    }

    public void showInputDialog(){
        inputDialog.show();
    }

}
