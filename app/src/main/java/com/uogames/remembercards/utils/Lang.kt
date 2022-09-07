package com.uogames.remembercards.utils

import com.uogames.flags.Countries
import java.util.*

data class Lang constructor(
	val locale: Locale,
	val countries: Countries
) {

	companion object {
		fun parse(string: String): Lang {
			val data = string.split("-")
			val locale = Locale.forLanguageTag(data[0])
			val countries = Countries.valueOf(data[1])
			return Lang(locale, countries)
		}
	}

}