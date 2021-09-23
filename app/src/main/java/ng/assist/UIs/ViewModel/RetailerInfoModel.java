package ng.assist.UIs.ViewModel;

public class RetailerInfoModel {
    private String userId;
    private String phonenumber;
    private String shopName;

    public RetailerInfoModel(String userId, String phonenumber, String shopName){
          this.userId = userId;
          this.phonenumber = phonenumber;
          this.shopName = shopName;
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
