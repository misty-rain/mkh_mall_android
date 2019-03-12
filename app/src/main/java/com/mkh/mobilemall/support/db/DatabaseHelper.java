package com.mkh.mobilemall.support.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.support.db.table.AccountTable;
import com.mkh.mobilemall.support.db.table.CategoryTable;
import com.mkh.mobilemall.support.db.table.TempComCarTable;
import com.mkh.mobilemall.support.db.table.TicketTable;


class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper singleton = null;

    private static final String DATABASE_NAME = "mkh.db";
    private static final int DATABASE_VERSION = 16;

    static final String CREATE_ACCOUNT_TABLE_SQL = "create table " + AccountTable.TABLE_NAME
            + "("
            + AccountTable.UID + " integer primary key autoincrement,"
            + AccountTable.OAUTH_TOKEN + " text,"
            + AccountTable.OAUTH_TOKEN_SECRET + " text,"
            + AccountTable.PORTRAIT + " text,"
            + AccountTable.USERNAME + " text,"
            + AccountTable.USERNICK + " text,"
            + AccountTable.AVATAR_URL + " text,"
            + AccountTable.INFOJSON + " text"
            + ");";

    static final String CREATE_CATEGORY_TABLE_SQL = "create table " + CategoryTable.TABLE_NAME
            + "("
            + CategoryTable.ID + " integer primary key autoincrement,"
            + CategoryTable.NAME + " text,"
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

    static final String CREATE_TEMPCOMCAR_TABLE_SQL = "create table " + TempComCarTable.TABLE_NAME
            + "("
            + TempComCarTable.ID + " integer primary key autoincrement,"
            + TempComCarTable.COMMODNAME + " text,"
            + TempComCarTable.SINGLECOMMODTOTALCOUNT + " text,"
            + TempComCarTable.SINGLECOMMODPRICE + " text,"
            + TempComCarTable.COMMODTOTALCOUNT + " text,"
            + TempComCarTable.COMMODTOTALPRICE + " text,"
            + TempComCarTable.SINGLECOMPICURL + " text,"
            + TempComCarTable.OPERATION_TIME + " datetime"
            + ");";

    static final String CREATE_POS_TICKET_TABLE_SQL = "CREATE TABLE " + TicketTable.TABLE_NAME
            + "("
            + TicketTable.ID + " integer primary key autoincrement,"
            + TicketTable.TICKET_BUINESS_NAME + " text,"
            + TicketTable.TICKET_VALIDATE_CODE + " text,"
            + TicketTable.TICKET_AMOUNT + " text"
            + ");";


    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_ACCOUNT_TABLE_SQL);
        db.execSQL(CREATE_CATEGORY_TABLE_SQL);
        db.execSQL(CREATE_TEMPCOMCAR_TABLE_SQL);
        db.execSQL(CREATE_POS_TICKET_TABLE_SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            default:
                deleteAllTableExceptAccount(db);
                //  createOtherTable(db);
        }


    }

    public static synchronized DatabaseHelper getInstance() {
        if (singleton == null) {
            singleton = new DatabaseHelper(GlobalContext.getInstance());
        }
        return singleton;
    }


    private void deleteAllTableExceptAccount(SQLiteDatabase db) {

        db.execSQL("DROP TABLE IF EXISTS " + AccountTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CategoryTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TempComCarTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TicketTable.TABLE_NAME);


    }
}
