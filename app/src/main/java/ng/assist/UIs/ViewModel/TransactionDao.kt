package ng.assist.UIs.ViewModel

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions")
    fun getAll(): ArrayList<Transactions>

    @Query("SELECT * FROM transactions")
    fun loadAllTransaction(): List<Transactions>

    @Insert
    fun insertAll(vararg transactions: Transactions)

    @Insert
    fun insert(transactions: Transactions)

    @Delete
    fun delete(transactions: Transactions)

}