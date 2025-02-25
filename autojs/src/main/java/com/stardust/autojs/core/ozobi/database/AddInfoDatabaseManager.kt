package com.stardust.autojs.core.ozobi.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor

class AddInfoDatabaseManager(private val dbHelper: AddInfoDatabaseHelper) {

    fun insertData(qq:String, result:String, time:String, loginQQ:String):Long{
        val db = dbHelper.writableDatabase
        val contentValue = ContentValues().apply {
            put(AddInfoDatabaseHelper.COLUMN_QQ,qq)
            put(AddInfoDatabaseHelper.COLUMN_RESULT,result)
            put(AddInfoDatabaseHelper.COLUMN_TIME,time)
            put(AddInfoDatabaseHelper.COLUMN_LOGIN_QQ,loginQQ)
        }
        return db.insert(AddInfoDatabaseHelper.TABLE_NAME,null,contentValue)
    }
    fun updateData(id:Int,qq:String, result:String, time:String, loginQQ:String):Int{
        val db = dbHelper.writableDatabase
        val contentValue = ContentValues().apply {
            put(AddInfoDatabaseHelper.COLUMN_QQ,qq)
            put(AddInfoDatabaseHelper.COLUMN_RESULT,result)
            put(AddInfoDatabaseHelper.COLUMN_TIME,time)
            put(AddInfoDatabaseHelper.COLUMN_LOGIN_QQ,loginQQ)
        }
        val rowAffected = db.update(
            AddInfoDatabaseHelper.TABLE_NAME,
            contentValue,
            "${AddInfoDatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
        return rowAffected
    }
    fun deleteData(id:Int):Int{
        val db = dbHelper.writableDatabase
        val rowAffected = db.delete(
            AddInfoDatabaseHelper.TABLE_NAME,
            "${AddInfoDatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
        return rowAffected
    }
    fun batchDeleteData(idArray:Array<String>):Int{
        val db = dbHelper.writableDatabase
        val rowAffected = db.delete(
            AddInfoDatabaseHelper.TABLE_NAME,
            "${AddInfoDatabaseHelper.COLUMN_ID} = ?",
            idArray
        )
        return rowAffected
    }
    fun clearTable():Int{
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete(AddInfoDatabaseHelper.TABLE_NAME,null,null)
        return rowsDeleted
    }
    fun queryData(columns:Array<String>?, selection:String?, selectionArgs:Array<String>?, groupBy:String?, having:String?, orderBy:String?):Cursor{
        val db = dbHelper.readableDatabase
        return db.query(
            AddInfoDatabaseHelper.TABLE_NAME,
            columns,
            selection,
            selectionArgs,
            groupBy,
            having,
            orderBy
        )
    }
    fun getTableDataCount():Long{
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${AddInfoDatabaseHelper.TABLE_NAME}",null)
        var count = 0L
        if(cursor.moveToFirst()){
            count = cursor.getLong(0)
        }
        cursor.close()
        return count
    }
    @SuppressLint("Range")
    fun getPageData(page:Int, pageSize:Int, orderBy:String?, isDesc:Boolean?):List<AddInfo>{
        val dataList = mutableListOf<AddInfo>()
        val offset = (page-1)*pageSize
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${AddInfoDatabaseHelper.TABLE_NAME} ORDER BY ${orderBy?: AddInfoDatabaseHelper.COLUMN_ID} ${if(isDesc == true) {
            "DESC"
        }else {
            "ASC"
        }} LIMIT ? OFFSET ?"
        try {
            db.rawQuery(query, arrayOf(pageSize.toString(),offset.toString())).use{cursor->
                if(cursor.moveToFirst()){
                    do{
                        val id = cursor.getInt(cursor.getColumnIndex(AddInfoDatabaseHelper.COLUMN_ID))
                        val qq = cursor.getString(cursor.getColumnIndex(AddInfoDatabaseHelper.COLUMN_QQ))
                        val result = cursor.getString(cursor.getColumnIndex(AddInfoDatabaseHelper.COLUMN_RESULT))
                        val time = cursor.getString(cursor.getColumnIndex(AddInfoDatabaseHelper.COLUMN_TIME))
                        val loginQQ = cursor.getString(cursor.getColumnIndex(AddInfoDatabaseHelper.COLUMN_LOGIN_QQ))
                        val model = AddInfo(id,qq,result,time,loginQQ)
                        dataList.add(model)
                    }while(cursor.moveToNext())
                }
            }
        }finally {
            db.close()
        }
        return dataList
    }
}