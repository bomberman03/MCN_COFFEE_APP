package koreatech.mcn.mcn_coffee_app.localStorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
/**
 * Created by blood_000 on 2016-08-30.
 */
public class OrderDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "mcnCoffee.db";
    public static final String ORDER_TABLE_NAME = "orders";
    public static final String ORDERS_COLUMN_CREATED_AT = "createdAt";
    public static final String ORDERS_COLUMN_UPDATED_AT = "updatedAt";
    public static final String ORDERS_COLUMN_ORDER = "id";
    public static final String ORDERS_COLUMN_STATUS = "status";

    public static final int ORDERS_STATUS_REQUEST = -1;
    public static final int ORDERS_STATUS_WAIT = 0;
    public static final int ORDERS_STATUS_COMPLETE = 1;
    public static final int ORDERS_STATUS_CANCEL = 2;
    public static final int ORDERS_STATUS_RECEIVE_REQUEST = 3;
    public static final int ORDERS_STATUS_RECEIVE = 4;

    private String TAG = "OrderDBHelper";

    public OrderDBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
        JodaTimeAndroid.init(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE ORDERS (createdAt timestamp not null, updatedAt timestamp not null, id text not null, status integer not null);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS orders");
        onCreate(db);
    }

    public void clearDB()
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS orders");
        onCreate(db);
    }

    public boolean insertOrder(OrderDTO order)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("createdAt", order.createdAt);
        contentValues.put("updatedAt", order.updatedAt);
        contentValues.put("id", order.id);
        contentValues.put("status", order.status);
        db.insert("orders", null, contentValues);
        return true;
    }

    public boolean updateOrder(OrderDTO order)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("createdAt", order.createdAt);
        contentValues.put("updatedAt", order.updatedAt);
        contentValues.put("status", order.status);
        db.update("orders", contentValues, "id = ? ", new String[] { order.id } );
        return true;
    }

    public ArrayList<OrderDTO> getOrder(Integer minStatus, Integer maxStatus)
    {
        ArrayList<OrderDTO> array_list = new ArrayList<OrderDTO>();

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from orders " +
                "where status >= " + minStatus + " and status <= " + maxStatus;
        Cursor res =  db.rawQuery( sql, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            String createdAt = res.getString(res.getColumnIndex(ORDERS_COLUMN_CREATED_AT));
            String updatedAt = res.getString(res.getColumnIndex(ORDERS_COLUMN_UPDATED_AT));
            String id  = res.getString(res.getColumnIndex(ORDERS_COLUMN_ORDER));
            int status  = res.getInt(res.getColumnIndex(ORDERS_COLUMN_STATUS));
            array_list.add(new OrderDTO(createdAt, updatedAt, id, status));
            res.moveToNext();
        }

        return array_list;
    }

    public String getRecentTimeStamp() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select MAX(updatedAt) as updatedAt from orders";
        Cursor res =  db.rawQuery( sql, null);
        res.moveToFirst();
        String ret = "";
        if(res.isAfterLast() == false) {
            ret = res.getString(res.getColumnIndex(ORDERS_COLUMN_UPDATED_AT));
        }
        return ret;
    }
}