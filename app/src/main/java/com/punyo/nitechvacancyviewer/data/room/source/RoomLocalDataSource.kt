package com.punyo.nitechvacancyviewer.data.room.source

import android.content.Context
import com.punyo.nitechvacancyviewer.application.GsonInstance
import com.punyo.nitechvacancyviewer.data.room.model.EventInfo
import com.punyo.nitechvacancyviewer.data.room.model.LectureRoomDao
import com.punyo.nitechvacancyviewer.data.room.model.LectureRoomDatabase
import com.punyo.nitechvacancyviewer.data.room.model.LectureRoomEntity
import com.punyo.nitechvacancyviewer.data.room.model.Room
import com.punyo.nitechvacancyviewer.data.room.model.RoomsDataModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.time.LocalDate
import java.time.LocalDateTime

class RoomLocalDataSource {
    private lateinit var db: LectureRoomDatabase
    private lateinit var roomDao: LectureRoomDao
    var loadedRoomsData: RoomsDataModel? = null
        private set

    suspend fun getDemoRoomsData(applicationContext: Context): RoomsDataModel {
        roomDao =
            androidx.room.Room
                .databaseBuilder(
                    applicationContext,
                    LectureRoomDatabase::class.java,
                    "lecture_room.db",
                ).createFromAsset("lecture_room.db")
                .build()
                .lectureRoomDao()
        val acquiredData = roomDao.getAll()
        val rooms: MutableList<Room> = mutableListOf()
        for (data in acquiredData) {
            val roomDisplayName = data.roomDisplayName
            val eventsInfo =
                GsonInstance.gson.fromJson(
                    data.eventsInfoJSON.replace(data.monthDay, LocalDate.now().toString()),
                    Array<EventInfo>::class.java,
                )
            rooms.add(Room(roomDisplayName, eventsInfo))
        }
        return RoomsDataModel(rooms.toTypedArray(), LocalDate.now())
    }

    suspend fun saveOneWeekRoomsDataToDBFromHTML(
        applicationContext: Context,
        html: String,
        date: LocalDate,
        overwriteOldRoomsData: Boolean = true,
    ) {
        initializeDB(applicationContext)
        if (overwriteOldRoomsData) {
            roomDao.deleteAll()
        }
        for (i in 0..6) {
            val roomsData = extractRoomsDataModelFromHTML(html, i, date)
            roomsData.rooms.forEach { room: Room ->
                roomDao.insert(
                    LectureRoomEntity(
                        monthDay = roomsData.date.toString(),
                        roomDisplayName = room.roomDisplayName,
                        eventsInfoJSON = GsonInstance.gson.toJson(room.eventsInfo),
                    ),
                )
            }
        }
    }

    suspend fun clearDB(applicationContext: Context) {
        initializeDB(applicationContext)
        roomDao.deleteAll()
    }

    suspend fun isRoomsDataExist(
        applicationContext: Context,
        date: LocalDate,
    ): Boolean {
        initializeDB(applicationContext)
        return roomDao.isDataExistByDate(date.toString()) > 0
    }

    suspend fun loadFromDB(
        applicationContext: Context,
        date: LocalDate,
    ) {
        initializeDB(applicationContext)
        val acquiredData = roomDao.getByDate(date.toString())
        if (acquiredData.isNotEmpty()) {
            val rooms: MutableList<Room> = mutableListOf()
            acquiredData.forEach { entity ->
                rooms.add(
                    Room(
                        roomDisplayName = entity.roomDisplayName,
                        eventsInfo =
                            GsonInstance.gson.fromJson(
                                entity.eventsInfoJSON,
                                Array<EventInfo>::class.java,
                            ),
                    ),
                )
            }
            loadedRoomsData = RoomsDataModel(rooms.toTypedArray(), date)
        } else {
            throw NullPointerException("No data found for the given date")
        }
    }

    private fun initializeDB(applicationContext: Context) {
        if (this::db.isInitialized.not() || this::roomDao.isInitialized.not()) {
            db =
                androidx.room.Room
                    .databaseBuilder(
                        applicationContext,
                        LectureRoomDatabase::class.java,
                        "lecture_room",
                    ).build()
            roomDao = db.lectureRoomDao()
        }
    }

    private fun extractRoomsDataModelFromHTML(
        html: String,
        row: Int,
        earliestDayOfData: LocalDate,
    ): RoomsDataModel {
        val rooms: MutableList<Room> = mutableListOf()
        val table: Element =
            Jsoup.parse(html).getElementsByClass("kyuko-shisetsu-nofixed")[row]
        val tableBody: Element = table.getElementsByTag("tbody").first()!!
        val column = tableBody.getElementsByTag("tr")
        column.forEach {
            val roomDisplayName = it.getElementsByClass("kyuko-shi-shisetsunm").text()
            val eventsData =
                it
                    .getElementsByAttributeValue("valign", "top")
                    .first()!!
                    .getElementsByClass("kyuko-shi-jugyo")
            val events: MutableList<EventInfo> = mutableListOf()
            eventsData.forEach { event ->
                val eventStrings = event.text().split("　")
                val eventTimes = eventStrings[0].split("-")
                events.add(
                    EventInfo(
                        start = parseTime(eventTimes[0]).plusDays(row.toLong()),
                        end = parseTime(eventTimes[1]).plusDays(row.toLong()),
                        eventDescription = eventStrings[1],
                    ),
                )
            }
            rooms.add(Room(roomDisplayName, events.toTypedArray()))
        }
        return RoomsDataModel(rooms.toTypedArray(), earliestDayOfData.plusDays(row.toLong()))
    }

    private fun parseTime(time: String): LocalDateTime {
        val timeArray = time.split(":")
        return LocalDateTime
            .now()
            .withHour(timeArray[0].toInt())
            .withMinute(timeArray[1].toInt())
            .withSecond(0)
    }
}
