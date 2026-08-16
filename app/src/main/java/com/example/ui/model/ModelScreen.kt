package com.example.ui.model

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ModelEntity
import com.example.ui.components.BrandChipsCarousel
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.SapphireBlue
import com.example.ui.theme.TechTeal
import com.example.viewmodel.GsmViewModel

@Composable
fun ModelScreen(
    viewModel: GsmViewModel,
    modifier: Modifier = Modifier
) {
    val selectedBrand by viewModel.selectedModelBrand.collectAsState()
    val searchQuery by viewModel.modelSearchQuery.collectAsState()
    val models by viewModel.modelsList.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setModelSearchQuery(it) },
                placeholder = { Text("Search phone models, chipset, model number...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setModelSearchQuery("") }) {
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

            // Brand Selector
            BrandChipsCarousel(
                selectedBrand = selectedBrand,
                onBrandSelected = { brand ->
                    viewModel.setSelectedModelBrand(brand)
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Models List
            if (models.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No phone models found for selected brand",
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
                    items(models, key = { it.id }) { model ->
                        ModelCard(
                            model = model,
                            onDelete = { viewModel.deleteModel(model) }
                        )
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = OrangePrimary,
            contentColor = Color.White,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 70.dp, end = 14.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Phone Model", modifier = Modifier.size(20.dp))
        }

        if (showAddDialog) {
            AddModelDialog(
                onDismiss = { showAddDialog = false },
                onSave = { brand, name, number, chip, android, ram, storage, net, battery, charge, notes ->
                    viewModel.saveModel(
                        brand = brand,
                        modelName = name,
                        modelNumber = number,
                        chipset = chip,
                        androidVersion = android,
                        ram = ram,
                        storage = storage,
                        network = net,
                        battery = battery,
                        charging = charge,
                        notes = notes
                    )
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun ModelCard(
    model: ModelEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(OrangePrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = model.brand,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                    }

                    if (model.modelNumber.isNotBlank()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = model.modelNumber,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${model.brand} ${model.modelName}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Spec Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SpecChip(
                    icon = Icons.Default.Memory,
                    label = model.chipset.ifBlank { "N/A" },
                    color = SapphireBlue,
                    modifier = Modifier.weight(1.2f)
                )
                SpecChip(
                    icon = Icons.Default.SdStorage,
                    label = "${model.ram} / ${model.storage}",
                    color = TechTeal,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SpecChip(
                    icon = Icons.Default.BatteryChargingFull,
                    label = "${model.battery} (${model.charging})",
                    color = ElectricAmber,
                    modifier = Modifier.weight(1.2f)
                )
                SpecChip(
                    icon = Icons.Default.NetworkCheck,
                    label = model.network.ifBlank { "4G LTE" },
                    color = NeonGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    if (model.androidVersion.isNotBlank()) {
                        Text(
                            text = "OS Version: ${model.androidVersion}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (model.notes.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                .padding(6.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Technician Servicing Guide:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangePrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = model.notes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddModelDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String, String, String, String, String) -> Unit
) {
    var brand by remember { mutableStateOf("Vivo") }
    var modelName by remember { mutableStateOf("") }
    var modelNumber by remember { mutableStateOf("") }
    var chipset by remember { mutableStateOf("MediaTek Helio G85") }
    var androidVersion by remember { mutableStateOf("Android 14 (Funtouch 14)") }
    var ram by remember { mutableStateOf("4GB / 6GB") }
    var storage by remember { mutableStateOf("64GB / 128GB") }
    var network by remember { mutableStateOf("4G LTE / Dual SIM") }
    var battery by remember { mutableStateOf("5000 mAh") }
    var charging by remember { mutableStateOf("15W Fast Charge") }
    var notes by remember { mutableStateOf("") }

    var brandExpanded by remember { mutableStateOf(false) }
    val brands = listOf("Samsung", "Vivo", "Xiaomi", "Redmi", "POCO", "OPPO", "Realme", "OnePlus", "Tecno", "Infinix", "Huawei", "Honor", "Motorola", "Nokia", "IQOO")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Phone Model Specs") },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    ExposedDropdownMenuBox(
                        expanded = brandExpanded,
                        onExpandedChange = { brandExpanded = !brandExpanded }
                    ) {
                        OutlinedTextField(
                            value = brand,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Phone Brand") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = brandExpanded,
                            onDismissRequest = { brandExpanded = false }
                        ) {
                            brands.forEach { b ->
                                DropdownMenuItem(
                                    text = { Text(b) },
                                    onClick = {
                                        brand = b
                                        brandExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = modelName,
                        onValueChange = { modelName = it },
                        label = { Text("Model Name (e.g. Vivo Y04, Galaxy A15)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = modelNumber,
                        onValueChange = { modelNumber = it },
                        label = { Text("Model Number / Code (e.g. V2338, SM-A155F)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = chipset,
                        onValueChange = { chipset = it },
                        label = { Text("Processor / Chipset (CPU/GPU)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = androidVersion,
                        onValueChange = { androidVersion = it },
                        label = { Text("Android Version & Custom UI") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = ram,
                            onValueChange = { ram = it },
                            label = { Text("RAM") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = storage,
                            onValueChange = { storage = it },
                            label = { Text("Storage") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = battery,
                            onValueChange = { battery = it },
                            label = { Text("Battery") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = charging,
                            onValueChange = { charging = it },
                            label = { Text("Charging Watt") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Technician Guide (Disassembly / Test Points)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (modelName.isNotBlank()) {
                        onSave(brand, modelName, modelNumber, chipset, androidVersion, ram, storage, network, battery, charging, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                enabled = modelName.isNotBlank()
            ) {
                Text("Save Model")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
