package com.code_23.ta_eye_go.DB

import android.content.Context
import androidx.room.*

@Entity(tableName = "datamodel",primaryKeys = ["도착정류장ID","도착정류장", "노선번호ID","노선번호","현재정류장ID","현재정류장"])
data class DataModel (
    @ColumnInfo(name = "도착정류장ID") val endNodeID: String,
    @ColumnInfo(name = "도착정류장") val endNodenm: String,
    @ColumnInfo(name = "노선번호ID") val routeID: String,
    @ColumnInfo(name = "노선번호") val routeNo: String,
    @ColumnInfo(name = "현재정류장ID") val startNodeID: String,
    @ColumnInfo(name = "현재정류장") val startNodenm: String

){
    constructor(): this( "","","","","","")
}

@Dao
interface DataModelDao {
    @Query("SELECT * FROM datamodel")
    fun getAll(): List<DataModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE) // 추가할 때 동일한 기록이면 무시하기
    fun insert(datamodel: DataModel)

    @Query("DELETE from datamodel")
    fun deleteAll()
}

@Database(entities = [DataModel::class], version = 7)
abstract class DataModelDB: RoomDatabase() {
    abstract fun datamodelDao(): DataModelDao

    companion object {
        private var INSTANCE: DataModelDB? = null
        fun getInstance(context: Context): DataModelDB? {
            if (INSTANCE == null) {
                synchronized(DataModelDB::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        DataModelDB::class.java, "datamodel.db")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}