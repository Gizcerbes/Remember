package com.uogames.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.uogames.database.entity.PhraseEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class TestPhraseDAO {
	private lateinit var db: MyDatabase

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

	fun setData() = runBlocking {
		db.phraseDAO().insert(PhraseEntity(1, "Hello", null, "en", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
		db.phraseDAO().insert(PhraseEntity(2, "World", null, "en", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
		db.phraseDAO().insert(PhraseEntity(3, "Hello World", null, "en", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
		db.phraseDAO().insert(PhraseEntity(4, "Привет", null, "ru", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
		db.phraseDAO().insert(PhraseEntity(5, "Мир", null, "ru", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
		db.phraseDAO().insert(PhraseEntity(6, "Привет Мир", null, "ru", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
	}

	@Test
	fun testCount() = runBlocking {
		val contFlow = db.phraseDAO().countFLOW()
		Assert.assertEquals(contFlow.first(), 0)
		setData()
		Assert.assertEquals(contFlow.first(), 6)
		val query = MutableStateFlow("")
		val countQuery = query.flatMapLatest { db.phraseDAO().countFlow(it) }
		Assert.assertEquals(countQuery.first(), 6)
		query.value = "hel"
		Assert.assertEquals(countQuery.first(), 2)
		query.value = "o w"
		Assert.assertEquals(countQuery.first(), 1)
		query.value = "т м"
		Assert.assertEquals(countQuery.first(), 1)
		query.value = "и"
		Assert.assertEquals(countQuery.first(), 3)
	}

	@Test
	fun testDelete() = runBlocking {
		setData()
		val id = db.phraseDAO().delete(PhraseEntity(3, "", null,  "en", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
		Assert.assertEquals(id, 1)
		val id2 = db.phraseDAO().delete(PhraseEntity(3, "", null,  "en", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
		Assert.assertEquals(id2, 0)
	}

}