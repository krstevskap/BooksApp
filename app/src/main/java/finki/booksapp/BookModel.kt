package finki.booksapp

data class BookModel(
    val author: String = "",
    val categoryId: Int = 0,
    val description: String = "",
    val picUrl: ArrayList<String> = ArrayList(),
    val price: Double = 0.0,
    val title: String = ""
) {
    constructor() : this("", 0, "", ArrayList(), 0.0, "")
}