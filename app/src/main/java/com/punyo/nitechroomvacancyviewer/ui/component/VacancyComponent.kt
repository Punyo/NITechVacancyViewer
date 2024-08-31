package com.punyo.nitechroomvacancyviewer.ui.component

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.punyo.nitechroomvacancyviewer.R
import com.punyo.nitechroomvacancyviewer.data.BuildingData
import com.punyo.nitechroomvacancyviewer.ui.model.VacancyComponentViewModel

@SuppressLint("DiscouragedApi")
@Composable
fun VacancyComponent(
    modifier: Modifier = Modifier,
    buildingsData: Array<BuildingData>,
    viewModel: VacancyComponentViewModel = viewModel()
) {
    val context = LocalContext.current
    LazyVerticalGrid(modifier = modifier.padding(8.dp), columns = GridCells.Fixed(2)) {
        items(buildingsData.size) { index ->
            val buildingData = buildingsData[index]
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
                numberOfRooms = buildingData.buildingRoomPrincipalNames.size.toUInt()
            )
        }
    }
}

@Composable
fun BuildingsCard(
    modifier: Modifier = Modifier,
    @StringRes buildingName: Int,
    @DrawableRes buildingImage: Int,
    numberOfVacantRooms: UInt,
    numberOfRooms: UInt
) {
    Card(modifier = modifier) {
        Image(
            painter = painterResource(id = buildingImage),
            contentScale = ContentScale.FillHeight,
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
    VacancyComponent(
        buildingsData = arrayOf(
            BuildingData(
                buildingNameResourceName = "BUILDING_1",
                buildingImageResourceName = "ic_launcher_foreground",
                buildingRoomPrincipalNames = arrayOf("Room A1", "Room A2", "Room A3")
            ),
            BuildingData(
                buildingNameResourceName = "BUILDING_2",
                buildingImageResourceName = "ic_launcher_foreground",
                buildingRoomPrincipalNames = arrayOf("Room B1", "Room B2", "Room B3")
            ),
            BuildingData(
                buildingNameResourceName = "BUILDING_1",
                buildingImageResourceName = "ic_launcher_foreground",
                buildingRoomPrincipalNames = arrayOf("Room A1", "Room A2", "Room A3")
            ),
            BuildingData(
                buildingNameResourceName = "BUILDING_2",
                buildingImageResourceName = "ic_launcher_foreground",
                buildingRoomPrincipalNames = arrayOf("Room B1", "Room B2", "Room B3")
            )
        )
    )
}
