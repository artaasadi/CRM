package objects;

import java.util.ArrayList;

public class User {
    private String name;
    private String familyName;
    private String reference;
    private String phoneNum;
    private String birthDay;
    private String imgUrl;
    private ArrayList<Orders> orders = new ArrayList<>();
    private int totalPrice;

    public User(String name, String familyName, String reference, String phoneNum, String birthDay, String imgUrl) {
        this.name = name;
        this.familyName = familyName;
        this.reference = reference;
        this.phoneNum = phoneNum;
        this.birthDay = birthDay;
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getReference() {
        return reference;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void addOrder(Orders order) {
        orders.add(order);
    }

    public ArrayList<Orders> getOrders() {
        return orders;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
