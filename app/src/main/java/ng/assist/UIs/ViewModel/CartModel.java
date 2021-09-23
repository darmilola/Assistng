package ng.assist.UIs.ViewModel;
public class CartModel{
    private String name;
    private String price;
    private String imageUrl;
    private String quantity;

    public CartModel(String name, String price, String imageUrl, String quantity){
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getName() {
        return name;
    }
}