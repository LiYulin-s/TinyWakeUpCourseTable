package org.example

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Serializable
data class WakeUpShareResponse(
    val status: String,
    val message: String,
    val data: String,
)

@Serializable
data class WakeUpConfig(
    val maxWeek: Int,
    val id: Int,
    val nodes: Int,
    val startDate: String,
)

@Serializable
data class WakeUpTableConfig(
    val courseLen: Int,
    val id: Int,
    val name: String,
    val sameBreakLen: Boolean,
    val sameLen: Boolean,
    val theBreakLen: Int,
)

@Serializable
data class WakeUpTimeConfig(
    val endTime: String,
    val startTime: String,
    val timeTable: Int,
    val node: Int
)

typealias WakeUpTimeConfigs = List<WakeUpTimeConfig>

@Serializable
data class WakeUpCourse(
    val color: String,
    val courseName: String,
    val credit: Float,
    val id: Int,
    val note: String,
    val tableId: Int,
)

typealias WakeUpCourses = List<WakeUpCourse>

@Serializable
data class WakeUpArrangement(
    val day: Int,
    val endTime: String,
    val endWeek: Int,
    val id: Int,
    val level: Int,
    val ownTime: Boolean,
    val room: String,
    val startNode: Int,
    val startTime: String,
    val startWeek: Int,
    val step: Int,
    val tableId: Int,
    val teacher: String,
    val type: Int,
)

typealias WakeUpArrangements = List<WakeUpArrangement>