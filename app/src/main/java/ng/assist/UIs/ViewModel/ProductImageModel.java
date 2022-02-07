package ng.assist.UIs.ViewModel;


import android.os.Parcel;
import android.os.Parcelable;

public class ProductImageModel implements Parcelable {
    private int id;
    private String imageUrl;
    private String productId;

    ProductImageModel(int id, String imageUrl, String productId){
        this.id = id;
        this.imageUrl = imageUrl;
        this.productId = productId;
    }

    protected ProductImageModel(Parcel in) {
        id = in.readInt();
        imageUrl = in.readString();
        productId = in.readString();
    }

    public static final Creator<ProductImageModel> CREATOR = new Creator<ProductImageModel>() {
        @Override
        public ProductImageModel createFromParcel(Parcel in) {
            return new ProductImageModel(in);
        }

        @Override
        public ProductImageModel[] newArray(int size) {
            return new ProductImageModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getProductId() {
        return productId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(imageUrl);
        dest.writeString(productId);
    }
}