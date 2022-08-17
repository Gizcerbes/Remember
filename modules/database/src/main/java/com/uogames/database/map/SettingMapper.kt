package com.uogames.database.map

import com.uogames.database.entity.SettingEntity
import com.uogames.dto.local.Setting

object SettingMapper : Map<SettingEntity, Setting> {

	override fun SettingEntity.toDTO() = Setting(
		key = key,
		value = value
	)


	override fun Setting.toEntity() = SettingEntity(
		key = key,
		value = value
	)

}