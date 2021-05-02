package com.liviolopez.citycrimes.utils.extensions

import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.liviolopez.citycrimes.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

fun View.setGone() { if(visibility != View.GONE) visibility = View.GONE }
fun View.setVisible() { if(visibility != View.VISIBLE) visibility = View.VISIBLE }

inline fun <T : View> T.visibleIf(isTrue: (T) -> Boolean) {
    if (isTrue(this))
        this.setVisible()
    else
        this.setGone()
}

fun View.showSnackBar(
    msg: String,
    duration: Int = Snackbar.LENGTH_LONG,
) {
    Snackbar.make(this, msg, duration).show()
}

suspend fun <T> TextInputLayout.setOptions(flow: Flow<List<T>>, show: (T) -> String, currentValIf: (T) -> Boolean, onClick: (T) -> Unit ) {
    val context = this.context
    flow.collect { values ->
        val adapter = ArrayAdapter(context, R.layout.text_item, values.map { show(it) })
        (this.editText as? AutoCompleteTextView)?.apply {
            setAdapter(adapter)
            values.firstOrNull { currentValIf(it) }?.let {
                setText(show(it))
            }
            setOnItemClickListener { _, _, position, _ ->
                onClick(values[position])
            }
        }
    }
}