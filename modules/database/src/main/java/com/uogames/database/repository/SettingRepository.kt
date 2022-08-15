package com.uogames.database.repository

import com.uogames.database.dao.SettingDAO
import com.uogames.database.map.SettingMapper.toDTO
import com.uogames.database.map.SettingMapper.toEntity
import com.uogames.dto.local.Setting
import kotlinx.coroutines.flow.map

class SettingRepository(val dao: SettingDAO) {

	suspend fun save(setting: Setting) = dao.save(setting.toEntity())

	suspend fun delete(setting: Setting) = dao.delete(setting.toEntity())

	suspend fun get(key: String) = dao.get(key)?.toDTO()

	fun getFlow(key: String) = dao.getFlow(key).map { it?.toDTO() }

}