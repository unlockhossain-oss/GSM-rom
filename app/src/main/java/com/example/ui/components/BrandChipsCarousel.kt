package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrimsonAccent
import com.example.ui.theme.DarkCrimson
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.SapphireBlue
import com.example.ui.theme.SoftPurple
import com.example.ui.theme.TechTeal

data class BrandItem(
    val name: String,
    val color: Color,
    val code: String
)

val BRAND_LIST = listOf(
    BrandItem("Samsung", SapphireBlue, "SAM"),
    BrandItem("Vivo", SoftPurple, "VIVO"),
    BrandItem("Xiaomi", OrangePrimary, "MI"),
    BrandItem("Redmi", DarkCrimson, "RED"),
    BrandItem("POCO", ElectricAmber, "POCO"),
    BrandItem("OPPO", TechTeal, "OPPO"),
    BrandItem("Realme", ElectricAmber, "RMX"),
    BrandItem("OnePlus", CrimsonAccent, "1+"),
    BrandItem("Tecno", SapphireBlue, "TEC"),
    BrandItem("Infinix", TechTeal, "INF"),
    BrandItem("Huawei", CrimsonAccent, "HW"),
    BrandItem("Honor", SapphireBlue, "HNR"),
    BrandItem("Itel", CrimsonAccent, "ITL"),
    BrandItem("Motorola", SoftPurple, "MOTO"),
    BrandItem("Nokia", SapphireBlue, "NOK"),
    BrandItem("IQOO", ElectricAmber, "IQOO")
)

@Composable
fun BrandChipsCarousel(
    selectedBrand: String,
    onBrandSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 14.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // "All Brands" option
        BrandCardItem(
            name = "All Brands",
            badgeText = "ALL",
            badgeColor = OrangePrimary,
            isSelected = selectedBrand == "All",
            onClick = { onBrandSelected("All") }
        )

        BRAND_LIST.forEach { brand ->
            BrandCardItem(
                name = brand.name,
                badgeText = brand.code,
                badgeColor = brand.color,
                isSelected = selectedBrand.equals(brand.name, ignoreCase = true),
                onClick = { onBrandSelected(brand.name) }
            )
        }
    }
}

@Composable
private fun BrandCardItem(
    name: String,
    badgeText: String,
    badgeColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isSelected) OrangePrimary else MaterialTheme.colorScheme.surface
            )
            .border(
                width = 1.dp,
                color = if (isSelected) OrangePrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 9.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isSelected) Color.White.copy(alpha = 0.25f) else badgeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeText.take(3),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isSelected) Color.White else badgeColor
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

