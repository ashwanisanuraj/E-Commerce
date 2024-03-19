package com.xero.myapplication.roomDb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface ProductDao {

    @Insert
    suspend fun insertProduct(productModel: ProductModel)

    @Delete
    suspend fun deleteProduct(productModel: ProductModel)

    @Query("SELECT * FROM products")
    fun getAllProduct() : LiveData<List<ProductModel>>

    @Query("SELECT * FROM products WHERE productId = :id")
    fun isExit(id : String) : ProductModel

}