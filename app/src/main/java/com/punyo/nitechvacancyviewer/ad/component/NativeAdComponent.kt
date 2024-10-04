package com.punyo.nitechvacancyviewer.ad.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.ads.nativead.NativeAd
import com.punyo.nitechvacancyviewer.R
import com.punyo.nitechvacancyviewer.ad.AdConstants
import com.punyo.nitechvacancyviewer.ad.NativeAdLoader
import com.punyo.nitechvacancyviewer.ui.theme.AppTheme
import kotlinx.coroutines.delay

@Composable
fun NativeAdComponent(modifier: Modifier = Modifier, adUnitId: String) {
    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    LaunchedEffect(key1 = Unit) {
        while (true) {
            nativeAd = NativeAdLoader.getAd(context, adUnitId)
            delay(AdConstants.NATIVE_AD_REFRESH_INTERVAL_MILLISECOND)
        }
    }
    nativeAd?.let { loadedAd ->
        NativeAdView(ad = loadedAd) { ad, contentView ->
            VacancyComponentNativeAdImpl(
                modifier = modifier,
                image = rememberAsyncImagePainter(model = ad.icon?.drawable),
                headline = ad.headline ?: "",
                body = ad.body ?: "",
                callToAction = ad.callToAction ?: "",
                onClick = {
                    contentView.performClick()
                }
            )
        }
    }
}

@Composable
private fun VacancyComponentNativeAdImpl(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    image: Painter,
    headline: String,
    body: String,
    callToAction: String
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 150.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)) {
            Image(
                painter = painterResource(id = R.drawable.ad_badge),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        Row {
            Image(
                modifier = modifier
                    .fillMaxHeight()
                    .widthIn(max = 150.dp)
                    .heightIn(max = 150.dp)
                    .clip(MaterialTheme.shapes.medium),
                painter = image,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    text = headline,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    text = body,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3
                )
                Button(
                    onClick = { onClick() },
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = callToAction)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun VacancyComponentNativeAdPreview() {
    AppTheme {
        VacancyComponentNativeAdImpl(
            image = painterResource(id = R.drawable.ic_launcher_foreground),
            headline = "Headline",
            body = "ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ",
            callToAction = "Call to action"
        )
    }
}
