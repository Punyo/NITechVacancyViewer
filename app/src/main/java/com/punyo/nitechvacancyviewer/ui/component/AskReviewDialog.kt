package com.punyo.nitechvacancyviewer.ui.component

import android.app.Activity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.play.core.review.ReviewManagerFactory
import com.punyo.nitechvacancyviewer.R
import com.punyo.nitechvacancyviewer.theme.AppTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun AskReviewDialog(onReviewFlowLaunched: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onReviewFlowLaunched,
        title = { Text(text = stringResource(id = R.string.UI_DIALOG_REVIEW_REQUEST_TITLE)) },
        text = { Text(text = stringResource(id = R.string.UI_DIALOG_REVIEW_REQUEST_TEXT)) },
        confirmButton = {
            TextButton(
                onClick = {
                    coroutineScope.launch {
                        val activity = context as? Activity
                        if (activity != null) {
                            try {
                                val manager = ReviewManagerFactory.create(context)
                                val reviewInfo = manager.requestReviewFlow().await()
                                manager.launchReviewFlow(activity, reviewInfo).await()
                            } catch (_: Exception) {
                                // レビューフローは必須ではないため、エラーは無視する
                            } finally {
                                onReviewFlowLaunched()
                            }
                        } else {
                            onReviewFlowLaunched()
                        }
                    }
                },
            ) {
                Text(text = stringResource(id = R.string.UI_DIALOG_REVIEW_REQUEST_BUTTON_CONFIRM))
            }
        },
        dismissButton = {
            TextButton(onClick = onReviewFlowLaunched) {
                Text(text = stringResource(id = R.string.UI_TEXT_CANCEL))
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun AskReviewDialogPreview() {
    AppTheme {
        AskReviewDialog(onReviewFlowLaunched = {})
    }
}