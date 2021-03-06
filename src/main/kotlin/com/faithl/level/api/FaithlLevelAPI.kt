package com.faithl.level.api

import com.faithl.level.FaithlLevel
import com.faithl.level.internal.core.Level
import com.faithl.level.internal.core.impl.Basic
import com.faithl.level.internal.core.impl.Temp
import com.faithl.level.internal.core.impl.Pure
import taboolib.common.platform.function.getDataFolder
import taboolib.common.util.asList
import taboolib.common5.FileWatcher
import taboolib.module.configuration.Configuration
import java.io.File

@Suppress("UNCHECKED_CAST")
object FaithlLevelAPI {

    /**
     * 已注册的等级
     */
    val registeredLevels = HashMap<String, Level>()

    /**
     * 已注册的脚本
     */
    val registeredScript = HashMap<String, List<String>>()

    /**
     * 主等级
     */
    val mainLevel: Level? = FaithlLevel.setting.getString("main-level")?.let { getLevel(it) }

    /**
     * 文件夹
     */
    val folderLevel = File(getDataFolder(), "levels")
    val folderScript = File(getDataFolder(), "scripts")

    /**
     * 注册一个等级
     *
     * @param name 名称
     * @param level 等级
     */
    fun registerLevel(name: String, level: Level) {
        registeredLevels[name] = level
    }

    /**
     * 注册一个脚本
     *
     * @param name 名称
     * @param list 脚本
     */
    fun registerScript(name: String, list: List<String>) {
        registeredScript[name] = list
    }

    /**
     * 注销一个等级
     *
     * @param name 名称
     */
    fun unRegisterLevel(name: String) {
        registeredLevels.remove(name)
    }

    /**
     * 注销一个脚本
     *
     * @param name 名称
     */
    fun unRegisterScript(name: String) {
        registeredScript.remove(name)
    }

    /**
     * 通过名称获取等级
     *
     * @param name 名称
     * @return 等级
     */
    fun getLevel(name: String): Level {
        return registeredLevels[name]!!
    }

    /**
     * 获取等级的名称
     *
     * @param level 等级
     * @return 等级名称
     */
    fun getName(level: Level): String {
        return registeredLevels.keys.find {
            registeredLevels[it] == level
        }!!
    }

    /**
     * 重新加载等级文件
     * 这个操作会清空缓存
     */
    fun reloadLevel() {
        registeredLevels.clear()
        loadLevelFromFile(folderLevel)
    }

    /**
     * 重新加载脚本文件
     * 这个操作会清空缓存
     */
    fun reloadScript() {
        registeredScript.clear()
        loadScriptFromFile(folderLevel)
    }

    /**
     * 从文件中加载等级
     * 这个操作不会清空缓存
     */
    fun loadLevelFromFile(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { loadLevelFromFile(it) }
        } else if (file.name.endsWith(".yml")) {
            val task = Runnable {
                val conf = Configuration.loadFromFile(file)
                try {
                    val type = conf.getString("type") ?: "basic"
                    when (type.lowercase()) {
                        "basic" -> registerLevel(
                            conf.getString("identify") ?: conf.getString("name")!!,
                            Basic(conf)
                        )
                        "pure" -> registerLevel(conf.getString("identify") ?: conf.getString("name")!!, Pure())
                        "temp" -> registerLevel(conf.getString("identify") ?: conf.getString("name")!!, Temp())
                    }
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
            task.run()
            FileWatcher.INSTANCE.addSimpleListener(file) {
                task.run()
            }
        }
    }

    /**
     * 从文件中加载脚本
     * 这个操作不会清空缓存
     */
    fun loadScriptFromFile(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { loadScriptFromFile(it) }
        } else if (file.name.endsWith(".yml")) {
            val conf = Configuration.loadFromFile(file)
            conf.getKeys(false).forEach { key ->
                registerScript(key, conf[key]?.asList() ?: mutableListOf())
            }
        }
    }

}