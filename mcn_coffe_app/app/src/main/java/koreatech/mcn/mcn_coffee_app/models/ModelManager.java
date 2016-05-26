package koreatech.mcn.mcn_coffee_app.models;

import java.util.HashMap;

/**
 * Created by blood_000 on 2016-05-23.
 */
public class ModelManager {

    private static ModelManager singleton = new ModelManager();

    private HashMap<Integer, Cafe> content = new HashMap<>();
    private HashMap<Integer, MenuModel> contentImage = new HashMap<>();
    private HashMap<Integer, Option> item = new HashMap<>();
    private HashMap<Integer, Order> step = new HashMap<>();
    private HashMap<Integer, User> stepFile = new HashMap<>();

    public static ModelManager getInstance(){
        return singleton;
    }


}
