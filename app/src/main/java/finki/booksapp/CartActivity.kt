package finki.booksapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cartItems: ArrayList<CartItem>
    private lateinit var dbref: DatabaseReference
    private lateinit var cartAdapter: CartAdapter
    private lateinit var totalPriceView: TextView
    private lateinit var buyButton: Button
    private lateinit var addressEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.viewCart)
        recyclerView.layoutManager = LinearLayoutManager(this)
        totalPriceView = findViewById(R.id.textView15)
        buyButton = findViewById(R.id.checkOut)
        addressEditText = findViewById(R.id.addressEditText)

        cartItems = ArrayList()
        cartAdapter = CartAdapter(cartItems)
        recyclerView.adapter = cartAdapter

        dbref = FirebaseDatabase.getInstance().getReference("Cart")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    cartItems.clear()
                    var total = 0.0
                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue(CartItem::class.java)
                        item?.let {
                            cartItems.add(it)
                            total += it.price
                        }
                    }
                    totalPriceView.text = total.toString()
                    cartAdapter.notifyDataSetChanged()
                } else {
                    totalPriceView.text = "0.0"
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        buyButton.setOnClickListener {
            val address = addressEditText.text.toString().trim()

            if (address.isEmpty()) {
                addressEditText.error = "Please enter your address"
                return@setOnClickListener
            }

            dbref.removeValue()

            val intent = Intent(this@CartActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }


}

