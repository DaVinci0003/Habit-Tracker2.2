package com.habitflow.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class Store(context: Context) {
    private val prefs = context.getSharedPreferences("habitflow", Context.MODE_PRIVATE)
    fun load(): MutableList<Habit> {
        val raw = prefs.getString("habits", "[]") ?: "[]"; val arr = JSONArray(raw); val out = mutableListOf<Habit>()
        for (i in 0 until arr.length()) { val o=arr.getJSONObject(i); val logs=mutableMapOf<String,Int>(); val jo=o.optJSONObject("logs"); jo?.keys()?.forEach { k -> logs[k]=jo.optInt(k) }; out.add(Habit(o.getLong("id"),o.getString("name"),o.optString("type","check"),o.optInt("target"),o.optInt("hour",20),o.optInt("minute",0),o.optInt("daysMask",127),o.optInt("endAfterDays",0),o.optInt("order",i),logs)) }
        return out.sortedBy { it.order }.toMutableList()
    }
    fun save(list: List<Habit>) { val arr=JSONArray(); list.forEachIndexed { idx,h -> val o=JSONObject(); o.put("id",h.id).put("name",h.name).put("type",h.type).put("target",h.target).put("hour",h.hour).put("minute",h.minute).put("daysMask",h.daysMask).put("endAfterDays",h.endAfterDays).put("order",idx); val jo=JSONObject(); h.logs.forEach{(k,v)->jo.put(k,v)}; o.put("logs",jo); arr.put(o) }; prefs.edit().putString("habits",arr.toString()).apply() }
    fun themeDark(): Boolean = prefs.getBoolean("dark", false)
    fun setThemeDark(v:Boolean) = prefs.edit().putBoolean("dark",v).apply()
    fun notifications(): Boolean = prefs.getBoolean("notifications",true)
    fun setNotifications(v:Boolean) = prefs.edit().putBoolean("notifications",v).apply()
    companion object { fun key(cal: Calendar = Calendar.getInstance()): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time) }
}
