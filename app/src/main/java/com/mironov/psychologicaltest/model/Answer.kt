package com.mironov.psychologicaltest.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Answer(
    val testId: Int,
    val questionId:Int,
    val answer:Int,
): Parcelable{
    @PrimaryKey(autoGenerate = true)
    var id:Int=0
}