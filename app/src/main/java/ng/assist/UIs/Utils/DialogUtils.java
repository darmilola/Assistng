package ng.assist.UIs.Utils;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;

import com.airbnb.lottie.LottieAnimationView;

import ng.assist.R;

public class DialogUtils {

    private Dialog loanDialog;
    private Context context;
    private LottieAnimationView loanAnimationView;
    private loanAnimationSuccessListener loanAnimationSuccessListener;

    public interface loanAnimationSuccessListener{
         void onSuccessAnimationCompleted();
    }

    public void setLoanAnimationSuccessListener(DialogUtils.loanAnimationSuccessListener loanAnimationSuccessListener) {
        this.loanAnimationSuccessListener = loanAnimationSuccessListener;
    }

    public DialogUtils(Context context){
        this.context = context;
        loanDialog = new Dialog(context);
        loanDialog.setContentView(R.layout.loan_dialog_layout);
        loanAnimationView = loanDialog.findViewById(R.id.loan_animation_view);
    }

    public void showLoanProcessingDialog() {
        loanDialog.setCancelable(false);
        loanDialog.show();
        loanAnimationView.setMaxFrame(30);
    }

    public void showLoanSuccessDialog(){

        loanAnimationView.pauseAnimation();
        loanAnimationView.setMinAndMaxFrame(0,89);
        loanAnimationView.resumeAnimation();
        loanAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {



            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                loanAnimationSuccessListener.onSuccessAnimationCompleted();
                closeLoanProcessingDialog();
            }
        });
    }

    public void closeLoanProcessingDialog(){
        if(loanDialog != null){
            loanDialog.dismiss();
            loanDialog.cancel();
        }
    }

}
