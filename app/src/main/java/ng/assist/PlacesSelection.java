package ng.assist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;



import java.util.Arrays;
import java.util.List;

public class PlacesSelection extends AppCompatActivity {

    private static final String PLACES_KEY = "AIzaSyC3E4CT1zmvybaW2bV_AzqeFIfw1z21pDQ";
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    // Set the fields to specify which types of place data to
    // return after the user has made a selection.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_selection);
    }

    private void initView(){
        // Initialize the SDK



    }
}
