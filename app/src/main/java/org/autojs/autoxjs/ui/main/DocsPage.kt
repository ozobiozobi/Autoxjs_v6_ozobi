package org.autojs.autoxjs.ui.main

import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.autojs.autoxjs.ui.main.web.EditorAppManager.Companion.loadHomeDocument
import org.autojs.autoxjs.ui.widget.SwipeRefreshWebView

const val TAG = "DocsPage"
const val DocumentSourceKEY = "DocumentSource"

@Composable
fun DocsPage(onInitDocsWebView:(WebView)->Unit) {
    EditorAppManagerScreen(onInitDocsWebView)
}

@Composable
fun EditorAppManagerScreen(onInitDocsWebView:(WebView)->Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // 可以在这里添加顶部导航栏
        }
    ) { padding ->
        AndroidView(
            modifier = Modifier.fillMaxSize().padding(padding),
            factory = { context ->
                SwipeRefreshWebView(context).apply {
                    onInitDocsWebView(this.webView)
                    loadHomeDocument(this.webView)
                }
            },
//            update = { swipeRefreshWebView ->
//                // 在这里更新 SwipeRefreshWebView 的状态
//            }
        )
    }
}

