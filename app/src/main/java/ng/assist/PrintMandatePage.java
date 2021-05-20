package ng.assist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.asksira.webviewsuite.WebViewSuite;

public class PrintMandatePage extends AppCompatActivity {

    WebViewSuite mandateWebview;
    String webContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_mandate_page);
        initView();
    }
    private void initView(){
        mandateWebview = findViewById(R.id.print_mandate_webview);
        webContent = getIntent().getStringExtra("web_content");
        mandateWebview.startLoadData(webContent,"text/html","UTF-8");

        mandateWebview.setOpenPDFCallback(new WebViewSuite.WebViewOpenPDFCallback() {
            @Override
            public void onOpenPDF() {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!mandateWebview.goBackIfPossible()) super.onBackPressed();

    }
}
