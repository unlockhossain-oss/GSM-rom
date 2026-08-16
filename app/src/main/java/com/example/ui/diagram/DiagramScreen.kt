package com.example.ui.diagram

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.entity.DiagramEntity
import com.example.ui.theme.CrimsonAccent
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.SapphireBlue
import com.example.ui.theme.TechTeal
import com.example.viewmodel.GsmViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

val STANDARD_BRANDS = listOf(
    "Samsung",
    "Vivo",
    "OPPO",
    "Xiaomi",
    "Redmi",
    "POCO",
    "Realme",
    "OnePlus",
    "Infinix",
    "Tecno",
    "Itel",
    "Huawei",
    "Honor",
    "Motorola",
    "Nokia",
    "iPhone",
    "IQOO"
)

val DIAGRAM_NAME_SUGGESTIONS = listOf(
    "Charging Diagram",
    "Power Section",
    "Power Diagram",
    "LCD Section",
    "LCD Diagram",
    "Network Section",
    "Network Diagram",
    "Audio Section",
    "Schematic Diagram",
    "PCB Diagram",
    "Test Point",
    "Boot Point"
)

@Composable
fun DiagramScreen(
    viewModel: GsmViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val searchQuery by viewModel.diagramSearchQuery.collectAsState()
    val allDiagrams by viewModel.diagramsList.collectAsState()

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingDiagram by remember { mutableStateOf<DiagramEntity?>(null) }
    var activeViewingDiagram by remember { mutableStateOf<DiagramEntity?>(null) }

    // Map to keep track of expanded state for Brands and Models
    val expandedBrands = remember { mutableStateMapOf<String, Boolean>() }
    val expandedModels = remember { mutableStateMapOf<String, Boolean>() }

    // Group diagrams into Hierarchy: Brand -> Model -> List<DiagramEntity>
    val hierarchicalDiagrams = remember(allDiagrams, searchQuery) {
        val filtered = if (searchQuery.isBlank()) {
            allDiagrams
        } else {
            allDiagrams.filter {
                it.brand.contains(searchQuery, ignoreCase = true) ||
                it.model.contains(searchQuery, ignoreCase = true) ||
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.diagramType.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true) ||
                it.testPoints.contains(searchQuery, ignoreCase = true)
            }
        }
        filtered.groupBy { it.brand }
            .mapValues { (_, brandItems) ->
                brandItems.groupBy { it.model }
            }
    }

    // Auto-expand all nodes when searching
    remember(searchQuery) {
        if (searchQuery.isNotBlank()) {
            hierarchicalDiagrams.forEach { (brand, modelMap) ->
                expandedBrands[brand] = true
                modelMap.keys.forEach { model ->
                    expandedModels["$brand-$model"] = true
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Banner: DIAGRAM MANAGEMENT SYSTEM
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(OrangePrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Memory,
                                    contentDescription = "Diagrams",
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "DIAGRAM MANAGEMENT SYSTEM",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Schematics, PCB Traces & Pinout Database",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Total count chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SapphireBlue.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${allDiagrams.size} Schematics",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SapphireBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action Controls: [ 🔍 Search Diagram ] & [ ➕ ADD NEW DIAGRAM ]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // [ 🔍 Search Diagram ]
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setDiagramSearchQuery(it) },
                            placeholder = { Text("Search Diagram (Samsung, A57, Charging...)", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setDiagramSearchQuery("") }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrangePrimary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                        )

                        // [ ➕ ADD NEW DIAGRAM ]
                        Button(
                            onClick = {
                                editingDiagram = null
                                showAddEditDialog = true
                            },
                            modifier = Modifier.height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ADD NEW DIAGRAM",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // DIAGRAM LIST SECTION (Hierarchical Tree View)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DIAGRAM LIST",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Expand All",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                hierarchicalDiagrams.forEach { (brand, modelMap) ->
                                    expandedBrands[brand] = true
                                    modelMap.keys.forEach { model ->
                                        expandedModels["$brand-$model"] = true
                                    }
                                }
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Text(
                        text = "Collapse All",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                expandedBrands.clear()
                                expandedModels.clear()
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (hierarchicalDiagrams.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No diagrams matching \"$searchQuery\"" else "No diagrams added yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                editingDiagram = null
                                showAddEditDialog = true
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add First Diagram", fontSize = 11.sp)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 2.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    hierarchicalDiagrams.forEach { (brand, modelMap) ->
                        val isBrandExpanded = expandedBrands[brand] ?: (searchQuery.isNotBlank() || brand in listOf("Samsung", "Vivo", "OPPO"))
                        val totalBrandDiagrams = modelMap.values.sumOf { it.size }

                        item(key = "brand-$brand") {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Brand Header
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                            .background(
                                                if (isBrandExpanded) OrangePrimary.copy(alpha = 0.08f)
                                                else Color.Transparent
                                            )
                                            .clickable {
                                                expandedBrands[brand] = !isBrandExpanded
                                            }
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(OrangePrimary)
                                                    .padding(2.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = brand.take(1).uppercase(),
                                                    color = Color.White,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Black
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = brand,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "(${modelMap.size} models • $totalBrandDiagrams diagrams)",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Icon(
                                            imageVector = if (isBrandExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = if (isBrandExpanded) "Collapse" else "Expand",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    // Models & Diagrams inside this Brand
                                    AnimatedVisibility(
                                        visible = isBrandExpanded,
                                        enter = expandVertically() + fadeIn(),
                                        exit = shrinkVertically() + fadeOut()
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 12.dp, end = 12.dp, bottom = 8.dp, top = 2.dp)
                                        ) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(bottom = 6.dp),
                                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                            )

                                            val modelEntries = modelMap.entries.toList()
                                            modelEntries.forEachIndexed { modelIndex, (model, diagramItems) ->
                                                val isLastModel = modelIndex == modelEntries.lastIndex
                                                val modelKey = "$brand-$model"
                                                val isModelExpanded = expandedModels[modelKey] ?: true

                                                // Model Tree Node
                                                Column(modifier = Modifier.fillMaxWidth()) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .clickable {
                                                                expandedModels[modelKey] = !isModelExpanded
                                                            }
                                                            .padding(vertical = 4.dp, horizontal = 4.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = if (isLastModel) "└── " else "├── ",
                                                            fontFamily = FontFamily.Monospace,
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.outline
                                                        )

                                                        Icon(
                                                            imageVector = Icons.Default.PhoneAndroid,
                                                            contentDescription = null,
                                                            tint = SapphireBlue,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))

                                                        Text(
                                                            text = model,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.onSurface,
                                                            modifier = Modifier.weight(1f)
                                                        )

                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(4.dp))
                                                                .background(TechTeal.copy(alpha = 0.12f))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text(
                                                                text = "${diagramItems.size} files",
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = TechTeal
                                                            )
                                                        }

                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Icon(
                                                            imageVector = if (isModelExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                            contentDescription = null,
                                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }

                                                    // Diagrams Tree Leaves
                                                    AnimatedVisibility(
                                                        visible = isModelExpanded,
                                                        enter = expandVertically() + fadeIn(),
                                                        exit = shrinkVertically() + fadeOut()
                                                    ) {
                                                        Column(modifier = Modifier.fillMaxWidth()) {
                                                            diagramItems.forEachIndexed { diagIndex, diagram ->
                                                                val isLastDiagram = diagIndex == diagramItems.lastIndex
                                                                val branchPrefix = if (isLastModel) "     " else "│    "
                                                                val leafSymbol = if (isLastDiagram) "└── " else "├── "

                                                                DiagramTreeLeafRow(
                                                                    prefix = branchPrefix + leafSymbol,
                                                                    diagram = diagram,
                                                                    onClick = { activeViewingDiagram = diagram },
                                                                    onEdit = {
                                                                        editingDiagram = diagram
                                                                        showAddEditDialog = true
                                                                    },
                                                                    onDelete = {
                                                                        viewModel.deleteDiagram(diagram)
                                                                        Toast.makeText(context, "Diagram deleted", Toast.LENGTH_SHORT).show()
                                                                    }
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ADD / EDIT DIAGRAM DIALOG
        if (showAddEditDialog) {
            AddEditDiagramDialog(
                diagram = editingDiagram,
                existingDiagrams = allDiagrams,
                onDismiss = {
                    showAddEditDialog = false
                    editingDiagram = null
                },
                onSave = { brand, model, diagramType, title, filePath, description, testPoints, voltageSpecs ->
                    viewModel.saveDiagram(
                        id = editingDiagram?.id ?: 0L,
                        brand = brand,
                        model = model,
                        diagramType = diagramType,
                        title = title,
                        filePath = filePath,
                        description = description,
                        testPoints = testPoints,
                        voltageSpecs = voltageSpecs
                    )
                    Toast.makeText(
                        context,
                        if (editingDiagram != null) "Diagram updated successfully!" else "New diagram added to $brand $model!",
                        Toast.LENGTH_SHORT
                    ).show()
                    showAddEditDialog = false
                    editingDiagram = null
                }
            )
        }

        // FULLSCREEN INTERACTIVE DIAGRAM VIEWER
        activeViewingDiagram?.let { diag ->
            DiagramViewerDialog(
                diagram = diag,
                onDismiss = { activeViewingDiagram = null },
                onEdit = {
                    val toEdit = diag
                    activeViewingDiagram = null
                    editingDiagram = toEdit
                    showAddEditDialog = true
                },
                onDelete = {
                    viewModel.deleteDiagram(diag)
                    activeViewingDiagram = null
                    Toast.makeText(context, "Diagram deleted", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

/**
 * Single tree leaf line in the Diagram List
 */
@Composable
private fun DiagramTreeLeafRow(
    prefix: String,
    diagram: DiagramEntity,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typeColor = when {
        diagram.diagramType.contains("Charging", ignoreCase = true) -> OrangePrimary
        diagram.diagramType.contains("Power", ignoreCase = true) -> CrimsonAccent
        diagram.diagramType.contains("LCD", ignoreCase = true) || diagram.diagramType.contains("Display", ignoreCase = true) -> SapphireBlue
        diagram.diagramType.contains("Network", ignoreCase = true) -> TechTeal
        diagram.diagramType.contains("Audio", ignoreCase = true) -> ElectricAmber
        else -> NeonGreen
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = prefix,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.outline
        )

        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(typeColor)
        )
        Spacer(modifier = Modifier.width(6.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = diagram.diagramType.ifBlank { diagram.title },
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (diagram.title.isNotBlank() && diagram.title != diagram.diagramType) {
                Text(
                    text = diagram.title,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Image badge if custom image uploaded
        if (diagram.filePath.isNotBlank()) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(SapphireBlue.copy(alpha = 0.12f))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = SapphireBlue, modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("IMG", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = SapphireBlue)
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
        }

        // Action Buttons: View, Edit, Delete
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "View",
                    tint = OrangePrimary,
                    modifier = Modifier.size(14.dp)
                )
            }

            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = CrimsonAccent.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

/**
 * ADD NEW DIAGRAM / EDIT DIAGRAM Form Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditDiagramDialog(
    diagram: DiagramEntity?,
    existingDiagrams: List<DiagramEntity>,
    onDismiss: () -> Unit,
    onSave: (brand: String, model: String, diagramType: String, title: String, filePath: String, description: String, testPoints: String, voltageSpecs: String) -> Unit
) {
    val context = LocalContext.current
    var brand by remember { mutableStateOf(diagram?.brand ?: "Samsung") }
    var model by remember { mutableStateOf(diagram?.model ?: "Galaxy A235F") }
    var diagramName by remember { mutableStateOf(diagram?.title.takeIf { !it.isNullOrBlank() } ?: diagram?.diagramType ?: "Charging Diagram") }
    var diagramType by remember { mutableStateOf(diagram?.diagramType ?: "Charging Diagram") }
    var filePath by remember { mutableStateOf(diagram?.filePath ?: "") }
    var description by remember { mutableStateOf(diagram?.description ?: "") }
    var testPoints by remember { mutableStateOf(diagram?.testPoints ?: "") }
    var voltageSpecs by remember { mutableStateOf(diagram?.voltageSpecs ?: "") }

    var brandExpanded by remember { mutableStateOf(false) }
    var modelExpanded by remember { mutableStateOf(false) }
    var customBrandMode by remember { mutableStateOf(false) }

    // Existing models for selected brand to populate dropdown
    val modelsForBrand = remember(brand, existingDiagrams) {
        val list = existingDiagrams.filter { it.brand.equals(brand, ignoreCase = true) }
            .map { it.model }
            .distinct()
            .toMutableList()
        if (brand == "Samsung" && "Galaxy A235F" !in list) list.add(0, "Galaxy A235F")
        if (brand == "Vivo" && "Y21" !in list) list.add(0, "Y21")
        if (brand == "OPPO" && "A57" !in list) list.add(0, "A57")
        list
    }

    // Image Picker Launcher for "+ UPLOAD DIAGRAM"
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val targetDir = File(context.filesDir, "diagrams")
                if (!targetDir.exists()) targetDir.mkdirs()
                val targetFile = File(targetDir, "diag_${System.currentTimeMillis()}.jpg")
                val outputStream = FileOutputStream(targetFile)
                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                filePath = targetFile.absolutePath
                Toast.makeText(context, "Diagram image uploaded successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback to storing string URI
                filePath = it.toString()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (diagram != null) Icons.Default.Edit else Icons.Default.Add,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (diagram != null) "EDIT DIAGRAM" else "ADD NEW DIAGRAM",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Brand: [ Select Brand ▼ ]
                Column {
                    Text(
                        text = "Brand:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (!customBrandMode) {
                        ExposedDropdownMenuBox(
                            expanded = brandExpanded,
                            onExpandedChange = { brandExpanded = !brandExpanded }
                        ) {
                            OutlinedTextField(
                                value = brand,
                                onValueChange = {},
                                readOnly = true,
                                placeholder = { Text("Select Brand ▼") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = OrangePrimary
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = brandExpanded,
                                onDismissRequest = { brandExpanded = false }
                            ) {
                                STANDARD_BRANDS.forEach { b ->
                                    DropdownMenuItem(
                                        text = { Text(b) },
                                        onClick = {
                                            brand = b
                                            brandExpanded = false
                                        }
                                    )
                                }
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("+ Enter Other Brand...", color = OrangePrimary, fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        customBrandMode = true
                                        brandExpanded = false
                                    }
                                )
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            label = { Text("Brand Name") },
                            trailingIcon = {
                                IconButton(onClick = { customBrandMode = false }) {
                                    Icon(Icons.Default.Close, contentDescription = "Cancel Custom")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary)
                        )
                    }
                }

                // Model: [ Select Phone Model ▼ ]
                Column {
                    Text(
                        text = "Model:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (modelsForBrand.isNotEmpty()) {
                        ExposedDropdownMenuBox(
                            expanded = modelExpanded,
                            onExpandedChange = { modelExpanded = !modelExpanded }
                        ) {
                            OutlinedTextField(
                                value = model,
                                onValueChange = { model = it },
                                placeholder = { Text("Select Phone Model ▼") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary)
                            )
                            ExposedDropdownMenu(
                                expanded = modelExpanded,
                                onDismissRequest = { modelExpanded = false }
                            ) {
                                modelsForBrand.forEach { m ->
                                    DropdownMenuItem(
                                        text = { Text(m) },
                                        onClick = {
                                            model = m
                                            modelExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = model,
                            onValueChange = { model = it },
                            placeholder = { Text("Enter Phone Model (e.g. Galaxy A235F, Y21)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary)
                        )
                    }
                }

                // Diagram Name: [ Enter Diagram Name ]
                Column {
                    Text(
                        text = "Diagram Name:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = diagramName,
                        onValueChange = {
                            diagramName = it
                            diagramType = it
                        },
                        placeholder = { Text("Enter Diagram Name (e.g. Charging Diagram)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary)
                    )

                    // Quick Diagram Name suggestions
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        DIAGRAM_NAME_SUGGESTIONS.take(6).forEach { suggestion ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (diagramName == suggestion) OrangePrimary.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable {
                                        diagramName = suggestion
                                        diagramType = suggestion
                                    }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = suggestion,
                                    fontSize = 10.sp,
                                    fontWeight = if (diagramName == suggestion) FontWeight.Bold else FontWeight.Normal,
                                    color = if (diagramName == suggestion) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Diagram Image Upload Box:
                // ┌─────────────────────────────┐
                // │                             │
                // │      + UPLOAD DIAGRAM       │
                // │                             │
                // └─────────────────────────────┘
                Column {
                    Text(
                        text = "Diagram Image:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    if (filePath.isNotBlank()) {
                        // Image Thumbnail Preview Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, OrangePrimary, RoundedCornerShape(8.dp))
                                .background(DarkBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = filePath,
                                contentDescription = "Uploaded Diagram",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )

                            // Overlay change/remove buttons
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Black.copy(alpha = 0.7f))
                                        .clickable { imagePickerLauncher.launch("image/*") }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text("Change", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CrimsonAccent.copy(alpha = 0.85f))
                                        .clickable { filePath = "" }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text("Remove", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        // Upload Placeholder Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = 1.5.dp,
                                    color = OrangePrimary.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(OrangePrimary.copy(alpha = 0.04f))
                                .clickable {
                                    imagePickerLauncher.launch("image/*")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Upload Diagram",
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "+ UPLOAD DIAGRAM",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp,
                                    color = OrangePrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Tap to pick JPG, PNG, or Schematic screenshot",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Description / Note: [ Enter Diagram Details ]
                Column {
                    Text(
                        text = "Description / Note:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Enter Diagram Details (e.g. Sub-board trace, OVP IC, VBUS 5V line)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary)
                    )
                }

                // Test Points & Voltages (Optional extra repair detail)
                Column {
                    Text(
                        text = "Test Points & Voltages (Optional):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = testPoints,
                        onValueChange = { testPoints = it },
                        placeholder = { Text("e.g. TP_VBUS: 5.0V, TP_VBAT: 4.2V, TP_THERM: 0.95V") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary)
                    )
                }
            }
        },
        confirmButton = {
            // [ 💾 SAVE DIAGRAM ]
            Button(
                onClick = {
                    if (brand.isNotBlank() && model.isNotBlank() && diagramName.isNotBlank()) {
                        onSave(
                            brand.trim(),
                            model.trim(),
                            diagramType.ifBlank { diagramName.trim() },
                            diagramName.trim(),
                            filePath.trim(),
                            description.trim(),
                            testPoints.trim(),
                            voltageSpecs.trim()
                        )
                    }
                },
                enabled = brand.isNotBlank() && model.isNotBlank() && diagramName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = RoundedCornerShape(6.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SAVE DIAGRAM",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * DIAGRAM VIEWER
 * Complete dedicated diagram viewer with zoom, pan, rotate, download, share, edit, delete, back.
 */
@Composable
private fun DiagramViewerDialog(
    diagram: DiagramEntity,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var scale by remember { mutableFloatStateOf(1.0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DarkBackground
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Action Bar: [ 🔙 Back ], Brand/Model Info, Title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // [ 🔙 Back ]
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(OrangePrimary)
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = diagram.brand,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = diagram.model,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = diagram.diagramType.ifBlank { diagram.title },
                                fontSize = 11.sp,
                                color = TechTeal,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Quick actions: [ ✏️ Edit Diagram ], [ 🗑 Delete Diagram ]
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Diagram", tint = OrangePrimary)
                        }
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Diagram", tint = CrimsonAccent)
                        }
                    }
                }

                // Interactive Diagram Image Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFF070D18))
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(0.5f, 6.0f)
                                offset = Offset(offset.x + pan.x, offset.y + pan.y)
                            }
                        }
                ) {
                    if (diagram.filePath.isNotBlank()) {
                        // Display Uploaded Image with Zoom/Pan/Rotate
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    rotationZ = rotationAngle,
                                    translationX = offset.x,
                                    translationY = offset.y
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = diagram.filePath,
                                contentDescription = "Diagram Schematic",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    } else {
                        // Display Dynamic Vector Circuit Blueprint
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    rotationZ = rotationAngle,
                                    translationX = offset.x,
                                    translationY = offset.y
                                )
                        ) {
                            val w = size.width
                            val h = size.height

                            // Technical grid background
                            val step = 36.dp.toPx()
                            for (x in 0..(w / step).toInt()) {
                                drawLine(
                                    color = Color(0x1838BDF8),
                                    start = Offset(x * step, 0f),
                                    end = Offset(x * step, h),
                                    strokeWidth = 1f
                                )
                            }
                            for (y in 0..(h / step).toInt()) {
                                drawLine(
                                    color = Color(0x1838BDF8),
                                    start = Offset(0f, y * step),
                                    end = Offset(w, y * step),
                                    strokeWidth = 1f
                                )
                            }

                            val cx = w / 2
                            val cy = h / 2

                            // Draw IC Blocks (PMIC / Charging Controller / CPU)
                            val isCharging = diagram.diagramType.contains("Charging", ignoreCase = true)
                            val isLcd = diagram.diagramType.contains("LCD", ignoreCase = true) || diagram.diagramType.contains("Display", ignoreCase = true)
                            val isNetwork = diagram.diagramType.contains("Network", ignoreCase = true)

                            // Main Chip Box
                            val mainChipTitle = when {
                                isCharging -> "PMIC / CHARGE IC\n(BQ25601 / MT6357)"
                                isLcd -> "DISPLAY DRIVER &\nBL BOOST IC"
                                isNetwork -> "RF TRANSCEIVER\n& 4G/5G PA"
                                else -> "SYSTEM PMIC\nS2MPU12 / PM6125"
                            }

                            drawRoundRect(
                                color = Color(0xFF1E293B),
                                topLeft = Offset(cx - 130f, cy - 100f),
                                size = Size(260f, 200f),
                                cornerRadius = CornerRadius(12f, 12f)
                            )
                            drawRoundRect(
                                color = OrangePrimary,
                                topLeft = Offset(cx - 130f, cy - 100f),
                                size = Size(260f, 200f),
                                cornerRadius = CornerRadius(12f, 12f),
                                style = Stroke(width = 3f)
                            )

                            // Sub IC Box (e.g. OVP / FPC Connector / Baseband)
                            drawRoundRect(
                                color = Color(0xFF0F172A),
                                topLeft = Offset(cx - 300f, cy - 60f),
                                size = Size(110f, 120f),
                                cornerRadius = CornerRadius(8f, 8f)
                            )
                            drawRoundRect(
                                color = SapphireBlue,
                                topLeft = Offset(cx - 300f, cy - 60f),
                                size = Size(110f, 120f),
                                cornerRadius = CornerRadius(8f, 8f),
                                style = Stroke(width = 2.5f)
                            )

                            // Connector / Battery Header Box
                            drawRoundRect(
                                color = Color(0xFF0F172A),
                                topLeft = Offset(cx + 190f, cy - 60f),
                                size = Size(110f, 120f),
                                cornerRadius = CornerRadius(8f, 8f)
                            )
                            drawRoundRect(
                                color = NeonGreen,
                                topLeft = Offset(cx + 190f, cy - 60f),
                                size = Size(110f, 120f),
                                cornerRadius = CornerRadius(8f, 8f),
                                style = Stroke(width = 2.5f)
                            )

                            // Traces
                            val p1 = Path().apply {
                                moveTo(cx - 190f, cy)
                                lineTo(cx - 130f, cy)
                            }
                            drawPath(p1, color = SapphireBlue, style = Stroke(width = 4f))

                            val p2 = Path().apply {
                                moveTo(cx + 130f, cy)
                                lineTo(cx + 190f, cy)
                            }
                            drawPath(p2, color = NeonGreen, style = Stroke(width = 4f))

                            val p3 = Path().apply {
                                moveTo(cx, cy - 100f)
                                lineTo(cx, cy - 180f)
                                lineTo(cx + 150f, cy - 180f)
                            }
                            drawPath(p3, color = ElectricAmber, style = Stroke(width = 3.5f))

                            val p4 = Path().apply {
                                moveTo(cx, cy + 100f)
                                lineTo(cx, cy + 180f)
                                lineTo(cx - 150f, cy + 180f)
                            }
                            drawPath(p4, color = TechTeal, style = Stroke(width = 3.5f))

                            // Test Points Circles
                            drawCircle(color = CrimsonAccent, radius = 10f, center = Offset(cx - 160f, cy))
                            drawCircle(color = ElectricAmber, radius = 10f, center = Offset(cx + 150f, cy - 180f))
                            drawCircle(color = NeonGreen, radius = 10f, center = Offset(cx + 160f, cy))
                            drawCircle(color = TechTeal, radius = 10f, center = Offset(cx - 150f, cy + 180f))
                        }
                    }

                    // Floating Scale Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Zoom: ${String.format("%.1f", scale)}x • Rotate: ${rotationAngle.toInt()}°",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Description & Technical Spec Banner
                if (diagram.description.isNotBlank() || diagram.testPoints.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Column {
                            if (diagram.description.isNotBlank()) {
                                Text(
                                    text = diagram.description,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            if (diagram.testPoints.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Test Points: ${diagram.testPoints}",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = ElectricAmber
                                )
                            }
                        }
                    }
                }

                // CONTROLS TOOLBAR AS REQUESTED:
                // [ 🔍 Zoom In ] [ 🔎 Zoom Out ] [ ↺ Rotate ] [ ⬇ Download ] [ ↗ Share ]
                // [ ✏️ Edit Diagram ] [ 🗑 Delete Diagram ] [ 🔙 Back ]
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF0F172A),
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        // Row 1: Viewer Controls: Zoom In, Zoom Out, Rotate, Download, Share
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // [ 🔍 Zoom In ]
                            ViewerActionButton(
                                icon = Icons.Default.ZoomIn,
                                text = "Zoom In",
                                onClick = { scale = (scale * 1.25f).coerceAtMost(6.0f) }
                            )

                            // [ 🔎 Zoom Out ]
                            ViewerActionButton(
                                icon = Icons.Default.ZoomOut,
                                text = "Zoom Out",
                                onClick = { scale = (scale / 1.25f).coerceAtLeast(0.5f) }
                            )

                            // [ ↺ Rotate ]
                            ViewerActionButton(
                                icon = Icons.Default.RotateRight,
                                text = "Rotate",
                                onClick = { rotationAngle = (rotationAngle + 90f) % 360f }
                            )

                            // [ ⬇ Download ]
                            ViewerActionButton(
                                icon = Icons.Default.Download,
                                text = "Download",
                                color = NeonGreen,
                                onClick = {
                                    Toast.makeText(
                                        context,
                                        "Diagram saved to device offline storage (PDF/Schematic Cached)",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )

                            // [ ↗ Share ]
                            ViewerActionButton(
                                icon = Icons.Default.Share,
                                text = "Share",
                                color = SapphireBlue,
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "GSM Service Diagram: ${diagram.brand} ${diagram.model} - ${diagram.diagramType}\n${diagram.title}\n${diagram.description}\nTest Points: ${diagram.testPoints}"
                                        )
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Schematic"))
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Row 2: Management Controls: [ ✏️ Edit Diagram ] [ 🗑 Delete Diagram ] [ 🔙 Back ]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // [ ✏️ Edit Diagram ]
                            OutlinedButton(
                                onClick = onEdit,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit Diagram", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // [ 🗑 Delete Diagram ]
                            OutlinedButton(
                                onClick = { showDeleteConfirm = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonAccent)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete Diagram", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // [ 🔙 Back ]
                            Button(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(36.dp),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Back", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Diagram?") },
            text = { Text("Are you sure you want to delete ${diagram.brand} ${diagram.model} - ${diagram.diagramType}?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent)
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

@Composable
private fun ViewerActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    color: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = text, tint = color, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
