package koreatech.mcn.mcn_coffee_app.localStorage;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

import koreatech.mcn.mcn_coffee_app.models.OrderList;

/**
 * Created by blood_000 on 2016-08-31.
 */
public class OrderListManager {

    private static OrderListManager singleton = new OrderListManager();

    private ArrayList<OrderDTO> waitList;
    private ArrayList<OrderDTO> completeList;
    private ArrayList<OrderDTO> receiveRequestList;

    private OrderDBHelper orderDBHelper;

    private String TAG = "OrderListManager";

    public static OrderListManager getInstance()
    {
        return singleton;
    }

    public void readFromDB(Context context)
    {
        orderDBHelper = new OrderDBHelper(context);
        waitList = orderDBHelper.getOrder(OrderDBHelper.ORDERS_STATUS_WAIT, OrderDBHelper.ORDERS_STATUS_WAIT);
        completeList = orderDBHelper.getOrder(OrderDBHelper.ORDERS_STATUS_COMPLETE, OrderDBHelper.ORDERS_STATUS_COMPLETE);
        receiveRequestList = orderDBHelper.getOrder(OrderDBHelper.ORDERS_STATUS_RECEIVE_REQUEST, OrderDBHelper.ORDERS_STATUS_RECEIVE_REQUEST);
    }

    public void insertNewOrder(OrderDTO order)
    {
        orderDBHelper.insertOrder(order);
        waitList.add(order);
    }

    public OrderDTO updateOrder(OrderDTO order)
    {
        Log.d(TAG, order.id + order.status);
        // when new order status is wait
        if(order.status == OrderDBHelper.ORDERS_STATUS_WAIT)
        {
            Iterator<OrderDTO> iterator = waitList.iterator();
            while(iterator.hasNext())
            {
                OrderDTO orderDTO = iterator.next();
                if(order.id.equals(orderDTO.id)) {
                    return null;
                }
            }
            // insert new wait order
            orderDBHelper.insertOrder(order);
            waitList.add(order);
            return order;
        }

        // when new order status is complete
        // before status can be request(-1), wait(0)
        if(order.status == OrderDBHelper.ORDERS_STATUS_COMPLETE)
        {
            Iterator<OrderDTO> iterator = waitList.iterator();
            while(iterator.hasNext())
            {
                OrderDTO orderDTO = iterator.next();
                if(order.id.equals(orderDTO.id)) {
                    completeList.add(orderDTO);
                    orderDBHelper.updateOrder(orderDTO);
                    iterator.remove();
                    return order;
                }
            }
            // insert new complete order
        }

        // new order status is receive
        // before status can be request(-1), wait(0), complete(1), receive request(3)
        if(order.status == OrderDBHelper.ORDERS_STATUS_RECEIVE)
        {
            Iterator<OrderDTO> iterator = receiveRequestList.iterator();
            while(iterator.hasNext())
            {
                OrderDTO orderDTO = iterator.next();
                if(order.id.equals(orderDTO.id)) {
                    orderDBHelper.updateOrder(orderDTO);
                    iterator.remove();
                    return order;
                }
            }
        }

        // new order status is cancel
        // before status can be request(-1), wait(0), complete(1)
        if(order.status == OrderDBHelper.ORDERS_STATUS_CANCEL)
        {
            Iterator<OrderDTO> iterator = waitList.iterator();
            while(iterator.hasNext())
            {
                OrderDTO orderDTO = iterator.next();
                if(order.id.equals(orderDTO.id)) {
                    orderDBHelper.updateOrder(orderDTO);
                    iterator.remove();
                    return order;
                }
            }
            iterator = completeList.iterator();
            while(iterator.hasNext())
            {
                OrderDTO orderDTO = iterator.next();
                if(order.id.equals(orderDTO.id)) {
                    orderDBHelper.updateOrder(orderDTO);
                    iterator.remove();
                    return order;
                }
            }
            iterator = receiveRequestList.iterator();
            while(iterator.hasNext())
            {
                OrderDTO orderDTO = iterator.next();
                if(order.id.equals(orderDTO.id)) {
                    orderDBHelper.updateOrder(orderDTO);
                    iterator.remove();
                    return order;
                }
            }
        }

        return null;
    }

    public ArrayList<OrderDTO> getRequestOrderList()
    {
        ArrayList<OrderDTO> requestList = new ArrayList<>();

        requestList.addAll(this.waitList);
        requestList.addAll(this.completeList);
        requestList.addAll(this.receiveRequestList);

        return requestList;
    }

}