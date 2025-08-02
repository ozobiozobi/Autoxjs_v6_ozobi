package org.autojs.autoxjs.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.autojs.autoxjs.Pref

class ExplorerViewModel : ViewModel() {

    companion object{
        object SortBy{
            const val NAME = 0
            const val LAST_MODIFY_TIME = 1
            const val SIZE = 2
            const val EXTENSION = 3
        }
    }

    private val _curDisplayPath = MutableStateFlow("")
    var curDisplayPath: StateFlow<String> = _curDisplayPath.asStateFlow()

    private val _curSortBy = MutableStateFlow(0)
    var curSortBy:StateFlow<Int> = _curSortBy.asStateFlow()

    private val _isDesSort = MutableStateFlow(false)
    var isDesSort:StateFlow<Boolean> = _isDesSort.asStateFlow()

    fun updateCurDisplayPath(path: String) {
        if(path.isNotEmpty()){
            _curDisplayPath.update { path }
        }
    }

    fun updateCurSortBy(sortBy:Int){
        if(sortBy == SortBy.NAME || sortBy == SortBy.LAST_MODIFY_TIME|| sortBy == SortBy.SIZE || sortBy == SortBy.EXTENSION){
            _curSortBy.update { sortBy }
            Pref.setExplorerCurSortBy(sortBy)
        }
    }

    fun updateIsDesSort(isDes:Boolean){
        _isDesSort.update { isDes }
        Pref.setExplorerIsDesSort(isDes)
    }
}