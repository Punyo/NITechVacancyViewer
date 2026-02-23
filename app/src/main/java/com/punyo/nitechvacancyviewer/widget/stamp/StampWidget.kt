package com.punyo.nitechvacancyviewer.widget.stamp

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.LocalSize
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.punyo.nitechvacancyviewer.R

/**
 * 打刻ウィジェット
 */
class StampWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        provideContent {
            StampWidgetInternal(
                buttonText = context.getString(R.string.WIDGET_STAMP_TEXT),
            )
        }
    }
}

@Composable
private fun StampWidgetInternal(
    buttonText: String,
    modifier: GlanceModifier = GlanceModifier,
) {
    val size = LocalSize.current
    val fontSize = (size.height.value * 0.3f).sp

    GlanceTheme {
        Box(
            modifier =
                modifier
                    .fillMaxSize()
                    .background(GlanceTheme.colors.primary)
                    .cornerRadius(8.dp)
                    .clickable(actionRunCallback<OnWidgetTapCallback>()),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = buttonText,
                style =
                    TextStyle(
                        color = GlanceTheme.colors.onPrimary,
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 48, heightDp = 48)
@Composable
fun StampWidgetPreview() {
    StampWidgetInternal(
        buttonText = "打刻",
    )
}