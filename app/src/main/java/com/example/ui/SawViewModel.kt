package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.AlternativeEntity
import com.example.data.model.AlternativeValueEntity
import com.example.data.model.CriteriaEntity
import com.example.data.repository.SawRepository
import com.example.data.saw.DefaultData
import com.example.data.saw.SawEngine
import com.example.data.saw.SawResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SawViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SawRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = SawRepository(database.sawDao())

        // Pre-populate database with default data if empty on start
        viewModelScope.launch {
            val existingCriteria = repository.allCriteria.first()
            if (existingCriteria.isEmpty()) {
                resetToDefault()
            } else {
                calculateSaw()
            }
        }
    }

    val criteriaList: StateFlow<List<CriteriaEntity>> = repository.allCriteria
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alternatives: StateFlow<List<AlternativeEntity>> = repository.allAlternatives
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alternativeValues: StateFlow<List<AlternativeValueEntity>> = repository.allAlternativeValues
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _sawResult = MutableStateFlow<SawResult?>(null)
    val sawResult: StateFlow<SawResult?> = _sawResult.asStateFlow()

    private val _isCalculating = MutableStateFlow(false)
    val isCalculating: StateFlow<Boolean> = _isCalculating.asStateFlow()

    fun calculateSaw() {
        viewModelScope.launch {
            _isCalculating.value = true
            val crit = criteriaList.value
            val alts = alternatives.value
            val vals = alternativeValues.value
            
            val result = SawEngine.calculate(crit, alts, vals)
            _sawResult.value = result
            _isCalculating.value = false
        }
    }

    fun resetToDefault() {
        viewModelScope.launch {
            _isCalculating.value = true
            repository.resetToDefault(
                DefaultData.criteriaList,
                DefaultData.alternatives,
                DefaultData.values
            )
            // Recalculate automatically with the fresh default dataset
            val crit = DefaultData.criteriaList
            val alts = DefaultData.alternatives
            val vals = DefaultData.values
            _sawResult.value = SawEngine.calculate(crit, alts, vals)
            _isCalculating.value = false
        }
    }

    fun updateCriteria(criteria: CriteriaEntity) {
        viewModelScope.launch {
            repository.updateCriteria(criteria)
        }
    }

    fun updateAlternativeName(id: Int, name: String) {
        viewModelScope.launch {
            repository.updateAlternativeName(id, name)
        }
    }

    fun updateAlternativeValues(alternativeId: Int, values: List<AlternativeValueEntity>) {
        viewModelScope.launch {
            repository.saveAlternativeValues(values)
        }
    }

    fun addNewAlternative(name: String, values: List<AlternativeValueEntity>) {
        viewModelScope.launch {
            repository.insertAlternativeWithValues(name, values)
        }
    }
}
