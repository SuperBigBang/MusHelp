package com.superbigbang.mushelp.di.modules;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.widget.Toast;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class StarbuzzDatabaseHelperModule extends SQLiteOpenHelper {
    public static final String EXTRA_DRINKNO = "drinkNo";
    private static final String DB_NAME = "starbuzz";
    private static final int DB_VERSION = 5;
    private static final String TABLE = "DRINK";
    private static final String NAME = "NAME";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String IMAGE_RESOURCE_ID = "IMAGE_RESOURCE_ID";
    private static final String FAVORITE = "FAVORITE";
    private SQLiteDatabase mDatabase;
    //   private SQLiteOpenHelper mStarbuzzDatabaseHelper;
    private Cursor mCursor;


    public StarbuzzDatabaseHelperModule(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static void insertDrink(SQLiteDatabase db, String name,
                                   String description, int resourceId) {
        ContentValues drinkValues = new ContentValues();
        drinkValues.put(NAME, name);
        drinkValues.put(DESCRIPTION, description);
        drinkValues.put(IMAGE_RESOURCE_ID, resourceId);
        db.insert(TABLE, null, drinkValues);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);
        mDatabase = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
        mDatabase = db;
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
     /*   if (oldVersion < 1) {
            db.execSQL("CREATE TABLE DRINK ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "NAME TEXT, "
                    + "DESCRIPTION TEXT, "
                    + "IMAGE_RESOURCE_ID INTEGER);");
            insertDrink(db, "Cappuccino", "Espresso, hot milk and steamed-milk foam", R.drawable.cappuccino);
            insertDrink(db, "Latte", "Espresso and steamed milk", R.drawable.latte);
            insertDrink(db, "Filter", "Our best drip coffee", R.drawable.filter);
        }
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE DRINK ADD COLUMN FAVORITE NUMERIC");
        } */
        if (oldVersion > 0 && oldVersion < 4) {
            db.execSQL("DROP TABLE DRINK");
        }
        if (oldVersion < 4) {
            db.execSQL("CREATE TABLE DRINK ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "NAME TEXT, "
                    + "DESCRIPTION TEXT, "
                    + "IMAGE_RESOURCE_ID INTEGER);");
      /*      insertDrink(db, "Cappuccino", "Espresso, hot milk and steamed-milk foam", R.drawable.cappuccino);
            insertDrink(db, "Latte", "Espresso and steamed milk", R.drawable.latte);
            insertDrink(db, "Filter", "Our best drip coffee", R.drawable.filter);*/
        }
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE DRINK ADD COLUMN FAVORITE NUMERIC");
        }
    }

    @Provides
    @Singleton
    public StarbuzzDatabaseHelperModule provideStarbuzzDatabaseHelperModule(Context context) {
        return new StarbuzzDatabaseHelperModule(context);
    }

    public String[] getItemSourceFromDatabase(String drinkNO, Context context) {
        String nameText = "";
        String descriptionText = "";
        String photoId = "";
        String favorite = "";

        try {
            SQLiteOpenHelper starbuzzDatabaseHelper = new StarbuzzDatabaseHelperModule(context);
            mDatabase = starbuzzDatabaseHelper.getReadableDatabase();
            mCursor = mDatabase.query(TABLE,
                    new String[]{NAME, DESCRIPTION, IMAGE_RESOURCE_ID, FAVORITE},
                    "_id = ?",
                    new String[]{drinkNO},
                    null, null, null);
            if (mCursor.moveToFirst()) {
                nameText = mCursor.getString(0);
                descriptionText = mCursor.getString(1);
                photoId = mCursor.getString(2);
                favorite = mCursor.getString(3);
            }
            closeDatabaseAndCursor();
        } catch (SQLiteException e) {
            if (mCursor != null || mDatabase != null) {
                closeDatabaseAndCursor();
            }
            makeSQLiteExceptionTOAST(context);
        }
        return new String[]{nameText, descriptionText, photoId, favorite};
    }

    public Cursor getCursor(Context context) {
        try {
            SQLiteOpenHelper starbuzzDatabaseHelper = new StarbuzzDatabaseHelperModule(context);
            mDatabase = starbuzzDatabaseHelper.getReadableDatabase();
            mCursor = mDatabase.query("DRINK",
                    new String[]{"_id", "NAME"},
                    null, null, null, null, null);
        } catch (SQLiteException e) {
            if (mCursor != null || mDatabase != null) {
                closeDatabaseAndCursor();
            }
            makeSQLiteExceptionTOAST(context);
        }
        return mCursor;
    }

  /* public Cursor getCursorForFavoritesList(Context context) {
}*/

    public void getCursorForFavoritesList(Context context, GetCursorForFavoritesListCallback callback) {
        GetCursorForFavoritesListTask getCursorForFavoritesListTask = new GetCursorForFavoritesListTask(callback);
        getCursorForFavoritesListTask.execute(context);
    }

    public void closeDatabaseAndCursor() {
        if (mCursor != null) mCursor.close();
        if (mDatabase != null) mDatabase.close();
        mCursor = null;
        mDatabase = null;
    }

    public void editDatabase(ContentValues contentValues, Context context, String id) {
        try {
            SQLiteOpenHelper starbuzzDatabaseHelper = new StarbuzzDatabaseHelperModule(context);
            mDatabase = starbuzzDatabaseHelper.getWritableDatabase();
            mDatabase.update("DRINK", contentValues, "_id = ?", new String[]{id});
            mDatabase.close();
            mDatabase = null;
        } catch (SQLiteException e) {
            if (mDatabase != null) {
                mDatabase.close();
                mDatabase = null;
            }
            makeSQLiteExceptionTOAST(context);
        }
    }

    private void makeSQLiteExceptionTOAST(Context context) {
        Toast toast = Toast.makeText(context, "Database unavailable", Toast.LENGTH_SHORT);
        toast.show();
    }

    public interface GetCursorForFavoritesListCallback {
        void getCursorForFavoritesList(Cursor cursor);
    }

    class GetCursorForFavoritesListTask extends AsyncTask<Context, Void, Cursor> {
        private final GetCursorForFavoritesListCallback callback;

        GetCursorForFavoritesListTask(GetCursorForFavoritesListCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Cursor doInBackground(Context... contexts) {
            try {
                SQLiteOpenHelper starbuzzDatabaseHelper = new StarbuzzDatabaseHelperModule(contexts[0]);
                mDatabase = starbuzzDatabaseHelper.getReadableDatabase();
                mCursor = mDatabase.query("DRINK",
                        new String[]{"_id", "NAME"},
                        FAVORITE + " = 1",
                        null, null, null, null);
            } catch (SQLiteException e) {
                if (mCursor != null || mDatabase != null) {
                    closeDatabaseAndCursor();
                }
                makeSQLiteExceptionTOAST(contexts[0]);
            }
            return mCursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (callback != null) {
                callback.getCursorForFavoritesList(cursor);
            }
        }
    }
}

