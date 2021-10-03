package com.mironov.psychologicaltest.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "question_table")
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val questionText: String,
): Parcelable