package finki.booksapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import finki.booksapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var addToCartButton: Button
    private lateinit var binding: ActivityDetailBinding
    private lateinit var database: DatabaseReference
    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addToCartButton = binding.addToCart
        database = FirebaseDatabase.getInstance().getReference("Items")

        val bookTitle = intent.getStringExtra("bookTitle")
        val bookCategory = intent.getStringExtra("bookCategory")
        val bookDescription = intent.getStringExtra("bookDescription")
        val bookPrice = intent.getDoubleExtra("bookPrice", 0.0)
        val bookPicUrl = intent.getStringExtra("bookPicUrl")

        binding.textTitle.text = bookTitle
        binding.textCategory.text = bookCategory
        binding.textDescription.text = bookDescription
        binding.textPrice.text = "$bookPrice ден"

        Glide.with(this)
            .load(bookPicUrl)
            .into(binding.imageView3)

        addToCartButton.setOnClickListener {
            db = FirebaseDatabase.getInstance().getReference("Cart")
            val item = CartItem(
                title = bookTitle ?: "",
                price = bookPrice,
                picture = arrayListOf(bookPicUrl ?: "")
            )

            addCartItem(item)

            navigateToCart()
        }
    }

    private fun addCartItem(item: CartItem) {
        val cartItemId = db.push().key ?: return
        db.child(cartItemId).setValue(item)
    }

    private fun navigateToCart() {
        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)
    }


}