package com.uogames.repository.providers

import com.uogames.database.repository.SettingRepository
import com.uogames.dto.local.Setting
import kotlinx.coroutines.flow.map

class SettingProvider(
	private val repository: SettingRepository
) {

	suspend fun save(key: String, value: String?) = repository.save(Setting(key, value))

	suspend fun remove(key: String) = repository.delete(Setting(key, ""))

	fun getFlow(key: String) = repository.getFlow(key).map { it?.value }

	suspend fun get(key: String) = repository.get(key)?.value

}