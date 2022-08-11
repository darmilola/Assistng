package ng.assist.UIs.ViewModel;

import android.os.Parcel;
import android.os.Parcelable;

public class Orders implements Parcelable {
    private String totalPrice;
    private String status;
    private String orderJson;
    private String orderId;
    private String userFirstname;
    private String userLastname;
    private String userEmail;
    private String price;
    private String date;
    private String userPhone;
    private String userAddress;
    private String landmark;
    private String state;
    private String lga;
    private String pickupName,pickupPhone,type,deliveryDate,trackingId,storeAddress,pickupDate,stage;



    public Orders(String orderId, String totalPrice,String status,String orderJson,String userFirstname, String userLastname,String userEmail, String userPhone, String userAddress, String landmark, String state, String lga,String pickupName,String pickupPhone, String pickupDate, String type,String deliveryDate,String trackingId,String storeAddress, String stage, String date){
        this.totalPrice = totalPrice;
        this.status = status;
        this.orderJson = orderJson;
        this.orderId = orderId;
        this.userFirstname = userFirstname;
        this.userLastname = userLastname;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userAddress = userAddress;
        this.landmark = landmark;
        this.state = state;
        this.lga = lga;
        this.pickupName = pickupName;
        this.pickupPhone = pickupPhone;
        this.pickupDate = pickupDate;
        this.type = type;
        this.deliveryDate = deliveryDate;
        this.trackingId = trackingId;
        this.storeAddress = storeAddress;
        this.stage = stage;
        this.date = date;
    }


    protected Orders(Parcel in) {
        totalPrice = in.readString();
        status = in.readString();
        orderJson = in.readString();
        orderId = in.readString();
        userFirstname = in.readString();
        userLastname = in.readString();
        userEmail = in.readString();
        price = in.readString();
        userPhone = in.readString();
        userAddress = in.readString();
        landmark = in.readString();
        state = in.readString();
        lga = in.readString();
        pickupName = in.readString();
        pickupPhone = in.readString();
        type = in.readString();
        deliveryDate = in.readString();
        trackingId = in.readString();
        storeAddress = in.readString();
        pickupDate = in.readString();
        stage = in.readString();
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

    public String getOrderId() {
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

    public String getLandmark() {
        return landmark;
    }

    public String getState() {
        return state;
    }

    public String getLga() {
        return lga;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public String getPickupName() {
        return pickupName;
    }

    public String getPickupPhone() {
        return pickupPhone;
    }

    public String getStage() {
        return stage;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public String getType() {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(totalPrice);
        dest.writeString(status);
        dest.writeString(orderJson);
        dest.writeString(orderId);
        dest.writeString(userFirstname);
        dest.writeString(userLastname);
        dest.writeString(userEmail);
        dest.writeString(price);
        dest.writeString(userPhone);
        dest.writeString(userAddress);
        dest.writeString(landmark);
        dest.writeString(state);
        dest.writeString(lga);
        dest.writeString(pickupName);
        dest.writeString(pickupPhone);
        dest.writeString(type);
        dest.writeString(deliveryDate);
        dest.writeString(trackingId);
        dest.writeString(storeAddress);
        dest.writeString(pickupDate);
        dest.writeString(stage);
    }

    public String getDate() {
        return date;
    }
}