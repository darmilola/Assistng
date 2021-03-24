package ng.assist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

public class BusCheckOut extends AppCompatActivity {

    MaterialButton paynow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_check_out);
        paynow = findViewById(R.id.bus_checkout_paynow);

        paynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusCheckOut.this,ViewTicket.class));
            }
        });
    }
}
