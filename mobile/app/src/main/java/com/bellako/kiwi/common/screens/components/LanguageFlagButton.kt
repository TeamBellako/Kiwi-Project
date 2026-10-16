package com.bellako.kiwi.common.screens.components

import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.sp
import com.bellako.kiwi.common.AppLanguageManager
import com.bellako.kiwi.common.ResourceProvider

@Composable
fun LanguageFlagButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val language by AppLanguageManager.language.collectAsState()

    IconButton(
        onClick = { AppLanguageManager.cycle(context) },
        modifier =
            modifier.semantics {
                contentDescription = ResourceProvider.stringResource("change_language")
            },
    ) {
        Text(
            text = language.flag,
            fontSize = 24.sp,
        )
    }
}
