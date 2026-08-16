package com.example.ui.customer

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CustomerEntity
import com.example.ui.components.AppDatePickerDialog
import com.example.ui.components.StatusBadge
import com.example.ui.components.VoicePlayerBar
import com.example.ui.components.showAppTimePicker
import com.example.ui.theme.DarkCrimson
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.TechTeal
import com.example.viewmodel.GsmViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customerId: Long,
    viewModel: GsmViewModel,
    onNavigateBack: () -> Unit,
    onEditCustomer: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val customers by viewModel.customersList.collectAsState()
    val customer = customers.find { it.id == customerId }

    var showStatusMenu by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showRescheduleDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Job Card #${customerId}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEditCustomer(customerId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DarkCrimson)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        if (customer == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Customer record not found", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Status and Primary Info Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = customer.customerName,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )

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
                                                    viewModel.updateCustomerStatus(customer.id, st)
                                                    showStatusMenu = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${customer.brand} ${customer.model}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OrangePrimary
                            )

                            if (customer.imei.isNotBlank()) {
                                Text(
                                    text = "IMEI: ${customer.imei}",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Contact Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${customer.mobileNumber}")
                                        }
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(32.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = "Call", tint = NeonGreen, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Call", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val cleanNum = customer.mobileNumber.replace("[^0-9+]".toRegex(), "")
                                        val invoiceMsg = generateInvoiceText(customer)
                                        val url = "https://api.whatsapp.com/send?phone=$cleanNum&text=${Uri.encode(invoiceMsg)}"
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        try {
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$cleanNum"))
                                            context.startActivity(smsIntent)
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .height(32.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = TechTeal, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("WhatsApp", color = TechTeal, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }

                                if (customer.gmail.isNotBlank()) {
                                    IconButton(
                                        onClick = {
                                            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                                data = Uri.parse("mailto:${customer.gmail}")
                                                putExtra(Intent.EXTRA_SUBJECT, "GSM Service: Job #${customer.id} Update")
                                                putExtra(Intent.EXTRA_TEXT, generateInvoiceText(customer))
                                            }
                                            context.startActivity(emailIntent)
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Email, contentDescription = "Email", tint = OrangePrimary, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Service Problem & Voice Memo
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Service & Problem Specification",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Category: ${customer.serviceType}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = "Problem Details: ${customer.problemDescription.ifBlank { "Standard service diagnosis" }}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            customer.voiceFilePath?.let { path ->
                                Spacer(modifier = Modifier.height(8.dp))
                                VoicePlayerBar(
                                    audioPlayer = viewModel.audioPlayer,
                                    voiceFilePath = path
                                )
                            }
                        }
                    }
                }

                // 3. Billing & Payment Invoice Box
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Billing & Financial Summary",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Service Charge:", fontSize = 12.sp)
                                Text("৳${customer.serviceCharge.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Advance Paid:", fontSize = 12.sp)
                                Text("৳${customer.advancePayment.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NeonGreen)
                            }

                            Divider(modifier = Modifier.padding(vertical = 6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Due Amount:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    "৳${customer.dueAmount.toInt()}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (customer.dueAmount > 0) OrangePrimary else NeonGreen
                                )
                            }
                        }
                    }
                }

                // 4. Delivery Schedule Box
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Alarm, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Scheduled Delivery", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${customer.deliveryDate} at ${customer.deliveryTime}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = { showRescheduleDatePicker = true },
                                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary.copy(alpha = 0.15f)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                modifier = Modifier.height(30.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Reschedule", color = OrangePrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }

                // 5. Share Job Invoice Action Button
                item {
                    Button(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "GSM Service Receipt #${customer.id}")
                                putExtra(Intent.EXTRA_TEXT, generateInvoiceText(customer))
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Service Invoice"))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Digital Invoice / Receipt", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }

            // Reschedule Date & Time Dialogs
            if (showRescheduleDatePicker) {
                AppDatePickerDialog(
                    initialDateMillis = if (customer.deliveryTimestamp > 0) customer.deliveryTimestamp else System.currentTimeMillis(),
                    onDateSelected = { formattedDate, millis ->
                        showRescheduleDatePicker = false
                        // Now prompt for time
                        showAppTimePicker(context) { formattedTime, hour, min ->
                            val cal = Calendar.getInstance().apply {
                                timeInMillis = millis
                                set(Calendar.HOUR_OF_DAY, hour)
                                set(Calendar.MINUTE, min)
                                set(Calendar.SECOND, 0)
                            }
                            viewModel.updateCustomerDeliveryTime(
                                id = customer.id,
                                date = formattedDate,
                                time = formattedTime,
                                timestamp = cal.timeInMillis,
                                customerName = customer.customerName,
                                brandModel = "${customer.brand} ${customer.model}",
                                serviceType = customer.serviceType,
                                mobileNumber = customer.mobileNumber
                            )
                        }
                    },
                    onDismiss = { showRescheduleDatePicker = false }
                )
            }

            // Delete Confirm Dialog
            if (showDeleteConfirm) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirm = false },
                    title = { Text("Delete Customer Record") },
                    text = { Text("Are you sure you want to permanently delete this job card #${customer.id}?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteCustomer(customer)
                                showDeleteConfirm = false
                                onNavigateBack()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkCrimson)
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirm = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

private fun generateInvoiceText(customer: CustomerEntity): String {
    return """
        ==================================
        📱 GSM SERVICE - JOB SHEET #${customer.id}
        ==================================
        Customer: ${customer.customerName}
        Phone: ${customer.mobileNumber}
        Address: ${customer.address.ifBlank { "N/A" }}
        
        Device: ${customer.brand} ${customer.model}
        IMEI: ${customer.imei.ifBlank { "N/A" }}
        Service Type: ${customer.serviceType}
        Problem: ${customer.problemDescription}
        Status: ${customer.status}
        
        ----------------------------------
        Total Bill: ৳${customer.serviceCharge.toInt()}
        Advance Paid: ৳${customer.advancePayment.toInt()}
        DUE AMOUNT: ৳${customer.dueAmount.toInt()}
        ----------------------------------
        Delivery Deadline: ${customer.deliveryDate} (${customer.deliveryTime})
        
        Thank you for trusting GSM Service Master!
        ==================================
    """.trimIndent()
}
