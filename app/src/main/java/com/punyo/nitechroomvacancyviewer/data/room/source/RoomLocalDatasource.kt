package com.punyo.nitechroomvacancyviewer.data.room.source

import com.punyo.nitechroomvacancyviewer.data.room.model.EventInfo
import com.punyo.nitechroomvacancyviewer.data.room.model.Room
import com.punyo.nitechroomvacancyviewer.data.room.model.RoomsDataModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.time.LocalDateTime

object RoomLocalDatasource {
    var loadedRoomsData: RoomsDataModel? = null
        private set

    fun loadFromHTML(html: String) {
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
            rooms.add(Room(roomDisplayName, events))
        }
        loadedRoomsData = RoomsDataModel(rooms.toTypedArray())
    }

    private fun parseTime(time: String): LocalDateTime {
        val timeArray = time.split(":")
        return LocalDateTime.now().withHour(timeArray[0].toInt()).withMinute(timeArray[1].toInt())
            .withSecond(0)
    }

}