package com.unreal.solarsite.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unreal.solarsite.R
import com.unreal.solarsite.presentation.small_components.SurveyListItemCard
import com.unreal.solarsite.ui.theme.DarkBlueText

@Composable
fun Dashboard(context: Context){
    data class FacilityItem(
        val id: String,
        val name: String,
        val status: String,          // "Draft" | "Synced" | "Ready for review"
        val iconRes: Int             // replace with your actual drawable resources
    )
    val facilityList = listOf(
        FacilityItem(
            id = "1",
            name = "Northstar Manufacturing",
            status = "Draft",
            iconRes = R.drawable.ic_factory          // factory icon
        ),
        FacilityItem(
            id = "2",
            name = "Greenfield Warehouse",
            status = "Synced",
            iconRes = R.drawable.ic_warehouse        // warehouse icon
        ),
        FacilityItem(
            id = "3",
            name = "Riverside Solar",
            status = "Ready for review",
            iconRes = R.drawable.ic_nav_sites            // solar panel icon
        ),
        FacilityItem(
            id = "4",
            name = "Apex Facility",
            status = "Draft",
            iconRes = R.drawable.ic_appartments         // building icon
        ),
        // extra items
        FacilityItem(
            id = "5",
            name = "Horizon Logistics",
            status = "Synced",
            iconRes = R.drawable.ic_warehouse
        ),
        FacilityItem(
            id = "6",
            name = "Summit Power Plant",
            status = "Ready for review",
            iconRes = R.drawable.ic_nav_sites
        )
    )

    Column (
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {
        //Cached sites - Image Cards
        Box(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .background(Color.White)
                .clip(RoundedCornerShape(18.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.solar_site_1),
                contentDescription = "Sample Image",
                contentScale = ContentScale.Crop, // Ensures the image fills the space cleanly
                modifier = Modifier.fillMaxSize()
            )

            Text(
                text = "4 active surveys",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                fontWeight = FontWeight.W500
            )
        }

        //New survey button
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF29B05E),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(top = 10.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_add_circle),
                contentDescription = "circle add",
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 5.dp)
            )
            Text(
                text = "New Survey",
                fontSize = 18.sp,
                fontWeight = FontWeight.W700
            )
        }

        // Recent Surveys List
        Text(
            text = "Recent Surveys",
            color = DarkBlueText,
            fontWeight = FontWeight.W700,
            fontSize = 18.sp,
            modifier = Modifier
                .padding(vertical = 10.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(facilityList, key = {it.id}) { item ->
                SurveyListItemCard(
                    item.name,
                    item.status,
                    item.iconRes,
                    { Toast.makeText(context, item.name, Toast.LENGTH_SHORT)}
                )
            }
        }
    }


}

@Composable
@Preview(showSystemUi = true)
fun showDashboard() {
    Dashboard(LocalContext.current)
}