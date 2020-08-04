package objects;

public class Orders {
    private Foods food;
    private String foodName;
    private int foodPrice;
    private String time;
    private String date;

    public Orders(Foods food, String time, String date){
        this.food = food;
        foodName = food.getName();
        foodPrice = food.getPrice();
        this.time = time;
        this.date = date;
    }

    public void setFood(Foods food) {
        this.food = food;
        foodName = food.getName();
        foodPrice = food.getPrice();
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Foods getFood() {
        return food;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getFoodName() {
        return foodName;
    }

    public int getFoodPrice() {
        return foodPrice;
    }
}
