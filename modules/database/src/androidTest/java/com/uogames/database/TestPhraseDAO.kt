package com.uogames.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.uogames.database.repository.PhraseRepository
import com.uogames.dto.local.LocalPhrase
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
	private lateinit var rep: PhraseRepository

	@Before
	fun createDB() {
		val appContext = InstrumentationRegistry.getInstrumentation().targetContext
		db = Room.inMemoryDatabaseBuilder(appContext, MyDatabase::class.java).build()
		rep = PhraseRepository(db.phraseDAO())
	}

	@After
	@Throws(IOException::class)
	fun closeDB() {
		db.close()
	}

	fun setData() = runBlocking {
		rep.insert(LocalPhrase(1, "Hello", null, "en", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
		rep.insert(LocalPhrase(2, "World", null, "en", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
		rep.insert(LocalPhrase(3, "Hello World", null, "en", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
		rep.insert(LocalPhrase(4, "Привет", null, "ru", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
		rep.insert(LocalPhrase(5, "Мир", null, "ru", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
		rep.insert(LocalPhrase(6, "Привет Мир", null, "ru", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
	}

	@Test
	fun testCount() = runBlocking {
		Assert.assertEquals(rep.count(), 0)
		setData()
		Assert.assertEquals(rep.count(), 6)
		Assert.assertEquals(rep.count(like = "hel"), 2)
		Assert.assertEquals(rep.count(like = "o w"), 1)
		Assert.assertEquals(rep.count(like = "т м"), 1)
		Assert.assertEquals(rep.count(like = "и"), 3)
		Assert.assertEquals(rep.count(lang = "en"), 3)
		Assert.assertEquals(rep.count(country = ""), 0)
	}

	@Test
	fun testRaw() = runBlocking {
		val repository = PhraseRepository(db.phraseDAO())
		setData()
		val phrase = repository.get(like = "Привет")
		Assert.assertEquals(phrase?.id ,4)
	}

}