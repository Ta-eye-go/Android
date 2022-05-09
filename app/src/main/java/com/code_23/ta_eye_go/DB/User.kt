package com.code_23.ta_eye_go.DB

import android.content.Context
import androidx.room.*

//database 안에 있는 테이블을 java나 kotlin 클래스로 나타낸 것이다. 데이터 모델 클래스 라고 볼 수 있다.
//Entity를 import해서 Entity로 선언된 클래스를 만들 수 있다. 데이터 모델인 User에 무엇이 들어갈지 정의
//각각의 entity는 고유 식별자인 기본키가 필요.

@Entity(tableName = "user")
class User(@PrimaryKey var id: String,
           @ColumnInfo(name = "guide_dog") var guide_dog: Boolean
){
    constructor(): this("nono",false)
}

//DB에 접근해 질의를 수행할 DAO. Query를 메소드로 작성해주어야 한다.
@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    /* import android.arch.persistence.room.OnConflictStrategy.REPLACE */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)

    @Update
    fun updateUser(user: User)

    // 현재 유저의 안내견 유무 데이터를 가져와줌
    @Query("SELECT guide_dog FROM user WHERE id IN (:email)")
    fun userdata(email: String): Boolean

    @Query("DELETE from user")
    fun deleteAll()

    @Query("DELETE from user WHERE id IN (:email)")
    fun delete(email: String)
}

//database holder를 포함하여, 앱의 영구 저장되는 데이터와 기본 연결을 위한 주 엑세스 지점이다.
//RoomDatabase를 extend 하는 추상 클래스여야 하며, 테이블과 버전을 정의하는 곳이다.
//Entity 모델을 기반으로 하고, dao의 메소드를 가지고 있는 데이터베이스를 생성하자. RoomDatabase()를 상속한다.
//MainActivity에서 호출하며 database 객체를 반환하거나 삭제할 수 있도록 getInstance()와 destroyInstance()메소드를 생성
@Database(entities = [User::class], version = 11)
abstract class UserDB: RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private var INSTANCE: UserDB? = null

        fun getInstance(context: Context): UserDB? {
            if (INSTANCE == null) {
                synchronized(UserDB::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        UserDB::class.java, "user.db")
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