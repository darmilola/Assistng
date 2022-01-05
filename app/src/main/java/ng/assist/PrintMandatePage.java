package ng.assist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;



public class PrintMandatePage extends AppCompatActivity {


    String webContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_mandate_page);
        initView();
    }
    private void initView(){

    }

    @Override
    public void onBackPressed() {


    }
}
