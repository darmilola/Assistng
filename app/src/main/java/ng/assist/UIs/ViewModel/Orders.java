package ng.assist.UIs.ViewModel;

import android.os.Parcel;
import android.os.Parcelable;

public class Orders implements Parcelable {
    private String totalPrice;
    private String status;
    private String orderJson;
    private int orderId;
    private String userFirstname;
    private String userLastname;
    private String userEmail;
    private String price;
    private String userPhone;
    private String userAddress;

    public Orders(int orderId, String totalPrice,String status,String orderJson,String userFirstname, String userLastname,String userEmail, String userPhone, String userAddress){
        this.totalPrice = totalPrice;
        this.status = status;
        this.orderJson = orderJson;
        this.orderId = orderId;
        this.userFirstname = userFirstname;
        this.userLastname = userLastname;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userAddress = userAddress;
    }


    protected Orders(Parcel in) {
        totalPrice = in.readString();
        status = in.readString();
        orderJson = in.readString();
        orderId = in.readInt();
        userFirstname = in.readString();
        userLastname = in.readString();
        userEmail = in.readString();
        price = in.readString();
        userPhone = in.readString();
        userAddress = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(totalPrice);
        dest.writeString(status);
        dest.writeString(orderJson);
        dest.writeInt(orderId);
        dest.writeString(userFirstname);
        dest.writeString(userLastname);
        dest.writeString(userEmail);
        dest.writeString(price);
        dest.writeString(userPhone);
        dest.writeString(userAddress);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Orders> CREATOR = new Creator<Orders>() {
        @Override
        public Orders createFromParcel(Parcel in) {
            return new Orders(in);
        }

        @Override
        public Orders[] newArray(int size) {
            return new Orders[size];
        }
    };

    public String getUserAddress() {
        return userAddress;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public String getUserFirstname() {
        return userFirstname;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getOrderJson() {
        return orderJson;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public String getUserLastname() {
        return userLastname;
    }

    public String getUserEmail() {
        return userEmail;
    }


}