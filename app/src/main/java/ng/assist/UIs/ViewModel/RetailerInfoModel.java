package ng.assist.UIs.ViewModel;

public class RetailerInfoModel {
    private String userId;
    private String phonenumber;
    private String address;
    private String shopName;

    public RetailerInfoModel(String userId, String phonenumber, String address, String shopName){
          this.userId = userId;
          this.phonenumber = phonenumber;
          this.address = address;
          this.shopName = shopName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getShopName() {
        return shopName;
    }

    public String getUserId() {
        return userId;
    }
}
