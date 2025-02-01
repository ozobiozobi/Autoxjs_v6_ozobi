package org.autojs.autoxjs.ui.voiceassistant

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text

// Created by ozobi - 2025/01/03

class SettingsActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContent {
            Text("设置页面")
        }
    }
}
