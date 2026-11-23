package com.aistudio.bitcoinledger.btcxyz.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LedgerDarkColorScheme =
  darkColorScheme(
    primary = LedgerGreen,
    onPrimary = LedgerDarkBackground,
    secondary = LedgerGreenLight,
    onSecondary = LedgerDarkBackground,
    background = LedgerDarkBackground,
    onBackground = LedgerTextPrimary,
    surface = LedgerSurface,
    onSurface = LedgerTextPrimary,
    error = LedgerRed,
    onError = LedgerTextPrimary,
    outline = LedgerBorder
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = LedgerDarkColorScheme,
    typography = Typography,
    content = content
  )
}
