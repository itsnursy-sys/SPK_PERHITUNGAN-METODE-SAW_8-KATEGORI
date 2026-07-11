package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CriteriaEntity

@Composable
fun CriteriaScreen(
    criteriaList: List<CriteriaEntity>,
    onUpdateCriteria: (CriteriaEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingCriteria by remember { mutableStateOf<CriteriaEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Hero Section / Header
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Criteria Configuration",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Konfigurasi Kriteria",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Kriteria menentukan bobot (W) dan arah optimasi (Benefit/Cost) dalam perhitungan metode SAW.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // List of Criteria
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(criteriaList, key = { it.code }) { criteria ->
                CriteriaItem(
                    criteria = criteria,
                    onClick = { editingCriteria = criteria }
                )
            }
        }
    }

    // Edit Dialog
    editingCriteria?.let { criteria ->
        EditCriteriaDialog(
            criteria = criteria,
            onDismiss = { editingCriteria = null },
            onSave = { updated ->
                onUpdateCriteria(updated)
                editingCriteria = null
            }
        )
    }
}

@Composable
fun CriteriaItem(
    criteria: CriteriaEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("criteria_item_${criteria.code}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left circular indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = criteria.code,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Criteria Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = criteria.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Bobot: ${criteria.weight}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    // Type Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (criteria.isBenefit) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.errorContainer
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (criteria.isBenefit) "Benefit" else "Cost",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (criteria.isBenefit) MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            IconButton(
                onClick = onClick,
                modifier = Modifier.testTag("edit_criteria_button_${criteria.code}")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Kriteria ${criteria.code}",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun EditCriteriaDialog(
    criteria: CriteriaEntity,
    onDismiss: () -> Unit,
    onSave: (CriteriaEntity) -> Unit
) {
    var name by remember { mutableStateOf(criteria.name) }
    var weightText by remember { mutableStateOf(criteria.weight.toString()) }
    var isBenefit by remember { mutableStateOf(criteria.isBenefit) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Edit Kriteria ${criteria.code}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Kriteria") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_criteria_name_input")
                )

                // Weight Input
                OutlinedTextField(
                    value = weightText,
                    onValueChange = {
                        weightText = it
                        errorText = null
                    },
                    label = { Text("Bobot (0.0 - 1.0)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = errorText != null,
                    modifier = Modifier.fillMaxWidth().testTag("edit_criteria_weight_input")
                )

                // Type Selection (Benefit vs Cost)
                Text(
                    text = "Tipe Kriteria",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ElevatedFilterChip(
                        selected = isBenefit,
                        onClick = { isBenefit = true },
                        label = { Text("Benefit") },
                        modifier = Modifier.weight(1f).testTag("chip_benefit")
                    )

                    ElevatedFilterChip(
                        selected = !isBenefit,
                        onClick = { isBenefit = false },
                        label = { Text("Cost") },
                        colors = FilterChipDefaults.elevatedFilterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier.weight(1f).testTag("chip_cost")
                    )
                }

                // Error Message
                AnimatedVisibility(visible = errorText != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Error Info",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = errorText ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                // Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("dialog_cancel_button")
                    ) {
                        Text("Batal")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val weight = weightText.toDoubleOrNull()
                            if (name.isBlank()) {
                                errorText = "Nama kriteria tidak boleh kosong"
                            } else if (weight == null || weight < 0.0 || weight > 10.0) {
                                // Although prompt states 0.0 to 1.0 is default, we allow any reasonable non-negative weight.
                                errorText = "Bobot harus berupa angka numerik non-negatif"
                            } else {
                                onSave(criteria.copy(name = name, weight = weight, isBenefit = isBenefit))
                            }
                        },
                        modifier = Modifier.testTag("dialog_save_button")
                    ) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}
