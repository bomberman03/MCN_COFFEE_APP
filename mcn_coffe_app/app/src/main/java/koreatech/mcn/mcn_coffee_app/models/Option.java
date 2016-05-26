package koreatech.mcn.mcn_coffee_app.models;

import android.graphics.Path;

import java.util.List;

/**
 * Created by blood_000 on 2016-05-23.
 */
public class Option {

    public String id;
    public String name;
    public int cost;
    public List<Option> options;

    public Option(String id, String name, int cost, List<Option> options){
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.options = options;
    }
}
