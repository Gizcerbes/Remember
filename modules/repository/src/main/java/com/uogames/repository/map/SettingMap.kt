package com.uogames.repository.map

import com.uogames.database.entity.SettingEntity
import com.uogames.dto.local.Setting

object SettingMap {

	fun SettingEntity.toDTO() = Setting(
		key = key,
		value = value
	)


	fun Setting.toEntity() = SettingEntity(
		key = key,
		value = value
	)

}