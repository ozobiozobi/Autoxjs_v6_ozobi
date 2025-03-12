package org.autojs.autoxjs.ui.main.web

enum class DocumentSource(val sourceName: String, val uri: String, val isLocal: Boolean = false) {
    
    DOC_V1_LOCAL("v1本地文档", "docs/v1", true),
    DOC_V2("v2在线文档", "https://autoxjs.dayudada.com/"),
    DOC_V2_LOCAL("v2本地文档", "docs/v2", true)
    // <
}