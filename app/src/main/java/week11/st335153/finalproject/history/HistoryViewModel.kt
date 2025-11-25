package week11.st335153.finalproject.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import week11.st335153.finalproject.data.ActivityRepository

class HistoryViewModel : ViewModel() {

    private val repo = ActivityRepository()
    val state = repo.historyState

    init {
        viewModelScope.launch {
            repo.loadHistory()
        }
    }
}
