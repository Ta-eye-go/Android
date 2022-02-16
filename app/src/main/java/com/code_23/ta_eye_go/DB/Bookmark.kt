package com.code_23.ta_eye_go.DB

import android.content.Context
import androidx.room.*

@Entity(tableName = "bookmark")
data class Bookmark (
    @PrimaryKey(autoGenerate = true) val no: Int,
    @ColumnInfo(name = "유저ID") var id: String,
    @ColumnInfo(name = "안내견") var guide_dog: Boolean,
    @ColumnInfo(name = "도시코드") val cityCode: String,
    @ColumnInfo(name = "현재정류장") val startNodenm: String,
    @ColumnInfo(name = "현재정류장ID") val startNodeID: String,
    @ColumnInfo(name = "노선번호") val routeID: String,
    @ColumnInfo(name = "도착정류장") val endNodenm: String,
    @ColumnInfo(name = "노선번호ID") val routeNo: String,
    @ColumnInfo(name = "도착정류장ID") val endNodeID: String
){
    constructor(): this( 0,"", false,"","","","","","","")
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmark")
    fun getAll(): List<Bookmark>

    /* import android.arch.persistence.room.OnConflictStrategy.REPLACE */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(bookmark: Bookmark)

    @Update
    fun updateBookmark(bookmark: Bookmark)

    @Query("DELETE from bookmark")
    fun deleteAll()
}

@Database(entities = [Bookmark::class], version = 1)
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