package com.punyo.nitechvacancyviewer.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.punyo.nitechvacancyviewer.R

@Composable
fun LastUpdateTimeTextComponent(modifier: Modifier = Modifier, lastUpdateTimeString: String) {
    Text(
        modifier = modifier.padding(8.dp),
        text = stringResource(id = R.string.UI_LAZYVERTICALGRID_TEXT_LASTUPDATETIME).format(
            lastUpdateTimeString
        ),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
