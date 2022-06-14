package com.uogames.flags

import androidx.annotation.DrawableRes
import java.util.*

enum class Countries(@DrawableRes val res: Int, val isoCode: String?, val country: Array<Text>) {
	AFGHANISTAN(
		R.drawable.ic_flag_af, "AF", arrayOf(
			Text(Locale("en"), "Islamic Republic of Afghanistan"),
			Text(Locale("prs"), "د افغانستان اسلامي امارت"),
			Text(Locale("ps"), "د افغانستان اسلامي جمهوریت")
		)
	),
	ALAND(
		R.drawable.ic_flag_ax, "AX", arrayOf(
			Text(Locale("sv"), "Åland"),
			Text(Locale("fi"), "Ahvenanmaan maakunta"),
			Text(Locale("sv"), "Landskapet Åland")
		)
	),
	ALBANIA(
		R.drawable.ic_flag_al, "AL", arrayOf(
			Text(Locale("en"), "Republic of Albania"),
			Text(Locale("sq"), "Republika e Shqipërisë")
		)
	),
	ALGERIA(
		R.drawable.ic_flag_dz, "DZ", arrayOf(
			Text(Locale("en"), "People's Democratic Republic of Algeria"),
			Text(Locale("ar"), "الجمهورية الجزائرية الديمقراطية الشعبية"),
			Text(Locale("fr"), "République algérienne démocratique et populaire"),
		)
	),
	AMERICAN_SAMOA(
		R.drawable.ic_flag_as, "AS", arrayOf(
			Text(Locale("en"), "American Samoa"),
			Text(Locale("sm"), "Amerika Sāmoa")
		)
	),
	ANDORRA(
		R.drawable.ic_flag_ad, "AD", arrayOf(
			Text(Locale("en"), "Principality of Andorra"),
			Text(Locale("ca"), "Principat d'Andorra")
		)
	),
	ANGOLA(
		R.drawable.ic_flag_ao, "AO", arrayOf(
			Text(Locale("en"), "Republic of Angola"),
			Text(Locale("pt"), "República de Angola")
		)
	),
	ANGUILLA(
		R.drawable.ic_flag_ai, "AI", arrayOf(
			Text(Locale("en"), "Anguilla")
		)
	),
	ANTARCTICA(
		R.drawable.ic_flag_aq, "AQ", arrayOf(
			Text(Locale("en"), "Antarctica")
		)
	),
	ANTIGUA_AND_BARBUDA(
		R.drawable.ic_flag_ag, "AG", arrayOf(
			Text(Locale("en"), "Antigua and Barbuda")
		)
	),
	ARGENTINA(
		R.drawable.ic_flag_ar, "AR", arrayOf(
			Text(Locale("en"), "Argentine Republic"),
			Text(Locale("es"), "República Argentina"),

			)
	),
	ARMENIA(
		R.drawable.ic_flag_am, "AM", arrayOf(
			Text(Locale("en"), "Republic of Armenia"),
			Text(Locale("hy"), "Հայաստանի Հանրապետություն")
		)
	),
	ARUBA(
		R.drawable.ic_flag_aw, "AW", arrayOf(
			Text(Locale("en"), "Aruba"),
			Text(Locale("nl"), "Aruba")
		)
	),
	AUSTRALIA(
		R.drawable.ic_flag_au, "AU", arrayOf(
			Text(Locale("en"), "Commonwealth of Australia")
		)
	),
	AUSTRIA(
		R.drawable.ic_flag_at, "AT", arrayOf(
			Text(Locale("en"), "Republic of Austria"),
			Text(Locale("de"), "Republik Österreich")
		)
	),
	AZERBAIJAN(
		R.drawable.ic_flag_az, "AZ", arrayOf(
			Text(Locale("en"), "Republic of Azerbaijan"),
			Text(Locale("az-Latn"), "Azərbaycan Respublikası")
		)
	),
	BAHAMAS(
		R.drawable.ic_flag_bs, "BS", arrayOf(
			Text(Locale("en"), "Commonwealth of The Bahamas")
		)
	),
	BAHRAIN(
		R.drawable.ic_flag_bh, "BH", arrayOf(
			Text(Locale("en"), "Kingdom of Bahrain"),
			Text(Locale("ar"), "مملكة البحرين")
		)
	),
	BANGLADESH(
		R.drawable.ic_flag_bd, "BD", arrayOf(
			Text(Locale("en"), "People's Republic of Bangladesh"),
			Text(Locale("bn"), "গণপ্রজাতন্ত্রী বাংলাদেশ")
		)
	),
	BARBADOS(
		R.drawable.ic_flag_bb, "BB", arrayOf(
			Text(Locale("en"), "Barbados")
		)
	),
	WHITE_RUSSIA(
		R.drawable.ic_flag_white_russia, "BY", arrayOf(
			Text(Locale("en"), "Republic of Belarus"),
			Text(Locale("ru"), "Республика Белоруссия")
		)
	),
	BELARUS(
		R.drawable.ic_flag_by, "BY", arrayOf(
			Text(Locale("en"), "Republic of Belarus"),
			Text(Locale("be"), "Рэспубліка Беларусь"),
			Text(Locale("ru"), "Республика Беларусь")
		)
	),
	BELGIUM(
		R.drawable.ic_flag_be, "BE", arrayOf(
			Text(Locale("en"), "Kingdom of Belgium"),
			Text(Locale("nl"), "Koninkrijk België"),
			Text(Locale("fr"), "Royaume de Belgique"),
			Text(Locale("de"), "Königreich Belgien")
		)
	),
	BELIZE(
		R.drawable.ic_flag_bz, "BZ", arrayOf(
			Text(Locale("en"), "Belize")
		)
	),
	BENIN(
		R.drawable.ic_flag_bj, "BJ", arrayOf(
			Text(Locale("en"), "Republic of Benin"),
			Text(Locale("fr"), "République du Bénin")
		)
	),
	BERMUDA(
		R.drawable.ic_flag_bm, "BM", arrayOf(
			Text(Locale("en"), "Bermuda")
		)
	),
	BHUTAN(
		R.drawable.ic_flag_bt, "BT", arrayOf(
			Text(Locale("en"), "Kingdom of Bhutan"),
			Text(Locale("dz"), "འབྲུག་རྒྱལ་ཁབ")
		)
	),
	BOLIVIA(
		R.drawable.ic_flag_bo, "BO", arrayOf(
			Text(Locale("en"), "Plurinational State of Bolivia"),
			Text(Locale("es"), "Estado Plurinacional de Bolivia"),
			Text(Locale("gn"), "Tetã Hetãvoregua Mborivia"),
			Text(Locale("ay"), "Wuliwya Walja Markanakana Suyu"),
			Text(Locale("qu"), "Puliwya Achka Aylluska Mamallaqta")
		)
	),
	BONAIRE(
		R.drawable.ic_flag_bonaire, "BQ", arrayOf(
			Text(Locale("en"), "Bonaire"),
			Text(Locale("pap"), "Boneiru")
		)
	),
	SINT_EUSTATIUS(
		R.drawable.ic_flag_sint_eustatius, "BQ", arrayOf(
			Text(Locale("en"), "Sint Eustatius")
		)
	),
	SABA(
		R.drawable.ic_flag_saba, "BQ", arrayOf(
			Text(Locale("en"), "Saba")
		)
	),
	BOSNIA_AND_HERZEGOVINA(
		R.drawable.ic_flag_ba, "BA", arrayOf(
			Text(Locale("en"), "Bosnia and Herzegovina"),
			Text(Locale("bs"), "Bosna i Hercegovina"),
			Text(Locale("sr"), "Босна и Херцеговина")
		)
	),
	BOTSWANA(
		R.drawable.ic_flag_bw, "BW", arrayOf(
			Text(Locale("en"), "Republic of Botswana"),
			Text(Locale("tn"), "Lefatshe la Botswana")
		)
	),
	BOUVET_ISLAND(
		R.drawable.ic_flag_no, "BV", arrayOf(
			Text(Locale("en"), "Bouvet Island"),
			Text(Locale("nb"), "Bouvetøya")
		)
	),
	BRAZIL(
		R.drawable.ic_flag_br, "BR", arrayOf(
			Text(Locale("en"), "Federative Republic of Brazil"),
			Text(Locale("pt"), "República Federativa do Brasil")
		)
	),
	BRITISH_INDIAN_OCEAN_TERRITORY(
		R.drawable.ic_flag_io, "IO", arrayOf(
			Text(Locale("en"), "British Indian Ocean Territory")
		)
	),
	BRITISH_VIRGIN_ISLANDS(
		R.drawable.ic_flag_bvi, "VG", arrayOf(
			Text(Locale("en"), "Virgin Islands")
		)
	),
	BRUNEI(
		R.drawable.ic_flag_bn, "BN", arrayOf(
			Text(Locale("en"), "Brunei Darussalam"),
			Text(Locale("ms"), "Negara Brunei Darussalam"),
			Text(Locale("ms-Arab"), "نݢارا بروني دارالسلام")
		)
	),
	BULGARIA(
		R.drawable.ic_flag_bg, "BG", arrayOf(
			Text(Locale("en"), "Republic of Bulgaria"),
			Text(Locale("bg"), "Република България")
		)
	),
	BURKINA_FASO(
		R.drawable.ic_flag_bf, "BF", arrayOf(
			Text(Locale("en"), "Burkina Faso")
		)
	),
	BURUNDI(
		R.drawable.ic_flag_bi, "BI", arrayOf(
			Text(Locale("en"), "Republic of Burundi"),
			Text(Locale("rn"), "Repuburika y’Uburundi"),
			Text(Locale("fr"), "République du Burundi")
		)
	),
	CAPE_VERDE(
		R.drawable.ic_flag_cv, "CV", arrayOf(
			Text(Locale("en"), "Republic of Cabo Verde"),
			Text(Locale("pt"), "República de Cabo Verde"),
			Text(Locale("kea"), "Repúblika di Kabu Verdi")
		)
	),
	CAMBODIA(
		R.drawable.ic_flag_kh, "KH",
		arrayOf(
			Text(Locale("en"), "Kingdom of Cambodia"),
			Text(Locale("km"), "ព្រះរាជាណាចក្រកម្ពុជា")
		),
	),
	CAMEROON(
		R.drawable.ic_flag_cm, "CM", arrayOf(
			Text(Locale("en"), "Republic of Cameroon"),
			Text(Locale("fr"), "République du Cameroun")
		)
	),
	CANADA(
		R.drawable.ic_flag_ca, "CA", arrayOf(
			Text(Locale("en"), "Canada")
		)
	),
	CAYMAN_ISLAND(
		R.drawable.ic_flag_ky, "KY", arrayOf(
			Text(Locale("en"), "Cayman Islands")
		)
	),
	CENTRAL_AFRICAN_REPUBLIC(
		R.drawable.ic_flag_cf, "CF", arrayOf(
			Text(Locale("en"), "Central African Republic"),
			Text(Locale("fr"), "République centrafricaine"),
			Text(Locale("sg"), "Ködörösêse tî Bêafrîka")
		)
	),
	CHAD(
		R.drawable.ic_flag_td, "TD", arrayOf(
			Text(Locale("en"), "Republic of Chad"),
			Text(Locale("ar"), "جمهورية تشاد"),
			Text(Locale("fr"), "République du Tchad")
		)
	),
	CHILE(
		R.drawable.ic_flag_cl, "CL", arrayOf(
			Text(Locale("en"), "Republic of Chile"),
			Text(Locale("es"), "República de Chile")
		)
	),
	CHINA(
		R.drawable.ic_flag_cn, "CN", arrayOf(
			Text(Locale("en"), "People's Republic of China"),
			Text(Locale("zh"), "中华人民共和国"),
		)
	),
	CHRISTMAS_ISLAND(
		R.drawable.ic_flag_cx, "CX", arrayOf(
			Text(Locale("en"), "Christmas Island"),
			Text(Locale("zh"), "圣诞岛领地 / 聖誕島領地"),
			Text(Locale("ms"), "Wilayah Pulau Krismas")
		)
	),
	COCOS_ISLANDS(
		R.drawable.ic_flag_cc, "CC", arrayOf(
			Text(Locale("en"), "Cocos (Keeling) Islands"),
			Text(Locale("coa"), "Pulu Kokos"),
			Text(Locale("ms"), "Wilayah Kepulauan Cocos")
		)
	),
	COLOMBIA(
		R.drawable.ic_flag_co, "CO", arrayOf(
			Text(Locale("en"), "Republic of Colombia"),
			Text(Locale("es"), "República de Colombia")
		)
	),
	COMOROS(
		R.drawable.ic_flag_km, "KM", arrayOf(
			Text(Locale("en"), "Union of the Comoros"),
			Text(Locale("zbj"), "Umoja wa Komori"),
			Text(Locale("fr"), "Union des Comores"),
			Text(Locale("ar"), "الاتحاد القمري")
		)
	),
	DEMOCRATIC_REPUBLIC_CONGO(
		R.drawable.ic_flag_cd, "CD", arrayOf(
			Text(Locale("en"), "Democratic Republic of the Congo"),
			Text(Locale("fr"), "République démocratique du Congo"),
			Text(Locale("ktu"), "Repubilika ya Kôngo ya Dimokalasi"),
			Text(Locale("ln"), "Republíki ya Kongó Demokratíki"),
			Text(Locale("sw"), "Jamhuri ya Kidemokrasia ya Kongo"),
			Text(Locale("lua"), "Ditunga dia Kongu wa Mungalaata")
		)
	),
	REPUBLIC_OF_THE_CONGO(
		R.drawable.ic_flag_cg, "CG", arrayOf(
			Text(Locale("en"), "Republic of the Congo"),
			Text(Locale("fr"), "République du Congo"),
			Text(Locale("mkw"), "Repubilika ya Kôngo"),
			Text(Locale("ln"), "Republíki ya Kongó")
		)
	),
	COOK_ISLAND(
		R.drawable.ic_flag_ck, "CK", arrayOf(
			Text(Locale("en"), "Cook Islands"),
			Text(Locale("rar"), "Kūki 'Āirani")
		)
	),
	COSTA_RICA(
		R.drawable.ic_flag_cr, "CR", arrayOf(
			Text(Locale("en"), "Republic of Costa Rica"),
			Text(Locale("rs"), "República de Costa Rica")
		)
	),
	IVORY_COAST(
		R.drawable.ic_flag_ci, "CI", arrayOf(
			Text(Locale("fr"), "République de Côte d'Ivoire")
		)
	),
	CROATIA(
		R.drawable.ic_flag_hr, "HR", arrayOf(
			Text(Locale("en"), "Republic of Croatia"),
			Text(Locale("hr"), "Republika Hrvatska")
		)
	),
	CUBA(
		R.drawable.ic_flag_cu, "CU", arrayOf(
			Text(Locale("en"), "Republic of Cuba"),
			Text(Locale("es"), "República de Cuba")
		)
	),
	CURACAO(
		R.drawable.ic_flag_cw, "CW", arrayOf(
			Text(Locale("pap"), "Kòrsou")
		)
	),
	CYPRUS(
		R.drawable.ic_flag_cy, "CY", arrayOf(
			Text(Locale("en"), "Republic of Cyprus"),
			Text(Locale("el"), "Κυπριακή Δημοκρατία"),
			Text(Locale("tr"), "Kıbrıs Cumhuriyeti")
		)
	),
	CZECH_REPUBLIC(
		R.drawable.ic_flag_cz, "CZ", arrayOf(
			Text(Locale("en"), "Czech Republic"),
			Text(Locale("cs"), "Česká republika")
		)
	),
	DENMARK(
		R.drawable.ic_flag_dk, "DK", arrayOf(
			Text(Locale("en"), "Denmark"),
			Text(Locale("da"), "Danmark")
		)
	),
	DJIBOUTI(
		R.drawable.ic_flag_dj, "DJ", arrayOf(
			Text(Locale("en"), "Republic of Djibouti"),
			Text(Locale("fr"), "République de Djibouti"),
			Text(Locale("ar"), "جمهورية جيبوتي"),
			Text(Locale("so"), "Jamhuuriyadda Jabuuti"),
			Text(Locale("aa"), "Gabuutih Ummuuno")
		)
	),
	DOMINICA(
		R.drawable.ic_flag_dm, "DM", arrayOf(
			Text(Locale("en"), "Commonwealth of Dominica")
		)
	),
	DOMINICAN_REPUBLIC(
		R.drawable.ic_flag_do, "DO", arrayOf(
			Text(Locale("en"), "Dominican Republic"),
			Text(Locale("es"), "República Dominicana")
		)
	),
	EAST_TIMOR(
		R.drawable.ic_flag_tl, "TL", arrayOf(
			Text(Locale("en"), "Democratic Republic of Timor-Leste"),
			Text(Locale("pt"), "República Democrática de Timor-Leste"),
			Text(Locale("tet"), "Repúblika Demokrátika de Timór-Leste")
		)
	),
	ECUADOR(
		R.drawable.ic_flag_ec, "EC", arrayOf(
			Text(Locale("en"), "Republic of Ecuador"),
			Text(Locale("es"), "República del Ecuador")
		)
	),
	EGYPT(
		R.drawable.ic_flag_eg, "EG", arrayOf(
			Text(Locale("en"), "Arab Republic of Egypt"),
			Text(Locale("ar"), "جمهورية مصر العربية")
		)
	),
	EL_SALVADOR(
		R.drawable.ic_flag_sv, "SV", arrayOf(
			Text(Locale("en"), "Republic of El Salvador"),
			Text(Locale("es"), "República de El Salvador")
		)
	),
	EQUATORIAL_GUINEA(
		R.drawable.ic_flag_gq, "GQ", arrayOf(
			Text(Locale("en"), "Republic of Equatorial Guinea"),
			Text(Locale("es"), "República de Guinea Ecuatorial"),
			Text(Locale("fr"), "République de Guinée Équatoriale"),
			Text(Locale("pt"), "República da Guiné Equatorial")
		)
	),
	ERITREA(
		R.drawable.ic_flag_er, "ER", arrayOf(
			Text(Locale("en"), "State of Eritrea")
		)
	),
	ESTONIA(
		R.drawable.ic_flag_ee, "EE", arrayOf(
			Text(Locale("en"), "Republic of Estonia"),
			Text(Locale("et"), "Eesti Vabariik")
		)
	),
	ESWATINI(
		R.drawable.ic_flag_sz, "SZ", arrayOf(
			Text(Locale("en"), "Kingdom of Eswatini"),
			Text(Locale("ss"), "Umbuso weSwatini")
		)
	),
	ETHIOPIA(
		R.drawable.ic_flag_et, "ET", arrayOf(
			Text(Locale("en"), "Federal Democratic Republic of Ethiopia"),
			Text(Locale("am"), "የኢትዮጵያ ፌዴራላዊ ዴሞክራሲያዊ ሪፐብሊክ"),
			Text(Locale("om"), "Rippabliikii Federaalawaa Dimokraatawaa Itiyoophiyaa"),
			Text(Locale("so"), "Jamhuuriyadda Dimuqraadiga Federaalka Itoobiya"),
			Text(Locale("ti"), "ፌዴራላዊ ዴሞክራሲያዊ ሪፐብሊክ ኢትዮጵያ"),
			Text(Locale("aa"), "Itiyoppiya Federaalak Demokraatik Rippeblikih")
		)
	),
	FALKLAND_ISLANDS(
		R.drawable.ic_flag_fk, "FK", arrayOf(
			Text(Locale("en"), "Falkland Islands")
		)
	),
	FAROE_ISLANDS(
		R.drawable.ic_flag_fo, "FO", arrayOf(
			Text(Locale("en"), "Faroe Islands"),
			Text(Locale("fo"), "Føroyar"),
			Text(Locale("da"), "Færøerne")
		)
	),
	FIJI(
		R.drawable.ic_flag_fj, "FJ", arrayOf(
			Text(Locale("en"), "Republic of Fiji"),
			Text(Locale("fj"), "Matanitu Tugalala o Viti"),
			Text(Locale("hif"), "फ़िजी गणराज्य")
		)
	),
	FINLAND(
		R.drawable.ic_flag_fi, "FI", arrayOf(
			Text(Locale("en"), "Republic of Finland"),
			Text(Locale("fi"), "Suomen tasavalta"),
			Text(Locale("sv"), "Republiken Finland")
		)
	),
	FRANCE(
		R.drawable.ic_flag_fr, "FR", arrayOf(
			Text(Locale("en"), "French Republic"),
			Text(Locale("fr"), "République française")
		)
	),
	FRENCH_GUIANA(
		R.drawable.ic_flag_fr, "GF", arrayOf(
			Text(Locale("en"), "French Guiana"),
			Text(Locale("fr"), "Guyane")
		)
	),
	FRENCH_POLYNESIA(
		R.drawable.ic_flag_pf, "PF", arrayOf(
			Text(Locale("en"), "French Polynesia"),
			Text(Locale("fr"), "Polynésie française"),
			Text(Locale("ty"), "Pōrīnetia Farāni")
		)
	),
	FRENCH_SOUTHERN_AND_ANTARCTIC_LANDS(
		R.drawable.ic_flag_tf, "TF", arrayOf(
			Text(Locale("en"), "French Southern and Antarctic Lands"),
			Text(Locale("fr"), "Terres australes et antarctiques françaises")
		)
	),
	GABON(
		R.drawable.ic_flag_ga, "GA", arrayOf(
			Text(Locale("en"), "Gabonese Republic"),
			Text(Locale("fr"), "République gabonaise")
		)
	),
	GAMBIA(
		R.drawable.ic_flag_gm, "GM", arrayOf(
			Text(Locale("en"), "Republic of The Gambia")
		)
	),
	GEORGIA(
		R.drawable.ic_flag_ge, "GE", arrayOf(
			Text(Locale("en"), "Georgia"),
			Text(Locale("ka"), "საქართველო")
		)
	),
	GERMANY(
		R.drawable.ic_flag_de, "DE", arrayOf(
			Text(Locale("en"), "Federal Republic of Germany"),
			Text(Locale("de"), "Bundesrepublik Deutschland")
		)
	),
	CHANA(
		R.drawable.ic_flag_gh, "GH", arrayOf(
			Text(Locale("en"), "Republic of Ghana")
		)
	),
	GIBRALTAR(
		R.drawable.ic_flag_gi, "GI", arrayOf(
			Text(Locale("en"), "Gibraltar")
		)
	),
	GREECE(
		R.drawable.ic_flag_gr, "GR", arrayOf(
			Text(Locale("en"), "Hellenic Republic"),
			Text(Locale("el"), "Ελληνική Δημοκρατία")
		)
	),
	GREENLAND(
		R.drawable.ic_flag_gl, "GL", arrayOf(
			Text(Locale("en"), "Greenland"),
			Text(Locale("kl"), "Kalaallit Nunaat"),
			Text(Locale("da"), "Grønland")
		)
	),
	GRENADA(
		R.drawable.ic_flag_gd, "GD", arrayOf(
			Text(Locale("en"), "Grenada"),
		)
	),
	GUADELOUPE(
		R.drawable.ic_flag_gp, "GP", arrayOf(
			Text(Locale("en"), "Guadeloupe"),
			Text(Locale("gcf"), "Gwadloup")
		)
	),
	GUAM(
		R.drawable.ic_flag_gu, "GU", arrayOf(
			Text(Locale("en"), "Guam"),
			Text(Locale("ch"), "Guåhan")
		)
	),
	GUATEMALA(
		R.drawable.ic_flag_gt, "GT", arrayOf(
			Text(Locale("en"), "Republic of Guatemala"),
			Text(Locale("es"), "República de Guatemala")
		)
	),
	GUERNSEY(
		R.drawable.ic_flag_gg, "GG", arrayOf(
			Text(Locale("en"), "Bailiwick of Guernsey"),
			Text(Locale("fr"), "Bailliage de Guernesey"),
			Text(Locale("nrf"), "Bailliage dé Guernési")
		)
	),
	GUINEA(
		R.drawable.ic_flag_gn, "GN", arrayOf(
			Text(Locale("en"), "Republic of Guinea"),
			Text(Locale("fr"), "République de Guinée")
		)
	),
	GUINEA_BISSAU(
		R.drawable.ic_flag_gw, "GW", arrayOf(
			Text(Locale("en"), "Republic of Guinea-Bissau"),
			Text(Locale("pt"), "República da Guiné-Bissau")
		)
	),
	GUYANA(
		R.drawable.ic_flag_gy, "GY", arrayOf(
			Text(Locale("en"), "Co-operative Republic of Guyana")
		)
	),
	HAITI(
		R.drawable.ic_flag_ht, "HT", arrayOf(
			Text(Locale("en"), "Republic of Haiti"),
			Text(Locale("fr"), "République d'Haïti"),
			Text(Locale("ht"), "Repiblik d Ayiti")
		)
	),
	HEARD_ISLAND_AND_MCDONALD_ISLANDS(
		R.drawable.ic_flag_au, "HM", arrayOf(
			Text(Locale("en"), "Territory of Heard Island and McDonald Islands")
		)
	),
	HOLY_SEE(
		R.drawable.ic_flag_va, "VA", arrayOf(
			Text(Locale("en"), "Holy See"),
			Text(Locale("la"), "Sancta Sedes"),
			Text(Locale("it"), "Santa Sede")
		)
	),
	HONDURAS(
		R.drawable.ic_flag_hn, "HN", arrayOf(
			Text(Locale("en"), "Republic of Honduras"),
			Text(Locale("es"), "República de Honduras")
		)
	),
	HONG_KONG(
		R.drawable.ic_flag_hk, "HK", arrayOf(
			Text(Locale("en"), "Hong Kong"),
			Text(Locale("zh"), "中華人民共和國香港特別行政區"),
			Text(Locale("yue"), "Jūng'wàh Yàhnmàhn Guhng'wòhgwok Hēunggóng Dahkbiht Hàhngjingkēui")
		)
	),
	HUNGARY(
		R.drawable.ic_flag_hu, "HU", arrayOf(
			Text(Locale("en"), "Hungary"),
			Text(Locale("hu"), "Magyarország")
		)
	),
	ICELAND(
		R.drawable.ic_flag_is, "IS", arrayOf(
			Text(Locale("en"), "Iceland"),
			Text(Locale("is"), "Ísland")
		)
	),
	INDIA(
		R.drawable.ic_flag_in, "IN", arrayOf(
			Text(Locale("en"), "Republic of India"),
			Text(Locale("hi"), "भारत गणराज्य")
		)
	),
	INDONESIA(
		R.drawable.ic_flag_id, "ID", arrayOf(
			Text(Locale("en"), "Republic of Indonesia"),
			Text(Locale("id"), "Republik Indonesia")
		)
	),
	IRAN(
		R.drawable.ic_flag_ir, "IR", arrayOf(
			Text(Locale("en"), "Islamic Republic of Iran"),
			Text(Locale("fa"), "جمهوری اسلامی ایران")
		)
	),
	IRAQ(
		R.drawable.ic_flag_iq, "IQ", arrayOf(
			Text(Locale("en"), "Republic of Iraq"),
			Text(Locale("ar"), "جمهورية العراق"),
			Text(Locale("ku"), "کۆماری عێراق")
		)
	),
	IRELAND(
		R.drawable.ic_flag_ie, "IE", arrayOf(
			Text(Locale("en"), "Ireland"),
			Text(Locale("ga"), "Éire")
		)
	),
	ISLE_OF_MAN(
		R.drawable.ic_flag_im, "IM", arrayOf(
			Text(Locale("en"), "Isle of Man"),
			Text(Locale("gv"), "Mannin, Ellan Vannin")
		)
	),
	ISRAEL(
		R.drawable.ic_flag_il, "IL", arrayOf(
			Text(Locale("en"), "State of Israel"),
			Text(Locale("he"), "מְדִינַת יִשְׂרָאֵל"),
			Text(Locale("ar"), "دَوْلَة إِسْرَائِيل")
		)
	),
	ITALY(
		R.drawable.ic_flag_it, "IT", arrayOf(
			Text(Locale("en"), "Italian Republic"),
			Text(Locale("it"), "Repubblica Italiana")
		)
	),
	JAMAICA(
		R.drawable.ic_flag_jm, "JM", arrayOf(
			Text(Locale("en"), "Jamaica"),
			Text(Locale("jam"), "Jumieka")
		)
	),
	JAPAN(
		R.drawable.ic_flag_jp, "JP", arrayOf(
			Text(Locale("en"), "Japan"),
			Text(Locale("ja"), "日本国")
		)
	),
	JERSEY(
		R.drawable.ic_flag_je, "JE", arrayOf(
			Text(Locale("en"), "Bailiwick of Jersey"),
			Text(Locale("fr"), "Bailliage de Jersey"),
			Text(Locale("nrf"), "Bailliage dé Jèrri")
		)
	),
	JORDAN(
		R.drawable.ic_flag_jo, "JO", arrayOf(
			Text(Locale("en"), "Hashemite Kingdom of Jordan"),
			Text(Locale("ar"), "المملكة الأردنية الهاشمية")
		)
	),
	KAZAKHSTAN(
		R.drawable.ic_flag_kz, "KZ", arrayOf(
			Text(Locale("en"), "Republic of Kazakhstan"),
			Text(Locale("kk"), "Қазақстан Республикасы"),
			Text(Locale("ru"), "Республика Казахстан")
		)
	),
	KENYA(
		R.drawable.ic_flag_ke, "KE", arrayOf(
			Text(Locale("en"), "Republic of Kenya"),
			Text(Locale("sw"), "Jamhuri ya Kenya")
		)
	),
	KIRIBATI(
		R.drawable.ic_flag_ki, "KI", arrayOf(
			Text(Locale("en"), "Republic of Kiribati"),
			Text(Locale("gil"), "Kiribati")
		)
	),
	SOUTH_KOREA(
		R.drawable.ic_flag_kr, "KR", arrayOf(
			Text(Locale("en"), "Republic of Korea"),
			Text(Locale("ko"), "대한민국")
		)
	),
	KUWAIT(
		R.drawable.ic_flag_kw, "KW", arrayOf(
			Text(Locale("en"), "State of Kuwait"),
			Text(Locale("ar"), "دولة الكويت")
		)
	),
	KYRGYZSTAN(
		R.drawable.ic_flag_kg, "KG", arrayOf(
			Text(Locale("en"), "Kyrgyz Republic"),
			Text(Locale("ky"), "Кыргыз Республикасы"),
			Text(Locale("ru"), "Кыргызская Республика")
		)
	),
	LAOS(
		R.drawable.ic_flag_la, "LA", arrayOf(
			Text(Locale("en"), "Lao People's Democratic Republic"),
			Text(Locale("lo"), "ສາທາລະນະລັດ ປະຊາທິປະໄຕ ປະຊາຊົນລາວ")
		)
	),
	LATVIA(
		R.drawable.ic_flag_lv, "LV", arrayOf(
			Text(Locale("en"), "Republic of Latvia"),
			Text(Locale("lv"), "Latvijas Republika"),
			Text(Locale("ltg"), "Latvejas Republika"),
			Text(Locale("liv"), "Leţmō Vabāmō")
		)
	),
	LEBANON(
		R.drawable.ic_flag_lb, "LB", arrayOf(
			Text(Locale("en"), "Republic of Lebanon"),
			Text(Locale("ar"), "ٱلْجُمْهُورِيَّةُ ٱللُّبْنَانِيَّةُ"),
			Text(Locale("fr"), "République libanaise")
		)
	),
	LESOTHO(
		R.drawable.ic_flag_ls, "LS", arrayOf(
			Text(Locale("en"), "Kingdom of Lesotho"),
			Text(Locale("st"), "Naha ea Lesotho")
		)
	),
	LIBERIA(
		R.drawable.ic_flag_lr, "LR", arrayOf(
			Text(Locale("en"), "Republic of Liberia")
		)
	),
	LIBYA(
		R.drawable.ic_flag_ly, "LY", arrayOf(
			Text(Locale("en"), "State of Libya"),
			Text(Locale("ar"), "دولة ليبيا")
		)
	),
	LIECHTENSTEIN(
		R.drawable.ic_flag_li, "LI", arrayOf(
			Text(Locale("en"), "Principality of Liechtenstein"),
			Text(Locale("de"), "Fürstentum Liechtenstein")
		)
	),
	LITHUANIA(
		R.drawable.ic_flag_lt, "LT", arrayOf(
			Text(Locale("en"), "Republic of Lithuania"),
			Text(Locale("lt"), "Lietuvos Respublika")
		)
	),
	LUXEMBOURG(
		R.drawable.ic_flag_lu, "LU", arrayOf(
			Text(Locale("en"), "Grand Duchy of Luxembourg"),
			Text(Locale("lb"), "Groussherzogtum Lëtzebuerg"),
			Text(Locale("fr"), "Grand-Duché de Luxembourg"),
			Text(Locale("de"), "Großherzogtum Luxemburg")
		)
	),
	MACAU(
		R.drawable.ic_flag_mo, "MO", arrayOf(
			Text(Locale("en"), "Macau"),
			Text(Locale("zh"), "中華人民共和國澳門特別行政區"),
			Text(Locale("yue"), "Jūng'wàh Yàhnmàhn Guhng'wòhgwok Oumún Dahkbiht Hàhngjingkēui"),
			Text(Locale("pt"), "Região Administrativa Especial de Macau da República Popular da China")
		)
	),
	NORTH_MACEDONIA(
		R.drawable.ic_flag_mk, "MK", arrayOf(
			Text(Locale("en"), "Republic of North Macedonia"),
			Text(Locale("mk"), "Република Северна Македонија"),
			Text(Locale("sq"), "Republika e Maqedonisë së Veriut")
		)
	),
	MADAGASCAR(
		R.drawable.ic_flag_mg, "MG", arrayOf(
			Text(Locale("en"), "Republic of Madagascar"),
			Text(Locale("mg"), "Repoblikan'i Madagasikara"),
			Text(Locale("fr"), "République de Madagascar")
		)
	),
	MALAWI(
		R.drawable.ic_flag_mw, "MW", arrayOf(
			Text(Locale("en"), "Republic of Malawi"),
			Text(Locale("ny"), "Dziko la Malaŵi"),
			Text(Locale("tum"), "Charu cha Malaŵi")
		)
	),
	MALAYSIA(
		R.drawable.ic_flag_my, "MY", arrayOf(
			Text(Locale("en"), "Malaysia")
		)
	),
	MALDIVES(
		R.drawable.ic_flag_mv, "MV", arrayOf(
			Text(Locale("en"), "Republic of Maldives"),
			Text(Locale("dv"), "ދިވެހިރާއްޖޭގެ ޖުމްހޫރިއްޔާ")
		)
	),
	MALI(
		R.drawable.ic_flag_ml, "ML", arrayOf(
			Text(Locale("en"), "Republic of Mali"),
			Text(Locale("fr"), "République du Mali")
		)
	),
	MALTA(
		R.drawable.ic_flag_mt, "MT", arrayOf(
			Text(Locale("en"), "Republic of Malta"),
			Text(Locale("mt"), "Repubblika ta' Malta")
		)
	),
	MARSHALL_ISLANDS(
		R.drawable.ic_flag_mh, "MH", arrayOf(
			Text(Locale("en"), "Republic of the Marshall Islands"),
			Text(Locale("mh"), "Aolepān Aorōkin Ṃajeḷ")
		)
	),
	MARTINIQUE(
		R.drawable.ic_flag_mq, "MQ", arrayOf(
			Text(Locale("en"), "Martinique"),
			Text(Locale("fr"), "Collectivité Territoriale de Martinique")
		)
	),
	MAURITANIA(
		R.drawable.ic_flag_mr, "MR", arrayOf(
			Text(Locale("en"), "Islamic Republic of Mauritania"),
			Text(Locale("ar"), "الجمهورية الإسلامية الموريتانية"),
			Text(Locale("fr"), "République islamique de Mauritanie")
		)
	),
	MAURITIUS(
		R.drawable.ic_flag_mu, "MU", arrayOf(
			Text(Locale("en"), "Republic of Mauritius"),
			Text(Locale("fr"), "République de Maurice"),
			Text(Locale("mfe"), "Repiblik Moris")
		)
	),
	MAYOTTE(
		R.drawable.ic_flag_fr, "YT", arrayOf(
			Text(Locale("en"), "Department of Mayotte"),
			Text(Locale("fr"), "Département de Mayotte")
		)
	),
	MEXICO(
		R.drawable.ic_flag_mx, "MX", arrayOf(
			Text(Locale("en"), "United Mexican States"),
			Text(Locale("es"), "Estados Unidos Mexicanos")
		)
	),
	MICRONESIA(
		R.drawable.ic_flag_fm, "FM", arrayOf(
			Text(Locale("en"), "Federated States of Micronesia")
		)
	),
	MOLDOVA(
		R.drawable.ic_flag_md, "MD", arrayOf(
			Text(Locale("en"), "Republic of Moldova"),
			Text(Locale("ro"), "Republica Moldova")
		)
	),
	MONACO(
		R.drawable.ic_flag_mc, "MC", arrayOf(
			Text(Locale("en"), "Principality of Monaco"),
			Text(Locale("fr"), "Principauté de Monaco"),
			Text(Locale("lij"), "Prinçipatu de Múnegu")
		)
	),
	MONGOLIA(
		R.drawable.ic_flag_mn, "MN", arrayOf(
			Text(Locale("en"), "Mongolia"),
			Text(Locale("mn"), "Монгол Улс")
		)
	),
	MONTENEGRO(
		R.drawable.ic_flag_me, "ME", arrayOf(
			Text(Locale("en"), "Montenegro"),
			Text(Locale("cnr-Latn"), "Crna Gora"),
			Text(Locale("cnr-Cyrl"), "Црна Гора")
		)
	),
	MONTSERRAT(
		R.drawable.ic_flag_ms, "MS", arrayOf(
			Text(Locale("en"), "Montserrat")
		)
	),
	MOROCCO(
		R.drawable.ic_flag_ma, "MA", arrayOf(
			Text(Locale("en"), "Kingdom of Morocco"),
			Text(Locale("ar"), "المملكة المغربية"),
			Text(Locale("zgh"), "ⵜⴰⴳⵍⴷⵉⵜ ⵏ ⵍⵎⵖⵔⵉⴱ")
		)
	),
	MOZAMBIQUE(
		R.drawable.ic_flag_mz, "MZ", arrayOf(
			Text(Locale("en"), "Republic of Mozambique"),
			Text(Locale("pt"), "República de Moçambique")
		)
	),
	MUANMAR(
		R.drawable.ic_flag_mm, "MM", arrayOf(
			Text(Locale("en"), "Republic of the Union of Myanmar"),
			Text(Locale("my"), "ပြည်ထောင်စု သမ္မတ မြန်မာနိုင်ငံတော်")
		)
	),
	NAMIBIA(
		R.drawable.ic_flag_na, "NA", arrayOf(
			Text(Locale("en"), "Republic of Namibia"),
			Text(Locale("af"), "Republiek van Namibië"),
			Text(Locale("de"), "Republik Namibia"),
			Text(Locale("naq"), "Namibiab Republiki dib"),
			Text(Locale("hz"), "Orepublika yaNamibia"),
			Text(Locale("kwn"), "Republika zaNamibia"),
			Text(Locale("tn"), "Rephaboliki ya Namibia"),
			Text(Locale("loz"), "Namibia ye Lukuluhile")
		)
	),
	NAURU(
		R.drawable.ic_flag_nr, "NR", arrayOf(
			Text(Locale("en"), "Republic of Nauru"),
			Text(Locale("na"), "Repubrikin Naoero")
		)
	),
	NEPAL(
		R.drawable.ic_flag_np, "NP", arrayOf(
			Text(Locale("en"), "Federal Democratic Republic of Nepal"),
			Text(Locale("ne"), "सङ्घीय लोकतान्त्रिक गणतन्त्र नेपाल")
		)
	),
	NETHERLANDS(
		R.drawable.ic_flag_nl, "NL", arrayOf(
			Text(Locale("en"), "Kingdom of the Netherlands"),
			Text(Locale("nl"), "Koninkrijk der Nederlanden")
		)
	),
	NEW_CALEDONIA(
		R.drawable.ic_flag_nc, "NC", arrayOf(
			Text(Locale("en"), "New Caledonia"),
			Text(Locale("fr"), "Nouvelle-Calédonie")
		)
	),
	NEW_ZEALAND(
		R.drawable.ic_flag_nz, "NZ", arrayOf(
			Text(Locale("en"), "New Zealand"),
			Text(Locale("mi"), "Aotearoa")
		)
	),
	NICARAGUA(
		R.drawable.ic_flag_ni, "NI", arrayOf(
			Text(Locale("en"), "Republic of Nicaragua"),
			Text(Locale("es"), "República de Nicaragua")
		)
	),
	NIGER(
		R.drawable.ic_flag_ne, "NE", arrayOf(
			Text(Locale("en"), "Republic of the Niger"),
			Text(Locale("fr"), "République du Niger")
		)
	),
	NIGERIA(
		R.drawable.ic_flag_ng, "NG", arrayOf(
			Text(Locale("en"), "Federal Republic of Nigeria"),
			Text(Locale("ha"), "Jamhuriyar Tarayyar Najeriya"),
			Text(Locale("yo"), "Orílẹ̀-èdè Olómìniira Àpapọ̀ Nàìjíríà"),
			Text(Locale("ig"), "Ọ̀hàńjíkọ̀ Ọ̀hànézè Naìjíríyà")
		)
	),
	NIUE(
		R.drawable.ic_flag_nu, "NU", arrayOf(
			Text(Locale("en"), "Niue"),
			Text(Locale("niu"), "Niuē")
		)
	),
	NORFOLK_ISLAND(
		R.drawable.ic_flag_nf, "NF", arrayOf(
			Text(Locale("en"), "Norfolk Island"),
			Text(null, "Teratri a' Norf'k Ailen")
		)
	),
	NORTH_KOREA(
		R.drawable.ic_flag_north_korea, "KP", arrayOf(
			Text(Locale("en"), "Democratic People's Republic of Korea"),
			Text(Locale("ko"), "조선민주주의인민공화국")
		)
	),
	NORTHERN_MARIANA_ISLANDS(
		R.drawable.ic_flag_mp, "MP", arrayOf(
			Text(Locale("en"), "Northern Mariana Islands"),
			Text(Locale("ch"), "Sankattan Siha Na Islas Mariånas"),
			Text(Locale("cal"), "Commonwealth Téél Falúw kka Efáng llól Marianas")
		)
	),
	NORWAY(
		R.drawable.ic_flag_no, "NO", arrayOf(
			Text(Locale("en"), "Kingdom of Norway"),
			Text(Locale("nb"), "Kongeriket Norge"),
			Text(Locale("nn"), "Kongeriket Noreg"),
			Text(Locale("se"), "Norgga gonagasriika"),
			Text(Locale("smj"), "Vuona gånågisrijkka"),
			Text(Locale("sma"), "Nöörjen gånkarïjhke"),
			Text(Locale("fkv"), "Norjan kuninkhaanvaltakunta")
		)
	),
	OMAN(
		R.drawable.ic_flag_om, "OM", arrayOf(
			Text(Locale("en"), "Sultanate of Oman"),
			Text(Locale("ar"), "سلطنة عُمان")
		)
	),
	PAKISTAN(
		R.drawable.ic_flag_pk, "PK", arrayOf(
			Text(Locale("en"), "Islamic Republic of Pakistan"),
			Text(Locale("ur"), "اِسلامی جمہوریہ پاكِستان")
		)
	),
	PALAU(
		R.drawable.ic_flag_pw, "PW", arrayOf(
			Text(Locale("en"), "Republic of Palau"),
			Text(Locale("pau"), "Beluu er a Belau")
		)
	),
	PALESTINE(
		R.drawable.ic_flag_ps, "PS", arrayOf(
			Text(Locale("en"), "State of Palestine"),
			Text(Locale("ar"), "دولة فلسطين")
		)
	),
	PANAMA(
		R.drawable.ic_flag_pa, "PA", arrayOf(
			Text(Locale("en"), "Republic of Panama"),
			Text(Locale("es"), "República de Panamá")
		)
	),
	PAPUA_NEW_GUINEA(
		R.drawable.ic_flag_pg, "PG", arrayOf(
			Text(Locale("en"), "Independent State of Papua New Guinea"),
			Text(Locale("tpi"), "Independen Stet bilong Papua Niugini"),
			Text(Locale("ho"), "Independen Stet bilong Papua Niu Gini")
		)
	),
	PARAGUAY(
		R.drawable.ic_flag_py, "PY", arrayOf(
			Text(Locale("en"), "Republic of Paraguay"),
			Text(Locale("es"), "República del Paraguay"),
			Text(Locale("gn"), "Tetã Paraguái")
		)
	),
	PERU(
		R.drawable.ic_flag_pe, "PE", arrayOf(
			Text(Locale("en"), "Republic of Peru"),
			Text(Locale("es"), "República del Perú"),
			Text(Locale("qu"), "Piruw Ripuwlika"),
			Text(Locale("ay"), "Piruwxa Ripuwlika")
		)
	),
	PHILIPPINES(
		R.drawable.ic_flag_ph, "PH", arrayOf(
			Text(Locale("en"), "Republic of the Philippines"),
			Text(Locale("fil"), "Republika ng Pilipinas")
		)
	),
	PITCAIRN_ISLANDS(
		R.drawable.ic_flag_pn, "PN", arrayOf(
			Text(Locale("en"), "Pitcairn Islands"),
			Text(Locale("pih"), "Pitkern Ailen")
		)
	),
	POLAND(
		R.drawable.ic_flag_pl, "PL", arrayOf(
			Text(Locale("en"), "Republic of Poland"),
			Text(Locale("pl"), "Rzeczpospolita Polska")
		)
	),
	PORTUGAL(
		R.drawable.ic_flag_pt, "PT", arrayOf(
			Text(Locale("en"), "Portuguese Republic"),
			Text(Locale("pt"), "República Portuguesa")
		)
	),
	PUERTO_RICO(
		R.drawable.ic_flag_pr, "PR", arrayOf(
			Text(Locale("en"), "Puerto Rico"),
			Text(Locale("es"), "Estado Libre Asociado de Puerto Rico")
		)
	),
	QATAR(
		R.drawable.ic_flag_qa, "QA", arrayOf(
			Text(Locale("en"), "State of Qatar"),
			Text(Locale("ar"), "دولة قطر")
		)
	),
	REUNION(
		R.drawable.ic_flag_fr, "RE", arrayOf(
			Text(Locale("en"), "Réunion"),
			Text(Locale("fr"), "La Réunion")
		)
	),
	ROMANIA(
		R.drawable.ic_flag_ro, "RO", arrayOf(
			Text(Locale("en"), "Romania"),
			Text(Locale("ro"), "România")
		)
	),
	RUSSIA(
		R.drawable.ic_flag_ru, "RU", arrayOf(
			Text(Locale("en"), "Russian Federation"),
			Text(Locale("ru"), "Российская Федерация")
		)
	),
	RWANDA(
		R.drawable.ic_flag_rw, "RW", arrayOf(
			Text(Locale("en"), "Republic of Rwanda"),
			Text(Locale("rw"), "Repubulika y'u Rwanda"),
			Text(Locale("fr"), "République du Rwanda"),
			Text(Locale("sw"), "Jamhuri ya Rwanda")
		)
	),
	SAINT_BARTHELEMY(
		R.drawable.ic_flag_fr, "BL", arrayOf(
			Text(Locale("en"), "Saint Barthélemy"),
			Text(Locale("fr"), "Saint-Barthélemy")
		)
	),
	SAINT_HELENA(
		R.drawable.ic_flag_saint_helena, "SH", arrayOf(
			Text(Locale("en"), "Saint Helena")
		)
	),
	ASCENSION_ISLAND(
		R.drawable.ic_flag_ascension_island, "SH", arrayOf(
			Text(Locale("en"), "Ascension Island")
		)
	),
	TRISTAN_DA_CUNHA(
		R.drawable.ic_flag_tristan_da_cunha, "SH", arrayOf(
			Text(Locale("en"), "Tristan da Cunha")
		)
	),
	SAINT_KITTS_AND_NEVIS(
		R.drawable.ic_flag_kn, "KN", arrayOf(
			Text(Locale("en"), "Federation of Saint Christopher and Nevis")
		)
	),
	SAINT_LUCIA(
		R.drawable.ic_flag_lc, "LC", arrayOf(
			Text(Locale("en"), "Saint Lucia")
		)
	),
	SAINT_MARTIN(
		R.drawable.ic_flag_fr, "MF", arrayOf(
			Text(Locale("en"), "Saint Martin"),
			Text(Locale("fr"), "Saint-Martin")
		)
	),
	SAINT_PIERRE_AND_MIQUELON(
		R.drawable.ic_flag_pm, "PM", arrayOf(
			Text(Locale("en"), "Saint Pierre and Miquelon"),
			Text(Locale("fr"), "Saint-Pierre-et-Miquelon")
		)
	),
	SAINT_VINCENT_AND_THE_GRENADINES(
		R.drawable.ic_flag_vc, "VC", arrayOf(
			Text(Locale("en"), "Saint Vincent and the Grenadines")
		)
	),
	SAMOA(
		R.drawable.ic_flag_ws, "WS", arrayOf(
			Text(Locale("en"), "Independent State of Samoa"),
			Text(Locale("sm"), "Malo Saʻoloto Tutoʻatasi o Sāmoa")
		)
	),
	SAN_MARINO(
		R.drawable.ic_flag_sm, "SM", arrayOf(
			Text(Locale("en"), "Republic of San Marino"),
			Text(Locale("it"), "Repubblica di San Marino"),
			Text(Locale("rgn"), "Ripóbblica d' San Marein")
		)
	),
	SAO_TOME_AND_PRINCIPE(
		R.drawable.ic_flag_st, "ST", arrayOf(
			Text(Locale("en"), "Democratic Republic of São Tomé and Príncipe"),
			Text(Locale("pt"), "República Democrática de São Tomé e Príncipe")
		)
	),
	SAUDI_ARABIA(
		R.drawable.ic_flag_sa, "SA", arrayOf(
			Text(Locale("en"), "Kingdom of Saudi Arabia"),
			Text(Locale("ar"), "ٱلْمَمْلَكَة ٱلْعَرَبِيَّة ٱلسُّعُوْدِيَّة")
		)
	),
	SENEGAL(
		R.drawable.ic_flag_sn, "SN", arrayOf(
			Text(Locale("en"), "Republic of Senegal"),
			Text(Locale("fr"), "République du Sénégal")
		)
	),
	SERBIA(
		R.drawable.ic_flag_rs, "RS", arrayOf(
			Text(Locale("en"), "Republic of Serbia"),
			Text(Locale("sr-Cyrl"), "Република Србија"),
			Text(Locale("sr-Latn"), "Republika Srbija")
		)
	),
	SEYCHELLES(
		R.drawable.ic_flag_sc, "SC", arrayOf(
			Text(Locale("en"), "Republic of Seychelles"),
			Text(Locale("fr"), "République des Seychelles"),
			Text(Locale("crs"), "Repiblik Sesel")
		)
	),
	SIERRA_LEONE(
		R.drawable.ic_flag_sl, "SL", arrayOf(
			Text(Locale("en"), "Republic of Sierra Leone")
		)
	),
	SINGAPORE(
		R.drawable.ic_flag_sg, "SG", arrayOf(
			Text(Locale("en"), "Republic of Singapore"),
			Text(Locale("ms"), "Republik Singapura"),
			Text(Locale("zh"), "新加坡共和国"),
			Text(Locale("ta"), "சிங்கப்பூர் குடியரசு")
		)
	),
	SINT_MAARTEN(
		R.drawable.ic_flag_sx, "SX", arrayOf(
			Text(Locale("en"), "Sint Maarten")
		)
	),
	SLOVAKIA(
		R.drawable.ic_flag_sk, "SK", arrayOf(
			Text(Locale("en"), "Slovak Republic"),
			Text(Locale("sk"), "Slovenská republika")
		)
	),
	SLOVENIA(
		R.drawable.ic_flag_si, "SI", arrayOf(
			Text(Locale("en"), "Republic of Slovenia"),
			Text(Locale("sl"), "Republika Slovenija")
		)
	),
	SOLOMON_ISLANDS(
		R.drawable.ic_flag_sb, "SB", arrayOf(
			Text(Locale("en"), "Solomon Islands")
		)
	),
	SOMALIA(
		R.drawable.ic_flag_so, "SO", arrayOf(
			Text(Locale("en"), "Federal Republic of Somalia"),
			Text(Locale("so"), "Jamhuuriyadda Federaalka Soomaaliya"),
			Text(Locale("ar"), "جمهورية الصومال الفيدرالية")
		)
	),
	SOUTH_AFRICA(
		R.drawable.ic_flag_za, "ZA", arrayOf(
			Text(Locale("en"), "Republic of South Africa"),
			Text(Locale("zu"), "iRiphabhuliki yaseNingizimu Afrika"),
			Text(Locale("xh"), "iRiphabhlikhi yoMzantsi Afrika"),
			Text(Locale("af"), "Republiek van Suid-Afrika"),
			Text(Locale("nso"), "Repabliki ya Afrika-Borwa"),
			Text(Locale("st"), "Rephaboliki ya Afrika Borwa"),
			Text(Locale("tn"), "Rephaboliki ya Aforika Borwa"),
			Text(Locale("ts"), "Riphabliki ya Afrika Dzonga"),
			Text(Locale("ss"), "iRiphabhulikhi yaseNingizimu-Afrika"),
			Text(Locale("ve"), "Riphabuḽiki ya Afurika Tshipembe"),
			Text(Locale("nr"), "iRiphabliki yeSewula Afrika")
		)
	),
	SOUTH_GEORGIA_AND_THE_SOUTH_SANDWICH_ISLANDS(
		R.drawable.ic_flag_gs, "GS", arrayOf(
			Text(Locale("en"), "South Georgia and the South Sandwich Islands")
		)
	),
	SOUTH_SUDAN(
		R.drawable.ic_flag_ss, "SS", arrayOf(
			Text(Locale("en"), "Republic of South Sudan")
		)
	),
	SPAIN(
		R.drawable.ic_flag_es, "ES", arrayOf(
			Text(Locale("en"), "Kingdom of Spain"),
			Text(Locale("es"), "Reino de España")
		)
	),
	SRI_LANKA(
		R.drawable.ic_flag_lk, "LK", arrayOf(
			Text(Locale("en"), "Democratic Socialist Republic of Sri Lanka"),
			Text(Locale("si"), "ශ්\u200Dරී ලංකා ප්\u200Dරජාතාන්ත්\u200Dරික සමාජවාදී ජනරජය"),
			Text(Locale("ta"), "இலங்கை சனநாயக சோசலிசக் குடியரசு")
		)
	),
	SUDAN(
		R.drawable.ic_flag_sd, "SD", arrayOf(
			Text(Locale("en"), "Republic of the Sudan"),
			Text(Locale("ar"), "جمهورية السودان")
		)
	),
	SURINAME(
		R.drawable.ic_flag_sr, "SR", arrayOf(
			Text(Locale("en"), "Republic of Suriname"),
			Text(Locale("nl"), "Republiek Suriname")
		)
	),
	SVALBARD(
		R.drawable.ic_flag_no, "SJ", arrayOf(
			Text(Locale("en"), "Svalbard")
		)
	),
	JAN_MAYEN(
		R.drawable.ic_flag_no, "SJ", arrayOf(
			Text(Locale("en"), "Jan Mayen")
		)
	),
	SWEDEN(
		R.drawable.ic_flag_se, "SE", arrayOf(
			Text(Locale("en"), "Kingdom of Sweden"),
			Text(Locale("sv"), "Konungariket Sverige")
		)
	),
	SWITZERLAND(
		R.drawable.ic_flag_ch, "CH", arrayOf(
			Text(Locale("en"), "Swiss Confederation"),
			Text(Locale("de"), "Schweizerische Eidgenossenschaft"),
			Text(Locale("fr"), "Confédération suisse"),
			Text(Locale("it"), "Confederazione Svizzera"),
			Text(Locale("rm"), "Confederaziun svizra"),
			Text(Locale("la"), "Confoederatio Helvetica")
		)
	),
	SYRIA(
		R.drawable.ic_flag_sy, "SY", arrayOf(
			Text(Locale("en"), "Syrian Arab Republic"),
			Text(Locale("ar"), "ٱلْجُمْهُورِيَّةُ ٱلْعَرَبِيَّةُ ٱلسُّورِيَّةُ")
		)
	),
	TAIWAN(
		R.drawable.ic_flag_taiwan, "TW", arrayOf(
			Text(Locale("en"), "Republic of China"),
			Text(Locale("zh"), "中華民國")
		)
	),
	TAJIKISTAN(
		R.drawable.ic_flag_tj, "TJ", arrayOf(
			Text(Locale("en"), "Republic of Tajikistan"),
			Text(Locale("tg"), "Ҷумҳурии Тоҷикистон"),
			Text(Locale("ru"), "Республика Таджикистан")
		)
	),
	TANZANIA(
		R.drawable.ic_flag_tz, "TZ", arrayOf(
			Text(Locale("en"), "United Republic of Tanzania"),
			Text(Locale("sw"), "Jamhuri ya Muungano wa Tanzania")
		)
	),
	THAILAND(
		R.drawable.ic_flag_th, "TH", arrayOf(
			Text(Locale("en"), "Kingdom of Thailand"),
			Text(Locale("th"), "ราชอาณาจักรไทย"),
		)
	),
	TOGO(
		R.drawable.ic_flag_tg, "TG", arrayOf(
			Text(Locale("en"), "Togolese Republic"),
			Text(Locale("fr"), "République togolaise")
		)
	),
	TOKELAU(
		R.drawable.ic_flag_tk, "TK", arrayOf(
			Text(Locale("en"), "Tokelau")
		)
	),
	TONGA(
		R.drawable.ic_flag_to, "TO", arrayOf(
			Text(Locale("en"), "Kingdom of Tonga"),
			Text(Locale("to"), "Puleʻanga Fakatuʻi ʻo Tonga")
		)
	),
	TRINIDAD_AND_TOBAGO(
		R.drawable.ic_flag_tt, "TT", arrayOf(
			Text(Locale("en"), "Republic of Trinidad and Tobago")
		)
	),
	TUNISIA(
		R.drawable.ic_flag_tn, "TN", arrayOf(
			Text(Locale("en"), "Republic of Tunisia"),
			Text(Locale("ar"), "الجمهورية التونسية"),
			Text(Locale("fr"), "République tunisienne")
		)
	),
	TURKEY(
		R.drawable.ic_flag_tr, "TR", arrayOf(
			Text(Locale("en"), "Republic of Türkiye"),
			Text(Locale("tr"), "Türkiye Cumhuriyeti")
		)
	),
	TURKMENISTAN(
		R.drawable.ic_flag_tm, "TM", arrayOf(
			Text(Locale("en"), "Turkmenistan"),
			Text(Locale("tk"), "Türkmenistan")
		)
	),
	TURKS_AND_CAICOS_ISLANDS(
		R.drawable.ic_flag_tc, "TC", arrayOf(
			Text(Locale("en"), "Turks and Caicos Islands")
		)
	),
	TUVALU(
		R.drawable.ic_flag_tv, "TV", arrayOf(
			Text(Locale("en"), "Tuvalu")
		)
	),
	UGANDA(
		R.drawable.ic_flag_ug, "UG", arrayOf(
			Text(Locale("en"), "Republic of Uganda"),
			Text(Locale("sw"), "Jamhuri ya Uganda")
		)
	),
	UKRAINE(
		R.drawable.ic_flag_ua, "UA", arrayOf(
			Text(Locale("en"), "Ukraine"),
			Text(Locale("uk"), "Україна")
		)
	),
	UNITED_ARAB_EMIRATES(
		R.drawable.ic_flag_ae, "AE", arrayOf(
			Text(Locale("en"), "United Arab Emirates"),
			Text(Locale("ar"), "الإمارات العربية المتحدة")
		)
	),
	UNITED_KINGDOM(
		R.drawable.ic_flag_gb, "GB", arrayOf(
			Text(Locale("en"), "United Kingdom of Great Britain and Northern Ireland")
		)
	),
	UNITED_STATES_OF_AMERICA(
		R.drawable.ic_flag_us, "US", arrayOf(
			Text(Locale("en"), "United States of America")
		)
	),
	URUGUAY(
		R.drawable.ic_flag_uy, "UY", arrayOf(
			Text(Locale("en"), "Oriental Republic of Uruguay"),
			Text(Locale("es"), "República Oriental del Uruguay")
		)
	),
	UZBEKISTAN(
		R.drawable.ic_flag_uz, "UZ", arrayOf(
			Text(Locale("en"), "Republic of Uzbekistan"),
			Text(Locale("uz"), "Oʻzbekiston Respublikasi")
		)
	),
	VANUATU(
		R.drawable.ic_flag_vu, "VU", arrayOf(
			Text(Locale("en"), "Republic of Vanuatu"),
			Text(Locale("bi"), "Ripablik blong Vanuatu"),
			Text(Locale("fr"), "République de Vanuatu")
		)
	),
	VENEZUELA(
		R.drawable.ic_flag_ve, "VE", arrayOf(
			Text(Locale("en"), "Bolivarian Republic of Venezuela"),
			Text(Locale("es"), "República Bolivariana de Venezuela")
		)
	),
	VIETNAM(
		R.drawable.ic_flag_vn, "VN", arrayOf(
			Text(Locale("en"), "Socialist Republic of Vietnam"),
			Text(Locale("vi"), "Cộng hòa Xã hội chủ nghĩa Việt Nam")
		)
	),
	UNITED_STATES_VIRGIN_ISLANDS(
		R.drawable.ic_flag_vi, "VI", arrayOf(
			Text(Locale("en"), "Virgin Islands of the United States")
		)
	),
	WALLIS_AND_FUTUNA(
		R.drawable.ic_flag_fr, "WF", arrayOf(
			Text(Locale("en"), "Wallis and Futuna"),
			Text(Locale("fr"), "Wallis-et-Futuna"),
			Text(Locale("wls"), "ʻUvea mo Futuna")
		)
	),
	YEMEN(
		R.drawable.ic_flag_ye, "YE", arrayOf(
			Text(Locale("en"), "Republic of Yemen"),
			Text(Locale("ar"), "ٱلْجُمْهُورِيَّةُ ٱلْيَمَنِيَّةُ")
		)
	),
	ZAMBIA(
		R.drawable.ic_flag_zm, "ZM", arrayOf(
			Text(Locale("en"), "Republic of Zambia")
		)
	),
	ZIMBABWE(
		R.drawable.ic_flag_zw, "ZW", arrayOf(
			Text(Locale("en"), "Republic of Zimbabwe"),
			Text(Locale("sn"), "Nyika yeZimbabwe"),
			Text(Locale("nd"), "Ilizwe leZimbabwe"),
			Text(Locale("ny"), "Dziko la Zimbabwe"),
			Text(Locale("kck"), "Hango yeZimbabwe"),
			Text(Locale("tyu"), "Zimbabwe Nù"),
			Text(Locale("nmq"), "Inyika yeZimbabwe"),
			Text(Locale("ts"), "Tiko ra Zimbabwe"),
			Text(Locale("st"), "Naha ya Zimbabwe"),
			Text(Locale("toi"), "Cisi ca Zimbabwe"),
			Text(Locale("ve"), "Shango ḽa Zimbabwe"),
			Text(Locale("xh"), "Ilizwe lase-Zimbabwe")
		)
	);

	data class Text(val locale: Locale?, val value: String)

}