package org.autojs.autoxjs.ui.floating.layoutinspector

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import org.autojs.autoxjs.R

class OzobiLayoutHierarchyView : AppCompatActivity() {
    private lateinit var nestedScrollView:NestedScrollView
    private lateinit var dataContainer:LinearLayout
    private lateinit var expandableLayout:LinearLayout
    private lateinit var expandButton:Button
    private lateinit var exitButton: Button
    private lateinit var hideOrShowButton:Button
    private lateinit var toBoundsButton: Button

    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nested_scrollview)

        nestedScrollView = findViewById(R.id.nested_scrollView)
        dataContainer = findViewById(R.id.nested_scrollView_container)
        expandableLayout = findViewById(R.id.nested_scrollView_hidden)
        expandButton = findViewById(R.id.nested_scrollView_expand_btn)
        exitButton = findViewById(R.id.nested_scrollView_exit_btn)
        hideOrShowButton = findViewById(R.id.nested_scrollView_hide_show_btn)
        toBoundsButton = findViewById(R.id.nested_scrollView_to_bounds_btn)

    }
    fun expandAllChild(){

    }
    fun toBounds(){

    }
    fun hideOrShow(){

    }
    fun exit(){

    }
}