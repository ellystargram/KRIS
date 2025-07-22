package window.kris

import java.awt.Color
import java.awt.GridLayout
import java.time.LocalDateTime
import javax.swing.*
import javax.swing.border.LineBorder

class TrainSelectPanel : JPanel() {
    val stationPanel = JPanel(GridLayout(2, 2))
    val departureStation = JComboBox<String>()
    val destinationStation = JComboBox<String>()
    private val srtStationList = listOf(
        "수서",
        "동탄",
        "평택지제",
        "천안아산",
        "오송",
        "대전",
        "공주",
        "김천구미",
        "익산",
        "서대구",
        "정읍",
        "전주",
        "동대구",
        "광주송정",
        "남원",
        "경주",
        "포항",
        "밀양",
        "나주",
        "곡성",
        "울산",
        "창원중앙",
        "순천",
        "창원",
        "여천",
        "마산",
        "여수EXPO",
        "진주"
    )

    val departureDateTimePanel = JPanel(GridLayout(2, 2))
    val departureDate = JTextField()
    val departureTime = JComboBox<String>()

    val tryIndexPanel = JPanel(GridLayout(2, 2))
    val tryStartIndexInput = JTextField("1")
    val tryStopIndexInput = JTextField("1")


    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = LineBorder(Color(128, 128, 128))
        srtStationList.forEach { srtStation ->
            departureStation.addItem(srtStation)
            destinationStation.addItem(srtStation)
        }

        addStationPanel()
        addTimePanel()
        addTryIndexPanel()
    }

    private fun addStationPanel() {
        stationPanel.add(JLabel("출발역"))
        stationPanel.add(JLabel("도착역"))
        stationPanel.add(departureStation)
        stationPanel.add(destinationStation)
        add(stationPanel)
    }

    private fun addTimePanel() {
        val currentDateTime = LocalDateTime.now()
        departureDateTimePanel.add(JLabel("출발일 yyyy-mm-dd"))
        departureDateTimePanel.add(JLabel("출발시간"))

        val nearDepartureDate = if (currentDateTime.hour != 23) {
            currentDateTime.toLocalDate()
        } else {
            currentDateTime.toLocalDate().plusDays(1)
        }
        departureDate.text = nearDepartureDate.toString()
        departureDateTimePanel.add(departureDate)

        for (i in 0..23) {
            departureTime.addItem("$i 시")
        }
        val nearDepartureTime = (currentDateTime.hour + 1) % 24
        departureTime.selectedItem = "$nearDepartureTime 시"

        departureDateTimePanel.add(departureTime)
        add(departureDateTimePanel)
    }

    private fun addTryIndexPanel() {
        tryIndexPanel.add(JLabel("열차시작순번"))
        tryIndexPanel.add(JLabel("열차종료순번"))
        tryIndexPanel.add(tryStartIndexInput)
        tryIndexPanel.add(tryStopIndexInput)
        add(tryIndexPanel)
    }
}