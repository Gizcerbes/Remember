package com.uogames.repository.providers

import com.uogames.database.repository.SettingRepository
import com.uogames.dto.Setting
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.map

class SettingProvider(
	private val repository: SettingRepository
) : Provider() {

	fun saveAsync(key: String, value: String?) = ioScope.async { repository.save(Setting(key, value)) }

	fun removeAsync(key: String) = ioScope.async { repository.delete(Setting(key, "")) }

	fun getFlow(key: String) = repository.getFlow(key).map { it?.value }

}