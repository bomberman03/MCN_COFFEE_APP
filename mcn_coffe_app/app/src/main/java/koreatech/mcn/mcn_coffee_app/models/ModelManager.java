package koreatech.mcn.mcn_coffee_app.models;

import java.util.HashMap;

/**
 * Created by blood_000 on 2016-05-23.
 */
public class ModelManager {

    private static ModelManager singleton = new ModelManager();

    private HashMap<Integer, Cafe> cafe = new HashMap<>();
    private HashMap<Integer, MenuModel> menuModel = new HashMap<>();
    private HashMap<Integer, Order> order = new HashMap<>();
    private HashMap<Integer, User> user = new HashMap<>();

    public static ModelManager getInstance(){
        return singleton;
    }


}
