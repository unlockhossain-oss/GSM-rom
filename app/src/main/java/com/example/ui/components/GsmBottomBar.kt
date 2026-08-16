package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.i18n.currentStrings
import com.example.ui.navigation.BottomNavItem
import com.example.ui.theme.OrangePrimary

@Composable
fun GsmBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val s = currentStrings()

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            // Unitary navigation row: all icons and labels are strictly equal-spaced and harmonized
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 2.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem.entries.forEach { item ->
                    val isSelected = currentRoute == item.route

                    val icon: ImageVector = when (item) {
                        BottomNavItem.HOME -> Icons.Default.Home
                        BottomNavItem.DIAGRAM -> Icons.Default.Memory
                        BottomNavItem.LCD -> Icons.Default.Smartphone
                        BottomNavItem.MODEL -> Icons.Default.PhoneAndroid
                        BottomNavItem.FILE -> Icons.Default.Folder
                        BottomNavItem.CUSTOMER -> Icons.Default.PeopleAlt
                    }

                    val labelText = when (item) {
                        BottomNavItem.HOME -> s.tabHome
                        BottomNavItem.DIAGRAM -> s.tabDiagram
                        BottomNavItem.LCD -> s.tabLcd
                        BottomNavItem.MODEL -> s.tabModel
                        BottomNavItem.FILE -> s.tabFile
                        BottomNavItem.CUSTOMER -> s.tabCustomer
                    }

                    val iconTint by animateColorAsState(
                        targetValue = if (isSelected) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(durationMillis = 200),
                        label = "iconTint"
                    )

                    val pillBackground by animateColorAsState(
                        targetValue = if (isSelected) OrangePrimary.copy(alpha = 0.16f) else Color.Transparent,
                        animationSpec = tween(durationMillis = 200),
                        label = "pillBackground"
                    )

                    // Unitary Item Container
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true, color = OrangePrimary)
                            ) {
                                onNavigate(item.route)
                            }
                            .padding(vertical = 4.dp)
                            .testTag("nav_item_${item.route}")
                    ) {
                        // Unitary Icon Pill
                        Box(
                            modifier = Modifier
                                .size(width = 44.dp, height = 26.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(pillBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = labelText,
                                tint = iconTint,
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Unitary Label
                        Text(
                            text = labelText,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = iconTint,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

