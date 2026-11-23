package com.aistudio.bitcoinledger.btcxyz.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Formats an 11-digit CPF string (e.g. "18028812345" -> "180.288.123-45")
 * purely visually, preserving exact cursor offset mapping so digits never scramble while typing.
 */
class CpfVisualTransformation : VisualTransformation {
  override fun filter(text: AnnotatedString): TransformedText {
    val digits = text.text.take(11)
    val out = buildString {
      digits.forEachIndexed { index, ch ->
        append(ch)
        if (index == 2 || index == 5) {
          if (index < digits.length - 1) append('.')
        } else if (index == 8) {
          if (index < digits.length - 1) append('-')
        }
      }
    }

    val offsetMapping = object : OffsetMapping {
      override fun originalToTransformed(offset: Int): Int {
        val clamped = offset.coerceIn(0, digits.length)
        var extra = 0
        if (clamped > 3) extra++
        if (clamped > 6) extra++
        if (clamped > 9) extra++
        return (clamped + extra).coerceAtMost(out.length)
      }

      override fun transformedToOriginal(offset: Int): Int {
        val clamped = offset.coerceIn(0, out.length)
        var digitsCount = 0
        for (i in 0 until clamped) {
          if (out[i].isDigit()) digitsCount++
        }
        return digitsCount.coerceAtMost(digits.length)
      }
    }

    return TransformedText(AnnotatedString(out), offsetMapping)
  }
}

fun formatCpfDigits(digits: String): String {
  val clean = digits.filter { it.isDigit() }.take(11)
  return buildString {
    clean.forEachIndexed { index, c ->
      if (index == 3 || index == 6) append('.')
      if (index == 9) append('-')
      append(c)
    }
  }
}
