package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import me.relex.circleindicator.CircleIndicator2;
import ng.assist.Adapters.AddProductImageAdapter;
import ng.assist.Adapters.ProductImageScrollAdapter;
import ng.assist.UIs.Babies;
import ng.assist.UIs.Computing;
import ng.assist.UIs.Electronics;
import ng.assist.UIs.Fashion;
import ng.assist.UIs.GroceryFastFoods;
import ng.assist.UIs.HealthAndBeauty;
import ng.assist.UIs.HomeAndOffice;
import ng.assist.UIs.PhoneAndSupplies;
import ng.assist.UIs.Utils.ListDialog;
import ng.assist.UIs.ViewModel.EcommerceDashboardModel;
import ng.assist.UIs.ViewModel.EstateDashboardModel;
import ng.assist.UIs.ViewModel.ProductImageModel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DashboardAddProduct extends AppCompatActivity {

    private static int PICK_IMAGE = 1;
    RecyclerView imagesRecyclerview;
    EditText title,price,description;
    String mTitle,mPrice,mDescription,mCategory,mAvailability;
    TextView  category;
    LinearLayout cancel,save;
    AddProductImageAdapter adapter;
    ArrayList<ProductImageModel> imagesList = new ArrayList<>();
    ArrayList<String> categoryList = new ArrayList<>();
    CircleIndicator2 imagesIndicator;
    ListDialog listDialog;
    String productId = "",displayImage = "";
    MaterialButton selectImage;
    String shopName, retailerId;
    LinearLayout scrollImageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_add_product);
        initView();
    }

    private void initView(){
        populateCategory();
        selectImage = findViewById(R.id.add_product_select_images);
        title = findViewById(R.id.add_product_title);
        price = findViewById(R.id.add_product_price);
        description = findViewById(R.id.add_product_description);
        category = findViewById(R.id.add_product_product_category);
        cancel = findViewById(R.id.add_product_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save = findViewById(R.id.add_product_save);
        scrollImageLayout = findViewById(R.id.scroll_image_layout);
        imagesRecyclerview = findViewById(R.id.product_image_recyclerview);
        imagesIndicator = findViewById(R.id.product_image_indicator);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();

        adapter = new AddProductImageAdapter(imagesList,this);
        LinearLayoutManager imagesManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        imagesRecyclerview.setLayoutManager(imagesManager);
        imagesRecyclerview.setAdapter(adapter);
        pagerSnapHelper.attachToRecyclerView(imagesRecyclerview);
        imagesIndicator.attachToRecyclerView(imagesRecyclerview, pagerSnapHelper);
        adapter.registerAdapterDataObserver(imagesIndicator.getAdapterDataObserver());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DashboardAddProduct.this);
        retailerId = preferences.getString("userEmail","");
        shopName = preferences.getString("shopName","");


        description.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                view.getParent().requestDisallowInterceptTouchEvent(true);
                if ((motionEvent.getAction() & MotionEvent.ACTION_UP) != 0 && (motionEvent.getActionMasked() & MotionEvent.ACTION_UP) != 0)
                {
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                }
                return false;
            }
        });


        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog = new ListDialog(categoryList,DashboardAddProduct.this);
                listDialog.showListDialog();
                listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
                    @Override
                    public void onItemClicked(String text) {
                        category.setText(text);
                    }
                });
            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitle = title.getText().toString().trim();
                mPrice = price.getText().toString().trim();
                mCategory = category.getText().toString().trim();
                mDescription = description.getText().toString().trim();

                if(isValidInput()){
                   EcommerceDashboardModel ecommerceDashboardModel = new EcommerceDashboardModel(productId,retailerId,mTitle,mPrice,mCategory,mDescription,shopName,"true",displayImage,DashboardAddProduct.this);
                   ecommerceDashboardModel.createProduct();
                   ecommerceDashboardModel.setCreateProductListener(new EcommerceDashboardModel.CreateProductListener() {
                       @Override
                       public void onSuccess() {
                           setResult(1);
                           finish();
                       }

                       @Override
                       public void onError(String message) {
                           Toast.makeText(DashboardAddProduct.this, message, Toast.LENGTH_SHORT).show();
                       }
                   });
                }
            }
        });

    }

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
    }

    private boolean isValidInput(){
        boolean isValid = true;
        if(TextUtils.isEmpty(mTitle)){
            isValid = false;
            title.setError("Required");
            return isValid;
        }
        if(TextUtils.isEmpty(mPrice)){
            isValid = false;
            price.setError("Required");
            return isValid;
        }
        if(TextUtils.isEmpty(mCategory)){
            isValid = false;
            category.setError("Required");
            return isValid;
        }
        if(TextUtils.isEmpty(mDescription)){
            isValid = false;
            description.setError("Required");
            return isValid;
        }
        if(displayImage.equalsIgnoreCase("")){
            isValid = false;
            Toast.makeText(this, "Upload at least one image", Toast.LENGTH_SHORT).show();
            return isValid;
        }
        return  isValid;
    }

    private void populateCategory(){
        categoryList.add("Groceries");
        categoryList.add("Electronics");
        categoryList.add("Computing");
        categoryList.add("Babies");
        categoryList.add("Fashion");
        categoryList.add("Health And Beauty");
        categoryList.add("Home And Office");
        categoryList.add("Phone And  Supplies");
    }


    // function to generate a random string of length n
    static String generateProductId()
    {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(50);

        for (int i = 0; i < 50; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    public String BitmapToString(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 60, baos);
        byte[] b = baos.toByteArray();
        String imgString = Base64.encodeToString(b, Base64.DEFAULT);
        return imgString;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                String imageString = BitmapToString(bitmap);

                if(productId.equalsIgnoreCase("")){
                    productId = generateProductId();
                }

                EcommerceDashboardModel ecommerceDashboardModel = new EcommerceDashboardModel(imageString,productId,DashboardAddProduct.this);
                ecommerceDashboardModel.uploadImage();
                ecommerceDashboardModel.setImageUploadListener(new EcommerceDashboardModel.ImageUploadListener() {
                    @Override
                    public void onUploadSuccessful(ProductImageModel productImageModel) {
                        if(displayImage.equalsIgnoreCase("")){
                            displayImage = productImageModel.getImageUrl();
                        }
                        adapter.addItem(productImageModel);
                        adapter.notifyDataSetChanged();
                        scrollImageLayout.setVisibility(View.VISIBLE);
                        imagesRecyclerview.scrollToPosition(adapter.getItemCount()-1);
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(DashboardAddProduct.this, message, Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (IOException e) {
                Toast.makeText(DashboardAddProduct.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
    }
}
