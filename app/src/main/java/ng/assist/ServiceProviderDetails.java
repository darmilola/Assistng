package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import me.relex.circleindicator.CircleIndicator2;
import ng.assist.Adapters.PortfolioAdapter;
import ng.assist.Adapters.ProductImageScrollAdapter;
import ng.assist.Adapters.ServiceProvidersAdapter;
import ng.assist.UIs.ItemDecorator;
import ng.assist.UIs.ViewModel.ProviderDetailsModel;
import ng.assist.UIs.ViewModel.ServicesModel;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ServiceProviderDetails extends AppCompatActivity {


    RecyclerView recyclerView;
    ProductImageScrollAdapter adapter;
    ArrayList<String> portfolioItemList = new ArrayList<>();
    LinearLayout chat,call;
    TextView name, jobTitle;
    TextView ratings, jobsCount, ratePerHour;
    ServicesModel servicesModel;
    ProviderDetailsModel providerDetailsModel;
    CircleIndicator2 imagesIndicator;
    NestedScrollView rootLayout;
    ProgressBar portfolioLoadingProgress;
    ImageView profileImage;
    LinearLayout imageScrollLayout;
    LinearLayout serviceProviderBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_details);
        initView();
    }

    private void initView() {
        call = findViewById(R.id.service_item_call);
        chat = findViewById(R.id.service_item_chat);
        serviceProviderBook = findViewById(R.id.provider_details_book);
        imageScrollLayout = findViewById(R.id.scroll_image_layout);
        rootLayout = findViewById(R.id.service_provider_details_root);
        portfolioLoadingProgress = findViewById(R.id.service_provider_details_progress);
        servicesModel = getIntent().getParcelableExtra("provider_info");
        name = findViewById(R.id.provider_details_name);
        jobTitle = findViewById(R.id.provider_details_title);
        ratings = findViewById(R.id.provider_details_rating);
        profileImage = findViewById(R.id.provider_details_image);
        jobsCount = findViewById(R.id.provider_details_jobs);
        ratePerHour = findViewById(R.id.provider_details_rate_per_hour);
        imagesIndicator = findViewById(R.id.product_image_indicator);
        recyclerView = findViewById(R.id.product_image_recyclerview);

        Glide.with(ServiceProviderDetails.this)
                .load(servicesModel.getHandymanImage())
                .placeholder(R.drawable.background_image)
                .error(R.drawable.background_image)
                .into(profileImage);

        name.setText(servicesModel.getHandymanFirstname()+" "+ servicesModel.getHandymanLastname());
        jobTitle.setText(servicesModel.getJobTitle());
        ratePerHour.setText(servicesModel.getRatePerHour());
        jobsCount.setText(servicesModel.getHandymanJobs());
        ratings.setText(servicesModel.getHandymanRatings());

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceProviderDetails.this,ChatActivity.class);
                intent.putExtra("receiverId",servicesModel.getHandymanId());
                intent.putExtra("receiverFirstname",servicesModel.getHandymanFirstname());
                intent.putExtra("receiverLastname",servicesModel.getHandymanLastname());
                intent.putExtra("receiverImageUrl",servicesModel.getHandymanImage());
                startActivity(intent);
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", servicesModel.getHandymanPhone(), null));
                startActivity(intent);
            }
        });


        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        LinearLayoutManager imagesManager = new LinearLayoutManager(ServiceProviderDetails.this, LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(imagesManager);

        providerDetailsModel = new ProviderDetailsModel(servicesModel.getHandymanId());
        providerDetailsModel.getProviderPortfolio();
        providerDetailsModel.setImagesReadyListener(new ProviderDetailsModel.ImagesReadyListener() {
            @Override
            public void onImageReady(ArrayList<String> imagesList) {
                adapter = new ProductImageScrollAdapter(imagesList,ServiceProviderDetails.this);
                recyclerView.setAdapter(adapter);
                pagerSnapHelper.attachToRecyclerView(recyclerView);
                imagesIndicator.attachToRecyclerView(recyclerView, pagerSnapHelper);
                adapter.registerAdapterDataObserver(imagesIndicator.getAdapterDataObserver());
                rootLayout.setVisibility(View.VISIBLE);
                portfolioLoadingProgress.setVisibility(View.GONE);
                imageScrollLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ServiceProviderDetails.this, message, Toast.LENGTH_SHORT).show();
                rootLayout.setVisibility(View.VISIBLE);
                portfolioLoadingProgress.setVisibility(View.GONE);
            }
        });

        serviceProviderBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceProviderDetails.this,PayServiceFee.class);
                intent.putExtra("provider_info",servicesModel);
                startActivity(intent);
            }
        });
    }

    public static int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
    }

}