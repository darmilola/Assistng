package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator2;
import ng.assist.Adapters.AddProductImageAdapter;
import ng.assist.Adapters.EditProductImageAdapter;
import ng.assist.UIs.Utils.ListDialog;
import ng.assist.UIs.ViewModel.EcommerceDashboardModel;
import ng.assist.UIs.ViewModel.GroceryModel;
import ng.assist.UIs.ViewModel.ProductImageModel;

public class EditProduct extends AppCompatActivity {

    private static int PICK_IMAGE = 1;
    RecyclerView imagesRecyclerview;
    EditText title,price,description;
    String mTitle,mPrice,mDescription,mCategory,mAvailability;
    TextView category;
    LinearLayout cancel,save;
    EditProductImageAdapter adapter;
    ArrayList<ProductImageModel> imagesList = new ArrayList<>();
    ArrayList<String> categoryList = new ArrayList<>();
    CircleIndicator2 imagesIndicator;
    ListDialog listDialog;
    String productId = "";
    MaterialButton selectImage;
    String shopName, retailerId;
    LinearLayout scrollImageLayout;
    GroceryModel groceryModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
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
        save = findViewById(R.id.add_product_save);
        scrollImageLayout = findViewById(R.id.scroll_image_layout);
        imagesRecyclerview = findViewById(R.id.product_image_recyclerview);
        imagesIndicator = findViewById(R.id.product_image_indicator);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        groceryModel = getIntent().getParcelableExtra("products");
        imagesList = getIntent().getParcelableArrayListExtra("images");
        adapter = new EditProductImageAdapter(imagesList,this);
        title.setText(groceryModel.getProductName());
        price.setText(groceryModel.getPrice());
        category.setText(groceryModel.getCategory());
        scrollImageLayout.setVisibility(View.VISIBLE);
        description.setText(groceryModel.getDescription());
        LinearLayoutManager imagesManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        imagesRecyclerview.setLayoutManager(imagesManager);
        imagesRecyclerview.setAdapter(adapter);
        pagerSnapHelper.attachToRecyclerView(imagesRecyclerview);
        imagesIndicator.attachToRecyclerView(imagesRecyclerview, pagerSnapHelper);
        adapter.registerAdapterDataObserver(imagesIndicator.getAdapterDataObserver());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(EditProduct.this);
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
                listDialog = new ListDialog(categoryList,EditProduct.this);
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
                productId = groceryModel.getItemId();

                if(isValidInput()){
                    EcommerceDashboardModel ecommerceDashboardModel = new EcommerceDashboardModel(productId,retailerId,mTitle,mPrice,mCategory,mDescription,shopName,"true",EditProduct.this);
                    ecommerceDashboardModel.updateProduct();
                    ecommerceDashboardModel.setCreateProductListener(new EcommerceDashboardModel.CreateProductListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(EditProduct.this, "Update Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(EditProduct.this, message, Toast.LENGTH_SHORT).show();
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
        return  isValid;
    }

    private void populateCategory(){
        categoryList.add("Fast-Foods");
        categoryList.add("Electronics");
        categoryList.add("Others");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                String imageString = BitmapToString(bitmap);

                EcommerceDashboardModel ecommerceDashboardModel = new EcommerceDashboardModel(imageString,groceryModel.getItemId(),EditProduct.this);
                ecommerceDashboardModel.uploadImage();
                ecommerceDashboardModel.setImageUploadListener(new EcommerceDashboardModel.ImageUploadListener() {
                    @Override
                    public void onUploadSuccessful(ProductImageModel productImageModel) {

                        adapter.addItem(productImageModel);
                        adapter.notifyDataSetChanged();
                        scrollImageLayout.setVisibility(View.VISIBLE);
                        imagesRecyclerview.scrollToPosition(adapter.getItemCount()-1);
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(EditProduct.this, message, Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (IOException e) {
                Toast.makeText(EditProduct.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
    }

    public String BitmapToString(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 60, baos);
        byte[] b = baos.toByteArray();
        String imgString = Base64.encodeToString(b, Base64.DEFAULT);
        return imgString;
    }

}