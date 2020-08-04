package objects;

public class UserLogin {
    private String name;
    private String familyName;
    private String phone;
    private int price;
    private String logInTime;
    private String logOutTime;

    public UserLogin (String name, String familyName, String phone, int price, String logInTime, String logOutTime) {
        this.name = name;
        this.familyName = familyName;
        this.phone = phone;
        this.price = price;
        this.logInTime = logInTime;
        this.logOutTime = logOutTime;
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

    public int getPrice() {
        return price;
    }

    public String getLogInTime() {
        return logInTime;
    }

    public String getLogOutTime() {
        return logOutTime;
    }
}
