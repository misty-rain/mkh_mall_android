package com.mkh.mobilemall.support.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.support.db.table.AccountTable;
import com.mkh.mobilemall.support.db.table.CategoryTable;
import com.mkh.mobilemall.support.db.table.TempComCarTable;
import com.mkh.mobilemall.support.db.table.TicketTable;

import java.io.*;

public class DBManager extends SQLiteOpenHelper {

    private SQLiteDatabase database;
    private Context context;

    private static final String DATABASE_NAME = "mkh.db";
    private static final int DATABASE_VERSION = 16;

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }




    static final String CREATE_CATEGORY_TABLE_SQL = "create table " + CategoryTable.TABLE_NAME
              + "("
              + CategoryTable.ID + " integer primary key autoincrement,"
              + CategoryTable.NAME + " text,"
              + CategoryTable.ICON_URL + " text,"
              + CategoryTable.DESCRIPTION + " text,"
              + CategoryTable.PARENT_ID + " text,"
              + CategoryTable.ORDER_INDEX + " text,"
              + CategoryTable.ROW_VERSION + " text,"
              + CategoryTable.IS_DELETED + " boolean,"
              + CategoryTable.CREATED_BY + " text,"
              + CategoryTable.CREATION_TIME + " datetime,"
              + CategoryTable.LAST_UPDATED_BY + " text,"
              + CategoryTable.LAST_UPDATE_TIME + " datetime"
              + ");";


    static final String CREATE_TEMPCOMCAR_TABLE_SQL = "create table IF NOT EXISTS "
            + TempComCarTable.TABLE_NAME + "(" + TempComCarTable.ID
            + " text,"
            + TempComCarTable.CART_ITEM_ID + " text,"
            + TempComCarTable.COMMODNAME + " text,"
            + TempComCarTable.SINGLECOMMODTOTALCOUNT + " text,"
            + TempComCarTable.SINGLECOMMODPRICE + " text,"
            + TempComCarTable.COMMODTOTALCOUNT + " text,"
            + TempComCarTable.COMMODTOTALPRICE + " text,"
            + TempComCarTable.SINGLECOMPICURL + " text,"
            + TempComCarTable.ITEM_NUMBER + " text,"
            + TempComCarTable.SHORT_NAME + " text,"
            + TempComCarTable.OPERATION_TIME + " datetime" + ");";





    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL(CREATE_CATEGORY_TABLE_SQL);
        db.execSQL(CREATE_TEMPCOMCAR_TABLE_SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            default:
                deleteAllTableExceptAccount(db);
               break;
        }


    }

    private void deleteAllTableExceptAccount(SQLiteDatabase db) {

        db.execSQL("DROP TABLE IF EXISTS " + CategoryTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TempComCarTable.TABLE_NAME);


    }
}
