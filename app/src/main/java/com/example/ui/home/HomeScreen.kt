package com.example.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CustomerEntity
import com.example.ui.components.BrandChipsCarousel
import com.example.ui.components.SocialPlatformsBar
import com.example.ui.components.StatusBadge
import com.example.ui.components.SummaryStatCards
import com.example.ui.components.VoicePlayerBar
import com.example.ui.navigation.Screen
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.SapphireBlue
import com.example.viewmodel.GsmViewModel

@Composable
fun HomeScreen(
    viewModel: GsmViewModel,
    onNavigateTo: (String) -> Unit,
    onCustomerSelected: (Long) -> Unit,
    onAddNewCustomer: () -> Unit,
    onBrandClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val totalCustomers by viewModel.totalCustomerCount.collectAsState()
    val pendingCount by viewModel.pendingCount.collectAsState()
    val completedCount by viewModel.completedCount.collectAsState()
    val totalDue by viewModel.totalDueAmount.collectAsState()
    val todayDeliveries by viewModel.todayDeliveries.collectAsState()
    val allCustomers by viewModel.customersList.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 0. Social Platforms Bar (WhatsApp, Telegram, Facebook, YouTube)
            item {
                SocialPlatformsBar()
            }

            // 1. Dashboard Summary Cards
            item {
                Text(
                    text = "SERVICE OVERVIEW",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                SummaryStatCards(
                    totalCustomers = totalCustomers,
                    pendingServices = pendingCount,
                    completedServices = completedCount,
                    todayDeliveriesCount = todayDeliveries.size,
                    totalDue = totalDue
                )
            }

            // 2. Horizontal Brands Scroll
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PHONE BRANDS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "View All Models",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OrangePrimary,
                        modifier = Modifier.clickable { onNavigateTo(Screen.Model.route) }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                BrandChipsCarousel(
                    selectedBrand = "All",
                    onBrandSelected = { brand ->
                        if (brand != "All") {
                            viewModel.setSelectedModelBrand(brand)
                            onNavigateTo(Screen.Model.route)
                        } else {
                            onNavigateTo(Screen.Model.route)
                        }
                    }
                )
            }

            // 3. Technical Core Workbench (3 Main Dedicated Hub Sections)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TECHNICAL WORKBENCH",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "3 Dedicated Hubs",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OrangePrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionButton(
                            title = "Schematics",
                            subtitle = "PCB & Circuits",
                            icon = Icons.Default.AccountTree,
                            color = SapphireBlue,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTo(Screen.Diagram.route) }
                        )
                        QuickActionButton(
                            title = "LCD Pinouts",
                            subtitle = "Display Specs",
                            icon = Icons.Default.Tv,
                            color = OrangePrimary,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTo(Screen.Lcd.route) }
                        )
                        QuickActionButton(
                            title = "Firmware Files",
                            subtitle = "ROM & Flash",
                            icon = Icons.Default.CloudDownload,
                            color = NeonGreen,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTo(Screen.File.route) }
                        )
                    }
                }
            }

            // 4. Today's Delivery Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TODAY'S DELIVERIES",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )
                    }

                    if (todayDeliveries.isNotEmpty()) {
                        Text(
                            text = "${todayDeliveries.size} Scheduled",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                    }
                }
            }

            if (todayDeliveries.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No phone deliveries scheduled for today",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(todayDeliveries, key = { it.id }) { customer ->
                    TodayDeliveryCard(
                        customer = customer,
                        viewModel = viewModel,
                        onCall = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${customer.mobileNumber}")
                            }
                            context.startActivity(intent)
                        },
                        onDetails = { onCustomerSelected(customer.id) }
                    )
                }
            }

            // 5. Recent Service Jobs Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT SERVICING JOBS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "View All Jobs",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OrangePrimary,
                        modifier = Modifier.clickable { onNavigateTo(Screen.Customer.route) }
                    )
                }
            }

            items(allCustomers.take(5), key = { "recent_${it.id}" }) { customer ->
                RecentJobCard(
                    customer = customer,
                    viewModel = viewModel,
                    onClick = { onCustomerSelected(customer.id) }
                )
            }
        }

        // Floating Action Button - High Density Compact
        ExtendedFloatingActionButton(
            onClick = onAddNewCustomer,
            icon = { Icon(Icons.Default.Add, contentDescription = "Add Customer", modifier = Modifier.size(18.dp)) },
            text = { Text("New Job", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
            containerColor = OrangePrimary,
            contentColor = Color.White,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 70.dp, end = 14.dp)
        )
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFF263248).copy(alpha = 0.85f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("quick_action_${title.lowercase().replace(" ", "_")}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Glowing Symbol Container
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                color.copy(alpha = 0.22f),
                                color.copy(alpha = 0.08f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = color.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            // Clear, Bold Title Label
            Text(
                text = title,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Descriptive Subtitle
            Text(
                text = subtitle,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TodayDeliveryCard(
    customer: CustomerEntity,
    viewModel: GsmViewModel,
    onCall: () -> Unit,
    onDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = customer.customerName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${customer.brand} ${customer.model}",
                        style = MaterialTheme.typography.bodySmall,
                        color = OrangePrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                StatusBadge(status = customer.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Delivery: ${customer.deliveryTime}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "Due: ৳${customer.dueAmount.toInt()}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (customer.dueAmount > 0) OrangePrimary else NeonGreen
                )
            }

            // Voice Memo if available
            customer.voiceFilePath?.let { path ->
                Spacer(modifier = Modifier.height(6.dp))
                VoicePlayerBar(
                    audioPlayer = viewModel.audioPlayer,
                    voiceFilePath = path
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = onCall,
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(14.dp), tint = NeonGreen)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = onDetails,
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = "Details", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Details", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun RecentJobCard(
    customer: CustomerEntity,
    viewModel: GsmViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(OrangePrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "${customer.customerName} • ${customer.brand} ${customer.model}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Text(
                        text = "Service: ${customer.serviceType} • Due: ৳${customer.dueAmount.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(status = customer.status)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

