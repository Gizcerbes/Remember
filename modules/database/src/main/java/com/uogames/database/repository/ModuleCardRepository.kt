package com.uogames.database.repository

import com.uogames.database.dao.ModuleCardDAO
import com.uogames.database.entity.ModuleCardEntity
import com.uogames.database.entity.ModuleEntity
import java.util.*

class ModuleCardRepository(
	private val dao: ModuleCardDAO
) {

	suspend fun insert(moduleCard: ModuleCardEntity) = dao.insert(moduleCard)

	suspend fun delete(moduleCard: ModuleCardEntity) = dao.delete(moduleCard) > 0

	suspend fun update(moduleCard: ModuleCardEntity) = dao.update(moduleCard) > 0

	fun getByModule(module: ModuleEntity) = dao.getByModuleID(module.id)

	fun getCountByModuleIdFlow(id: Int) = dao.getCountByModuleIdFlow(id)

	suspend fun getCountByModuleId(id: Int) = dao.getCountByModuleId(id)

	suspend fun getByPositionOfModule(idModule: Int, position: Int) = dao.getByPositionOfModule(idModule, position)

	suspend fun getViewByPositionOfModule(idModule: Int, position: Int) = dao.getByPositionOfModule(idModule, position)

	suspend fun getRandomModule(idModule: Int) = dao.getRandomModuleCard(idModule)

	suspend fun getRandomModuleView(idModule: Int) = dao.getRandomModuleCard(idModule)

	suspend fun getRandomModuleViewWithout(idModule: Int, idCard: Array<Int>) = dao.getRandomWithout(idModule, idCard)

	suspend fun getRandomModuleViewWithoutPhrases(idModule: Int, phraseIds: Array<Int>) =
		dao.getRandomWithoutPhrases(idModule, phraseIds)


	suspend fun getUnknowableView(idModule: Int) = dao.getUnknowable(idModule)

	suspend fun getConfusingView(idModule: Int, idPhrase: Int) = dao.getConfusing(idModule, idPhrase)

	suspend fun getConfusingWithoutPhrases(idModule: Int, idPhrase: Int, phraseIds: Array<Int>) =
		dao.getConfusingWithoutPhrases(idModule, idPhrase, phraseIds)


	suspend fun getRandomModuleWithout(idModule: Int, idCard: Int) = dao.getRandomWithout(idModule, idCard)

	suspend fun getById(id: Int) = dao.getById(id)

	suspend fun getViewById(id: Int) = dao.getById(id)

	suspend fun getByGlobalId(globalId: UUID) = dao.getByGlobalId(globalId)

	suspend fun removeByModuleId(idModule: Int) = dao.removeByModule(idModule)

}