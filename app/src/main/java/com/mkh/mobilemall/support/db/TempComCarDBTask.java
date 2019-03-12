package com.mkh.mobilemall.support.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.bean.TempComCarBean;
import com.mkh.mobilemall.support.db.table.TempComCarTable;
import com.mkh.mobilemall.utils.DateUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author misty-rain
 * @ClassName: TempComCarDBTask
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014-12-11 下午1:45:59
 */
public class TempComCarDBTask {
    private static TempComCarDBTask singleton = null;

    private SQLiteDatabase wsd = null;

    private SQLiteDatabase rsd = null;

    private TempComCarDBTask() {

    }

    public static TempComCarDBTask getInstance() {

        if (singleton == null) {
            // DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
            // SQLiteDatabase wsd = databaseHelper.getWritableDatabase();
            // SQLiteDatabase rsd = databaseHelper.getReadableDatabase();

            singleton = new TempComCarDBTask();
            singleton.wsd = new DBManager(GlobalContext.getInstance()).getWritableDatabase();
            ;
            singleton.rsd = new DBManager(GlobalContext.getInstance()).getReadableDatabase();
            ;
        }

        return singleton;
    }


    /**
     * 创建or 修改 子商品表
     *
     * @param category
     * @return
     */


    public int addOrUpdateItemToTempCar(TempComCarBean item) {

        ContentValues cv = new ContentValues();
        cv.put(TempComCarTable.ID, item.getId());
        cv.put(TempComCarTable.CART_ITEM_ID,item.getCartItemId());
        cv.put(TempComCarTable.COMMODNAME, item.getCommodName());
        cv.put(TempComCarTable.SINGLECOMMODTOTALCOUNT,
                item.getSingleCommodTotalCount());
        cv.put(TempComCarTable.SINGLECOMMODPRICE, item.getSingleCommodPrice());
        cv.put(TempComCarTable.COMMODTOTALCOUNT, item.getCommodTotalCount());
        cv.put(TempComCarTable.COMMODTOTALPRICE, item.getCommodTotalPrice());
        cv.put(TempComCarTable.SINGLECOMPICURL, item.getSingleComPicUrl());
        cv.put(TempComCarTable.ITEM_NUMBER, item.getNumber());
        cv.put(TempComCarTable.SHORT_NAME, item.getShortName());
        cv.put(TempComCarTable.OPERATION_TIME, item.getOperationTime());


        Cursor c = rsd.query(TempComCarTable.TABLE_NAME, null,
                TempComCarTable.CART_ITEM_ID + "=?", new String[]{String.valueOf(item.getCartItemId())}, null,
                null, null);

        if (c != null && c.getCount() > 0) {
            String[] args = {String.valueOf(item.getCartItemId())};
            rsd.update(TempComCarTable.TABLE_NAME, cv, TempComCarTable.CART_ITEM_ID
                    + "=?", args);
            return 1;
        } else {

            wsd.insert(TempComCarTable.TABLE_NAME, TempComCarTable.CART_ITEM_ID, cv);
            return 0;
        }

    }


    /**
     * 修改单个商品的数量and 价格
     *
     * @param
     * @return
     */


    public int updateItemCarsCountAndPrice(final String id, final int count,
                                           final BigDecimal price) {

        ContentValues cv = new ContentValues();
        cv.put(TempComCarTable.ID, id);
        cv.put(TempComCarTable.SINGLECOMMODTOTALCOUNT, count);
        cv.put(TempComCarTable.COMMODTOTALCOUNT, count);
        cv.put(TempComCarTable.COMMODTOTALPRICE, price.toString());
        cv.put(TempComCarTable.OPERATION_TIME, DateUtil.getCurrentTime());

        Cursor c = rsd.query(TempComCarTable.TABLE_NAME, null,
                TempComCarTable.CART_ITEM_ID + "=?", new String[]{id}, null, null,
                null);

        if (c != null && c.getCount() > 0) {
            String[] args = {id};
            rsd.update(TempComCarTable.TABLE_NAME, cv, TempComCarTable.CART_ITEM_ID
                    + "=?", args);

        }
        return 0;
    }


    /**
     * 移除购物车的商品
     *
     * @param comID
     * @return
     */

    public int removeTempCarCom(final String comID) {
        int result = 0;
        if (comID != null) {
            String[] params = comID.split(",");

            result = wsd.delete(TempComCarTable.TABLE_NAME, TempComCarTable.ID
                    + "=?", params);
        } else {
            wsd.execSQL("DELETE FROM " + TempComCarTable.TABLE_NAME);
        }
        return result;

    }


    /**
     * 根据CartitemId移除购物车的商品
     *
     * @param
     * @return
     */

    public int removeTempCarComByCartItemId(final String cartItemId) {
        int result = 0;
        if (cartItemId != null) {
            String[] params = cartItemId.split(",");

            result = wsd.delete(TempComCarTable.TABLE_NAME, TempComCarTable.CART_ITEM_ID
                      + "=?", params);
        } else {
            wsd.execSQL("DELETE FROM " + TempComCarTable.TABLE_NAME);
        }
        return result;

    }

    /**
     * 获得所有购物车中的商品
     *
     * @return
     */

    public List<TempComCarBean> getTempComCarList() {

        String sql = "select * from " + TempComCarTable.TABLE_NAME + " ORDER BY " + TempComCarTable.OPERATION_TIME + " DESC ";
        Cursor c = rsd.rawQuery(sql, null);
        List<TempComCarBean> list = new ArrayList<TempComCarBean>();
        while (c.moveToNext()) {
            TempComCarBean item = new TempComCarBean();
            int colid = c.getColumnIndex(TempComCarTable.ID);
            item.setId(c.getString(colid));

             colid = c.getColumnIndex(TempComCarTable.CART_ITEM_ID);
            item.setCartItemId(c.getLong(colid));


            colid = c.getColumnIndex(TempComCarTable.COMMODNAME);
            item.setCommodName(c.getString(colid));

            colid = c.getColumnIndex(TempComCarTable.SINGLECOMMODTOTALCOUNT);
            item.setSingleCommodTotalCount(c.getInt(colid));

            colid = c.getColumnIndex(TempComCarTable.SINGLECOMMODPRICE);
            item.setSingleCommodPrice(Double.parseDouble(c.getString(colid)));

            colid = c.getColumnIndex(TempComCarTable.COMMODTOTALCOUNT);
            item.setCommodTotalCount(c.getInt(colid));

            colid = c.getColumnIndex(TempComCarTable.COMMODTOTALPRICE);
            item.setCommodTotalPrice(Double.parseDouble(c.getString(colid)));

            colid = c.getColumnIndex(TempComCarTable.SINGLECOMPICURL);
            item.setSingleComPicUrl(c.getString(colid));

            colid = c.getColumnIndex(TempComCarTable.OPERATION_TIME);
            item.setOperationTime(c.getString(colid));

            colid = c.getColumnIndex(TempComCarTable.ITEM_NUMBER);
            item.setNumber(c.getString(colid));


            colid = c.getColumnIndex(TempComCarTable.SHORT_NAME);
            item.setShortName(c.getString(colid));


            item.setFlag("false");

            list.add(item);
        }
        return list;

    }


    /**
     * 通过购物车ID，查询单个商品记录
     *
     * @return
     */

    public TempComCarBean getTempComByCartItemId(final String ID) {

        String sql = "select * from " + TempComCarTable.TABLE_NAME + " where " + TempComCarTable.CART_ITEM_ID + " = " + ID;
        Cursor c = rsd.rawQuery(sql, null);
        TempComCarBean item = null;
        while (c.moveToNext()) {
            item = new TempComCarBean();
            int colid = c.getColumnIndex(TempComCarTable.ID);
            item.setId(c.getString(colid));

            colid = c.getColumnIndex(TempComCarTable.CART_ITEM_ID);
            item.setCartItemId(c.getLong(colid));

            colid = c.getColumnIndex(TempComCarTable.COMMODNAME);
            item.setCommodName(c.getString(colid));

            colid = c.getColumnIndex(TempComCarTable.SINGLECOMMODTOTALCOUNT);
            item.setSingleCommodTotalCount(c.getInt(colid));

            colid = c.getColumnIndex(TempComCarTable.SINGLECOMMODPRICE);
            item.setSingleCommodPrice(Double.parseDouble(c.getString(colid)));

            colid = c.getColumnIndex(TempComCarTable.COMMODTOTALCOUNT);
            item.setCommodTotalCount(c.getInt(colid));

            colid = c.getColumnIndex(TempComCarTable.COMMODTOTALPRICE);
            item.setCommodTotalPrice(Double.parseDouble(c.getString(colid)));

            //	colid = c.getColumnIndex(TempComCarTable.SINGLECOMPICURL);
            //item.setSingleComPicUrl(c.getString(colid));
            colid = c.getColumnIndex(TempComCarTable.ITEM_NUMBER);
            item.setNumber(c.getString(colid));


            colid = c.getColumnIndex(TempComCarTable.SHORT_NAME);
            item.setShortName(c.getString(colid));

            colid = c.getColumnIndex(TempComCarTable.OPERATION_TIME);
            item.setOperationTime(c.getString(colid));
            item.setFlag("false");


        }
        return item;

    }

    /**
     * 根据item id 查询单个item
     * @param ID
     * @return
     */
    public TempComCarBean getTempComByItemId(final String itemId) {

        String sql = "select * from " + TempComCarTable.TABLE_NAME + " where " + TempComCarTable.ID + " = " + itemId;
        Cursor c = rsd.rawQuery(sql, null);
        TempComCarBean item = null;
        while (c.moveToNext()) {
            item = new TempComCarBean();
            int colid = c.getColumnIndex(TempComCarTable.ID);
            item.setId(c.getString(colid));

            colid = c.getColumnIndex(TempComCarTable.CART_ITEM_ID);
            item.setCartItemId(c.getLong(colid));

            colid = c.getColumnIndex(TempComCarTable.COMMODNAME);
            item.setCommodName(c.getString(colid));

            colid = c.getColumnIndex(TempComCarTable.SINGLECOMMODTOTALCOUNT);
            item.setSingleCommodTotalCount(c.getInt(colid));

            colid = c.getColumnIndex(TempComCarTable.SINGLECOMMODPRICE);
            item.setSingleCommodPrice(Double.parseDouble(c.getString(colid)));

            colid = c.getColumnIndex(TempComCarTable.COMMODTOTALCOUNT);
            item.setCommodTotalCount(c.getInt(colid));

            colid = c.getColumnIndex(TempComCarTable.COMMODTOTALPRICE);
            item.setCommodTotalPrice(Double.parseDouble(c.getString(colid)));

            //	colid = c.getColumnIndex(TempComCarTable.SINGLECOMPICURL);
            //item.setSingleComPicUrl(c.getString(colid));
            colid = c.getColumnIndex(TempComCarTable.ITEM_NUMBER);
            item.setNumber(c.getString(colid));


            colid = c.getColumnIndex(TempComCarTable.SHORT_NAME);
            item.setShortName(c.getString(colid));

            colid = c.getColumnIndex(TempComCarTable.OPERATION_TIME);
            item.setOperationTime(c.getString(colid));
            item.setFlag("false");


        }
        return item;

    }


/*
     * 获得订单的价格总额
	 */

    public BigDecimal getAllOrderPrice() {

        String sql = "select sum(" + TempComCarTable.COMMODTOTALPRICE
                + ") as totalprice " + " from " + TempComCarTable.TABLE_NAME + " ORDER BY " + TempComCarTable.OPERATION_TIME + " DESC ";
        Cursor c = rsd.rawQuery(sql, null);
        BigDecimal bd = null;
        while (c.moveToNext()) {
            int colid = c.getColumnIndex("totalprice");
            if (c.getString(colid) != null)
                bd = new BigDecimal(c.getString(colid));
        }
        return bd;

    }

    /**
     * 获得所有购物车中的商品的总数量
     *
     * @return
     */

    public int  getTempComCarTotalCount() {
        int totalCount=0;
        String sql = "select * from " + TempComCarTable.TABLE_NAME + " ORDER BY " + TempComCarTable.OPERATION_TIME + " DESC ";
        Cursor c = rsd.rawQuery(sql, null);
        while (c.moveToNext()) {
            TempComCarBean item = new TempComCarBean();

            int colid = c.getColumnIndex(TempComCarTable.COMMODTOTALCOUNT);
            item.setCommodTotalCount(c.getInt(colid));
            totalCount+=item.getCommodTotalCount();

        }
        return totalCount;

    }


}
