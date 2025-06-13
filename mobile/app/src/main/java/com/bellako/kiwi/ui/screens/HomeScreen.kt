package com.bellako.kiwi.ui.screens

import com.bellako.kiwi.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.services.common.CommonTestTags
import com.bellako.kiwi.ui.components.Kiwi_Button
import com.bellako.kiwi.ui.components.Kiwi_H1
import com.bellako.kiwi.ui.components.Kiwi_Image
import com.bellako.kiwi.ui.components.Kiwi_Spacer
import com.bellako.kiwi.ui.components.Kiwi_TextArguments
import com.bellako.kiwi.ui.theme.KiwiTheme

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(White)
            .testTag(CommonTestTags.HOME_SCREEN),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Kiwi_H1(Kiwi_TextArguments(
            "HOME",
            bold = true
        ))
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    KiwiTheme {
        HomeScreen()
    }
}