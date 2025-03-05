package org.example

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.until
import kotlinx.serialization.json.Json
import org.http4k.client.ApacheClient
import org.http4k.client.ApacheClient.invoke
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Request.Companion.invoke
import kotlinx.datetime.format.char
import kotlinx.datetime.format.DateTimeFormatBuilder
import kotlinx.datetime.format.Padding

typealias DayTable = MutableList<CourseTable.Node>
typealias WeekTable = MutableList<DayTable>

class CourseTable (
        private val courses: WakeUpCourses,
        private val configs: WakeUpTimeConfigs,
        private val arrangements: WakeUpArrangements,
        private val dateConfig: WakeUpConfig,
    ) {
    companion object {
        @JvmStatic
        fun fromShareCode(shareCode: String): CourseTable {
            val headers = mapOf(
                "User-Agent" to "okhttp/4.12.0",
                "Accept-Encoding" to "gzip",
                "version" to "257"
            )
            val client = ApacheClient()
            val request = Request(Method.GET, "https://i.wakeup.fun/share_schedule/get?key=$shareCode").headers(
                headers.toList()
            )
            val response = client(request)

            val json = Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            }

            val data = json.decodeFromString<WakeUpShareResponse>(response.body.toString()).data
            if (data == "") {
                throw Exception("No data found")
            }
            val rawDataArray = data.split("\n")
            val config = json.decodeFromString<WakeUpTableConfig>(rawDataArray[0])
            val timeConfigs = json.decodeFromString<WakeUpTimeConfigs>(rawDataArray[1])
            val dateConfig = json.decodeFromString<WakeUpConfig>(rawDataArray[2])
            val courses = json.decodeFromString<WakeUpCourses>(rawDataArray[3])
            val arrangements = json.decodeFromString<WakeUpArrangements>(rawDataArray[4])
            return CourseTable(courses, timeConfigs, arrangements, dateConfig)
        }
    }

    private val table: MutableList<WeekTable> = mutableListOf()

    init {
        repeat(dateConfig.maxWeek) {
            val weekTable: WeekTable = mutableListOf()
            repeat(7) {
                val dayTable: DayTable = mutableListOf()
                repeat(dateConfig.nodes) {
                    dayTable += CourseTable.Node()
                }
                weekTable += dayTable
            }
            table += weekTable
        }

        courses.forEach { course ->
            arrangements.filter {
                it.id == course.id
            }.forEach { arrangement ->
                (arrangement.startWeek - 1..arrangement.endWeek - 1).forEach { week ->
                    if (arrangement.step != 1) {
                        (arrangement.startNode - 1 ..arrangement.startNode + arrangement.step - 2).forEachIndexed { index, node ->
                            table[week][arrangement.day - 1][node] = Node(
                                Course(
                                    name = course.courseName,
                                    credit = course.credit,
                                    teacher = arrangement.teacher,
                                    room = arrangement.room,
                                ),
                                index != (arrangement.step - 1),
                                index != 0,
                            )
                        }
                    } else {
                        table[week][arrangement.day - 1][arrangement.startNode - 1] = Node(
                            Course(
                                name = course.courseName,
                                credit = course.credit,
                                teacher = arrangement.teacher,
                                room = arrangement.room,
                            ),
                            false,
                            false,
                        )
                    }
                }
            }
        }
    }

    data class Course(
        val name: String, val credit: Float, val teacher: String, val room: String
    )

    data class Node(
        val course: Course? = null,
        val downwardContinuous: Boolean = false,
        val upwardContinuous: Boolean = false,
    )

    fun getCourse(dateTime: LocalDate): Iterable<Node> {
        val startTime = LocalDate.parse(dateConfig.startDate, LocalDate.Format {
            year()
            char('-')
            monthNumber(Padding.NONE)
            char('-')
            dayOfMonth()
        })
        val span = startTime.until(dateTime, DateTimeUnit.DAY)
        val week = span / 7
        val day = dateTime.dayOfWeek
        val dayCourse = table[week.toInt()][day.value - 1]
        println("Week: $week")
        println("Day: $day")
        return dayCourse
    }

    fun toString(week: Int): String {
        var s = "Mon. \t\t Tue. \t\t Wed. \t\t Thu. \t\t Fri. \t\t Sat. \t\t Sun \n"
        (0..table[0][0].size - 1).forEach { i ->
            (0..6).forEach { day ->
                s += table[week][day][i].course?.let { "${it.name} \t " } ?: "\t\t"
            }
            s += "\n"
        }
        return s
    }

}