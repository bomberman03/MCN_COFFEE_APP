package koreatech.mcn.mcn_coffee_app.models;

import java.util.List;

/**
 * Created by blood_000 on 2016-05-23.
 */
public class Order {

    public String id;
    public MenuModel menu;
    public List<Option> options;
    public int cost;
    public int count;

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", menu=" + menu +
                ", options=" + options +
                ", cost=" + cost +
                ", count=" + count +
                '}';
    }

    public Order(String id, MenuModel menu, List<Option> options, int cost, int count){
        this.id = id;
        this.menu = menu;
        this.options = options;
        this.cost = cost;
        this.count = count;
    }
}
