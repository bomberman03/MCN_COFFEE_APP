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

    public Order(String id, MenuModel menu, List<Option> options, int cost){
        this.id = id;
        this.menu = menu;
        this.options = options;
        this.cost = cost;
    }
}
