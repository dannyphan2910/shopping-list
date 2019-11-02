package hu.ait.shoppinglist.data

import android.arch.persistence.room.*

@Dao
interface ItemDAO {
    @Query("SELECT * FROM shoppinglisttable ORDER BY position ASC")
    fun getAllItems(): List<Item>

    @Insert
    fun insertItem(item: Item): Long

    @Delete
    fun deleteItem(item: Item)

    @Update
    fun updateItem(item: Item)

    @Query("DELETE FROM shoppinglisttable")
    fun deleteAll()

    @Query("SELECT * FROM shoppinglisttable WHERE category LIKE '%Food%'")
    fun filterByFood() : List<Item>

    @Query("SELECT * FROM shoppinglisttable WHERE category LIKE '%Household%'")
    fun filterByHousehold() : List<Item>

    @Query("SELECT * FROM shoppinglisttable WHERE category LIKE '%Clothing%'")
    fun filterByClothing() : List<Item>

    @Query("SELECT * FROM shoppinglisttable WHERE category LIKE '%Electronics%'")
    fun filterByElectronics() : List<Item>

    @Query("SELECT * FROM shoppinglisttable WHERE category LIKE '%Other%'")
    fun filterByOther() : List<Item>

    @Query("SELECT * FROM shoppinglisttable WHERE purchased = 0")
    fun filterByNotPurchased() : List<Item>

    @Query("SELECT * FROM shoppinglisttable WHERE purchased = 1")
    fun filterByPurchased() : List<Item>
}