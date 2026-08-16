package com.example.ui.file

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.FileEntity
import com.example.ui.theme.DarkCrimson
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.SapphireBlue
import com.example.ui.theme.SoftPurple
import com.example.ui.theme.TechTeal
import com.example.viewmodel.GsmViewModel

val FILE_CATEGORIES = listOf(
    "All",
    "Firmware",
    "Flash File",
    "ENG ROM",
    "Dump File",
    "DA File",
    "Auth File",
    "OTA File",
    "Recovery",
    "Boot File",
    "PDF",
    "Tool",
    "Driver",
    "Other"
)

@Composable
fun FileScreen(
    viewModel: GsmViewModel,
    modifier: Modifier = Modifier
) {
    val selectedType by viewModel.selectedFileType.collectAsState()
    val searchQuery by viewModel.fileSearchQuery.collectAsState()
    val files by viewModel.filesList.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setFileSearchQuery(it) },
                placeholder = { Text("Search firmware, flash files, DA/Auth, dump...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setFileSearchQuery("") }) {
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

            // Category Chips
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FILE_CATEGORIES.forEach { category ->
                    val isSelected = selectedType == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isSelected) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { viewModel.setSelectedFileType(category) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // File List
            if (files.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No files found for category",
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
                    items(files, key = { it.id }) { fileEntity ->
                        FileCard(
                            fileEntity = fileEntity,
                            onDelete = { viewModel.deleteFile(fileEntity) }
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
            Icon(Icons.Default.Add, contentDescription = "Add File", modifier = Modifier.size(20.dp))
        }

        if (showAddDialog) {
            AddFileDialog(
                onDismiss = { showAddDialog = false },
                onSave = { name, brand, model, android, type, path, size, version, desc ->
                    viewModel.saveFileEntity(
                        fileName = name,
                        brand = brand,
                        model = model,
                        androidVersion = android,
                        fileType = type,
                        filePath = path,
                        fileSize = size,
                        version = version,
                        description = desc
                    )
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun FileCard(
    fileEntity: FileEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val tagColor = when (fileEntity.fileType) {
        "Firmware" -> SapphireBlue
        "Flash File" -> DarkCrimson
        "ENG ROM" -> ElectricAmber
        "DA File", "Auth File" -> SoftPurple
        "Dump File" -> TechTeal
        else -> OrangePrimary
    }

    Card(
        modifier = modifier.fillMaxWidth(),
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
                            .background(tagColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = fileEntity.fileType,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = tagColor
                        )
                    }

                    if (fileEntity.brand.isNotBlank()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${fileEntity.brand} ${fileEntity.model}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
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
                text = fileEntity.fileName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            if (fileEntity.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = fileEntity.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Size: ${fileEntity.fileSize.ifBlank { "N/A" }}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (fileEntity.version.isNotBlank()) {
                    Text(
                        text = "Build: ${fileEntity.version}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (fileEntity.androidVersion.isNotBlank()) {
                    Text(
                        text = "OS: ${fileEntity.androidVersion}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "GSM File: ${fileEntity.fileName}")
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "File: ${fileEntity.fileName}\nCategory: ${fileEntity.fileType}\nModel: ${fileEntity.brand} ${fileEntity.model}\nSize: ${fileEntity.fileSize}\nInfo: ${fileEntity.description}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Service File Info"))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Share", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        val searchUrl = "https://www.google.com/search?q=${Uri.encode("${fileEntity.brand} ${fileEntity.model} ${fileEntity.fileName} official firmware")}"
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl))
                        context.startActivity(browserIntent)
                    },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(30.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Get File / ROM", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddFileDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String, String, String) -> Unit
) {
    var fileName by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("Vivo") }
    var model by remember { mutableStateOf("") }
    var androidVersion by remember { mutableStateOf("Android 14") }
    var fileType by remember { mutableStateOf("Firmware") }
    var filePath by remember { mutableStateOf("") }
    var fileSize by remember { mutableStateOf("4.2 GB") }
    var version by remember { mutableStateOf("PD2338F_EX_A_14.0.1.0") }
    var description by remember { mutableStateOf("") }

    var brandExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    val brands = listOf("Samsung", "Vivo", "Xiaomi", "Redmi", "POCO", "OPPO", "Realme", "OnePlus", "Tecno", "Infinix", "Huawei", "Honor", "Motorola", "Nokia", "IQOO", "Universal / Tools")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Service File / ROM") },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = fileName,
                        onValueChange = { fileName = it },
                        label = { Text("File Name (e.g. Vivo_Y04_Flash_File.zip)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = brandExpanded,
                            onExpandedChange = { brandExpanded = !brandExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = brand,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Brand") },
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

                        ExposedDropdownMenuBox(
                            expanded = typeExpanded,
                            onExpandedChange = { typeExpanded = !typeExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = fileType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Category") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = typeExpanded,
                                onDismissRequest = { typeExpanded = false }
                            ) {
                                FILE_CATEGORIES.filter { it != "All" }.forEach { t ->
                                    DropdownMenuItem(
                                        text = { Text(t) },
                                        onClick = {
                                            fileType = t
                                            typeExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        label = { Text("Model (e.g. Y04, A15)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = fileSize,
                            onValueChange = { fileSize = it },
                            label = { Text("Size") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = version,
                            onValueChange = { version = it },
                            label = { Text("Build / Version") },
                            modifier = Modifier.weight(1.2f)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Flashing Instructions / Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fileName.isNotBlank()) {
                        onSave(fileName, brand, model, androidVersion, fileType, filePath, fileSize, version, description)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                enabled = fileName.isNotBlank()
            ) {
                Text("Save File")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
