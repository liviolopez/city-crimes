package com.liviolopez.citycrimes.utils.extensions

import android.widget.NumberPicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

fun NumberPicker.getValueChangeStateFlow(default: Int): StateFlow<Int> {
    val intValue = MutableStateFlow(default)

    setOnValueChangedListener { _, _, newVal ->
        intValue.value = newVal
    }

    return intValue
}

fun <T> CoroutineScope.collectFlow(flow: Flow<T>, body: suspend (T) -> Unit) {
    flow.onEach { body(it) }
        .launchIn(this)
}