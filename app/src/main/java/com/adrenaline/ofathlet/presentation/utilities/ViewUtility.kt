package com.adrenaline.ofathlet.presentation.utilities

import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment

object ViewUtility {

    var orientation = 0
    private var slotFieldHeight = 0
    var isHeightCorrect = false

    fun updateFieldHeight(
        fieldForSlots: ImageView,
        vto: ViewTreeObserver,
    ) {
        vto.addOnGlobalLayoutListener {
            slotFieldHeight = fieldForSlots.height
            if (slotFieldHeight != 0) isHeightCorrect = true
        }
    }

    fun getFieldHeight(): Int = slotFieldHeight

    fun getSlotHeight(): Int = slotFieldHeight / 3

    fun hideSoftKeyboard(view: View, fragment: Fragment) {
        val inputMethodManager =
            fragment.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun makeTextAutoSize(textView: TextView) {
        TextViewCompat.setAutoSizeTextTypeWithDefaults(
            textView,
            TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
        )
    }

}