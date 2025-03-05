# WakeUp 课程表
这是一个第三方 WakeUp 课程表解析库，基于 WakeUp 的分享机制。
## 使用方法
```kotlin
fun main() {
    val shareCode = "dl79q-5J98gobKbpM9co-"
    val courseTable = CourseTable.fromShareCode(shareCode)
    courseTable.getCourse(Clock.System.todayIn(TimeZone.currentSystemDefault())).forEach {
        println("${it.course?.name }\t${ it.course?.room }\t${ it.course?.teacher }")
    }
}
```