package finki.booksapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(private val cartItems: List<CartItem>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.titleItemCart)
        val price: TextView = itemView.findViewById(R.id.priceItemCart)
        val pic: ImageView = itemView.findViewById(R.id.picCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.viewholder_cart, parent, false)
        return CartViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = cartItems[position]
        holder.title.text = currentItem.title
        holder.price.text = "${currentItem.price} ден"
        Glide.with(holder.itemView.context)
            .load(currentItem.picture[0])
            .into(holder.pic)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
}