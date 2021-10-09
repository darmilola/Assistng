package ng.assist.UIs.ViewModel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transactions (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "type") val type: Int?,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "timestamp") val timestamp: String?,
    @ColumnInfo(name = "amount") val amount: String?,
    @ColumnInfo(name = "orderId") val orderId: String?
)
