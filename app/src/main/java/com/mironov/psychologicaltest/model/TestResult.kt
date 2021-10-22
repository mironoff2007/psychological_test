package com.mironov.psychologicaltest.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class TestResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val resultText: String
): Parcelable