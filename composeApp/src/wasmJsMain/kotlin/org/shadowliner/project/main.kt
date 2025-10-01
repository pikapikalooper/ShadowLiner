package org.shadowliner.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        RemoveLoaderOnce()
        App()
    }
}

@Composable
private fun RemoveLoaderOnce() {
    LaunchedEffect(Unit) {
        document.getElementById("app-loader")
            ?.let { it.parentElement?.removeChild(it) }
    }
}