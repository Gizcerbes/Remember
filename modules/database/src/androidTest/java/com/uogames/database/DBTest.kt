package com.uogames.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.uogames.database.entity.CardEntity
import com.uogames.database.entity.PhraseEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DBTest {
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

	private fun setData() = runBlocking {
		db.phraseDAO().insert(PhraseEntity(1, "Hello", "en", null, null, null, 0, 0, 0,0,""))
		db.phraseDAO().insert(PhraseEntity(2, "World", "en", null, null, null, 0,0, 0,0,""))
		db.phraseDAO().insert(PhraseEntity(3, "Hello World", "en", null, null, null, 0,0, 0,0,""))
		db.phraseDAO().insert(PhraseEntity(4, "Привет", "ru", null, null, null, 0,0, 0,0,""))
		db.phraseDAO().insert(PhraseEntity(5, "Мир", "ru", null, null, null, 0,0, 0,0,""))
		db.phraseDAO().insert(PhraseEntity(6, "Привет Мир", "ru", null, null, null, 0,0, 0,0,""))

		db.cardDAO().insert(CardEntity(1, 1, 4, null,"",0,0,0,0,""))
		db.cardDAO().insert(CardEntity(2, 2, 5, null,"",0,0,0,0,""))
		db.cardDAO().insert(CardEntity(3, 3, 6, null,"",0,0,0,0,""))

	}

	@Test
	fun innerCountTest() = runBlocking {
		setData()
		Assert.assertEquals(db.cardDAO().getCountFlow("").first(), 3)
		Assert.assertEquals(db.cardDAO().getCountFlow("ет").first(), 2)
		Assert.assertEquals(db.cardDAO().getCountFlow("he").first(), 2)
		Assert.assertEquals(db.cardDAO().getCountFlow("o w").first(), 1)
	}

	@Test
	fun getLikeTest() = runBlocking {
		setData()
		Assert.assertEquals(db.cardDAO().getCardFlow("hello", 0).first()?.id, 1)
		Assert.assertEquals(db.cardDAO().getCardFlow("hello", 1).first()?.id, 3)
		Assert.assertEquals(db.cardDAO().getCardFlow("ив", 1).first()?.id, 3)
	}


}