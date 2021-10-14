package ng.assist.UIs.ViewModel

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Transactions::class), version = 1)
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionDao():TransactionDao;
    companion object {
        var INSTANCE: TransactionDatabase? = null

        fun getTransactionDatabase(context: Context): TransactionDatabase? {
            if (INSTANCE == null){
                synchronized(TransactionDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, TransactionDatabase::class.java, "transactions").allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}