package week11.st335153.finalproject.data

data class Workout(
    val id: String = "",
    val steps: Int = 0,
    val minutes: Int = 0,
    val notes: String = "",
    val date: Long = System.currentTimeMillis()
) {
    val dateFormatted: String
        get() = java.text.SimpleDateFormat("MMM dd, yyyy").format(date)
}
