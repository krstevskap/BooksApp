package finki.booksapp

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import finki.booksapp.databinding.ViewHolderRecommendedBinding

class BookAdapter(var list: ArrayList<BookModel>) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private var context: Context? = null
    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(book: BookModel)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookAdapter.BookViewHolder {
        context = parent.context
        val binding =
            ViewHolderRecommendedBinding.inflate(LayoutInflater.from(context), parent, false)
        return BookViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        if (position < list.size) {
            val book = list[position]

            holder.binding.bookTitle.text = book.title
            holder.binding.bookPrice.text = "${book.price} ден"

            val reqOptions = RequestOptions().transform(CenterCrop())
            Glide.with(holder.itemView.context)
                .load(book.picUrl.getOrElse(0) { "" })
                .apply(reqOptions)
                .into(holder.binding.bookPic)

            holder.itemView.setOnClickListener {
                itemClickListener?.onItemClick(book)
            }
        } else {
            Log.e(
                "BookAdapter",
                "onBindViewHolder: Position $position out of bounds for list size ${list.size}"
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateBooks(newBooks: ArrayList<BookModel>) {
        list = newBooks
        notifyDataSetChanged()
    }


    class BookViewHolder(val binding: ViewHolderRecommendedBinding) :
        RecyclerView.ViewHolder(binding.root)

}
