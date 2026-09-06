package com.punyo.nitechvacancyviewer.data.room.model

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LectureRoomDaoTest {
    private lateinit var database: LectureRoomDatabase
    private lateinit var dao: LectureRoomDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database =
            Room.inMemoryDatabaseBuilder(context, LectureRoomDatabase::class.java)
                .build()
        dao = database.lectureRoomDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun replaceAllReplacesEveryExistingEntity() =
        runBlocking {
            dao.insert(entity(id = 1, date = "2026-09-01", room = "old"))

            dao.replaceAll(
                listOf(
                    entity(id = 2, date = "2026-09-02", room = "new-a"),
                    entity(id = 3, date = "2026-09-03", room = "new-b"),
                ),
            )

            assertEquals(listOf("new-a", "new-b"), dao.getAll().map { it.roomDisplayName })
        }

    @Test
    fun replaceAllRollsBackDeletionWhenInsertionFails() =
        runBlocking {
            val oldEntity = entity(id = 1, date = "2026-09-01", room = "old")
            dao.insert(oldEntity)

            runCatching {
                dao.replaceAll(
                    listOf(
                        entity(id = 2, date = "2026-09-02", room = "new-a"),
                        entity(id = 2, date = "2026-09-03", room = "new-b"),
                    ),
                )
            }.onSuccess { error("The duplicate primary key should fail insertion") }

            assertEquals(listOf(oldEntity), dao.getAll().toList())
        }

    private fun entity(
        id: Int,
        date: String,
        room: String,
    ) = LectureRoomEntity(
        uniqueId = id,
        monthDay = date,
        roomDisplayName = room,
        eventsInfoJSON = "[]",
    )
}
