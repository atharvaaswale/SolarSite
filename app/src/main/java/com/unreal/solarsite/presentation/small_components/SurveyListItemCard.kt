package com.unreal.solarsite.presentation.small_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unreal.solarsite.R
import com.unreal.solarsite.ui.theme.BlueGrey
import com.unreal.solarsite.ui.theme.BlueText
import com.unreal.solarsite.ui.theme.DarkBlueContainer
import com.unreal.solarsite.ui.theme.DarkBlueText
import com.unreal.solarsite.ui.theme.DullBlue
import com.unreal.solarsite.ui.theme.DullOrange
import com.unreal.solarsite.ui.theme.GreyText
import com.unreal.solarsite.ui.theme.LightBlueContainer
import com.unreal.solarsite.ui.theme.OrangeContainer
import com.unreal.solarsite.ui.theme.OrangeText

@Composable
fun SurveyListItemCard(
    title: String,
    statusText: String,
    icon: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = LightBlueContainer,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = "trailingIcon",
                    modifier = Modifier
                        .size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.W700,
                    color = DarkBlueText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                //Text(text = title, fontWeight = FontWeight.W700)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = when (statusText) {
                                "Draft" -> LightBlueContainer
                                "Synced" -> DarkBlueContainer
                                "Ready for review" -> OrangeContainer
                                else -> LightBlueContainer
                            },
                            shape = RoundedCornerShape(50.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                color = when (statusText) {
                                    "Draft" -> BlueGrey
                                    "Synced" -> DullBlue
                                    "Ready for review" -> DullOrange
                                    else -> BlueGrey
                                },
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = statusText,
                        fontWeight = FontWeight.W600,
                        color = when (statusText) {
                            "Draft" -> GreyText
                            "Synced" -> BlueText
                            "Ready for review" -> OrangeText
                            else -> GreyText
                        }
                    )
                }
            }


        }
    }
}

@Composable
@Preview
fun ShowSurveyListItemCard() {
    SurveyListItemCard(
        "Northstar Manufacturing",
        "Ready for review",
        R.drawable.ic_factory
    )
}