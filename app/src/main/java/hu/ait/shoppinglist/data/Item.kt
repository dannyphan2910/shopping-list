package hu.ait.shoppinglist.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "shoppinglisttable")
data class Item(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "category") var category: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "amount") var amount: Long,
    @ColumnInfo(name = "price") var price: Double,
    @ColumnInfo(name = "details") var details: String,

    @ColumnInfo(name = "position") var position: Long,

    @ColumnInfo(name = "purchased") var purchased: Boolean,
    @ColumnInfo(name = "expanded") var expanded: Boolean
) : Serializable