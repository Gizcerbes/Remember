package com.uogames.database.map

import com.uogames.database.entity.SettingEntity
import com.uogames.dto.Setting

object SettingMapper : Map<SettingEntity, Setting> {

	override fun SettingEntity.toDTO(): Setting {
		return Setting(
			key = key,
			value = value
		)
	}

	override fun Setting.toEntity(): SettingEntity {
		return SettingEntity(
			key = key,
			value = value
		)
	}
}