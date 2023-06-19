package com.uogames.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.uogames.database.entity.ImageEntity
import com.uogames.database.entity.CardEntity
import com.uogames.database.entity.PhraseEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class TestImageDAO {

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
		db.imageDAO().insert(ImageEntity(1,"1", null, null))
		db.imageDAO().insert(ImageEntity(2,"2",null, null))
		db.imageDAO().insert(ImageEntity(3,"3",null, null))
		db.imageDAO().insert(ImageEntity(4,"4",null, null))

		db.phraseDAO().insert(PhraseEntity(1, "Hello", null, "en", "BELARUS", null, 1, 0, 0, 0, UUID.randomUUID(),""))
		db.phraseDAO().insert(PhraseEntity(2, "World", null, "en", "BELARUS", null, 2, 0, 0, 0, UUID.randomUUID(),""))
		db.phraseDAO().insert(PhraseEntity(3, "Hello World", null, "en", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
		db.phraseDAO().insert(PhraseEntity(4, "Привет", null, "ru", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
		db.phraseDAO().insert(PhraseEntity(5, "Мир", null, "ru", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))
		db.phraseDAO().insert(PhraseEntity(6, "Привет Мир", null, "ru", "BELARUS", null, null, 0, 0, 0, UUID.randomUUID(),""))

		db.cardDAO().insert(CardEntity(1,1,2,4,"",0,0,0,null,null))
	}

	@Test
	fun getFree()= runBlocking {
		setData()
		Assert.assertEquals(db.imageDAO().countFlow().first(), 4)
		Assert.assertEquals(db.imageDAO().freeImages().count(), 1)
		db.imageDAO().freeImages().forEach{
			db.imageDAO().delete(it)
		}
		Assert.assertEquals(db.imageDAO().countFlow().first(), 3)
	}


}