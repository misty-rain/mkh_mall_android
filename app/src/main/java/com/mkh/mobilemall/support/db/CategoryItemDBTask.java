package com.mkh.mobilemall.support.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.bean.CategoryRetailItem;
import com.mkh.mobilemall.support.db.table.CategoryItemTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/1/8.
 */
public class CategoryItemDBTask {

    private static CategoryItemDBTask singleton = null;

    private SQLiteDatabase wsd = null;

    private SQLiteDatabase rsd = null;

    private CategoryItemDBTask() {

    }

    public static CategoryItemDBTask getInstance() {

        if (singleton == null) {
            // DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
            // SQLiteDatabase wsd = databaseHelper.getWritableDatabase();
            // SQLiteDatabase rsd = databaseHelper.getReadableDatabase();

            singleton = new CategoryItemDBTask();
            singleton.wsd = new DBManager(GlobalContext.getInstance()).getWritableDatabase();
            singleton.rsd = new DBManager(GlobalContext.getInstance()).getReadableDatabase();
        }

        return singleton;
    }

    /**
     * 创建or 修改 类型关联表
     *
     * @param
     * @return
     */

    public int addOrUpdateCategory(CategoryRetailItem categoryRetailItem) {

        ContentValues cv = new ContentValues();
        cv.put(CategoryItemTable.ID, categoryRetailItem.getId());
        cv.put(CategoryItemTable.ITEM_ID, categoryRetailItem.getItemId());
        cv.put(CategoryItemTable.CATEGORY_ID, categoryRetailItem.getCategoryRetailId());

        cv.put(CategoryItemTable.ROW_VERSION, "0");
        cv.put(CategoryItemTable.IS_DELETED, false);
        //  cv.put(CategoryItemTable.CREATED_BY, "");
        //  cv.put(CategoryItemTable.CREATION_TIME, categoryRetailItem.getCreation_time());
        // cv.put(CategoryItemTable.LAST_UPDATED_BY, categoryRetailItem.getLast_updated_by());
        //  cv.put(CategoryItemTable.LAST_UPDATE_TIME, TimeUtil.getCurrentTime());

        Cursor c = rsd.query(CategoryItemTable.TABLE_NAME, null, CategoryItemTable.ID
                        + "=?", new String[]{categoryRetailItem.getId().toString()}, null,
                null, null);

        if (c != null && c.getCount() > 0) {
            String[] args = {categoryRetailItem.getId().toString()};
            rsd.update(CategoryItemTable.TABLE_NAME, cv, CategoryItemTable.ID + "=?",
                    args);
            c.close();
            return 1;
        } else {

            wsd.insert(CategoryItemTable.TABLE_NAME, CategoryItemTable.ID, cv);
            c.close();
            return 0;
        }

    }

    /**
     * 移除所有商品
     */
    public void removeCategoryItem() {
        wsd.execSQL("DELETE FROM " + CategoryItemTable.TABLE_NAME);
    }


    /**
     * 查询所有物料类别关联
     *
     * @return
     */
    public List<CategoryRetailItem> getCategoryRetailItemList() {

        String sql = "select * from " + CategoryItemTable.TABLE_NAME;
        Cursor c = rsd.rawQuery(sql, null);
        List<CategoryRetailItem> list = new ArrayList<CategoryRetailItem>();
        while (c.moveToNext()) {
            CategoryRetailItem item = new CategoryRetailItem();
            int colid = c.getColumnIndex(CategoryItemTable.ID);
            item.setId(c.getLong(colid));

            colid = c.getColumnIndex(CategoryItemTable.ITEM_ID);
            item.setItemId(c.getLong(colid));

            colid = c.getColumnIndex(CategoryItemTable.CATEGORY_ID);
            item.setCategoryRetailId(c.getLong(colid));


            list.add(item);
        }
        return list;

    }
}
