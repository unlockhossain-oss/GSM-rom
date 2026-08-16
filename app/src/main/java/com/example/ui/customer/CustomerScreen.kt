package com.example.ui.customer

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CustomerEntity
import com.example.ui.components.StatusBadge
import com.example.ui.components.VoicePlayerBar
import com.example.ui.theme.DarkCrimson
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.SapphireBlue
import com.example.viewmodel.GsmViewModel
import java.util.Locale

val CUSTOMER_STATUS_LIST = listOf(
    "All",
    "Received",
    "Checking",
    "Processing",
    "Waiting for Parts",
    "Completed",
    "Delivered",
    "Cancelled"
)

@Composable
fun CustomerScreen(
    viewModel: GsmViewModel,
    onAddNewCustomer: () -> Unit,
    onCustomerClick: (Long) -> Unit,
    onEditCustomer: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val searchQuery by viewModel.customerSearchQuery.collectAsState()
    val selectedStatus by viewModel.selectedStatusFilter.collectAsState()
    val customers by viewModel.customersList.collectAsState()

    var customerToDelete by remember { mutableStateOf<CustomerEntity?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setCustomerSearchQuery(it) },
                placeholder = { Text("Search customer, mobile, IMEI, model...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setCustomerSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            // Status Filter Tabs
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CUSTOMER_STATUS_LIST.forEach { status ->
                    val isSelected = selectedStatus == status
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isSelected) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { viewModel.setStatusFilter(status) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = status,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Customer List
            if (customers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PeopleAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No servicing customers found",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 2.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(customers, key = { it.id }) { customer ->
                        CustomerJobCard(
                            customer = customer,
                            viewModel = viewModel,
                            onCardClick = { onCustomerClick(customer.id) },
                            onEdit = { onEditCustomer(customer.id) },
                            onDelete = { customerToDelete = customer },
                            onStatusChange = { newStatus ->
                                viewModel.updateCustomerStatus(customer.id, newStatus)
                            },
                            onCall = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${customer.mobileNumber}")
                                }
                                context.startActivity(intent)
                            },
                            onWhatsApp = {
                                val cleanNum = customer.mobileNumber.replace("[^0-9+]".toRegex(), "")
                                val url = "https://api.whatsapp.com/send?phone=$cleanNum&text=${Uri.encode("Hello ${customer.customerName}, regarding your ${customer.brand} ${customer.model} servicing at GSM Service...")}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    // Fallback to SMS
                                    val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$cleanNum"))
                                    context.startActivity(smsIntent)
                                }
                            }
                        )
                    }
                }
            }
        }

        // Floating Action Button - High Density Compact
        FloatingActionButton(
            onClick = onAddNewCustomer,
            containerColor = OrangePrimary,
            contentColor = Color.White,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 70.dp, end = 14.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Customer", modifier = Modifier.size(20.dp))
        }

        // Confirm Delete Dialog
        customerToDelete?.let { cust ->
            AlertDialog(
                onDismissRequest = { customerToDelete = null },
                title = { Text("Delete Customer Record", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to permanently delete ${cust.customerName}'s service job card (${cust.brand} ${cust.model})?", fontSize = 13.sp) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteCustomer(cust)
                            customerToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkCrimson),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Delete", fontSize = 12.sp)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { customerToDelete = null }) {
                        Text("Cancel", fontSize = 12.sp)
                    }
                }
            )
        }
    }
}

@Composable
private fun CustomerJobCard(
    customer: CustomerEntity,
    viewModel: GsmViewModel,
    onCardClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onStatusChange: (String) -> Unit,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // Header Row: Customer Name + Device + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = customer.customerName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${customer.brand} ${customer.model} • ${customer.serviceType}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = OrangePrimary
                    )
                }

                Box {
                    Box(
                        modifier = Modifier
                            .clickable { showStatusMenu = true }
                    ) {
                        StatusBadge(status = customer.status)
                    }

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        CUSTOMER_STATUS_LIST.filter { it != "All" }.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st, fontSize = 12.sp) },
                                onClick = {
                                    onStatusChange(st)
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Problem Description
            if (customer.problemDescription.isNotBlank()) {
                Text(
                    text = "Problem: ${customer.problemDescription}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Financial & Delivery Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Charge", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("৳${customer.serviceCharge.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text("Advance Paid", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("৳${customer.advancePayment.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = NeonGreen)
                }
                Column {
                    Text("Due Amount", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "৳${customer.dueAmount.toInt()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (customer.dueAmount > 0) OrangePrimary else NeonGreen
                    )
                }
                Column {
                    Text("Delivery Date", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        customer.deliveryDate.ifBlank { "Not set" },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Voice Memo Playback if recorded
            customer.voiceFilePath?.let { path ->
                Spacer(modifier = Modifier.height(6.dp))
                VoicePlayerBar(
                    audioPlayer = viewModel.audioPlayer,
                    voiceFilePath = path
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onCall,
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(13.dp), tint = NeonGreen)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Call", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onWhatsApp,
                    modifier = Modifier
                        .weight(1.1f)
                        .height(30.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "WhatsApp", modifier = Modifier.size(13.dp), tint = SapphireBlue)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("WhatsApp", color = SapphireBlue, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = OrangePrimary, modifier = Modifier.size(16.dp))
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DarkCrimson, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

