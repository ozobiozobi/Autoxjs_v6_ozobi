package com.stardust.automator.search

import com.stardust.automator.UiObject
import com.stardust.automator.filter.Filter
import java.util.LinkedList

/**
 * Created by Stardust on 2017/3/9.
 */

object DFS : SearchAlgorithm {

    override fun search(root: UiObject, filter: Filter, limit: Int,isRefresh:Boolean): ArrayList<UiObject> {
        val result = ArrayList<UiObject>()
        val stack = LinkedList<UiObject>()
        stack.push(root)
        // Added by ibozo - 2024/10/31 >
        if(isRefresh)
            root.refresh()
        //
        while (stack.isNotEmpty()) {
            val parent = stack.pop()
            // Added by ibozo - 2024/10/31 >
            if(isRefresh)
                parent.refresh()
            //
            for (i in parent.childCount - 1 downTo 0) {
                val child = parent.child(i) ?: continue
                stack.push(child)
            }
            if (filter.filter(parent)) {
                result.add(parent)
                if (result.size >= limit) {
                    break
                }
            } else {
                if (parent !== root) {
                    parent.recycle()
                }
            }
        }
        return result
    }
}
