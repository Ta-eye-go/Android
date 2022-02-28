package com.code_23.ta_eye_go.DB

import android.content.Context
import androidx.room.*

@Entity(tableName = "record",primaryKeys = ["도착정류장ID","도착정류장", "노선번호ID","노선번호","현재정류장ID","현재정류장"])
data class Record (
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
interface RecordDao {
        @Query("SELECT * FROM record")
        fun getAll(): List<Record>

        @Insert(onConflict = OnConflictStrategy.IGNORE) // 추가할 때 동일한 기록이면 무시하기
        fun insert(record: Record)

        @Update
        fun updateRecord(record: Record)

        @Query("DELETE from record")
        fun deleteAll()
}

@Database(entities = [Record::class], version = 6)
abstract class RecordDB: RoomDatabase() {
        abstract fun recordDao(): RecordDao

        companion object {
                private var INSTANCE: RecordDB? = null
                fun getInstance(context: Context): RecordDB? {
                        if (INSTANCE == null) {
                                synchronized(RecordDB::class) {
                                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                                RecordDB::class.java, "record.db")
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