package com.uogames.database.repository

import com.uogames.database.dao.SettingDAO
import com.uogames.database.entity.SettingEntity

class SettingRepository(val dao: SettingDAO) {

	suspend fun save(setting: SettingEntity) = dao.save(setting)

	suspend fun delete(setting: SettingEntity) = dao.delete(setting)

	suspend fun get(key: String) = dao.get(key)

	fun getFlow(key: String) = dao.getFlow(key)

}