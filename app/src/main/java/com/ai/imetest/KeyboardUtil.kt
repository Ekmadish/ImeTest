package com.ai.imetest

import android.app.Activity
import android.content.Context
import android.widget.EditText
import android.inputmethodservice.KeyboardView
import android.widget.LinearLayout
import android.inputmethodservice.Keyboard
import android.text.Editable
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener
import android.os.Build
import android.text.InputType
import android.view.View
import com.ai.imetest.KeyboardUtil
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import com.ai.imetest.R
import java.lang.IllegalArgumentException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class KeyboardUtil(
    private val mActivity: Activity,
    edit: EditText,
    keyboard_view: KeyboardView,
    private val mLinearLayout: LinearLayout,
    isNumber: Boolean
) {
    private val mContext: Context
    private lateinit var mKeyboardView: KeyboardView
    private lateinit var  mEdit: EditText

    /**
     * 英文键盘
     */
    private lateinit var english_keyboard: Keyboard

    /**
     * 数字键盘
     */
    private lateinit var number_keyboard: Keyboard

    /**
     * 符号键盘
     */
    private lateinit var symbol_keyboard: Keyboard
    /**
     * 软键盘切换判断
     */
    //    private boolean isChange = true;
    /**
     * 字母大小写切换
     */
    private var isCapital = false

    /**
     * 默认键盘 true english   false number
     */
    private val isNumber: Boolean
    private lateinit var editable: Editable
    private val listener: OnKeyboardActionListener = object : OnKeyboardActionListener {
        override fun swipeUp() {}
        override fun swipeRight() {}
        override fun swipeLeft() {}
        override fun swipeDown() {}
        override fun onText(text: CharSequence) {
            val start = mEdit  .selectionStart
            editable!!.insert(start, text)
        }

        override fun onRelease(primaryCode: Int) {}
        override fun onPress(primaryCode: Int) {}
        override fun onKey(primaryCode: Int, keyCodes: IntArray) {
            val start = mEdit.selectionStart
            when (primaryCode) {
                Keyboard.KEYCODE_SHIFT -> {
                    shiftEnglish()
                    mKeyboardView.keyboard = english_keyboard
                }
                Keyboard.KEYCODE_DELETE -> if (editable != null && editable.length > 0 && start > 0) {
                    editable.delete(start - 1, start)
                }
                KEYCODE_ABC -> {
                    shiftEnglish()
                    mKeyboardView.keyboard = english_keyboard
                }
                KEYCODE_NUM -> mKeyboardView.keyboard = number_keyboard
                KEYCODE_SYMBOL -> mKeyboardView.keyboard = symbol_keyboard
                else -> editable!!.insert(start, Character.toString(primaryCode.toChar()))
            }
        }
    }

    /**
     * 英文键盘大小写切换
     */
    private fun shiftEnglish() {
        val keyList = english_keyboard.keys
        for (key in keyList) {
            if (key.label != null && isKey(key.label.toString())) {
                if (isCapital) {
                    key.label = key.label.toString().toLowerCase()
                    key.codes[0] = key.codes[0] + 32
                } else {
                    key.label = key.label.toString().toUpperCase()
                    key.codes[0] = key.codes[0] - 32
                }
            }
        }
        isCapital = !isCapital
    }

    /**
     * 判断此key是否正确，且存在
     *
     * @param key
     * @return
     */
    private fun isKey(key: String): Boolean {
        val lowercase = "abcdefghijklmnopqrstuvwxyz"
        return if (lowercase.indexOf(key.toLowerCase()) > -1) {
            true
        } else false
    }

    /**
     * 软键盘展示状态
     */
    val isShow: Boolean
        get() = mLinearLayout.visibility == View.VISIBLE

    /**
     * 软键盘展示
     */
    fun showKeyboard() {
        if (!isShow) {
            mLinearLayout.visibility = View.VISIBLE
        }
    }

    /**
     * 软键盘隐藏
     */
    fun hideKeyboard() {
        if (isShow) {
            mLinearLayout.visibility = View.GONE
            mLinearLayout.animation = moveToViewBottom()
        }
    }

    /**
     * 禁掉系统软键盘
     */
    fun forbidSoftInputMethod() {
        mActivity.window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )
        val currentVersion = Build.VERSION.SDK_INT
        var methodName: String? = null
        if (currentVersion >= 16) {
            // 4.2
            methodName = "setShowSoftInputOnFocus"
        } else if (currentVersion >= 14) {
            // 4.0
            methodName = "setSoftInputShownOnFocus"
        }
        if (methodName == null) {
            mEdit.inputType = InputType.TYPE_NULL
        } else {
            val cls = EditText::class.java
            val setShowSoftInputOnFocus: Method
            try {
                setShowSoftInputOnFocus = cls.getMethod(
                    methodName,
                    Boolean::class.javaPrimitiveType
                )
                setShowSoftInputOnFocus.isAccessible = true
                setShowSoftInputOnFocus.invoke(mEdit, false)
            } catch (e: NoSuchMethodException) {
                mEdit.inputType = InputType.TYPE_NULL
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val KEYCODE_ABC = 9001
        private const val KEYCODE_SYMBOL = 9002
        private const val KEYCODE_NUM = 9003

        /**
         * 从控件所在位置移动到控件的底部
         *
         * @return
         */
        fun moveToViewBottom(): Animation {
            val mHiddenAction = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f
            )
            mHiddenAction.duration = 300
            return mHiddenAction
        }
    }

    init {
        mContext = mActivity
        mEdit = edit
        this.isNumber = isNumber
        english_keyboard = Keyboard(mContext, R.xml.symbol_abc)
        number_keyboard = Keyboard(mContext, R.xml.symbol_num)
        symbol_keyboard = Keyboard(mContext, R.xml.symbol_symbol)
        mKeyboardView = keyboard_view
        if (isNumber) {
            mKeyboardView.keyboard = english_keyboard
        } else {
            mKeyboardView.keyboard = number_keyboard
        }
        editable = mEdit.text
        mKeyboardView.isEnabled = true
        mKeyboardView.isPreviewEnabled = false
        mKeyboardView.setOnKeyboardActionListener(listener)
    }
}