package finki.booksapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class MainActivity : AppCompatActivity(), CategoryAdapter.CategoryClickListener,
    BookAdapter.OnItemClickListener {

    private lateinit var dbref: DatabaseReference
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var bookRecyclerView: RecyclerView
    private lateinit var categoryList: ArrayList<CategoryModel>
    private lateinit var booksList: ArrayList<BookModel>
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var bookAdapter: BookAdapter
    private lateinit var searchView: SearchView
    private lateinit var searchList: ArrayList<BookModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        categoryRecyclerView = findViewById(R.id.viewCategory)
        bookRecyclerView = findViewById(R.id.viewRecommendation)

        categoryRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        bookRecyclerView.layoutManager =
            GridLayoutManager(this, 2)

        categoryRecyclerView.setHasFixedSize(true)
        bookRecyclerView.setHasFixedSize(true)

        categoryList = ArrayList()
        booksList = ArrayList()

        searchView = findViewById(R.id.search)
        searchList = ArrayList()

        setupBookRecyclerView()
        setupSearchView()
        setupCategoryRecyclerView()

        loadCategoryData()
        loadBooksData()

        val imageViewHome = findViewById<ImageView>(R.id.imageView4)
        val textViewHome = findViewById<TextView>(R.id.textView13)

        imageViewHome.setOnClickListener {
            clearFiltersAndSearch()
        }

        textViewHome.setOnClickListener {
            clearFiltersAndSearch()
        }

        bookAdapter.setOnItemClickListener(this)

        val textViewCart: TextView = findViewById(R.id.textView14)
        textViewCart.setOnClickListener {
            startActivity(Intent(this@MainActivity, CartActivity::class.java))
        }

        val imageViewCart: ImageView = findViewById(R.id.imageView5)
        imageViewCart.setOnClickListener {
            startActivity(Intent(this@MainActivity, CartActivity::class.java))
        }

    }

    private fun clearFiltersAndSearch() {
        searchList.clear()
        searchList.addAll(booksList)
        bookAdapter.notifyDataSetChanged()
        searchView.setQuery("", false)
        searchView.clearFocus()
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                handleSearch(newText.orEmpty())
                return true
            }
        })
    }

    private fun handleSearch(query: String) {
        val filteredBooks = if (query.isEmpty()) {
            ArrayList(booksList)
        } else {
            val lowercaseQuery = query.toLowerCase(Locale.getDefault())
            booksList.filter {
                it.title.toLowerCase(Locale.getDefault()).contains(lowercaseQuery)
            } as ArrayList<BookModel>
        }
        bookAdapter.updateBooks(filteredBooks)
    }

    private fun setupCategoryRecyclerView() {
        categoryAdapter = CategoryAdapter(categoryList, this)
        categoryRecyclerView.adapter = categoryAdapter
    }

    private fun setupBookRecyclerView() {
        bookAdapter = BookAdapter(ArrayList())
        bookAdapter.setOnItemClickListener(this)
        bookRecyclerView.adapter = bookAdapter
    }

    private fun loadCategoryData() {
        dbref = FirebaseDatabase.getInstance().getReference("Category")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryList.clear()
                if (snapshot.exists()) {
                    for (cat in snapshot.children) {
                        val category = cat.getValue(CategoryModel::class.java)
                        category?.let {
                            categoryList.add(it)
                        }
                    }
                    categoryAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching category data: ${error.message}")
            }
        })
    }

    private fun loadBooksData() {
        dbref = FirebaseDatabase.getInstance().getReference("Items")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                booksList.clear()
                if (snapshot.exists()) {
                    for (b in snapshot.children) {
                        val book = b.getValue(BookModel::class.java)
                        book?.let {
                            booksList.add(it)
                        }
                    }
                    searchList.clear()
                    searchList.addAll(booksList)
                    bookAdapter.updateBooks(searchList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching book data: ${error.message}")
            }
        })
    }

    override fun onCategoryClicked(category: CategoryModel) {
        filterBooksByCategory(category.id)
    }

    private fun filterBooksByCategory(categoryId: Int) {
        val filteredBooks = if (categoryId == -1) {
            ArrayList(searchList)
        } else {
            searchList.filter {
                it.categoryId == categoryId
            } as ArrayList<BookModel>
        }
        bookAdapter.updateBooks(filteredBooks)
    }

    private fun getCategoryTitleById(categoryId: Int): String? {
        val category = categoryList.find { it.id == categoryId }
        return category?.title
    }


    override fun onItemClick(book: BookModel) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("bookTitle", book.title)
            putExtra("bookCategory", getCategoryTitleById(book.categoryId))
            putExtra("bookDescription", book.description)
            putExtra("bookPrice", book.price)
            putExtra("bookPicUrl", book.picUrl[0])
        }
        startActivity(intent)
    }
}