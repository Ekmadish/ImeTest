package com.ai.imetest

import androidx.appcompat.app.AppCompatActivity
import android.inputmethodservice.KeyboardView
import android.widget.EditText
import android.widget.LinearLayout
import com.ai.imetest.KeyboardUtil
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import com.ai.imetest.R
import android.view.View.OnTouchListener
import android.view.MotionEvent
import android.view.View

class MainActivity : AppCompatActivity() {
    private var kv_keyboard: KeyboardView? = null
    private var editText: EditText? = null
    private var ll_keyboard_father: LinearLayout? = null
    private var keyboardUtil: KeyboardUtil? = null
    private var rl_hide_keyboard: RelativeLayout? = null
    private var father: ConstraintLayout? = null
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        kv_keyboard = findViewById(R.id.kv_keyboard)
        editText = findViewById(R.id.edittext)
        ll_keyboard_father = findViewById(R.id.ll_keyboard_father)
//        rl_hide_keyboard = findViewById(R.id.rl_hide_keyboard)
//        father = findViewById(R.id.view_root)
        keyboardUtil = KeyboardUtil(this, editText!!, kv_keyboard!!, ll_keyboard_father!!, true)
        keyboardUtil!!.forbidSoftInputMethod()
        keyboardUtil!!.hideKeyboard()


        findViewById<EditText>(R.id.edittext).setOnTouchListener(OnTouchListener { view, motionEvent ->
            keyboardUtil!!.showKeyboard()
            false
        })

        findViewById<RelativeLayout>(R.id.rl_hide_keyboard).setOnClickListener(View.OnClickListener { keyboardUtil!!.hideKeyboard() })
        findViewById<ConstraintLayout>(R.id.view_root).setOnClickListener(View.OnClickListener { if (keyboardUtil!!.isShow) keyboardUtil!!.hideKeyboard() })
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event)
    }

//    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
//        return if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Log.e("TAG", "$keyCode------------")
//            if (keyboardUtil!!.isShow) {
//                keyboardUtil!!.hideKeyboard()
//                false
//            } else {
//                super.onKeyDown(keyCode, event)
//            }
//        } else {
//            super.onKeyDown(keyCode, event)
//        }
//    }
}