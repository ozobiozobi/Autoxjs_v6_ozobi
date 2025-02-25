package org.autojs.autoxjs.ui.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.autojs.autoxjs.R
import org.autojs.autoxjs.ui.main.MainActivity

/**
 * Created by Stardust on 2017/7/7.
 */
class SplashActivity : ComponentActivity() {
    private var mAlreadyEnterNextActivity = false
    private var mPaused = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        lifecycleScope.launch {
            delay(INIT_TIMEOUT)
            enterNextActivity()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
         if (hasFocus) {
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                 window.attributes = window.attributes.apply {
                     layoutInDisplayCutoutMode =
                         WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                 }
             }

             WindowCompat.setDecorFitsSystemWindows(window, false)
             val controller = ViewCompat.getWindowInsetsController(window.decorView)
             controller?.hide(WindowInsetsCompat.Type.systemBars())
             controller?.systemBarsBehavior =
                 WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

         }
    }

    private fun init() {
        setContent {
            val systemUiController = rememberSystemUiController()
            val color=MaterialTheme.colors.background
            SideEffect {
                systemUiController.setNavigationBarColor(color = color, darkIcons = true)
            }
            Column(Modifier.fillMaxSize()) {
                Spacer(
                    modifier = Modifier
                        .windowInsetsTopHeight(WindowInsets.statusBars)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(), contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(R.drawable.autojs_logo1),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                    )
                }
                Text(
                    text = stringResource(id = R.string.powered_by_autojs),
                    color = Color(0xdd000000),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "Modified by Ozobi",
                    color = Color(0xf69C7CFF),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(0.dp,0.dp,0.dp,10.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(
                    modifier = Modifier
                        .windowInsetsBottomHeight(WindowInsets.navigationBars)
                )
            }

        }
    }

    override fun onPause() {
        super.onPause()
        mPaused = true
    }

    override fun onResume() {
        super.onResume()
        if (mPaused) {
            mPaused = false
            enterNextActivity()
        }
    }

    private fun enterNextActivity() {
        if (mAlreadyEnterNextActivity) return
        if (mPaused) {
            return
        }
        mAlreadyEnterNextActivity = true
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        private val LOG_TAG = SplashActivity::class.java.simpleName
        private const val INIT_TIMEOUT: Long = 800
    }
}