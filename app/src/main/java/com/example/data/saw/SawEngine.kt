package com.example.data.saw

import com.example.data.model.AlternativeEntity
import com.example.data.model.AlternativeValueEntity
import com.example.data.model.CriteriaEntity

data class NormalizedValue(
    val alternativeId: Int,
    val criteriaCode: String,
    val originalValue: Double,
    val normalizedValue: Double
)

data class AlternativePreference(
    val alternative: AlternativeEntity,
    val preferenceValue: Double,
    val rank: Int = 0
)

data class SawResult(
    val normalizedMatrix: List<NormalizedValue>, // Rij
    val preferences: List<AlternativePreference> // Vi, sorted
)

object SawEngine {
    fun calculate(
        criteriaList: List<CriteriaEntity>,
        alternatives: List<AlternativeEntity>,
        values: List<AlternativeValueEntity>
    ): SawResult {
        if (criteriaList.isEmpty() || alternatives.isEmpty() || values.isEmpty()) {
            return SawResult(emptyList(), emptyList())
        }

        // Map for quick lookup: alternativeId -> criteriaCode -> value
        val valueMap = values.groupBy { it.alternativeId }
            .mapValues { (_, valList) -> valList.associate { it.criteriaCode to it.value } }

        // Find min and max for each criteria
        val mins = mutableMapOf<String, Double>()
        val maxs = mutableMapOf<String, Double>()

        for (criteria in criteriaList) {
            val critValues = values.filter { it.criteriaCode == criteria.code }.map { it.value }
            if (critValues.isNotEmpty()) {
                mins[criteria.code] = critValues.minOrNull() ?: 0.0
                maxs[criteria.code] = critValues.maxOrNull() ?: 1.0
            } else {
                mins[criteria.code] = 0.0
                maxs[criteria.code] = 1.0
            }
        }

        // Perform Normalization
        val normalizedValues = mutableListOf<NormalizedValue>()
        for (alt in alternatives) {
            val altValues = valueMap[alt.id] ?: emptyMap()
            for (criteria in criteriaList) {
                val originalValue = altValues[criteria.code] ?: 0.0
                val normalizedVal = if (criteria.isBenefit) {
                    val maxVal = maxs[criteria.code] ?: 1.0
                    if (maxVal > 0.0) originalValue / maxVal else 0.0
                } else {
                    val minVal = mins[criteria.code] ?: 0.0
                    if (originalValue > 0.0) minVal / originalValue else 0.0
                }
                normalizedValues.add(
                    NormalizedValue(
                        alternativeId = alt.id,
                        criteriaCode = criteria.code,
                        originalValue = originalValue,
                        normalizedValue = normalizedVal
                    )
                )
            }
        }

        // Calculate Preference (Vi)
        val prefMap = normalizedValues.groupBy { it.alternativeId }
        val preferences = alternatives.map { alt ->
            val altNorms = prefMap[alt.id] ?: emptyList()
            var sum = 0.0
            for (norm in altNorms) {
                val criteria = criteriaList.find { it.code == norm.criteriaCode }
                if (criteria != null) {
                    sum += norm.normalizedValue * criteria.weight
                }
            }
            AlternativePreference(alternative = alt, preferenceValue = sum)
        }

        // Sort preferences descending and assign rank
        val sortedPreferences = preferences.sortedByDescending { it.preferenceValue }
            .mapIndexed { index, pref ->
                pref.copy(rank = index + 1)
            }

        return SawResult(
            normalizedMatrix = normalizedValues,
            preferences = sortedPreferences
        )
    }
}
