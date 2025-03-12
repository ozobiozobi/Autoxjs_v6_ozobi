package com.stardust.automator.search

import com.stardust.automator.UiObject
import com.stardust.automator.filter.Filter
import java.util.ArrayDeque

object BFS : SearchAlgorithm {

    override fun search(root: UiObject, filter: Filter, limit: Int, isRefresh:Boolean): ArrayList<UiObject> {
        val result = ArrayList<UiObject>()
        val queue = ArrayDeque<UiObject>()
        // Added by ibozo - 2024/10/31 >
        if(isRefresh)
            root.refresh()
        //<
        queue.add(root)
        while (!queue.isEmpty()) {
            val top = queue.poll()
            // Added by ibozo - 2024/10/31 >
            if(isRefresh)
                top.refresh()
            //<
            val isTarget = filter.filter(top)
            if (isTarget) {
                result.add(top)
                if (result.size > limit) {
                    return result
                }
            }
            for (i in 0 until top.childCount) {
                queue.add(top.child(i) ?: continue)
            }
            if (!isTarget && top !== root) {
                top.recycle()
            }
        }
        return result
    }
}