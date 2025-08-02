package org.autojs.autoxjs.ui.main.fileitem

import com.ozobi.MixedTextComparator

class FileItemComparator: MixedTextComparator() {
    fun compare(a: FileItem, b: FileItem): Int {
        return super.compare(a.name, b.name)
    }
}