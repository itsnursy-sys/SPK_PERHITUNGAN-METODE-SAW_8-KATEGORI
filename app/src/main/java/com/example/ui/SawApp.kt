package com.example.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AlternativeScreen
import com.example.ui.screens.CalculationScreen
import com.example.ui.screens.CriteriaScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SawApp(
    viewModel: SawViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val criteriaList by viewModel.criteriaList.collectAsState()
    val alternatives by viewModel.alternatives.collectAsState()
    val alternativeValues by viewModel.alternativeValues.collectAsState()
    val sawResult by viewModel.sawResult.collectAsState()
    val isCalculating by viewModel.isCalculating.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "SPK - Metode SAW",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = MaterialTheme.typography.titleLarge.fontWeight
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.List, contentDescription = "Kriteria") },
                    label = { Text("Kriteria") },
                    modifier = Modifier.testTag("tab_kriteria")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.People, contentDescription = "Alternatif") },
                    label = { Text("Alternatif") },
                    modifier = Modifier.testTag("tab_alternatif")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = "Perhitungan") },
                    label = { Text("Perhitungan") },
                    modifier = Modifier.testTag("tab_perhitungan")
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        when (selectedTab) {
            0 -> {
                CriteriaScreen(
                    criteriaList = criteriaList,
                    onUpdateCriteria = { viewModel.updateCriteria(it) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            1 -> {
                AlternativeScreen(
                    criteriaList = criteriaList,
                    alternatives = alternatives,
                    alternativeValues = alternativeValues,
                    onUpdateAlternativeValues = { altId, vals -> viewModel.updateAlternativeValues(altId, vals) },
                    onUpdateAlternativeName = { altId, name -> viewModel.updateAlternativeName(altId, name) },
                    onAddAlternative = { name, vals -> viewModel.addNewAlternative(name, vals) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            2 -> {
                CalculationScreen(
                    criteriaList = criteriaList,
                    alternatives = alternatives,
                    sawResult = sawResult,
                    isCalculating = isCalculating,
                    onCalculate = { viewModel.calculateSaw() },
                    onReset = { viewModel.resetToDefault() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
