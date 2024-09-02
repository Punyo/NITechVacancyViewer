package com.punyo.nitechroomvacancyviewer.ui.component

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.punyo.nitechroomvacancyviewer.R
import com.punyo.nitechroomvacancyviewer.data.building.BuildingRepository
import com.punyo.nitechroomvacancyviewer.data.building.model.Building
import com.punyo.nitechroomvacancyviewer.data.building.source.BuildingLocalDatasource
import com.punyo.nitechroomvacancyviewer.ui.model.VacancyComponentViewModel

@SuppressLint("DiscouragedApi")
@Composable
fun VacancyComponent(
    modifier: Modifier = Modifier,
    viewModel: VacancyComponentViewModel = VacancyComponentViewModel(
        BuildingRepository(BuildingLocalDatasource())
    )
) {
    val context = LocalContext.current
    val currentState = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.loadBuildings(context.resources.openRawResource(R.raw.buildings))
    }
    if(currentState.buildings == null) {
        LoadingProgressIndicatorComponent()
    }
    currentState.buildings?.let { buildings ->
        LazyVerticalGrid(modifier = modifier.padding(8.dp), columns = GridCells.Fixed(2)) {
            items(buildings.size) { index ->
                val buildingData = buildings[index]
                val numberOfVacantRooms =
                    viewModel.getNumberOfVacantRoom(buildingData.buildingRoomPrincipalNames)
                BuildingsCard(
                    modifier = Modifier.padding(8.dp),
                    buildingName = context.resources.getIdentifier(
                        buildingData.buildingNameResourceName,
                        "string",
                        context.packageName
                    ),
                    buildingImage = context.resources.getIdentifier(
                        buildingData.buildingImageResourceName,
                        "drawable",
                        context.packageName
                    ),
                    numberOfVacantRooms = numberOfVacantRooms,
                    numberOfRooms = buildingData.buildingRoomPrincipalNames.size.toUInt(),
                    onClick = { viewModel.onBuildingSelected(index) }
                )
            }
        }
    }
}

@Composable
fun BuildingsCard(
    modifier: Modifier = Modifier,
    @StringRes buildingName: Int,
    @DrawableRes buildingImage: Int,
    numberOfVacantRooms: UInt,
    numberOfRooms: UInt,
    onClick : () -> Unit
) {
    ElevatedCard(modifier = modifier.clickable { onClick() }, elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(MaterialTheme.shapes.medium),
            painter = painterResource(id = buildingImage),
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(id = buildingName)
        )
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            text = stringResource(id = buildingName),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            text = stringResource(id = R.string.UI_CARD_TEXT_VACANTANDMAXROOMS).format(
                numberOfVacantRooms.toInt(),
                numberOfRooms.toInt()
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
fun CardPreview() {
}
