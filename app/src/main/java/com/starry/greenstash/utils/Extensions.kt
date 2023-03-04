package com.starry.greenstash.utils

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

fun String.toToast(context: Context, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, this, length).show()
}

fun String.validateAmount() =
    this.isNotEmpty() && this.isNotBlank() && this != "0" && this != "0.0"
            && !this.startsWith("0.00") && !this.endsWith(".")