package objects;

public class Costs {

    private String name;
    private String vahed;
    private float number;
    private int totalPrice;
    private String date;
    private String time;

    public Costs(String name, float number, String vahed, int totalPrice, String date, String time) {
        this.name = name;
        this.vahed = vahed;
        this.totalPrice = totalPrice;
        this.number = number;
        this.date = date;
        this.time = time;
    }

    public void setNumber(int n) {
        this.number = n;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVahed(String vahed) {
        this.vahed = vahed;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public float getNumber() {
        return number;
    }

    public String getVahed() {
        return vahed;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

}
