package com.uogames.database.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.uogames.database.dao.ModuleDAO
import com.uogames.database.entity.ModuleEntity
import com.uogames.database.map.ModuleMap.toDTO
import com.uogames.database.map.ModuleMap.toEntity
import com.uogames.database.map.ViewMap
import com.uogames.dto.local.LocalModule
import com.uogames.dto.local.LocalModuleView
import kotlinx.coroutines.flow.map
import java.util.*

class ModuleRepository(
    private val dao: ModuleDAO,
    private val map: ViewMap<ModuleEntity, LocalModuleView>
) {

    suspend fun insert(module: LocalModule) = dao.insert(module.toEntity())

    suspend fun delete(module: LocalModule) = dao.delete(module.toEntity()) > 0

    suspend fun update(module: LocalModule) = dao.update(module.toEntity()) > 0

    suspend fun count(text: String?): Int {
        return if (text == null) dao.count()
        else dao.count(text)
    }

    suspend fun count(
        text: String? = null,
        fLang: String? = null,
        sLang: String? = null,
        fCountry: String? = null,
        sCountry: String? = null
    ): Int {
        val builder = StringBuilder()
        val params = ArrayList<Any>()
        builder.append("SELECT COUNT(DISTINCT m.id) FROM modules AS m ")
        builder.append("LEFT JOIN  module_card AS mc ")
        builder.append("ON m.id = mc.id_module ")
        builder.append("LEFT JOIN cards_table AS ct ")
        builder.append("ON ct.id = mc.id_card ")
        builder.append("LEFT JOIN phrase_table AS pt1 ")
        builder.append("ON pt1.id = ct.id_phrase ")
        builder.append("LEFT JOIN phrase_table AS pt2 ")
        builder.append("ON pt2.id = ct.id_translate ")
        if (text != null || fLang != null || sLang != null || fCountry != null || sCountry != null) builder.append("WHERE ")
        text?.let {
            builder.append("m.name LIKE  '%' || ? || '%' ")
            params.add(it)
        }
        fLang?.let {
            if (params.isNotEmpty()) builder.append("AND ")
            builder.append("pt1.lang = ? ")
            params.add(it)
        }
        sLang?.let {
            if (params.isNotEmpty()) builder.append("AND ")
            builder.append("pt2.lang = ? ")
            params.add(it)
        }
        fCountry?.let {
            if (params.isNotEmpty()) builder.append("AND ")
            builder.append("pt1.country = ? ")
            params.add(it)
        }
        sCountry?.let {
            if (params.isNotEmpty()) builder.append("AND ")
            builder.append("pt2.country = ? ")
            params.add(it)
        }
        return dao.count(SimpleSQLiteQuery(builder.toString(), params.toArray()))
    }

    private suspend fun getEntity(
        text: String? = null,
        fLang: String? = null,
        sLang: String? = null,
        fCountry: String? = null,
        sCountry: String? = null,
        newest: Boolean = false,
        position: Int? = null
    ): ModuleEntity? {
        val builder = StringBuilder()
        val params = ArrayList<Any>()
        builder.append("SELECT DISTINCT m.* FROM modules AS m ")
        builder.append("LEFT JOIN  module_card AS mc ")
        builder.append("ON m.id = mc.id_module ")
        builder.append("LEFT JOIN cards_table AS ct ")
        builder.append("ON ct.id = mc.id_card ")
        builder.append("LEFT JOIN phrase_table AS pt1 ")
        builder.append("ON pt1.id = ct.id_phrase ")
        builder.append("LEFT JOIN phrase_table AS pt2 ")
        builder.append("ON pt2.id = ct.id_translate ")
        if (text != null || fLang != null || sLang != null || fCountry != null || sCountry != null) builder.append("WHERE ")
        text?.let {
            builder.append("m.name LIKE  '%' || ? || '%' ")
            params.add(it)
        }
        fLang?.let {
            if (params.isNotEmpty()) builder.append("AND ")
            builder.append("pt1.lang = ? ")
            params.add(it)
        }
        sLang?.let {
            if (params.isNotEmpty()) builder.append("AND ")
            builder.append("pt2.lang = ? ")
            params.add(it)
        }
        fCountry?.let {
            if (params.isNotEmpty()) builder.append("AND ")
            builder.append("pt1.country = ? ")
            params.add(it)
        }
        sCountry?.let {
            if (params.isNotEmpty()) builder.append("AND ")
            builder.append("pt2.country = ? ")
            params.add(it)
        }
        if (newest) builder.append("ORDER BY m.time_change DESC ")
        else builder.append("ORDER BY length(m.name), m.name ")
        position?.let { builder.append("LIMIT $position, 1") }
        return dao.get(SimpleSQLiteQuery(builder.toString(), params.toArray()))
    }

    suspend fun get(
        text: String? = null,
        fLang: String? = null,
        sLang: String? = null,
        fCountry: String? = null,
        sCountry: String? = null,
        newest: Boolean = false,
        position: Int? = null
    ) = getEntity(text, fLang, sLang, fCountry, sCountry, newest, position)?.toDTO()

    suspend fun getView(
        text: String? = null,
        fLang: String? = null,
        sLang: String? = null,
        fCountry: String? = null,
        sCountry: String? = null,
        newest: Boolean = false,
        position: Int? = null
    ) = getEntity(text, fLang, sLang, fCountry, sCountry, newest, position)?.let { map.toDTO(it) }

    suspend fun get(text: String?, position: Int): LocalModule? {
        return if (text == null) dao.get(position)?.toDTO()
        else dao.get(text, position)?.toDTO()
    }

    suspend fun getView(text: String?, position: Int) : LocalModuleView? {
        return if (text == null) dao.get(position)?.let { map.toDTO(it) }
        else dao.get(text, position)?.let { map.toDTO(it) }
    }

    fun getCount() = dao.getCount()

    fun getCountLike(like: String) = dao.getCountLike(like)

    fun getList() = dao.getList().map { it.map { module -> module.toDTO() } }

    fun getListLike(like: String) = dao.getListLike(like).map { it.map { module -> module.toDTO() } }

    suspend fun getById(id: Int) = dao.getById(id)?.toDTO()

    suspend fun getViewById(id: Int) = dao.getById(id)?.let { map.toDTO(it) }

    suspend fun getByGlobalId(globalId: UUID) = dao.getByGlobalId(globalId)?.toDTO()

    suspend fun getByPosition(like: String, position: Int) = dao.getByPosition(like, position)?.toDTO()

    fun getByIdFlow(id: Int) = dao.getByIdFlow(id).map { it?.toDTO() }

    fun isChanged(id: Int) = dao.isChanged(id)

}