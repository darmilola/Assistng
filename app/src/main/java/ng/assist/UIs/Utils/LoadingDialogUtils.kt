package ng.assist.UIs.Utils

import android.app.Dialog
import android.content.Context
import android.widget.TextView


import ng.assist.R

class LoadingDialogUtils(private val mContext: Context) {

    private val loadingDialog: Dialog
    private val loadingText: TextView

    init {
        loadingDialog = Dialog(mContext)
        loadingDialog.setContentView(R.layout.loading_dialog)
        loadingDialog.setCancelable(false)
        loadingText = loadingDialog.findViewById(R.id.loading_dialog_text)
    }

    fun showLoadingDialog(text: String) {
        loadingText.text = text
        loadingDialog.show()
    }

    fun cancelLoadingDialog() {
        loadingDialog.dismiss()
    }

}
