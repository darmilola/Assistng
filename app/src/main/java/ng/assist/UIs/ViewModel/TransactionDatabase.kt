package ng.assist.UIs.ViewModel

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(User::class), version = 1)
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionDao():TransactionDao;
}