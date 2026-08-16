package com.example.ui.lcd

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tv
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.LcdEntity
import com.example.i18n.currentStrings
import com.example.ui.components.BrandChipsCarousel
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.OrangePrimary
import com.example.viewmodel.GsmViewModel

data class LcdGroup(
    val brand: String,
    val groupName: String,
    val models: List<LcdEntity>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LcdScreen(
    viewModel: GsmViewModel,
    modifier: Modifier = Modifier
) {
    val strings = currentStrings()
    val selectedBrand by viewModel.selectedLcdBrand.collectAsState()
    val searchQuery by viewModel.lcdSearchQuery.collectAsState()
    val lcds by viewModel.lcdsList.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedLcdForDetail by remember { mutableStateOf<LcdEntity?>(null) }
    var editingLcd by remember { mutableStateOf<LcdEntity?>(null) }

    // Group LCDs by Brand & GroupName
    val groupedLcds = remember(lcds) {
        lcds.groupBy { "${it.brand}|||${it.groupName.ifBlank { "${it.brand} ${it.model} Series" }}" }
            .map { (key, list) ->
                val parts = key.split("|||")
                val brand = parts.getOrNull(0) ?: ""
                val groupName = parts.getOrNull(1) ?: "${list.firstOrNull()?.model ?: ""} Series"
                LcdGroup(brand = brand, groupName = groupName, models = list)
            }
    }

    val totalGroupsCount = groupedLcds.size

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Controls: Search Bar & Brand Filter
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                // Search Bar (Dark Navy Container with subtle dark border & Orange focus)
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setLcdSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = strings.lcdSearchPlaceholder,
                            fontSize = 13.sp,
                            color = DarkTextSecondary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = DarkTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setLcdSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = DarkTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .testTag("lcd_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = Color(0xFF263248),
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface,
                        focusedTextColor = DarkTextPrimary,
                        unfocusedTextColor = DarkTextPrimary,
                        cursorColor = OrangePrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Brand Filter Carousel (Matches Reference Photo: Orange for Selected, Dark Navy for Unselected)
                BrandChipsCarousel(
                    selectedBrand = selectedBrand,
                    onBrandSelected = { viewModel.setSelectedLcdBrand(it) },
                    modifier = Modifier.fillMaxWidth()
                )

                if (groupedLcds.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val groupCountFormatted = try {
                        String.format(strings.lcdTotalGroupsFound, totalGroupsCount)
                    } catch (e: Exception) {
                        "$totalGroupsCount LCD groups"
                    }

                    Text(
                        text = groupCountFormatted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkTextSecondary,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 2. LCD Group Cards List or Reference-Matched Empty State
            if (groupedLcds.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Empty State Icon (Subtle Dark Navy Box with phone icon)
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceVariant.copy(alpha = 0.6f))
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFF263248),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Smartphone,
                                contentDescription = null,
                                tint = DarkTextSecondary.copy(alpha = 0.8f),
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = strings.lcdNotFound,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = DarkTextSecondary,
                            textAlign = TextAlign.Center
                        )

                        if (searchQuery.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "\"$searchQuery\"",
                                fontSize = 12.sp,
                                color = OrangePrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(groupedLcds, key = { "${it.brand}_${it.groupName}" }) { group ->
                        LcdGroupCard(
                            group = group,
                            onModelClick = { lcd ->
                                selectedLcdForDetail = lcd
                            }
                        )
                    }
                }
            }
        }

        // Floating Action Button - Add Model (Matching Reference Photo: Orange Squircle with White +)
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = OrangePrimary,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .testTag("add_lcd_fab")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = strings.lcdAddNewModelTitle,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    // Model Detail Specification Sheet Dialog
    selectedLcdForDetail?.let { lcd ->
        LcdDetailDialog(
            lcd = lcd,
            onDismiss = { selectedLcdForDetail = null },
            onEdit = {
                editingLcd = lcd
                selectedLcdForDetail = null
            },
            onDelete = {
                viewModel.deleteLcd(lcd)
                selectedLcdForDetail = null
            }
        )
    }

    // Add / Edit Dialog
    if (showAddDialog || editingLcd != null) {
        AddOrEditLcdDialog(
            existingLcd = editingLcd,
            onDismiss = {
                showAddDialog = false
                editingLcd = null
            },
            onSave = { brand, groupName, model, modelCode, lcdName, lcdType, displaySize, resolution, connectorType, compatibleModels, price, notes ->
                viewModel.saveLcd(
                    id = editingLcd?.id ?: 0L,
                    brand = brand,
                    groupName = groupName,
                    model = model,
                    modelCode = modelCode,
                    lcdName = lcdName,
                    lcdType = lcdType,
                    displaySize = displaySize,
                    resolution = resolution,
                    connectorType = connectorType,
                    touchInfo = "Capacitive Multi-touch",
                    compatibleModels = compatibleModels,
                    price = price,
                    stock = "In Stock",
                    notes = notes,
                    imagePath = editingLcd?.imagePath ?: ""
                )
                showAddDialog = false
                editingLcd = null
            }
        )
    }
}

/**
 * LCD Group Card matching Dark Navy + Orange theme:
 * - Dark Navy Card Surface
 * - Orange Brand Pill on Left
 * - Light Gray Model Count on Right
 * - Dark Surface Model Chips with Light Text
 * - Orange Show More / Show Less Action
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LcdGroupCard(
    group: LcdGroup,
    onModelClick: (LcdEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = currentStrings()
    var isExpanded by remember { mutableStateOf(false) }
    val maxInitialItems = 3
    val totalModels = group.models.size
    val hasExpandableItems = totalModels > maxInitialItems

    val displayedModels = if (isExpanded || !hasExpandableItems) {
        group.models
    } else {
        group.models.take(maxInitialItems)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, Color(0xFF263248)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .animateContentSize()
        ) {
            // Header: Brand Badge on Left & Model Count on Right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Pill Badge with Orange Accent
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = OrangePrimary.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.35f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = group.brand,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OrangePrimary
                        )
                    }
                }

                // Right Header: Model count text (e.g. "9 models" / "৯টি মডেল")
                val modelsCountFormatted = try {
                    String.format(strings.lcdModelsCount, totalModels)
                } catch (e: Exception) {
                    "$totalModels models"
                }

                Text(
                    text = modelsCountFormatted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = DarkTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Model Chips FlowRow Layout
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                displayedModels.forEach { lcd ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DarkSurfaceVariant,
                        border = BorderStroke(1.dp, Color(0xFF263248)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onModelClick(lcd) }
                            .testTag("model_chip_${lcd.model}")
                    ) {
                        Text(
                            text = lcd.model,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = DarkTextPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Expand / Collapse Row if items > 3
            if (hasExpandableItems) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { isExpanded = !isExpanded }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isExpanded) {
                        Text(
                            text = strings.lcdShowLess,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OrangePrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Show Less",
                            tint = OrangePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        val remainingCount = totalModels - maxInitialItems
                        val showMoreFormatted = try {
                            String.format(strings.lcdShowMore, remainingCount)
                        } catch (e: Exception) {
                            "Show $remainingCount more"
                        }
                        Text(
                            text = showMoreFormatted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OrangePrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Show More",
                            tint = OrangePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Detailed Specification Modal Dialog for a selected Model
 */
@Composable
fun LcdDetailDialog(
    lcd: LcdEntity,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val strings = currentStrings()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        titleContentColor = DarkTextPrimary,
        textContentColor = DarkTextSecondary,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = OrangePrimary.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.35f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = lcd.brand,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = lcd.model,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DarkTextPrimary
                    )
                    if (lcd.modelCode.isNotBlank()) {
                        Text(
                            text = "${strings.lcdModelCode}: ${lcd.modelCode}",
                            fontSize = 12.sp,
                            color = DarkTextSecondary
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = DarkTextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // LCD Visual Schematic Banner (Dark Navy Surface with Orange Accent)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkSurfaceVariant,
                    border = BorderStroke(1.dp, Color(0xFF263248)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = strings.lcdDisplaySpecification,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary
                            )
                            Text(
                                text = lcd.lcdType.ifBlank { strings.lcdDefaultType },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkTextPrimary
                            )
                            if (lcd.displaySize.isNotBlank() || lcd.resolution.isNotBlank()) {
                                Text(
                                    text = "${lcd.displaySize} • ${lcd.resolution}",
                                    fontSize = 11.sp,
                                    color = DarkTextSecondary
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // Specs List
                SpecItem(
                    icon = Icons.Default.Layers,
                    title = strings.lcdGroupSeries,
                    value = lcd.groupName.ifBlank { "${lcd.brand} ${lcd.model} Series" }
                )

                if (lcd.lcdName.isNotBlank()) {
                    SpecItem(
                        icon = Icons.Default.Tv,
                        title = strings.lcdPartName,
                        value = lcd.lcdName
                    )
                }

                SpecItem(
                    icon = Icons.Default.Cable,
                    title = strings.lcdConnectorType,
                    value = lcd.connectorType.ifBlank { "Standard Pinout FPC" },
                    highlight = true
                )

                if (lcd.compatibleModels.isNotBlank()) {
                    SpecItem(
                        icon = Icons.Default.Smartphone,
                        title = strings.lcdCompatibleSharedModels,
                        value = lcd.compatibleModels
                    )
                }

                if (lcd.price > 0.0) {
                    SpecItem(
                        icon = Icons.Default.MonetizationOn,
                        title = strings.lcdApproxPrice,
                        value = "৳${lcd.price.toInt()}"
                    )
                }

                if (lcd.notes.isNotBlank()) {
                    SpecItem(
                        icon = Icons.Default.Info,
                        title = strings.lcdTechnicianNotes,
                        value = lcd.notes
                    )
                }
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.lcdDeleteModel, fontSize = 12.sp)
                }

                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary, contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.lcdEditSpecs, fontSize = 12.sp, color = Color.White)
                }
            }
        }
    )

    if (showDeleteConfirm) {
        val deleteDescFormatted = try {
            String.format(strings.lcdDeleteConfirmDesc, lcd.brand, lcd.model)
        } catch (e: Exception) {
            "Delete ${lcd.brand} ${lcd.model}?"
        }

        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = DarkSurface,
            title = { Text(strings.lcdDeleteConfirmTitle, color = DarkTextPrimary) },
            text = { Text(deleteDescFormatted, color = DarkTextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(strings.lcdDeleteModel, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(strings.lcdCancelBtn, color = DarkTextSecondary)
                }
            }
        )
    }
}

@Composable
fun SpecItem(
    icon: ImageVector,
    title: String,
    value: String,
    highlight: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (highlight) OrangePrimary.copy(alpha = 0.12f) else DarkSurfaceVariant,
        border = BorderStroke(
            1.dp,
            if (highlight) OrangePrimary.copy(alpha = 0.4f) else Color(0xFF263248)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (highlight) OrangePrimary else DarkTextSecondary,
                modifier = Modifier
                    .size(18.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkTextSecondary
                )
                Text(
                    text = value,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (highlight) OrangePrimary else DarkTextPrimary
                )
            }
        }
    }
}

/**
 * Comprehensive Admin Dialog to Add or Edit LCD Model specifications
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditLcdDialog(
    existingLcd: LcdEntity?,
    onDismiss: () -> Unit,
    onSave: (
        brand: String,
        groupName: String,
        model: String,
        modelCode: String,
        lcdName: String,
        lcdType: String,
        displaySize: String,
        resolution: String,
        connectorType: String,
        compatibleModels: String,
        price: Double,
        notes: String
    ) -> Unit
) {
    val strings = currentStrings()
    var brand by remember { mutableStateOf(existingLcd?.brand ?: "Vivo") }
    var groupName by remember { mutableStateOf(existingLcd?.groupName ?: "") }
    var model by remember { mutableStateOf(existingLcd?.model ?: "") }
    var modelCode by remember { mutableStateOf(existingLcd?.modelCode ?: "") }
    var lcdName by remember { mutableStateOf(existingLcd?.lcdName ?: "") }
    var lcdType by remember { mutableStateOf(existingLcd?.lcdType ?: "IPS LCD") }
    var displaySize by remember { mutableStateOf(existingLcd?.displaySize ?: "6.22 inch") }
    var resolution by remember { mutableStateOf(existingLcd?.resolution ?: "720 × 1520") }
    var connectorType by remember { mutableStateOf(existingLcd?.connectorType ?: "Compatible LCD Connector 34-pin FPC") }
    var compatibleModels by remember { mutableStateOf(existingLcd?.compatibleModels ?: "") }
    var priceText by remember { mutableStateOf(if ((existingLcd?.price ?: 0.0) > 0) existingLcd?.price?.toInt().toString() else "1350") }
    var notes by remember { mutableStateOf(existingLcd?.notes ?: "") }

    var brandExpanded by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    val brandOptions = listOf("Vivo", "Samsung", "Oppo", "Xiaomi", "Realme", "OnePlus", "Tecno", "Infinix", "Itel", "Honor", "Huawei", "Nokia", "Moto")

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = OrangePrimary,
        unfocusedBorderColor = Color(0xFF263248),
        focusedContainerColor = DarkSurfaceVariant,
        unfocusedContainerColor = DarkSurfaceVariant,
        focusedTextColor = DarkTextPrimary,
        unfocusedTextColor = DarkTextPrimary,
        focusedLabelColor = OrangePrimary,
        unfocusedLabelColor = DarkTextSecondary,
        cursorColor = OrangePrimary
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = if (existingLcd != null) strings.lcdEditModelTitle else strings.lcdAddNewModelTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = DarkTextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isError) {
                    Text(
                        text = strings.lcdFormValidationErr,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Brand Selector Dropdown
                ExposedDropdownMenuBox(
                    expanded = brandExpanded,
                    onExpandedChange = { brandExpanded = !brandExpanded }
                ) {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(strings.lcdBrandNameLabel) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(8.dp),
                        colors = textFieldColors
                    )
                    ExposedDropdownMenu(
                        expanded = brandExpanded,
                        onDismissRequest = { brandExpanded = false },
                        modifier = Modifier.background(DarkSurface)
                    ) {
                        brandOptions.forEach { b ->
                            DropdownMenuItem(
                                text = { Text(b, color = DarkTextPrimary) },
                                onClick = {
                                    brand = b
                                    brandExpanded = false
                                    if (groupName.isBlank()) {
                                        groupName = "$b Series"
                                    }
                                }
                            )
                        }
                    }
                }

                // Group Name
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text(strings.lcdGroupNameLabel) },
                    placeholder = { Text(strings.lcdGroupNameHint) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = textFieldColors
                )

                // Model Name
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text(strings.lcdModelNameLabel) },
                    placeholder = { Text(strings.lcdModelNameHint) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = textFieldColors
                )

                // Model Code
                OutlinedTextField(
                    value = modelCode,
                    onValueChange = { modelCode = it },
                    label = { Text(strings.lcdModelCodeLabel) },
                    placeholder = { Text(strings.lcdModelCodeHint) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = textFieldColors
                )

                // LCD Type
                OutlinedTextField(
                    value = lcdType,
                    onValueChange = { lcdType = it },
                    label = { Text(strings.lcdTypeLabel) },
                    placeholder = { Text(strings.lcdTypeHint) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = textFieldColors
                )

                // Display Size & Resolution in a Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = displaySize,
                        onValueChange = { displaySize = it },
                        label = { Text(strings.lcdSizeLabel) },
                        placeholder = { Text(strings.lcdSizeHint) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = textFieldColors
                    )

                    OutlinedTextField(
                        value = resolution,
                        onValueChange = { resolution = it },
                        label = { Text(strings.lcdResolutionLabel) },
                        placeholder = { Text(strings.lcdResolutionHint) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = textFieldColors
                    )
                }

                // Connector Type
                OutlinedTextField(
                    value = connectorType,
                    onValueChange = { connectorType = it },
                    label = { Text(strings.lcdConnectorTypeLabel) },
                    placeholder = { Text(strings.lcdConnectorTypeHint) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = textFieldColors
                )

                // Compatible Models
                OutlinedTextField(
                    value = compatibleModels,
                    onValueChange = { compatibleModels = it },
                    label = { Text(strings.lcdCompatibleModelsLabel) },
                    placeholder = { Text(strings.lcdCompatibleModelsHint) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = textFieldColors
                )

                // Price
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text(strings.lcdPriceLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = textFieldColors
                )

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(strings.lcdNotesLabel) },
                    placeholder = { Text(strings.lcdNotesHint) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    minLines = 2,
                    colors = textFieldColors
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (brand.isBlank() || model.isBlank()) {
                        isError = true
                        return@Button
                    }
                    val finalGroup = groupName.ifBlank { "$brand $model Series" }
                    val finalLcdName = lcdName.ifBlank { "$model Display" }
                    val priceVal = priceText.toDoubleOrNull() ?: 0.0
                    onSave(
                        brand,
                        finalGroup,
                        model,
                        modelCode,
                        finalLcdName,
                        lcdType,
                        displaySize,
                        resolution,
                        connectorType,
                        compatibleModels,
                        priceVal,
                        notes
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary, contentColor = Color.White)
            ) {
                Text(strings.lcdSaveBtn, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.lcdCancelBtn, color = DarkTextSecondary)
            }
        }
    )
}
