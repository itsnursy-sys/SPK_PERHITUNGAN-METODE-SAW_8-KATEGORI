package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AlternativeEntity
import com.example.data.model.AlternativeValueEntity
import com.example.data.model.CriteriaEntity

@Composable
fun AlternativeScreen(
    criteriaList: List<CriteriaEntity>,
    alternatives: List<AlternativeEntity>,
    alternativeValues: List<AlternativeValueEntity>,
    onUpdateAlternativeValues: (Int, List<AlternativeValueEntity>) -> Unit,
    onUpdateAlternativeName: (Int, String) -> Unit,
    onAddAlternative: (String, List<AlternativeValueEntity>) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingAlternative by remember { mutableStateOf<AlternativeEntity?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val valueMap = remember(alternativeValues) {
        alternativeValues.groupBy { it.alternativeId }
            .mapValues { (_, list) -> list.associateBy { it.criteriaCode } }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Hero / Header Section
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.secondary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = "Alternatives",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Matriks Alternatif",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Masukkan nilai kecocokan setiap alternatif terhadap masing-masing kriteria.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            if (alternatives.isEmpty() || criteriaList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "No data",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "Tidak ada data alternatif atau kriteria.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                // Main Matrix Table Grid
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        val scrollState = rememberScrollState()

                        // Table Header Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            // Frozen Column: Alternative Title
                            Text(
                                text = "Alternatif",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .width(110.dp)
                                    .padding(12.dp),
                                textAlign = TextAlign.Start,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            // Scrollable Columns: Criteria Codes
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .horizontalScroll(scrollState)
                            ) {
                                criteriaList.forEach { criteria ->
                                    Text(
                                        text = criteria.code,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .width(70.dp)
                                            .padding(vertical = 12.dp),
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Space for Action column header
                            Text(
                                text = "Aksi",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .width(60.dp)
                                    .padding(vertical = 12.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        // Table Content Rows
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(alternatives, key = { it.id }) { alternative ->
                                val altValues = valueMap[alternative.id] ?: emptyMap()
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Frozen Column: Alternative Name
                                    Column(
                                        modifier = Modifier
                                            .width(110.dp)
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = alternative.name.split(" ").firstOrNull() ?: alternative.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = alternative.name.substringAfter("(", "").substringBefore(")"),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    // Scrollable Columns: Values
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .horizontalScroll(scrollState),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        criteriaList.forEach { criteria ->
                                            val cellValue = altValues[criteria.code]?.value ?: 0.0
                                            Text(
                                                text = String.format("%.1f", cellValue),
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier
                                                    .width(70.dp)
                                                    .padding(vertical = 12.dp),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }

                                    // Action Column
                                    Box(
                                        modifier = Modifier.width(60.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        IconButton(
                                            onClick = { editingAlternative = alternative },
                                            modifier = Modifier.testTag("edit_alternative_btn_${alternative.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit ${alternative.name}",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // Padding for FAB
        }

        // Floating Action Button to Add New Alternative
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp)
                .testTag("add_alternative_fab")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Tambah Alternatif"
            )
        }
    }

    // Edit Alternative Dialog
    editingAlternative?.let { alternative ->
        val currentValues = valueMap[alternative.id]?.values?.toList() ?: emptyList()
        EditAlternativeDialog(
            alternative = alternative,
            criteriaList = criteriaList,
            currentValues = currentValues,
            onDismiss = { editingAlternative = null },
            onSave = { updatedName, updatedValues ->
                onUpdateAlternativeName(alternative.id, updatedName)
                onUpdateAlternativeValues(alternative.id, updatedValues)
                editingAlternative = null
            }
        )
    }

    // Add Alternative Dialog
    if (showAddDialog) {
        AddAlternativeDialog(
            criteriaList = criteriaList,
            onDismiss = { showAddDialog = false },
            onSave = { name, values ->
                onAddAlternative(name, values)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun EditAlternativeDialog(
    alternative: AlternativeEntity,
    criteriaList: List<CriteriaEntity>,
    currentValues: List<AlternativeValueEntity>,
    onDismiss: () -> Unit,
    onSave: (String, List<AlternativeValueEntity>) -> Unit
) {
    var name by remember { mutableStateOf(alternative.name) }
    
    // Store values as mutable state map
    val valMap = remember(currentValues) {
        criteriaList.associate { criteria ->
            val existing = currentValues.find { it.criteriaCode == criteria.code }
            criteria.code to mutableStateOf(existing?.value?.toString() ?: "0.0")
        }
    }

    var errorText by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Edit Alternatif",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // LazyColumn to handle inputs without layout overflow
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nama Alternatif") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("edit_alt_name_field")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nilai Kriteria:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(criteriaList) { criteria ->
                        val stateValue = valMap[criteria.code]!!
                        OutlinedTextField(
                            value = stateValue.value,
                            onValueChange = { stateValue.value = it },
                            label = { Text("${criteria.code} - ${criteria.name}") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("edit_alt_val_field_${criteria.code}")
                        )
                    }
                }

                // Error Message Panel
                AnimatedVisibility(visible = errorText != null) {
                    Text(
                        text = errorText ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorText = "Nama tidak boleh kosong"
                                return@Button
                            }

                            val resultValues = mutableListOf<AlternativeValueEntity>()
                            for (criteria in criteriaList) {
                                val textVal = valMap[criteria.code]?.value ?: ""
                                val parsed = textVal.toDoubleOrNull()
                                if (parsed == null || parsed < 0.0) {
                                    errorText = "Nilai untuk ${criteria.code} harus berupa angka numerik non-negatif"
                                    return@Button
                                }
                                resultValues.add(
                                    AlternativeValueEntity(
                                        alternativeId = alternative.id,
                                        criteriaCode = criteria.code,
                                        value = parsed
                                    )
                                )
                            }
                            onSave(name, resultValues)
                        },
                        modifier = Modifier.testTag("dialog_save_alt_btn")
                    ) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}

@Composable
fun AddAlternativeDialog(
    criteriaList: List<CriteriaEntity>,
    onDismiss: () -> Unit,
    onSave: (String, List<AlternativeValueEntity>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    
    // Store values as mutable state map starting with "0.0"
    val valMap = remember {
        criteriaList.associate { criteria ->
            criteria.code to mutableStateOf("0.0")
        }
    }

    var errorText by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Tambah Alternatif Baru",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nama Alternatif (cth: A6 - Faisal)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("add_alt_name_field")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nilai Kriteria:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(criteriaList) { criteria ->
                        val stateValue = valMap[criteria.code]!!
                        OutlinedTextField(
                            value = stateValue.value,
                            onValueChange = { stateValue.value = it },
                            label = { Text("${criteria.code} - ${criteria.name}") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("add_alt_val_field_${criteria.code}")
                        )
                    }
                }

                AnimatedVisibility(visible = errorText != null) {
                    Text(
                        text = errorText ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorText = "Nama tidak boleh kosong"
                                return@Button
                            }

                            val resultValues = mutableListOf<AlternativeValueEntity>()
                            for (criteria in criteriaList) {
                                val textVal = valMap[criteria.code]?.value ?: ""
                                val parsed = textVal.toDoubleOrNull()
                                if (parsed == null || parsed < 0.0) {
                                    errorText = "Nilai untuk ${criteria.code} harus berupa angka numerik non-negatif"
                                    return@Button
                                }
                                resultValues.add(
                                    AlternativeValueEntity(
                                        alternativeId = 0, // Placeholder, will be mapped in repository
                                        criteriaCode = criteria.code,
                                        value = parsed
                                    )
                                )
                            }
                            onSave(name, resultValues)
                        },
                        modifier = Modifier.testTag("dialog_add_alt_btn")
                    ) {
                        Text("Tambah")
                    }
                }
            }
        }
    }
}
