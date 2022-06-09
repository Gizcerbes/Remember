package com.uogames.flags

import androidx.annotation.DrawableRes

enum class Countries(@DrawableRes val res: Int, val isoCode: String?, val country: Array<String>) {
	AFGHANISTAN(
		R.drawable.ic_flag_af,
		"AF",
		arrayOf(
			"Islamic Republic of Afghanistan",
			"د افغانستان اسلامي امارت",
			"Jumhūrī-yi Islāmī-yi Afġānistān",
			"د افغانستان اسلامي جمهوریت",
			"Da Afġānistān Islāmī Jumhoryat"
		)
	),
	ALAND(R.drawable.ic_flag_ax, "AX", arrayOf("Åland", "Ahvenanmaa", "Region of Åland", "Ahvenanmaan maakunta", "Landskapet Åland")),
	REPUBLIC_OF_ALBANIA(R.drawable.ic_flag_al, "AL", arrayOf("Republic of Albania", "Republika e Shqipërisë")),
	ALGERIA(
		R.drawable.ic_flag_dz,
		"DZ",
		arrayOf(
			"People's Democratic Republic of Algeria",
			"الجمهورية الجزائرية الديمقراطية الشعبية",
			"al-Jumhūriyya al-Jazāʾiriyya ad-Dīmuqrāṭiyya aš‑Šaʿbiyya",
			"République algérienne démocratique et populaire"
		)
	),
	AMERICAN_SAMOA(R.drawable.ic_flag_as, "AS", arrayOf("American Samoa", "Amerika Sāmoa")),
	ANDORRA(R.drawable.ic_flag_ad, "AD", arrayOf("Principality of Andorra", "Principat d'Andorra")),
	ANGOLA(R.drawable.ic_flag_ao, "AO", arrayOf("Republic of Angola", "República de Angola")),
	ANGUILLA(R.drawable.ic_flag_ai, "AI", arrayOf("Anguilla")),
	ANTARCTICA(R.drawable.ic_flag_aq, "AQ", arrayOf("Antarctica")),
	ANTIGUA_AND_BARBUDA(R.drawable.ic_flag_ag, "AG", arrayOf("Antigua and Barbuda", "Wadadli and Wa'omoni")),
	ARGENTINA(R.drawable.ic_flag_ar, "AR", arrayOf("Argentine Republic", "República Argentina")),
	ARMENIA(R.drawable.ic_flag_am, "AM", arrayOf("Republic of Armenia", "Հայաստանի Հանրապետություն", "Hayastani Hanrapetut'yun")),
	ARUBA(R.drawable.ic_flag_aw, "AW", arrayOf("Aruba")),
	AUSTRALIA(R.drawable.ic_flag_au, "AU", arrayOf("Commonwealth of Australia")),
	AUSTRIA(R.drawable.ic_flag_at, "AT", arrayOf("Republic of Austria", "Republik Österreich")),
	AZERBAIJAN(R.drawable.ic_flag_az, "AZ", arrayOf("Republic of Azerbaijan", "Azərbaycan Respublikası")),
	BAHAMAS(R.drawable.ic_flag_bs, "BS", arrayOf("Commonwealth of The Bahamas")),
	BAHRAIN(R.drawable.ic_flag_bh, "BH", arrayOf("Kingdom of Bahrain", "مملكة البحرين", "Mamlakat al-Bahrayn")),
	BANGLADESH(R.drawable.ic_flag_bd, "BD", arrayOf("People's Republic of Bangladesh", "গণপ্রজাতন্ত্রী বাংলাদেশ", "Gônoprojatontrī Bangladesh")),
	BARBADOS(R.drawable.ic_flag_bb, "BB", arrayOf("Barbados")),
	WHITE_RUSSIA(R.drawable.ic_flag_white_russia, "BY", arrayOf("Republic of Belarus", "Республика Белоруссия")),
	BELARUS(R.drawable.ic_flag_by, "BY", arrayOf("Republic of Belarus", "Рэспубліка Беларусь", "Республика Беларусь")),
	BELGIUM(R.drawable.ic_flag_be, "BE", arrayOf("Kingdom of Belgium", "Koninkrijk België", "Royaume de Belgique", "Königreich Belgien")),
	BELIZE(R.drawable.ic_flag_bz, "BZ", arrayOf("Belize")),
	BENIN(R.drawable.ic_flag_bj, "BJ", arrayOf("Republic of Benin", "République du Bénin")),
	BERMUDA(R.drawable.ic_flag_bm, "BM", arrayOf("Bermuda")),
	BHUTAN(R.drawable.ic_flag_bt, "BT", arrayOf("Kingdom of Bhutan", "འབྲུག་རྒྱལ་ཁབ", "Druk Gyal Khap")),
	BOLIVIA(
		R.drawable.ic_flag_bo,
		"BO",
		arrayOf(
			"Plurinational State of Bolivia",
			"Estado Plurinacional de Bolivia",
			"Tetã Hetãvoregua Mborivia",
			"Wuliwya Walja Markanakana Suyu",
			"Puliwya Achka Aylluska Mamallaqta"
		)
	),
	BONAIRE(R.drawable.ic_flag_bonaire, "BQ", arrayOf("Bonaire", "Boneiru")),
	SINT_EUSTATIUS(R.drawable.ic_flag_sint_eustatius, "BQ", arrayOf("Sint Eustatius")),
	SABA(R.drawable.ic_flag_saba, "BQ", arrayOf("Saba")),
	BOSNIA_AND_HERZEGOVINA(R.drawable.ic_flag_ba, "BA", arrayOf("Bosnia and Herzegovina", "Bosna i Hercegovina", "Босна и Херцеговина")),
	BOTSWANA(R.drawable.ic_flag_bw, "BW", arrayOf("Republic of Botswana", "Lefatshe la Botswana")),
	BOUVET_ISLAND(R.drawable.ic_flag_no, "BV", arrayOf("Bouvet Island", "Bouvetøya")),
	BRAZIL(R.drawable.ic_flag_br, "BR", arrayOf("Federative Republic of Brazil", "República Federativa do Brasil")),
	BRITISH_INDIAN_OCEAN_TERRITORY(R.drawable.ic_flag_io, "IO", arrayOf("British Indian Ocean Territory")),
	BRITISH_VIRGIN_ISLANDS(R.drawable.ic_flag_bvi, "VG", arrayOf("Virgin Islands")),
	BRUNEI(R.drawable.ic_flag_bn, "BN", arrayOf("Brunei Darussalam", "Negara Brunei Darussalam", "نݢارا بروني دارالسلام")),
	BULGARIA(R.drawable.ic_flag_bg, "BG", arrayOf("Republic of Bulgaria", "Република България", "Republika Bǎlgariya")),
	BURKINA_FASO(R.drawable.ic_flag_bf, "BF", arrayOf("Burkina Faso")),
	BURUNDI(R.drawable.ic_flag_bi, "BI", arrayOf("Republic of Burundi", "Repuburika y’Uburundi", "République du Burundi")),
	CAPE_VERDE(R.drawable.ic_flag_cv, "CV", arrayOf("Republic of Cabo Verde", "República de Cabo Verde", "Repúblika di Kabu Verdi")),
	CAMBODIA(R.drawable.ic_flag_kh, "KH", arrayOf("Kingdom of Cambodia", "ព្រះរាជាណាចក្រកម្ពុជា", "Preăh Réachéanachâkr Kâmpŭchéa")),
	CAMEROON(R.drawable.ic_flag_cm, "CM", arrayOf("Republic of Cameroon", "République du Cameroun")),
	CANADA(R.drawable.ic_flag_ca, "CA", arrayOf("Canada")),
	CAYMAN_ISLAND(R.drawable.ic_flag_ky, "KY", arrayOf("Cayman Islands")),
	CENTRAL_AFRICAN_REPUBLIC(R.drawable.ic_flag_cf, "CF", arrayOf("Central African Republic", "République centrafricaine", "Ködörösêse tî Bêafrîka")),
	CHAD(R.drawable.ic_flag_td, "TD", arrayOf("Republic of Chad", "جمهورية تشاد", "Jumhūriyyat Tšād", "République du Tchad")),
	CHILE(R.drawable.ic_flag_cl, "CL", arrayOf("Republic of Chile", "República de Chile")),
	CHINA(R.drawable.ic_flag_cn, "CN", arrayOf("People's Republic of China", "中华人民共和国", "Zhōnghuá Rénmín Gònghéguó")),
	CHRISTMAS_ISLAND(R.drawable.ic_flag_cx, "CX", arrayOf("Christmas Island", "圣诞岛领地 / 聖誕島領地", "Wilayah Pulau Krismas")),
	COCOS_ISLANDS(R.drawable.ic_flag_cc, "CC", arrayOf("Cocos (Keeling) Islands", "Pulu Kokos", "Wilayah Kepulauan Cocos")),
	COLOMBIA(R.drawable.ic_flag_co, "CO", arrayOf("Republic of Colombia", "República de Colombia")),
	COMOROS(R.drawable.ic_flag_km, "KM", arrayOf("Union of the Comoros", "Umoja wa Komori", "Union des Comores", "الاتحاد القمري")),
	DEMOCRATIC_REPUBLIC_CONGO(
		R.drawable.ic_flag_cd,
		"CD",
		arrayOf(
			"Democratic Republic of the Congo",
			"République démocratique du Congo",
			"Repubilika ya Kôngo ya Dimokalasi",
			"Republíki ya Kongó Demokratíki",
			"Jamhuri ya Kidemokrasia ya Kongo",
			"Ditunga dia Kongu wa Mungalaata"
		)
	),
	REPUBLIC_OF_THE_CONGO(
		R.drawable.ic_flag_cg,
		"CG",
		arrayOf("Republic of the Congo", "République du Congo", "Repubilika ya Kôngo", "Republíki ya Kongó")
	),
	COOK_ISLAND(R.drawable.ic_flag_ck, "CK", arrayOf("Cook Islands", "Kūki 'Āirani")),
	COSTA_RICA(R.drawable.ic_flag_cr, "CR", arrayOf("Republic of Costa Rica", "República de Costa Rica")),
	IVORY_COAST(R.drawable.ic_flag_ci, "CI", arrayOf("Republic of Côte d'Ivoire", "République de Côte d'Ivoire")),
	CROATIA(R.drawable.ic_flag_hr, "HR", arrayOf("Republic of Croatia", "Republika Hrvatska")),
	CUBA(R.drawable.ic_flag_cu, "CU", arrayOf("Republic of Cuba", "República de Cuba")),
	CURACAO(R.drawable.ic_flag_cw, "CW", arrayOf("Curaçao", "Kòrsou")),
	CYPRUS(R.drawable.ic_flag_cy, "CY", arrayOf("Republic of Cyprus", "Κυπριακή Δημοκρατία", "Kıbrıs Cumhuriyeti")),
	CZECH_REPUBLIC(R.drawable.ic_flag_cz, "CZ", arrayOf("Czech Republic", "Česká republika")),
	DENMARK(R.drawable.ic_flag_dk, "DK", arrayOf("Denmark", "Danmark")),
	DJIBOUTI(
		R.drawable.ic_flag_dj,
		"DJ",
		arrayOf("Republic of Djibouti", "République de Djibouti", "Jumhūrīyah Jībūtī", "جمهورية جيبوتي", "Jamhuuriyadda Jabuuti", "Gabuutih Ummuuno")
	),
	DOMINICA(R.drawable.ic_flag_dm, "DM", arrayOf("Commonwealth of Dominica")),
	DOMINICAN_REPUBLIC(R.drawable.ic_flag_do, "DO", arrayOf("Dominican Republic", "República Dominicana")),
	EAST_TIMOR(
		R.drawable.ic_flag_tl,
		"TL",
		arrayOf("Democratic Republic of Timor-Leste", "República Democrática de Timor-Leste", "Repúblika Demokrátika de Timór-Leste")
	),
	ECUADOR(R.drawable.ic_flag_ec, "EC", arrayOf("Republic of Ecuador", "República del Ecuador")),
	EGYPT(R.drawable.ic_flag_eg, "EG", arrayOf("Arab Republic of Egypt", "جمهورية مصر العربية")),
	EL_SALVADOR(R.drawable.ic_flag_sv, "SV", arrayOf("Republic of El Salvador", "República de El Salvador")),
	EQUATORIAL_GUINEA(
		R.drawable.ic_flag_gq,
		"GQ",
		arrayOf(
			"Republic of Equatorial Guinea",
			"República de Guinea Ecuatorial",
			"République de Guinée Équatoriale",
			"República da Guiné Equatorial"
		)
	),
	ERITREA(R.drawable.ic_flag_er, "ER", arrayOf("State of Eritrea")),
	ESTONIA(R.drawable.ic_flag_ee, "EE", arrayOf("Republic of Estonia", "Eesti Vabariik")),
	ESWATINI(R.drawable.ic_flag_sz, "SZ", arrayOf("Kingdom of Eswatini", "Umbuso weSwatini")),
	ETHIOPIA(
		R.drawable.ic_flag_et,
		"ET",
		arrayOf(
			"Federal Democratic Republic of Ethiopia",
			"የኢትዮጵያ ፌዴራላዊ ዴሞክራሲያዊ ሪፐብሊክ",
			"Ye-Ītyōṗṗyā Fēdēralawī Dēmokirasīyawī Rīpebilīk",
			"Rippabliikii Federaalawaa Dimokraatawaa Itiyoophiyaa",
			"Jamhuuriyadda Dimuqraadiga Federaalka Itoobiya",
			"ፌዴራላዊ ዴሞክራሲያዊ ሪፐብሊክ ኢትዮጵያ",
			"Fēdēralawī Dēmokirasīyawī Rīpebilīki Ítiyop'iya",
			"Itiyoppiya Federaalak Demokraatik Rippeblikih"
		)
	),
	FALKLAND_ISLANDS(R.drawable.ic_flag_fk, "FK", arrayOf("Falkland Islands")),
	FAROE_ISLANDS(R.drawable.ic_flag_fo, "FO", arrayOf("Faroe Islands", "Føroyar", "Færøerne")),
	FIJI(R.drawable.ic_flag_fj, "FJ", arrayOf("Republic of Fiji", "Matanitu Tugalala o Viti", "फ़िजी गणराज्य", "Fijī Gaṇarājya")),
	FINLAND(R.drawable.ic_flag_fi, "FI", arrayOf("Republic of Finland", "Suomen tasavalta", "Republiken Finland")),
	FRANCE(R.drawable.ic_flag_fr, "FR", arrayOf("French Republic", "République française")),
	FRENCH_GUIANA(R.drawable.ic_flag_fr, "GF", arrayOf("French Guiana", "Guyane")),
	FRENCH_POLYNESIA(R.drawable.ic_flag_pf, "PF", arrayOf("French Polynesia", "Polynésie française", "Pōrīnetia Farāni")),
	FRENCH_SOUTHERN_AND_ANTARCTIC_LANDS(
		R.drawable.ic_flag_tf,
		"TF",
		arrayOf("French Southern and Antarctic Lands", "Terres australes et antarctiques françaises")
	),
	GABON(R.drawable.ic_flag_ga, "GA", arrayOf("Gabonese Republic", "République gabonaise")),
	GAMBIA(R.drawable.ic_flag_gm, "GM", arrayOf("Republic of The Gambia")),
	GEORGIA(R.drawable.ic_flag_ge, "GE", arrayOf("Georgia", "საქართველო", "Sakartvelo")),
	GERMANY(R.drawable.ic_flag_de, "DE", arrayOf("Federal Republic of Germany", "Bundesrepublik Deutschland")),
	CHANA(R.drawable.ic_flag_gh, "GH", arrayOf("Republic of Ghana")),
	GIBRALTAR(R.drawable.ic_flag_gi, "GI", arrayOf("Gibraltar")),
	GREECE(R.drawable.ic_flag_gr, "GR", arrayOf("Hellenic Republic", "Ελληνική Δημοκρατία", "Ellinikí Dimokratía")),
	GREENLAND(R.drawable.ic_flag_gl, "GL", arrayOf("Greenland", "Kalaallit Nunaat", "Grønland")),
	GRENADA(R.drawable.ic_flag_gd, "GD", arrayOf("Grenada", "Gwenad")),
	GUADELOUPE(R.drawable.ic_flag_gp, "GP", arrayOf("Guadeloupe", "Gwadloup")),
	GUAM(R.drawable.ic_flag_gu, "GU", arrayOf("Guam", "Guåhan")),
	GUATEMALA(R.drawable.ic_flag_gt, "GT", arrayOf("Republic of Guatemala", "República de Guatemala")),
	GUERNSEY(R.drawable.ic_flag_gg, "GG", arrayOf("Bailiwick of Guernsey", "Bailliage de Guernesey", "Bailliage dé Guernési")),
	GUINEA(R.drawable.ic_flag_gn, "GN", arrayOf("Republic of Guinea", "République de Guinée")),
	GUINEA_BISSAU(R.drawable.ic_flag_gw, "GW", arrayOf("Republic of Guinea-Bissau", "República da Guiné-Bissau")),
	GUYANA(R.drawable.ic_flag_gy, "GY", arrayOf("Co-operative Republic of Guyana")),
	HAITI(R.drawable.ic_flag_ht, "HT", arrayOf("Republic of Haiti", "République d'Haïti", "Repiblik d Ayiti")),
	HEARD_ISLAND_AND_MCDONALD_ISLANDS(R.drawable.ic_flag_au, "HM", arrayOf("Territory of Heard Island and McDonald Islands")),
	HOLY_SEE(R.drawable.ic_flag_va, "VA", arrayOf("Holy See", "Sancta Sedes", "Santa Sede")),
	HONDURAS(R.drawable.ic_flag_hn, "HN", arrayOf("Republic of Honduras", "República de Honduras")),
	HONG_KONG(
		R.drawable.ic_flag_hk,
		"HK",
		arrayOf(
			"Hong Kong",
			"Hong Kong Special Administrative Region of the People's Republic of China",
			"中華人民共和國香港特別行政區",
			"Jūng'wàh Yàhnmàhn Guhng'wòhgwok Hēunggóng Dahkbiht Hàhngjingkēui"
		)
	),
	HUNGARY(R.drawable.ic_flag_hu, "HU", arrayOf("Hungary", "Magyarország")),
	ICELAND(R.drawable.ic_flag_is, "IS", arrayOf("Iceland", "Ísland")),
	INDIA(R.drawable.ic_flag_in, "IN", arrayOf("Republic of India", "Bhārat Gaṇarājya", "भारत गणराज्य")),
	INDONESIA(R.drawable.ic_flag_id, "ID", arrayOf("Republic of Indonesia", "Republik Indonesia")),
	IRAN(R.drawable.ic_flag_ir, "IR", arrayOf("Islamic Republic of Iran", "جمهوری اسلامی ایران", "Jomhuri-ye Eslâmi-ye Irân")),
	IRAQ(R.drawable.ic_flag_iq, "IQ", arrayOf("Republic of Iraq", "جمهورية العراق", "Jumhūriīyet al-ʿIrāq", "کۆماری عێراق", "Komarî Êraq")),
	IRELAND(R.drawable.ic_flag_ie, "IE", arrayOf("Ireland", "Éire")),
	ISLE_OF_MAN(R.drawable.ic_flag_im, "IM", arrayOf("Isle of Man", "Mannin, Ellan Vannin")),
	ISRAEL(R.drawable.ic_flag_il, "IL", arrayOf("State of Israel", "מְדִינַת יִשְׂרָאֵל", "دَوْلَة إِسْرَائِيل")),
	ITALY(R.drawable.ic_flag_it, "IT", arrayOf("Italian Republic", "Repubblica Italiana")),
	JAMAICA(R.drawable.ic_flag_jm, "JM", arrayOf("Jamaica", "Jumieka")),
	JAPAN(R.drawable.ic_flag_jp, "JP", arrayOf("Japan", "日本国", "Nippon-koku or Nihon-koku")),
	JERSEY(R.drawable.ic_flag_je, "JE", arrayOf("Bailiwick of Jersey", "Bailliage de Jersey", "Bailliage dé Jèrri")),
	JORDAN(
		R.drawable.ic_flag_jo,
		"JO",
		arrayOf("Hashemite Kingdom of Jordan", "المملكة الأردنية الهاشمية", "Al-Mamlakah al-’Urdunniyyah Al-Hāshimiyyah")
	),
	KAZAKHSTAN(
		R.drawable.ic_flag_kz,
		"KZ",
		arrayOf("Republic of Kazakhstan", "Қазақстан Республикасы", "Qazaqstan Respublikasy", "Республика Казахстан", "Respublika Kazakhstan")
	),
	KENYA(R.drawable.ic_flag_ke, "KE", arrayOf("Republic of Kenya", "Jamhuri ya Kenya")),
	KIRIBATI(R.drawable.ic_flag_ki, "KI", arrayOf("Republic of Kiribati", "Kiribati")),
	SOUTH_KOREA(R.drawable.ic_flag_kr, "KR", arrayOf("Republic of Korea", "대한민국", "Daehan Minguk")),
	KUWAIT(R.drawable.ic_flag_kw, "KW", arrayOf("State of Kuwait", "دولة الكويت", "Dawlat al-Kuwayt")),
	KYRGYZSTAN(
		R.drawable.ic_flag_kg,
		"KG",
		arrayOf("Kyrgyz Republic", "Кыргыз Республикасы", "Kyrgyz Respublikasy", "Кыргызская Республика", "Kyrgyzskaya Respublika")
	),
	LAOS(
		R.drawable.ic_flag_la,
		"LA",
		arrayOf("Lao People's Democratic Republic", "ສາທາລະນະລັດ ປະຊາທິປະໄຕ ປະຊາຊົນລາວ", "Sathalanalat Paxathipatai Paxaxôn Lao")
	),
	LATVIA(R.drawable.ic_flag_lv, "LV", arrayOf("Republic of Latvia", "Latvijas Republika", "Latvejas Republika", "Leţmō Vabāmō")),
	LEBANON(
		R.drawable.ic_flag_lb,
		"LB",
		arrayOf("Republic of Lebanon", "ٱلْجُمْهُورِيَّةُ ٱللُّبْنَانِيَّةُ", "al-jumhūrīyah al-Lubnānīyah", "République libanaise")
	),
	LESOTHO(R.drawable.ic_flag_ls, "LS", arrayOf("Kingdom of Lesotho", "Naha ea Lesotho")),
	LIBERIA(R.drawable.ic_flag_lr, "LR", arrayOf("Republic of Liberia")),
	LIBYA(R.drawable.ic_flag_ly, "LY", arrayOf("State of Libya", "دولة ليبيا", "Dawlat Lībiyā")),
	LIECHTENSTEIN(R.drawable.ic_flag_li, "LI", arrayOf("Principality of Liechtenstein", "Fürstentum Liechtenstein")),
	LITHUANIA(R.drawable.ic_flag_lt, "LT", arrayOf("Republic of Lithuania", "Lietuvos Respublika")),
	LUXEMBOURG(
		R.drawable.ic_flag_lu,
		"LU",
		arrayOf("Grand Duchy of Luxembourg", "Groussherzogtum Lëtzebuerg", "Grand-Duché de Luxembourg", "Großherzogtum Luxemburg")
	),
	MACAU(
		R.drawable.ic_flag_mo,
		"MO",
		arrayOf(
			"Macau",
			"中華人民共和國澳門特別行政區",
			"Jūng'wàh Yàhnmàhn Guhng'wòhgwok Oumún Dahkbiht Hàhngjingkēui",
			"Região Administrativa Especial de Macau da República Popular da China"
		)
	),
	NORTH_MACEDONIA(
		R.drawable.ic_flag_mk,
		"MK",
		arrayOf("Republic of North Macedonia", "Република Северна Македонија", "Republika e Maqedonisë së Veriut")
	),
	MADAGASCAR(R.drawable.ic_flag_mg, "MG", arrayOf("Republic of Madagascar", "Repoblikan'i Madagasikara", "République de Madagascar")),
	MALAWI(R.drawable.ic_flag_mw, "MW", arrayOf("Republic of Malawi", "Dziko la Malaŵi", "Charu cha Malaŵi")),
	MALAYSIA(R.drawable.ic_flag_my, "MY", arrayOf("Malaysia")),
	MALDIVES(R.drawable.ic_flag_mv, "MV", arrayOf("Republic of Maldives", "ދިވެހިރާއްޖޭގެ ޖުމްހޫރިއްޔާ", "Dhivehi Raajjeyge Jumhooriyyaa")),
	MALI(R.drawable.ic_flag_ml, "ML", arrayOf("Republic of Mali", "République du Mali")),
	MALTA(R.drawable.ic_flag_mt, "MT", arrayOf("Republic of Malta", "Repubblika ta' Malta")),
	MARSHALL_ISLANDS(R.drawable.ic_flag_mh, "MH", arrayOf("Republic of the Marshall Islands", "Aolepān Aorōkin Ṃajeḷ")),
	MARTINIQUE(
		R.drawable.ic_flag_mq,
		"MQ",
		arrayOf("Martinique", "Territorial Collectivity of Martinique", "Collectivité Territoriale de Martinique")
	),
	MAURITANIA(
		R.drawable.ic_flag_mr,
		"MR",
		arrayOf(
			"Islamic Republic of Mauritania",
			"الجمهورية الإسلامية الموريتانية",
			"al-Jumhūrīyah al-Islāmīyah al-Mūrītānīyah",
			"République islamique de Mauritanie"
		)
	),
	MAURITIUS(R.drawable.ic_flag_mu, "MU", arrayOf("Republic of Mauritius", "République de Maurice", "Repiblik Moris")),
	MAYOTTE(R.drawable.ic_flag_fr, "YT", arrayOf("Department of Mayotte", "Département de Mayotte")),
	MEXICO(R.drawable.ic_flag_mx, "MX", arrayOf("United Mexican States", "Estados Unidos Mexicanos")),
	MICRONESIA(R.drawable.ic_flag_fm, "FM", arrayOf("Federated States of Micronesia")),
	MOLDOVA(R.drawable.ic_flag_md, "MD", arrayOf("Republic of Moldova", "Republica Moldova")),
	MONACO(R.drawable.ic_flag_mc, "MC", arrayOf("Principality of Monaco", "Principauté de Monaco", "Prinçipatu de Múnegu")),
	MONGOLIA(R.drawable.ic_flag_mn, "MN", arrayOf("Mongolia", "Монгол Улс")),
	MONTENEGRO(R.drawable.ic_flag_me, "ME", arrayOf("Montenegro", "Crna Gora", "Црна Гора")),
	MONTSERRAT(R.drawable.ic_flag_ms, "MS", arrayOf("Montserrat")),
	MOROCCO(R.drawable.ic_flag_ma, "MA", arrayOf("Kingdom of Morocco", "المملكة المغربية", "ⵜⴰⴳⵍⴷⵉⵜ ⵏ ⵍⵎⵖⵔⵉⴱ")),
	MOZAMBIQUE(R.drawable.ic_flag_mz, "MZ", arrayOf("Republic of Mozambique", "República de Moçambique")),
	MUANMAR(
		R.drawable.ic_flag_mm,
		"MM",
		arrayOf("Republic of the Union of Myanmar", "ပြည်ထောင်စု သမ္မတ မြန်မာနိုင်ငံတော်", "Pyidaunzu Thanmăda Myăma Nainngandaw")
	),
	NAMIBIA(
		R.drawable.ic_flag_na,
		"NA",
		arrayOf(
			"Republic of Namibia",
			"Republiek van Namibië",
			"Republik Namibia",
			"Namibiab Republiki dib",
			"Orepublika yaNamibia",
			"Republika zaNamibia",
			"Rephaboliki ya Namibia",
			"Namibia ye Lukuluhile"
		)
	),
	NAURU(R.drawable.ic_flag_nr, "NR", arrayOf("Republic of Nauru", "Repubrikin Naoero")),
	NEPAL(
		R.drawable.ic_flag_np,
		"NP",
		arrayOf("Federal Democratic Republic of Nepal", "सङ्घीय लोकतान्त्रिक गणतन्त्र नेपाल", "Saṅghīya Lokatāntrik Gaṇatantra Nepāl")
	),
	NETHERLANDS(R.drawable.ic_flag_nl, "NL", arrayOf("Kingdom of the Netherlands", "Koninkrijk der Nederlanden")),
	NEW_CALEDONIA(R.drawable.ic_flag_nc, "NC", arrayOf("New Caledonia", "Nouvelle-Calédonie")),
	NEW_ZEALAND(R.drawable.ic_flag_nz, "NZ", arrayOf("New Zealand", "Aotearoa")),
	NICARAGUA(R.drawable.ic_flag_ni, "NI", arrayOf("Republic of Nicaragua", "República de Nicaragua")),
	NIGER(R.drawable.ic_flag_ne, "NE", arrayOf("Republic of the Niger", "République du Niger")),
	NIGERIA(
		R.drawable.ic_flag_ng,
		"NG",
		arrayOf(
			"Federal Republic of Nigeria",
			"Jamhuriyar Tarayyar Najeriya",
			"Orílẹ̀-èdè Olómìniira Àpapọ̀ Nàìjíríà",
			"Ọ̀hàńjíkọ̀ Ọ̀hànézè Naìjíríyà"
		)
	),
	NIUE(R.drawable.ic_flag_nu, "NU", arrayOf("Niue", "Niuē")),
	NORFOLK_ISLAND(R.drawable.ic_flag_nf, "NF", arrayOf("Norfolk Island", "Territory of Norfolk Island", "Teratri a' Norf'k Ailen")),
	NORTH_KOREA(
		R.drawable.ic_flag_north_korea,
		"KP",
		arrayOf("Democratic People's Republic of Korea", "조선민주주의인민공화국", "Chosŏn Minjujuŭi Inmin Konghwaguk")
	),
	NORTHERN_MARIANA_ISLANDS(
		R.drawable.ic_flag_mp,
		"MP",
		arrayOf(
			"Northern Mariana Islands",
			"Commonwealth of the Northern Mariana Islands",
			"Sankattan Siha Na Islas Mariånas",
			"Commonwealth Téél Falúw kka Efáng llól Marianas"
		)
	),
	NORWAY(
		R.drawable.ic_flag_no,
		"NO",
		arrayOf(
			"Kingdom of Norway",
			"Kongeriket Norge",
			"Kongeriket Noreg",
			"Norgga gonagasriika",
			"Vuona gånågisrijkka",
			"Nöörjen gånkarïjhke",
			"Norjan kuninkhaanvaltakunta"
		)
	),
	OMAN(R.drawable.ic_flag_om, "OM", arrayOf("Sultanate of Oman", "سلطنة عُمان", "Salṭanat ʻUmān")),
	PAKISTAN(R.drawable.ic_flag_pk, "PK", arrayOf("Islamic Republic of Pakistan", "اِسلامی جمہوریہ پاكِستان", "Islāmī Jumhūriyah Pākistān")),
	PALAU(R.drawable.ic_flag_pw, "PW", arrayOf("Republic of Palau", "Beluu er a Belau")),
	PALESTINE(R.drawable.ic_flag_ps, "PS", arrayOf("State of Palestine", "دولة فلسطين", "Dawlat Filasṭīn")),
	PANAMA(R.drawable.ic_flag_pa, "PA", arrayOf("Republic of Panama", "República de Panamá")),
	PAPUA_NEW_GUINEA(
		R.drawable.ic_flag_pg,
		"PG",
		arrayOf("Independent State of Papua New Guinea", "Independen Stet bilong Papua Niugini", "Independen Stet bilong Papua Niu Gini")
	),
	PARAGUAY(R.drawable.ic_flag_py, "PY", arrayOf("Republic of Paraguay", "República del Paraguay", "Tetã Paraguái")),
	PERU(R.drawable.ic_flag_pe, "PE", arrayOf("Republic of Peru", "República del Perú", "Piruw Ripuwlika", "Piruwxa Ripuwlika")),
	PHILIPPINES(R.drawable.ic_flag_ph, "PH", arrayOf("Republic of the Philippines", "Republika ng Pilipinas")),
	PITCAIRN_ISLANDS(R.drawable.ic_flag_pn, "PN", arrayOf("Pitcairn Islands", "Pitkern Ailen")),
	POLAND(R.drawable.ic_flag_pl, "PL", arrayOf("Republic of Poland", "Rzeczpospolita Polska")),
	PORTUGAL(R.drawable.ic_flag_pt, "PT", arrayOf("Portuguese Republic", "República Portuguesa")),
	PUERTO_RICO(
		R.drawable.ic_flag_pr,
		"PR",
		arrayOf("Puerto Rico", "Commonwealth of Puerto Rico", "Free Associated State of Puerto Rico", "Estado Libre Asociado de Puerto Rico")
	),
	QATAR(R.drawable.ic_flag_qa, "QA", arrayOf("State of Qatar", "دولة قطر", "Dawlat Qaṭar")),
	REUNION(R.drawable.ic_flag_fr, "RE", arrayOf("Réunion", "La Réunion")),
	ROMANIA(R.drawable.ic_flag_ro, "RO", arrayOf("Romania", "România")),
	RUSSIA(R.drawable.ic_flag_ru, "RU", arrayOf("Russian Federation", "Российская Федерация")),
	RWANDA(R.drawable.ic_flag_rw, "RW", arrayOf("Republic of Rwanda", "Repubulika y'u Rwanda", "République du Rwanda", "Jamhuri ya Rwanda")),
	SAINT_BARTHELEMY(R.drawable.ic_flag_fr, "BL", arrayOf("Saint Barthélemy", "Saint-Barthélemy")),
	SAINT_HELENA(R.drawable.ic_flag_saint_helena, "SH", arrayOf("Saint Helena")),
	ASCENSION_ISLAND(R.drawable.ic_flag_ascension_island, "SH", arrayOf("Ascension Island")),
	TRISTAN_DA_CUNHA(R.drawable.ic_flag_tristan_da_cunha, "SH", arrayOf("Tristan da Cunha")),
	SAINT_KITTS_AND_NEVIS(R.drawable.ic_flag_kn, "KN", arrayOf("Federation of Saint Christopher and Nevis")),
	SAINT_LUCIA(R.drawable.ic_flag_lc, "LC", arrayOf("Saint Lucia")),
	SAINT_MARTIN(R.drawable.ic_flag_fr, "MF", arrayOf("Saint Martin", "Saint-Martin")),
	SAINT_PIERRE_AND_MIQUELON(R.drawable.ic_flag_pm, "PM", arrayOf("Saint Pierre and Miquelon", "Saint-Pierre-et-Miquelon")),
	SAINT_VINCENT_AND_THE_GRENADINES(R.drawable.ic_flag_vc, "VC", arrayOf("Saint Vincent and the Grenadines")),
	SAMOA(R.drawable.ic_flag_ws, "WS", arrayOf("Independent State of Samoa", "Malo Saʻoloto Tutoʻatasi o Sāmoa")),
	SAN_MARINO(R.drawable.ic_flag_sm, "SM", arrayOf("Republic of San Marino", "Repubblica di San Marino", "Ripóbblica d' San Marein")),
	SAO_TOME_AND_PRINCIPE(
		R.drawable.ic_flag_st,
		"ST",
		arrayOf("Democratic Republic of São Tomé and Príncipe", "República Democrática de São Tomé e Príncipe")
	),
	SAUDI_ARABIA(
		R.drawable.ic_flag_sa,
		"SA",
		arrayOf("Kingdom of Saudi Arabia", "ٱلْمَمْلَكَة ٱلْعَرَبِيَّة ٱلسُّعُوْدِيَّة", "Al-Mamlakah al-ʿArabīyah as-Suʿūdīyah")
	),
	SENEGAL(R.drawable.ic_flag_sn, "SN", arrayOf("Republic of Senegal", "République du Sénégal")),
	SERBIA(R.drawable.ic_flag_rs, "RS", arrayOf("Republic of Serbia", "Република Србија", "Republika Srbija")),
	SEYCHELLES(R.drawable.ic_flag_sc, "SC", arrayOf("Republic of Seychelles", "République des Seychelles", "Repiblik Sesel")),
	SIERRA_LEONE(R.drawable.ic_flag_sl, "SL", arrayOf("Republic of Sierra Leone")),
	SINGAPORE(R.drawable.ic_flag_sg, "SG", arrayOf("Republic of Singapore", "Republik Singapura", "新加坡共和国", "சிங்கப்பூர் குடியரசு")),
	SINT_MAARTEN(R.drawable.ic_flag_sx, "SX", arrayOf("Sint Maarten")),
	SLOVAKIA(R.drawable.ic_flag_sk, "SK", arrayOf("Slovak Republic", "Slovenská republika")),
	SLOVENIA(R.drawable.ic_flag_si, "SI", arrayOf("Republic of Slovenia", "Republika Slovenija")),
	SOLOMON_ISLANDS(R.drawable.ic_flag_sb, "SB", arrayOf("Solomon Islands", "Solomon Aelan")),
	SOMALIA(
		R.drawable.ic_flag_so,
		"SO",
		arrayOf(
			"Federal Republic of Somalia",
			"Jamhuuriyadda Federaalka Soomaaliya",
			"جمهورية الصومال الفيدرالية",
			"Jumhūriyah as-Sūmāl al-Fīdirāliyah"
		)
	),
	SOUTH_AFRICA(
		R.drawable.ic_flag_za,
		"ZA",
		arrayOf(
			"Republic of South Africa",
			"iRiphabhuliki yaseNingizimu Afrika",
			"iRiphabhlikhi yoMzantsi Afrika",
			"Republiek van Suid-Afrika",
			"Repabliki ya Afrika-Borwa",
			"Rephaboliki ya Afrika Borwa",
			"Rephaboliki ya Aforika Borwa",
			"Riphabliki ya Afrika Dzonga",
			"iRiphabhulikhi yaseNingizimu-Afrika",
			"Riphabuḽiki ya Afurika Tshipembe",
			"iRiphabliki yeSewula Afrika"
		)
	),
	SOUTH_GEORGIA_AND_THE_SOUTH_SANDWICH_ISLANDS(R.drawable.ic_flag_gs, "GS", arrayOf("South Georgia and the South Sandwich Islands")),
	SOUTH_SUDAN(R.drawable.ic_flag_ss, "SS", arrayOf("Republic of South Sudan")),
	SPAIN(R.drawable.ic_flag_es, "ES", arrayOf("Kingdom of Spain", "Reino de España")),
	SRI_LANKA(
		R.drawable.ic_flag_lk,
		"LK",
		arrayOf(
			"Democratic Socialist Republic of Sri Lanka",
			"ශ්\u200Dරී ලංකා ප්\u200Dරජාතාන්ත්\u200Dරික සමාජවාදී ජනරජය",
			"இலங்கை சனநாயக சோசலிசக் குடியரசு",
			"Śrī Laṅkā Prajātāntrika Samājavādī Janarajaya",
			"Ilaṅkai Jaṉanāyaka Sōsalisak Kuṭiyarasu"
		)
	),
	SUDAN(R.drawable.ic_flag_sd, "SD", arrayOf("Republic of the Sudan", "جمهورية السودان", "Jumhūriyyat as-Sūdān")),
	SURINAME(R.drawable.ic_flag_sr, "SR", arrayOf("Republic of Suriname", "Republiek Suriname")),
	SVALBARD(R.drawable.ic_flag_no, "SJ", arrayOf("Svalbard")),
	JAN_MAYEN(R.drawable.ic_flag_no, "SJ", arrayOf("Jan Mayen")),
	SWEDEN(R.drawable.ic_flag_se, "SE", arrayOf("Kingdom of Sweden", "Konungariket Sverige")),
	SWITZERLAND(
		R.drawable.ic_flag_ch,
		"CH",
		arrayOf(
			"Swiss Confederation",
			"Schweizerische Eidgenossenschaft",
			"Confédération suisse",
			"Confederazione Svizzera",
			"Confederaziun svizra",
			"Confoederatio Helvetica"
		)
	),
	SYRIA(
		R.drawable.ic_flag_sy,
		"SY",
		arrayOf("Syrian Arab Republic", "ٱلْجُمْهُورِيَّةُ ٱلْعَرَبِيَّةُ ٱلسُّورِيَّةُ", "al-Jumhūrīyah al-ʻArabīyah as-Sūrīyah")
	),
	TAIWAN(R.drawable.ic_flag_taiwan, "TW", arrayOf("Republic of China", "中華民國", "Zhōnghuá Mínguó")),
	TAJIKISTAN(
		R.drawable.ic_falg_tj,
		"TJ",
		arrayOf("Republic of Tajikistan", "Ҷумҳурии Тоҷикистон", "Jumhurii Tojikiston", "Республика Таджикистан", "Respublika Tadjikistan")
	),
	TANZANIA(R.drawable.ic_flag_tz, "TZ", arrayOf("United Republic of Tanzania", "Jamhuri ya Muungano wa Tanzania")),
	THAILAND(R.drawable.ic_flag_th, "TH", arrayOf("Kingdom of Thailand", "ราชอาณาจักรไทย", "Ratcha-anachak Thai")),
	TOGO(R.drawable.ic_flag_tg, "TG", arrayOf("Togolese Republic", "République togolaise")),
	TOKELAU(R.drawable.ic_flag_tk, "TK", arrayOf("Tokelau")),
	TONGA(R.drawable.ic_flag_to, "TO", arrayOf("Kingdom of Tonga", "Puleʻanga Fakatuʻi ʻo Tonga")),
	TRINIDAD_AND_TOBAGO(R.drawable.ic_flag_tt, "TT", arrayOf("Republic of Trinidad and Tobago")),
	TUNISIA(R.drawable.ic_flag_tn, "TN", arrayOf("Republic of Tunisia", "الجمهورية التونسية", "al-Jumhūrīyah at-Tūnisīyah", "République tunisienne")),
	TURKEY(R.drawable.ic_flag_tr, "TR", arrayOf("Republic of Türkiye", "Türkiye Cumhuriyeti")),
	TURKMENISTAN(R.drawable.ic_flag_tm, "TM", arrayOf("Turkmenistan", "Türkmenistan")),
	TURKS_AND_CAICOS_ISLANDS(R.drawable.ic_flag_tc, "TC", arrayOf("Turks and Caicos Islands")),
	TUVALU(R.drawable.ic_flag_tv, "TV", arrayOf("Tuvalu")),
	UGANDA(R.drawable.ic_flag_ug, "UG", arrayOf("Republic of Uganda", "Jamhuri ya Uganda")),
	UKRAINE(R.drawable.ic_flag_ua, "UA", arrayOf("Ukraine", "Україна")),
	UNITED_ARAB_EMIRATES(
		R.drawable.ic_flag_ae,
		"AE",
		arrayOf("United Arab Emirates", "الإمارات العربية المتحدة", "al-ʾImārāt al-ʿArabīyah al-Muttaḥidah")
	),
	UNITED_KINGDOM(R.drawable.ic_flag_gb, "GB", arrayOf("United Kingdom of Great Britain and Northern Ireland")),
	UNITED_STATES_OF_AMERICA(R.drawable.ic_flag_us, "US", arrayOf("United States of America")),
	URUGUAY(R.drawable.ic_flag_uy, "UY", arrayOf("Oriental Republic of Uruguay", "República Oriental del Uruguay")),
	UZBEKISTAN(R.drawable.ic_flag_uz, "UZ", arrayOf("Republic of Uzbekistan", "Oʻzbekiston Respublikasi")),
	VANUATU(R.drawable.ic_flag_vu, "VU", arrayOf("Republic of Vanuatu", "Ripablik blong Vanuatu", "République de Vanuatu")),
	VENEZUELA(R.drawable.ic_flag_ve, "VE", arrayOf("Bolivarian Republic of Venezuela", "República Bolivariana de Venezuela")),
	VIETNAM(R.drawable.ic_flag_vn, "VN", arrayOf("Socialist Republic of Vietnam", "Cộng hòa Xã hội chủ nghĩa Việt Nam")),
	UNITED_STATES_VIRGIN_ISLANDS(R.drawable.ic_flag_vi, "VI", arrayOf("Virgin Islands of the United States")),
	WALLIS_AND_FUTUNA(R.drawable.ic_flag_fr, "WF", arrayOf("Wallis and Futuna", "Wallis-et-Futuna", "ʻUvea mo Futuna")),
	YEMEN(R.drawable.ic_flag_ye, "YE", arrayOf("Republic of Yemen", "ٱلْجُمْهُورِيَّةُ ٱلْيَمَنِيَّةُ", "al-Jumhūrīyah al-Yamanīyah")),
	ZAMBIA(R.drawable.ic_flag_zm, "ZM", arrayOf("Republic of Zambia")),
	ZIMBABWE(
		R.drawable.ic_flag_zw,
		"ZW",
		arrayOf(
			"Republic of Zimbabwe",
			"Nyika yeZimbabwe",
			"Ilizwe leZimbabwe",
			"Dziko la Zimbabwe",
			"Hango yeZimbabwe",
			"Zimbabwe Nù",
			"Inyika yeZimbabwe",
			"Tiko ra Zimbabwe",
			"Naha ya Zimbabwe",
			"Cisi ca Zimbabwe",
			"Shango ḽa Zimbabwe",
			"Ilizwe lase-Zimbabwe"
		)
	)

}