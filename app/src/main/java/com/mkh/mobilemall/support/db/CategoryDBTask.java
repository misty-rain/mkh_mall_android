package com.mkh.mobilemall.support.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.bean.CategoryBean;
import com.mkh.mobilemall.support.db.table.CategoryTable;
import com.xiniunet.api.domain.master.ItemCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author misty-rain
 * @ClassName: CategoryDBTask
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014-12-9 下午7:19:41
 */
public class CategoryDBTask {
    private static CategoryDBTask singleton = null;

    private SQLiteDatabase wsd = null;

    private SQLiteDatabase rsd = null;

    private CategoryDBTask() {

    }

    public static CategoryDBTask getInstance() {

        if (singleton == null) {
            // DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
            // SQLiteDatabase wsd = databaseHelper.getWritableDatabase();
            // SQLiteDatabase rsd = databaseHelper.getReadableDatabase();

            singleton = new CategoryDBTask();
            singleton.wsd = new DBManager(GlobalContext.getInstance()).getWritableDatabase();
            singleton.rsd = new DBManager(GlobalContext.getInstance()).getReadableDatabase();
        }

        return singleton;
    }

    /**
     * 创建or 修改 类型表
     *
     * @param category
     * @return
     */

    /**
     * 移除所有商品类别
     */
    public void removeAllCategory() {
        wsd.execSQL("DELETE FROM " + CategoryTable.TABLE_NAME);
    }

           public int addOrUpdateCategory(ItemCategory category) {


        ContentValues cv = new ContentValues();
        cv.put(CategoryTable.ID, category.getId());
        cv.put(CategoryTable.NAME, category.getName());
        cv.put(CategoryTable.ICON_URL,category.getIconUrl());
        cv.put(CategoryTable.PARENT_ID, category.getParentId());

        Cursor c = rsd.query(CategoryTable.TABLE_NAME, null, CategoryTable.ID
                        + "=?", new String[]{category.getId().toString()}, null,
                null, null);

        if (c != null && c.getCount() > 0) {
            String[] args = {category.getId().toString()};
            rsd.update(CategoryTable.TABLE_NAME, cv, CategoryTable.ID + "=?",
                    args);
            c.close();
            return 1;
        } else {

            wsd.insert(CategoryTable.TABLE_NAME, CategoryTable.ID, cv);
            c.close();
            return 0;
        }




    }

    public void addOrUpdateCategoryForFast(List<ItemCategory> categorieList) {
        String sql = " INSERT INTO " + CategoryTable.TABLE_NAME + " (" + CategoryTable.ID + "," + CategoryTable.NAME + "," + CategoryTable.ICON_URL + "," + CategoryTable.PARENT_ID + ") VALUES(?,?,?,?)";
        rsd.beginTransaction();
        SQLiteStatement st = rsd.compileStatement(sql);
        for (int i = 0; i < categorieList.size(); i++) {
            st.bindLong(1, categorieList.get(i).getId() != null ? categorieList.get(i).getId() : new Long("0.0"));
            st.bindString(2, categorieList.get(i).getName() != null ? categorieList.get(i).getName() : "test");
            st.bindString(3, categorieList.get(i).getIconUrl()!=null?String.valueOf(categorieList.get(i).getIconUrl()):"");
            st.bindLong(4, categorieList.get(i).getParentId() != null ? categorieList.get(i).getParentId() : new Long("0"));
            st.execute();
            st.clearBindings();
        }
        rsd.setTransactionSuccessful();
        rsd.endTransaction();

    }


        /**
         * 获得category 表中 记录的条数
         *
         * @return
         */
    public int getCategoryTableRecyleCount() {
        String sql = "select count(*) from " + CategoryTable.TABLE_NAME;
        Cursor c = rsd.rawQuery(sql, null);
        return c.getCount();

    }

    /**
     * 获得所有分类集合
     *
     * @return
     */
    public List<ItemCategory> getCategoryList() {

        String sql = "select * from " + CategoryTable.TABLE_NAME;
        // Cursor c = rsd.rawQuery(sql, null);
        Cursor c = rsd.rawQuery(sql, null);
        List<ItemCategory> list = new ArrayList<ItemCategory>();
        while (c.moveToNext()) {
            ItemCategory category = new ItemCategory();
            int colid = c.getColumnIndex(CategoryTable.ID);
            category.setId(c.getLong(colid));

            colid = c.getColumnIndex(CategoryTable.NAME);
            category.setName(c.getString(colid));

            colid = c.getColumnIndex(CategoryTable.ICON_URL);
            category.setIconUrl(c.getString(colid));

            colid = c.getColumnIndex(CategoryTable.PARENT_ID);
            category.setParentId(c.getLong(colid));


            list.add(category);
        }
        return list;

    }

    /**
     * 获得父分类集合
     *
     * @return
     */
    public List<ItemCategory> getParentCategoryList() {

        String sql = "select * from " + CategoryTable.TABLE_NAME + " where "
                + CategoryTable.PARENT_ID + " is null or " + CategoryTable.PARENT_ID + " = 0 ";
        // String sql = "select * from " + CategoryTable.TABLE_NAME;

        Cursor c = rsd.rawQuery(sql, null);
        List<ItemCategory> list = new ArrayList<ItemCategory>();
        while (c.moveToNext()) {
            ItemCategory category = new ItemCategory();
            int colid = c.getColumnIndex(CategoryTable.ID);
            category.setId(c.getLong(colid));

            colid = c.getColumnIndex(CategoryTable.NAME);
            category.setName(c.getString(colid));

            colid = c.getColumnIndex(CategoryTable.ICON_URL);
            category.setIconUrl(c.getString(colid));

            colid = c.getColumnIndex(CategoryTable.PARENT_ID);
            category.setParentId(c.getLong(colid));



            list.add(category);
        }
        if(list.size()>0)
        list=assemblePaentList(list);


        return list;

    }


    public List<ItemCategory> assemblePaentList(List<ItemCategory> list){
        ItemCategory itemCategory=list.get(list.size()-1);
        list.add(0,itemCategory);
        list.remove(list.size()-1);
        return list;

    }

    /**
     * 获得子分类list
     *
     * @return
     */
    public List<ItemCategory> getChildCategoryList() {

        String sql = "select * from " + CategoryTable.TABLE_NAME + " where "
                + CategoryTable.PARENT_ID + " in ( select " + CategoryTable.ID
                + " from " + CategoryTable.TABLE_NAME + ")";
        Cursor c = rsd.rawQuery(sql, null);
        List<ItemCategory> list = new ArrayList<ItemCategory>();
        while (c.moveToNext()) {
            ItemCategory category = new ItemCategory();
            int colid = c.getColumnIndex(CategoryTable.ID);
            category.setId(c.getLong(colid));

            colid = c.getColumnIndex(CategoryTable.NAME);
            category.setName(c.getString(colid));

            colid = c.getColumnIndex(CategoryTable.ICON_URL);
            category.setIconUrl(c.getString(colid));

            colid = c.getColumnIndex(CategoryTable.PARENT_ID);
            category.setParentId(c.getLong(colid));



            list.add(category);
        }
        return list;

    }

    /**
     * 获得子分类list
     *
     * @return
     */
    public List<ItemCategory> getCategoryListByParentId(final Long parentId) {

        String sql = "select * from " + CategoryTable.TABLE_NAME + " where "
                + CategoryTable.PARENT_ID + " = " + parentId;
        Cursor c = rsd.rawQuery(sql, null);
        List<ItemCategory> list = new ArrayList<ItemCategory>();
        while (c.moveToNext()) {
            ItemCategory category = new ItemCategory();
            int colid = c.getColumnIndex(CategoryTable.ID);
            category.setId(c.getLong(colid));

            colid = c.getColumnIndex(CategoryTable.NAME);
            category.setName(c.getString(colid));

            colid = c.getColumnIndex(CategoryTable.ICON_URL);
            category.setIconUrl(c.getString(colid));

            colid = c.getColumnIndex(CategoryTable.PARENT_ID);
            category.setParentId(c.getLong(colid));


            list.add(category);
        }
        return list;

    }

}
