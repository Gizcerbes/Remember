package com.uogames.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.uogames.database.entity.ErrorCardEntity
import com.uogames.database.entity.PhraseEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class TestErrorCardDAO {

	lateinit var db: MyDatabase

	@Before
	fun createDB() {
		val appContext = InstrumentationRegistry.getInstrumentation().targetContext
		db = Room.inMemoryDatabaseBuilder(appContext, MyDatabase::class.java).build()
	}

	@After
	@Throws(IOException::class)
	fun closeDB() {
		db.close()
	}
//
//	private fun setData() = runBlocking {
//		db.phraseDAO().insert(PhraseEntity(1, "Hello", null, "en", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
//		db.phraseDAO().insert(PhraseEntity(2, "World", null, "en", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
//		db.phraseDAO().insert(PhraseEntity(3, "Hello World", null, "en", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
//		db.phraseDAO().insert(PhraseEntity(4, "Привет", null, "ru", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
//		db.phraseDAO().insert(PhraseEntity(5, "Мир", null, "ru", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
//		db.phraseDAO().insert(PhraseEntity(6, "Привет Мир", null, "ru", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
//		db.errorCardDAO().insert(ErrorCardEntity(0, 1, 2, 0, 0, 100))
//		db.errorCardDAO().insert(ErrorCardEntity(0, 1, 3, 0, 0, 100))
//		db.errorCardDAO().insert(ErrorCardEntity(0, 2, 3, 0, 0, 100))
//		db.errorCardDAO().insert(ErrorCardEntity(0, 3, 1, 0, 0, 100))
//	}

//	@Test
//	fun getFree() = runBlocking {
//		setData()
//		assert(true)
//	}

}