package finki.booksapp

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CartItem(
    var title: String = "",
    var price: Double = 0.0,
    var picture: ArrayList<String> = arrayListOf()
)

