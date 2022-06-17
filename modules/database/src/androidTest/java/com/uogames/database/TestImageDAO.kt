package com.uogames.database

import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.uogames.database.entity.ImageEntity
import com.uogames.database.entity.NewCardEntity
import com.uogames.database.entity.PhraseEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

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
		db.imageDAO().insert(ImageEntity(1,"1"))
		db.imageDAO().insert(ImageEntity(2,"2"))
		db.imageDAO().insert(ImageEntity(3,"3"))
		db.imageDAO().insert(ImageEntity(4,"4"))

		db.phraseDAO().insert(PhraseEntity(1,"1", null,null, null, 1, 0))
		db.phraseDAO().insert(PhraseEntity(2,"2", null,null, null, 2, 0))

		db.newCardDAO().insert(NewCardEntity(1,1,2,4))
	}

	@Test
	fun getFree()= runBlocking {
		setData()
		Assert.assertEquals(db.imageDAO().count().first(), 4)
		db.imageDAO().clean()
		Assert.assertEquals(db.imageDAO().count().first(), 3)
	}


}