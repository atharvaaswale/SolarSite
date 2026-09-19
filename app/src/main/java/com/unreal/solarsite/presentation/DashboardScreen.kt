package com.unreal.solarsite.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unreal.solarsite.R

@Composable
fun Dashboard(){
    Column (
        modifier = Modifier.padding(horizontal = 5.dp)
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

        //
    }


}

@Composable
@Preview(showSystemUi = true)
fun showDashboard() {
    Dashboard()
}