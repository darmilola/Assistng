package ng.assist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ChatFullImage extends AppCompatActivity {

    ImageView fullImage;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_full_image);
        fullImage = findViewById(R.id.chat_full_image);
        url = getIntent().getStringExtra("imageUrl");
        Picasso.get().load(url).into(fullImage);
    }
}
