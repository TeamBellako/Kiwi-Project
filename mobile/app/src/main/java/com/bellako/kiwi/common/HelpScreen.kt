package com.bellako.kiwi.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.ui.components.Kiwi_Button
import com.bellako.kiwi.ui.components.Kiwi_Spacer
import com.bellako.kiwi.ui.theme.KiwiTheme

@Composable
fun HelpScreen(
    onBackToHome: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Need Help?",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "If you have any questions or need support, feel free to reach out to us at:",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "bellakotech@gmail.com",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clickable() {
                    openEmailClient(
                        context,
                        "bellakotech@gmail.com"
                    )
                }
                .padding(bottom = 24.dp)
                .testTag(CommonTestTags.HELP_SCREEN)
        )

        Kiwi_Button(
            "Back To Home",
            onBackToHome
        )
    }
}

private fun openEmailClient(context: Context, email: String) {
    val subject = "Help Request"
    val body = "Hello,\n\nI need assistance with..."

    val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
    emailIntent.putExtra(Intent.EXTRA_TEXT, body)

    try {
        context.startActivity(emailIntent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Preview
@Composable
fun HelpScreenPreview() {
    KiwiTheme {
        HelpScreen() {}
    }
}
