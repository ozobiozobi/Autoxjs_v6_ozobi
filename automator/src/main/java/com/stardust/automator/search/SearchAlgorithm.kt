package com.stardust.automator.search

import com.stardust.automator.UiObject
import com.stardust.automator.filter.Filter

interface SearchAlgorithm {

    fun search(root: UiObject, filter: Filter, limit: Int = Int.MAX_VALUE, isRefresh:Boolean): ArrayList<UiObject>// Modified by ibozo - 2024/10/31
}