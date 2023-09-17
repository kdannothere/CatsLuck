package com.adrenaline.ofathlet.presentation.utilities

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

object ViewUtility {

    fun hideSoftKeyboard(view: View, fragment: Fragment) {
        val inputMethodManager =
            fragment.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}