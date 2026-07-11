package com.example.data.saw

import com.example.data.model.AlternativeEntity
import com.example.data.model.AlternativeValueEntity
import com.example.data.model.CriteriaEntity

object DefaultData {
    val criteriaList = listOf(
        CriteriaEntity("C1", "Pengalaman Kerja (C1)", 0.8, isBenefit = true),
        CriteriaEntity("C2", "Tes Akademik (C2)", 0.8, isBenefit = true),
        CriteriaEntity("C3", "Wawancara (C3)", 1.0, isBenefit = true),
        CriteriaEntity("C4", "Portofolio (C4)", 0.6, isBenefit = true),
        CriteriaEntity("C5", "Kemampuan Teknis (C5)", 1.0, isBenefit = true),
        CriteriaEntity("C6", "Sikap/Etika Kerja (C6)", 1.0, isBenefit = true),
        CriteriaEntity("C7", "Jarak Domisili (C7)", 1.0, isBenefit = false), // Cost
        CriteriaEntity("C8", "Ekspektasi Gaji (C8)", 0.8, isBenefit = false)  // Cost
    )

    val alternatives = listOf(
        AlternativeEntity(1, "A1 (Budi Prasetyo)"),
        AlternativeEntity(2, "A2 (Ani Wijaya)"),
        AlternativeEntity(3, "A3 (Candra Saputra)"),
        AlternativeEntity(4, "A4 (Dewi Lestari)"),
        AlternativeEntity(5, "A5 (Eko Wahyudi)")
    )

    val values = listOf(
        // A1
        AlternativeValueEntity(1, "C1", 0.6),
        AlternativeValueEntity(1, "C2", 0.8),
        AlternativeValueEntity(1, "C3", 0.6),
        AlternativeValueEntity(1, "C4", 0.6),
        AlternativeValueEntity(1, "C5", 0.8),
        AlternativeValueEntity(1, "C6", 0.8),
        AlternativeValueEntity(1, "C7", 0.6),
        AlternativeValueEntity(1, "C8", 0.8),

        // A2
        AlternativeValueEntity(2, "C1", 1.0),
        AlternativeValueEntity(2, "C2", 0.2),
        AlternativeValueEntity(2, "C3", 0.8),
        AlternativeValueEntity(2, "C4", 0.8),
        AlternativeValueEntity(2, "C5", 0.6),
        AlternativeValueEntity(2, "C6", 1.0),
        AlternativeValueEntity(2, "C7", 0.6),
        AlternativeValueEntity(2, "C8", 0.6),

        // A3
        AlternativeValueEntity(3, "C1", 0.6),
        AlternativeValueEntity(3, "C2", 0.2),
        AlternativeValueEntity(3, "C3", 0.6),
        AlternativeValueEntity(3, "C4", 0.8),
        AlternativeValueEntity(3, "C5", 0.6),
        AlternativeValueEntity(3, "C6", 0.6),
        AlternativeValueEntity(3, "C7", 1.0),
        AlternativeValueEntity(3, "C8", 0.6),

        // A4
        AlternativeValueEntity(4, "C1", 0.8),
        AlternativeValueEntity(4, "C2", 0.8),
        AlternativeValueEntity(4, "C3", 0.8),
        AlternativeValueEntity(4, "C4", 0.8),
        AlternativeValueEntity(4, "C5", 0.8),
        AlternativeValueEntity(4, "C6", 1.0),
        AlternativeValueEntity(4, "C7", 1.0),
        AlternativeValueEntity(4, "C8", 1.0),

        // A5
        AlternativeValueEntity(5, "C1", 1.0),
        AlternativeValueEntity(5, "C2", 1.0),
        AlternativeValueEntity(5, "C3", 1.0),
        AlternativeValueEntity(5, "C4", 0.8),
        AlternativeValueEntity(5, "C5", 0.6),
        AlternativeValueEntity(5, "C6", 1.0),
        AlternativeValueEntity(5, "C7", 1.0),
        AlternativeValueEntity(5, "C8", 1.0)
    )
}
