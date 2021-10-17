package com.mironov.psychologicaltest.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val questionText: String?,
    val subQuestionText: String?,
    val type:String?,
    val answer:String?,
    val inc:Int?
): Parcelable