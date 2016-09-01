package koreatech.mcn.mcn_coffee_app.localStorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by blood_000 on 2016-08-30.
 */
public class OrderDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "mcnCoffee.db";
    public static final String ORDER_TABLE_NAME = "orders";
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
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE ORDERS (id text not null, status integer not null);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS orders");
        onCreate(db);
    }

    public boolean insertOrder(OrderDTO order)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", order.id);
        contentValues.put("status", order.status);
        db.insert("orders", null, contentValues);
        return true;
    }

    public boolean updateOrder(OrderDTO order)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
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
        Log.d(TAG, sql);
        Cursor res =  db.rawQuery( sql, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            String id  = res.getString(res.getColumnIndex(ORDERS_COLUMN_ORDER));
            int status  = res.getInt(res.getColumnIndex(ORDERS_COLUMN_STATUS));
            array_list.add(new OrderDTO(id, status));
            res.moveToNext();
        }

        return array_list;
    }
}