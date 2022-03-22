package com.code_23.ta_eye_go.DB

import android.content.Context
import androidx.room.*

@Entity(tableName = "driver", primaryKeys = ["id"])
data class Driver (
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "현재정류장") val startNodenm: String,
    @ColumnInfo(name = "도착정류장") val endNodenm: String,
    @ColumnInfo(name = "안내견") val guide_dog: Boolean
){
    constructor(): this( "0","","",false)
}

@Dao
interface DriverDao {
    @Query("SELECT * FROM driver")
    fun getAll(): List<Driver>

    @Insert(onConflict = OnConflictStrategy.IGNORE) // 추가할 때 동일한 기록이면 무시하기
    fun insert(driver: Driver)

    @Update
    fun updateDriver(driver: Driver)

    @Query("DELETE from driver where 도착정류장 = (:endNodenm)")
    fun delete(endNodenm: String)

    @Query("DELETE from driver")
    fun deleteAll()
}

@Database(entities = [Driver::class], version = 1)
abstract class DriverDB: RoomDatabase() {
    abstract fun driverDao(): DriverDao

    companion object {
        private var INSTANCE: DriverDB? = null

        fun getInstance(context: Context): DriverDB? {
            if (INSTANCE == null) {
                synchronized(DriverDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        DriverDB::class.java, "driver.db"
                    )
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