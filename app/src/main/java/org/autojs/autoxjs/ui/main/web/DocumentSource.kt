package org.autojs.autoxjs.ui.main.web

enum class DocumentSource(val sourceName: String, val uri: String, val isLocal: Boolean = false) {
    // Modified by ozobi - 2025/01/10 > 添加 v2 本地、在线文档
    DOC_V1_LOCAL("v1本地文档", "docs/v1", true),
    DOC_V2("v2在线文档", "https://autoxjs.dayudada.com/"),
    DOC_V2_LOCAL("v2本地文档", "docs/v2", true)
    // <
}