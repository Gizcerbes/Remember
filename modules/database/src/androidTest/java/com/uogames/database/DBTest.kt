package com.uogames.database

import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.uogames.database.entity.NewCardEntity
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
		db.phraseDAO().insert(PhraseEntity(1, "Hello", "en", null, null))
		db.phraseDAO().insert(PhraseEntity(2, "World", "en", null, null))
		db.phraseDAO().insert(PhraseEntity(3, "Hello World", "en", null, null))
		db.phraseDAO().insert(PhraseEntity(4, "Привет", "ru", null, null))
		db.phraseDAO().insert(PhraseEntity(5, "Мир", "ru", null, null))
		db.phraseDAO().insert(PhraseEntity(6, "Привет Мир", "ru", null, null))

		db.newCardDAO().insert(NewCardEntity(1, 1, 4, null))
		db.newCardDAO().insert(NewCardEntity(2, 2, 5, null))
		db.newCardDAO().insert(NewCardEntity(3, 3, 6, null))

	}

	@Test
	fun innerCountTest() = runBlocking {

		setData()
		Assert.assertEquals(db.newCardDAO().getCountFlow("").first(), 3)
		Assert.assertEquals(db.newCardDAO().getCountFlow("ет").first(), 2)
		Assert.assertEquals(db.newCardDAO().getCountFlow("he").first(), 2)
		Assert.assertEquals(db.newCardDAO().getCountFlow("o w").first(), 1)
	}

	@Test
	fun getLikeTest() = runBlocking {
		setData()
		Assert.assertEquals(db.newCardDAO().getCardFlow("hello", 0).first()?.id, 1)
		Assert.assertEquals(db.newCardDAO().getCardFlow("hello", 1).first()?.id, 3)
		Assert.assertEquals(db.newCardDAO().getCardFlow("ив", 1).first()?.id, 3)
	}


}