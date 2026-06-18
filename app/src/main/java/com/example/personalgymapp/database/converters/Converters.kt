package com.example.personalgymapp.database.converters

import androidx.room.TypeConverter
import com.example.personalgymapp.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromWorkoutExerciseList(value: List<WorkoutExercise>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toWorkoutExerciseList(value: String): List<WorkoutExercise> {
        val listType = object : TypeToken<List<WorkoutExercise>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromSessionSetResultList(value: List<SessionSetResult>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toSessionSetResultList(value: String): List<SessionSetResult> {
        val listType = object : TypeToken<List<SessionSetResult>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromGPSPointList(value: List<GPSPoint>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toGPSPointList(value: String?): List<GPSPoint>? {
        return value?.let {
            val listType = object : TypeToken<List<GPSPoint>>() {}.type
            gson.fromJson(it, listType)
        }
    }
}
