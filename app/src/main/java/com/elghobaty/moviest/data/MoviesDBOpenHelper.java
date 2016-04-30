package com.elghobaty.moviest.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import com.elghobaty.moviest.data.MoviesContract.MovieEntry;
import com.elghobaty.moviest.data.MoviesContract.ReviewEntry;
import com.elghobaty.moviest.data.MoviesContract.TrailerEntry;

class MoviesDBOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "moviest.db";

    private static final String TEMPORARY_TABLE_NAME_PREFIX = "temp_";

    public MoviesDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        createTables(DB);
    }


    private void createTables(SQLiteDatabase DB, String movieTablePrefix) {
        String SQL_CREATE_MOVIES_TABLE = createMoviesTableSQL(movieTablePrefix + MovieEntry.TABLE_NAME);
        DB.execSQL(SQL_CREATE_MOVIES_TABLE);


        String SQL_CREATE_TRAILERS_TABLE = createTrailersTableSQL(movieTablePrefix + TrailerEntry.TABLE_NAME);
        DB.execSQL(SQL_CREATE_TRAILERS_TABLE);


        final String SQL_CREATE_REVIEWS_TABLE = createReviewsTableSQL(movieTablePrefix + ReviewEntry.TABLE_NAME);

        DB.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    private void createTables(SQLiteDatabase DB) {
        createTables(DB, "");
    }


    private String createMoviesTableSQL(String tableName) {
        return "CREATE TABLE " + tableName + " ( " +
                MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RATING + " REAL NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_FAVOURED_AT + " TEXT);";
    }

    private String createTrailersTableSQL(String tableName) {
        return "CREATE TABLE " + tableName + " (" +
                TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                TrailerEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_SOURCE + " TEXT NOT NULL UNIQUE, " +
                " FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ")); ";
    }

    private String createReviewsTableSQL(String tableName) {
        return "CREATE TABLE " + tableName + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_BODY + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ")); ";
    }


    @Override
    public void onUpgrade(SQLiteDatabase DB, int oldVersion, int newVersion) {
        try{
            DB.beginTransaction();
            createTemporaryTables(DB);
            copyDataToTemporaryTables(DB);
            dropExistingTables(DB);
            onCreate(DB);
            copyDataBack(DB);
            DB.setTransactionSuccessful();
        } finally {
            DB.endTransaction();
        }
    }

    private void dropExistingTables(SQLiteDatabase DB) {
        DB.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        DB.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        DB.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
    }

    private void copyDataBack(SQLiteDatabase DB) {
        copyFromTemporaryTable(DB, MovieEntry.TABLE_NAME, MovieEntry.getColumnList());
        copyFromTemporaryTable(DB, TrailerEntry.TABLE_NAME, TrailerEntry.getColumnList());
        copyFromTemporaryTable(DB, ReviewEntry.TABLE_NAME, ReviewEntry.getColumnList());
    }


    private void createTemporaryTables(SQLiteDatabase DB) {
        createTables(DB, TEMPORARY_TABLE_NAME_PREFIX);
    }

    private void copyDataToTemporaryTables(SQLiteDatabase DB) {
        copyToTemporaryTable(DB, MovieEntry.TABLE_NAME, MovieEntry.getColumnList());
        copyToTemporaryTable(DB, TrailerEntry.TABLE_NAME, TrailerEntry.getColumnList());
        copyToTemporaryTable(DB, ReviewEntry.TABLE_NAME, ReviewEntry.getColumnList());
    }

    private void copyToTemporaryTable(SQLiteDatabase DB, String tableName, String[] columnList) {
        String columnListString = TextUtils.join(",", columnList);
        String temporaryTableName = TEMPORARY_TABLE_NAME_PREFIX + tableName;
        String copyMoviesTableQuery = buildCopyQuery(columnListString, temporaryTableName, tableName);
        DB.execSQL(copyMoviesTableQuery);
    }
    private void copyFromTemporaryTable(SQLiteDatabase DB, String tableName, String[] columnList) {
        String columnListString = TextUtils.join(",", columnList);
        String temporaryTableName = TEMPORARY_TABLE_NAME_PREFIX + tableName;
        String copyMoviesTableQuery = buildCopyQuery(columnListString, tableName, temporaryTableName);
        DB.execSQL(copyMoviesTableQuery);

    }

    private String buildCopyQuery(String columnList, String sourceTable, String destinationTable) {
        return "INSERT INTO " + sourceTable
                + "(" + columnList + ") " +
                "SELECT " + columnList + " FROM " + destinationTable;
    }

}