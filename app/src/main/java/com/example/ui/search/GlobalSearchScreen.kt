package com.example.ui.search

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StatusBadge
import com.example.ui.navigation.Screen
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.SapphireBlue
import com.example.ui.theme.SoftPurple
import com.example.ui.theme.TechTeal
import com.example.viewmodel.GsmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchScreen(
    viewModel: GsmViewModel,
    onNavigateBack: () -> Unit,
    onCustomerSelected: (Long) -> Unit,
    onNavigateToScreen: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.globalSearchQuery.collectAsState()
    val searchResults by viewModel.globalSearchResults.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setGlobalSearchQuery(it) },
                        placeholder = { Text("Search everything in GSM Database...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setGlobalSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        if (searchQuery.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Search across Models, Schematics, LCDs, Files, and Customers",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (searchResults.totalCount == 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No results found for \"$searchQuery\"",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Customers Section
                if (searchResults.customers.isNotEmpty()) {
                    item {
                        SearchSectionHeader(
                            title = "Customer Job Cards (${searchResults.customers.size})",
                            icon = Icons.Default.PeopleAlt,
                            color = OrangePrimary
                        )
                    }
                    items(searchResults.customers, key = { "c_${it.id}" }) { customer ->
                        SearchResultCard(
                            title = customer.customerName,
                            subtitle = "${customer.brand} ${customer.model} • ${customer.serviceType} • ৳${customer.dueAmount.toInt()} due",
                            tag = customer.status,
                            tagColor = OrangePrimary,
                            onClick = { onCustomerSelected(customer.id) }
                        )
                    }
                }

                // Phone Models Section
                if (searchResults.models.isNotEmpty()) {
                    item {
                        SearchSectionHeader(
                            title = "Phone Models (${searchResults.models.size})",
                            icon = Icons.Default.PhoneAndroid,
                            color = SapphireBlue
                        )
                    }
                    items(searchResults.models, key = { "m_${it.id}" }) { model ->
                        SearchResultCard(
                            title = "${model.brand} ${model.modelName}",
                            subtitle = "Chipset: ${model.chipset} • RAM/Storage: ${model.ram}/${model.storage}",
                            tag = model.brand,
                            tagColor = SapphireBlue,
                            onClick = { onNavigateToScreen(Screen.Model.route) }
                        )
                    }
                }

                // Diagrams Section
                if (searchResults.diagrams.isNotEmpty()) {
                    item {
                        SearchSectionHeader(
                            title = "Schematics & Diagrams (${searchResults.diagrams.size})",
                            icon = Icons.Default.Memory,
                            color = TechTeal
                        )
                    }
                    items(searchResults.diagrams, key = { "d_${it.id}" }) { diagram ->
                        SearchResultCard(
                            title = "${diagram.brand} ${diagram.model} - ${diagram.title}",
                            subtitle = "Type: ${diagram.diagramType} • ${diagram.testPoints}",
                            tag = diagram.diagramType,
                            tagColor = TechTeal,
                            onClick = { onNavigateToScreen(Screen.Diagram.route) }
                        )
                    }
                }

                // LCDs Section
                if (searchResults.lcds.isNotEmpty()) {
                    item {
                        SearchSectionHeader(
                            title = "LCD Displays (${searchResults.lcds.size})",
                            icon = Icons.Default.Smartphone,
                            color = SoftPurple
                        )
                    }
                    items(searchResults.lcds, key = { "l_${it.id}" }) { lcd ->
                        SearchResultCard(
                            title = "${lcd.brand} ${lcd.model} - ${lcd.lcdName}",
                            subtitle = "Type: ${lcd.lcdType} • Price: ৳${lcd.price.toInt()} • Compatible: ${lcd.compatibleModels}",
                            tag = lcd.stock,
                            tagColor = SoftPurple,
                            onClick = { onNavigateToScreen(Screen.Lcd.route) }
                        )
                    }
                }

                // Files Section
                if (searchResults.files.isNotEmpty()) {
                    item {
                        SearchSectionHeader(
                            title = "Service Files & ROMs (${searchResults.files.size})",
                            icon = Icons.Default.Folder,
                            color = NeonGreen
                        )
                    }
                    items(searchResults.files, key = { "f_${it.id}" }) { file ->
                        SearchResultCard(
                            title = file.fileName,
                            subtitle = "Category: ${file.fileType} • Model: ${file.brand} ${file.model} • Size: ${file.fileSize}",
                            tag = file.fileType,
                            tagColor = NeonGreen,
                            onClick = { onNavigateToScreen(Screen.File.route) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchSectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SearchResultCard(
    title: String,
    subtitle: String,
    tag: String,
    tagColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(tagColor.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = tag,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = tagColor
                )
            }
        }
    }
}
