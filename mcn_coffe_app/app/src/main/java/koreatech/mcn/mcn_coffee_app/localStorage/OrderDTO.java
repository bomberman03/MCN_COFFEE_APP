package koreatech.mcn.mcn_coffee_app.localStorage;

import android.content.Context;

import org.joda.time.DateTime;

import java.sql.Timestamp;

/**
 * Created by blood_000 on 2016-08-31.
 */
public class OrderDTO {

    public String createdAt;
    public String updatedAt;

    public String id;
    public int status;

    public static String isoTimeToTimeStamp(Context context, String isoTime)
    {
        DateTime dateTime = new DateTime(isoTime);
        Timestamp timestamp = new Timestamp(dateTime.getMillis());
        return timestamp.toString();
    }

    public OrderDTO(String createdAt, String updatedAt, String id, int status)
    {

        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.id = id;
        this.status = status;
    }

}
