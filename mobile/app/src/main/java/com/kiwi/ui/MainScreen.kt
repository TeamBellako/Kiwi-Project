package com.kiwi.ui

import HelloCard
import HelloViewModel
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kiwi.network.RetrofitClient

@Composable
fun MainScreen() {
    HelloCard(viewModel = HelloViewModel(RetrofitClient.apiService), id = 1)
}
