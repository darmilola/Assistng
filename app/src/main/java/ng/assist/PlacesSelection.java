package ng.assist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class PlacesSelection extends AppCompatActivity {

    private static final String PLACES_KEY = "AIzaSyC3E4CT1zmvybaW2bV_AzqeFIfw1z21pDQ";
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    // Set the fields to specify which types of place data to
    // return after the user has made a selection.
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_selection);
    }

    private void initView(){
        // Initialize the SDK
        Places.initialize(getApplicationContext(), PLACES_KEY);

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

    }
}
