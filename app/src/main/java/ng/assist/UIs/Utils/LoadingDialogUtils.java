package ng.assist.UIs.Utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import ng.assist.R;

public class LoadingDialogUtils {

    private Dialog loadingDialog;
    private TextView loadingText;
    private Context mContext;

    public LoadingDialogUtils(Context context){
        this.mContext = context;
        loadingDialog = new Dialog(mContext);
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.setCancelable(false);
        loadingText = loadingDialog.findViewById(R.id.loading_dialog_text);
    }

    public void showLoadingDialog(String text){
        loadingText.setText(text);
        loadingDialog.show();
    }
    public void cancelLoadingDialog(){
        loadingDialog.dismiss();
    }

}
