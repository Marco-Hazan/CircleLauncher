package com.example.circlelauncher

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject

data class Group(
    val id: Long,
    val name: String,
    val color: Int,
    val icon: String,
    val apps: List<String>
)

const val MAX_APPS = 8

class LauncherState(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("launcher", Context.MODE_PRIVATE)

    var groups by mutableStateOf(load())
        private set

    private fun defaults(): List<Group> = listOf(
        Group(1, "Finance", 0, "letter", emptyList()),
        Group(2, "Work", 1, "letter", emptyList()),
        Group(3, "Social", 2, "letter", emptyList()),
        Group(4, "Media", 3, "letter", emptyList())
    )

    private fun load(): List<Group> {
        val raw = prefs.getString("groups", null) ?: return defaults()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                val a = o.getJSONArray("apps")
                Group(
                    o.getLong("id"),
                    o.getString("name"),
                    o.getInt("color"),
                    o.getString("icon"),
                    (0 until a.length()).map { a.getString(it) }
                )
            }
        } catch (e: Exception) {
            defaults()
        }
    }

    private fun commit(list: List<Group>) {
        groups = list
        val arr = JSONArray()
        list.forEach { g ->
            val o = JSONObject()
            o.put("id", g.id)
            o.put("name", g.name)
            o.put("color", g.color)
            o.put("icon", g.icon)
            val a = JSONArray()
            g.apps.forEach { a.put(it) }
            o.put("apps", a)
            arr.put(o)
        }
        prefs.edit().putString("groups", arr.toString()).apply()
    }

    fun upsert(group: Group) {
        val exists = groups.any { it.id == group.id }
        commit(if (exists) groups.map { if (it.id == group.id) group else it } else groups + group)
    }

    fun delete(id: Long) {
        commit(groups.filter { it.id != id })
    }

    fun addApp(id: Long, pkg: String) {
        commit(groups.map {
            if (it.id == id && it.apps.size < MAX_APPS && pkg !in it.apps) it.copy(apps = it.apps + pkg) else it
        })
    }

    fun removeApp(id: Long, pkg: String) {
        commit(groups.map { if (it.id == id) it.copy(apps = it.apps.filter { p -> p != pkg }) else it })
    }
}
