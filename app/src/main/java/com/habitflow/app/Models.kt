package com.habitflow.app

data class Habit(
    var id: Long, var name: String, var type: String = "check", var target: Int = 0,
    var hour: Int = 20, var minute: Int = 0, var daysMask: Int = 127,
    var endAfterDays: Int = 0, var order: Int = 0,
    var logs: MutableMap<String, Int> = mutableMapOf()
)
