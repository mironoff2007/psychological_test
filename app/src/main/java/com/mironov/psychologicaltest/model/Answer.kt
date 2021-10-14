package com.mironov.psychologicaltest.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Answer(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val user: String,
    val testName: String,
    val questionId:Int,
    val questionText:String,
    val answer:String,
): Parcelable