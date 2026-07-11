package com.example.data.repository

import com.example.data.database.SawDao
import com.example.data.model.AlternativeEntity
import com.example.data.model.AlternativeValueEntity
import com.example.data.model.CriteriaEntity
import kotlinx.coroutines.flow.Flow

class SawRepository(private val sawDao: SawDao) {
    val allCriteria: Flow<List<CriteriaEntity>> = sawDao.getAllCriteria()
    val allAlternatives: Flow<List<AlternativeEntity>> = sawDao.getAllAlternatives()
    val allAlternativeValues: Flow<List<AlternativeValueEntity>> = sawDao.getAllAlternativeValues()

    suspend fun updateCriteria(criteria: CriteriaEntity) {
        sawDao.updateCriteria(criteria.code, criteria.name, criteria.weight, criteria.isBenefit)
    }

    suspend fun updateAlternativeName(id: Int, name: String) {
        sawDao.updateAlternativeName(id, name)
    }

    suspend fun saveAlternativeValues(values: List<AlternativeValueEntity>) {
        sawDao.insertAlternativeValues(values)
    }

    suspend fun insertAlternativeWithValues(name: String, values: List<AlternativeValueEntity>) {
        val altId = sawDao.insertAlternative(AlternativeEntity(name = name))
        val valuesWithId = values.map { it.copy(alternativeId = altId.toInt()) }
        sawDao.insertAlternativeValues(valuesWithId)
    }

    suspend fun resetToDefault(
        defaultCriteria: List<CriteriaEntity>,
        defaultAlternatives: List<AlternativeEntity>,
        defaultValues: List<AlternativeValueEntity>
    ) {
        sawDao.resetToDefault(defaultCriteria, defaultAlternatives, defaultValues)
    }
}
