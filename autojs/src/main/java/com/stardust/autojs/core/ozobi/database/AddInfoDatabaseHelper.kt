package com.stardust.autojs.core.ozobi.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class AddInfoDatabaseHelper(context:Context):SQLiteOpenHelper(context,
    DATABASE_NAME,null,
    DATABASE_VERSION
) {
    private val TAG = "ozobiLog"
    companion object{
        const val DATABASE_NAME = "ozobiAddInfo.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "addInfo"
        const val COLUMN_ID = "id"
        const val COLUMN_QQ = "qq"
        const val COLUMN_RESULT = "result"
        const val COLUMN_TIME = "sendTime"
        const val COLUMN_LOGIN_QQ = "loginQQ"

        private val TABLE_CREATE = """
            CREATE TABLE $TABLE_NAME(
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_QQ TEXT NOT NULL,
            $COLUMN_RESULT TEXT NOT NULL,
            $COLUMN_TIME TEXT NOT NULL,
            $COLUMN_LOGIN_QQ TEXT NOT NULL
            )
        """.trimIndent()
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d(TAG, "onCreate: $TABLE_CREATE")
        db?.execSQL(TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade")
//        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
//        onCreate(db)
    }

}