package objects;

public class UserOrder {
    private String name;
    private String familyName;
    private String phone;
    private String productName;
    private int productPrice;
    private String orderTime;

    public UserOrder(String name, String familyName, String phone, String productName, int productPrice, String orderTime){
        this.name = name;
        this.familyName = familyName;
        this.phone = phone;
        this.productName = productName;
        this.productPrice = productPrice;
        this.orderTime = orderTime;
    }

    public String getName() {
        return name;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getPhone() {
        return phone;
    }

    public String getProductName() {
        return productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public String getOrderTime() {
        return orderTime;
    }
}
