package koreatech.mcn.mcn_coffee_app.localStorage;

/**
 * Created by blood_000 on 2016-08-31.
 */
public class OrderDTO {

    public String id;
    public int status;

    public OrderDTO(String id, int status)
    {
        this.id = id;
        this.status = status;
    }

}
