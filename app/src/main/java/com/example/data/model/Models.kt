package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "criteria")
data class CriteriaEntity(
    @PrimaryKey val code: String, // e.g., "C1"
    val name: String,             // e.g., "Pengalaman Kerja"
    val weight: Double,           // e.g., 0.8
    val isBenefit: Boolean        // true = Benefit, false = Cost
)

@Entity(tableName = "alternatives")
data class AlternativeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String              // e.g., "A1 (Budi)"
)

@Entity(
    tableName = "alternative_values",
    primaryKeys = ["alternativeId", "criteriaCode"]
)
data class AlternativeValueEntity(
    val alternativeId: Int,
    val criteriaCode: String,
    val value: Double
)
