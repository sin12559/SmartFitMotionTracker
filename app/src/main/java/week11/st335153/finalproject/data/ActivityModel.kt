package week11.st335153.finalproject.data

data class ActivityModel(
    val steps: Float = 0f,
    val light: Float = 0f,
    val movement: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)
