package com.code_23.ta_eye_go.DB

import android.content.Context
import androidx.room.*

@Entity(tableName = "bookmark")
data class Bookmark (
    @PrimaryKey(autoGenerate = true) val no: Int,
    @ColumnInfo(name = "현재정류장") val startNodenm: String,
    @ColumnInfo(name = "현재정류장ID") val startNodeID: String,
    @ColumnInfo(name = "도착정류장") val endNodenm: String,
    @ColumnInfo(name = "도착정류장ID") val endNodeID: String,
    @ColumnInfo(name = "노선번호") val routeID: String
){
    constructor(): this( 0,"","","","","")
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmark")
    fun getAll(): List<Bookmark>

    @Insert(onConflict = OnConflictStrategy.IGNORE) // 추가할 때 동일한 기록이면 무시하기
    fun insert(bookmark: Bookmark)

    @Update
    fun updateBookmark(bookmark: Bookmark)

    @Query("DELETE from bookmark")
    fun deleteAll()
}

@Database(entities = [Bookmark::class], version = 2)
abstract class BookmarkDB: RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        private var INSTANCE: BookmarkDB? = null

        fun getInstance(context: Context): BookmarkDB? {
            if (INSTANCE == null) {
                synchronized(BookmarkDB::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        BookmarkDB::class.java, "bookmark.db")
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