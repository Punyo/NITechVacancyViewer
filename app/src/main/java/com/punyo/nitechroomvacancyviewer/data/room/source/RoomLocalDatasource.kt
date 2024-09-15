package com.punyo.nitechroomvacancyviewer.data.room.source

import android.app.Application
import com.punyo.nitechroomvacancyviewer.GsonInstance
import com.punyo.nitechroomvacancyviewer.data.room.model.EventInfo
import com.punyo.nitechroomvacancyviewer.data.room.model.LectureRoomDao
import com.punyo.nitechroomvacancyviewer.data.room.model.LectureRoomDatabase
import com.punyo.nitechroomvacancyviewer.data.room.model.LectureRoomEntity
import com.punyo.nitechroomvacancyviewer.data.room.model.Room
import com.punyo.nitechroomvacancyviewer.data.room.model.RoomsDataModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.time.LocalDateTime
import java.time.MonthDay

object RoomLocalDatasource {
    private const val DBNAME = "lecture_room"
    private lateinit var db: LectureRoomDatabase
    private lateinit var roomDao: LectureRoomDao
    var loadedRoomsData: RoomsDataModel? = null
        private set

    fun loadFromHTML(html: String) {
        loadedRoomsData = extractRoomsDataModelFromHTML(html)
    }

    suspend fun saveToDBFromHTML(application: Application, html: String, date: MonthDay) {
        initializeDB(application)
        val roomsData = extractRoomsDataModelFromHTML(html)
        roomsData.rooms.forEach { room: Room ->
            roomDao.insert(
                LectureRoomEntity(
                    monthDay = date.toString(),
                    roomDisplayName = room.roomDisplayName,
                    eventsInfoJSON = GsonInstance.gson.toJson(room.eventsInfo)
                )
            )
        }
    }

    suspend fun isRoomsDataExist(application: Application, date: MonthDay): Boolean {
        initializeDB(application)
        return roomDao.isDataExistByDate(date.toString()) > 0
    }

    suspend fun loadFromDB(application: Application, date: MonthDay) {
        initializeDB(application)
        val acquiredData = roomDao.getByDate(date.toString())
        if (acquiredData.isNotEmpty()) {
            val rooms: MutableList<Room> = mutableListOf()
            acquiredData.forEach { entity ->
                rooms.add(
                    Room(
                        roomDisplayName = entity.roomDisplayName,
                        eventsInfo = GsonInstance.gson.fromJson(
                            entity.eventsInfoJSON,
                            Array<EventInfo>::class.java
                        )
                    )
                )
            }
            loadedRoomsData = RoomsDataModel(rooms.toTypedArray())
        } else {
            throw NullPointerException("No data found for the given date")
        }
    }

    private fun initializeDB(application: Application) {
        if (this::db.isInitialized.not() || this::roomDao.isInitialized.not()) {
            db = androidx.room.Room.databaseBuilder(
                application,
                LectureRoomDatabase::class.java, DBNAME
            ).build()
            roomDao = db.lectureRoomDao()
        }
    }

    private fun extractRoomsDataModelFromHTML(html: String): RoomsDataModel {
        val rooms: MutableList<Room> = mutableListOf()
        val table: Element =
            Jsoup.parse(html).getElementsByClass("kyuko-shisetsu-nofixed").first()!!
        val tableBody: Element = table.getElementsByTag("tbody").first()!!
        val column = tableBody.getElementsByTag("tr")
        column.forEach {
            val roomDisplayName = it.getElementsByClass("kyuko-shi-shisetsunm").text()
            val eventsData = it.getElementsByAttributeValue("valign", "top").first()!!
                .getElementsByClass("kyuko-shi-jugyo")
            val events: MutableList<EventInfo> = mutableListOf()
            eventsData.forEach() { event ->
                val eventStrings = event.text().split("　")
                val eventTimes = eventStrings[0].split("-")
                events.add(
                    EventInfo(
                        start = parseTime(eventTimes[0]),
                        end = parseTime(eventTimes[1]),
                        eventDescription = eventStrings[1]
                    )
                )
            }
            rooms.add(Room(roomDisplayName, events.toTypedArray()))
        }
        return RoomsDataModel(rooms.toTypedArray())
    }

    private fun parseTime(time: String): LocalDateTime {
        val timeArray = time.split(":")
        return LocalDateTime.now().withHour(timeArray[0].toInt()).withMinute(timeArray[1].toInt())
            .withSecond(0)
    }
}