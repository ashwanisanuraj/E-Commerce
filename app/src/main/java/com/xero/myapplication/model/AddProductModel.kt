package com.xero.myapplication.model

data class AddProductModel(
    var productName: String = "",
    var productDescription: String = "",
    var productCoverImg: String = "",
    var productCategory: String = "",
    var productId: String = "",
    var productMrp: String = "",
    var productSp: String = "",
    var productImages: List<String> = emptyList()
)
