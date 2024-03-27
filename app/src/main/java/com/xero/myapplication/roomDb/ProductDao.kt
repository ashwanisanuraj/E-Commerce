package com.xero.myapplication.roomDb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProductDao {

    // Methods for managing cart items
    @Insert
    suspend fun insertCartProduct(productModel: ProductModel)

    @Delete
    suspend fun deleteCartProduct(productModel: ProductModel)

    @Query("SELECT * FROM products")
    fun getAllCartProducts(): LiveData<List<ProductModel>>

    @Query("SELECT * FROM products WHERE productId = :id")
    fun isProductInCart(id: String): ProductModel

    // Methods for managing wishlist items
    @Insert
    suspend fun insertWishlistProduct(wishlistItem: WishlistItem)

    @Delete
    suspend fun deleteWishlistProduct(wishlistItem: WishlistItem)

    @Query("SELECT * FROM wishlist")
    fun getAllWishlistItems(): LiveData<List<WishlistItem>>

    @Query("SELECT * FROM wishlist WHERE productId = :id")
    fun isItemInWishlist(id: String): WishlistItem?
}
