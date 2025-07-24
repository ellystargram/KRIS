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
    val tryStartIndexInput = JComboBox<Int>()
    val tryStopIndexInput = JComboBox<Int>()

    val seatTypePanel = JPanel(GridLayout(1, 3))
    val generalSeatButton = JCheckBox("일반석")
    val firstSeatButton = JCheckBox("특실")
    val reserveHoldButton = JCheckBox("예약대기")

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
        addSeatTypePanel()
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

        for (i in 0..22 step 2) {
            departureTime.addItem("$i 시")
        }
        val nearDepartureTime = (currentDateTime.hour + 1) % 24
        departureTime.selectedItem = "$nearDepartureTime 시"

        departureDateTimePanel.add(departureTime)
        add(departureDateTimePanel)
    }

    private fun addTryIndexPanel() {
        tryIndexPanel.add(JLabel("열차시작순번"))
        tryIndexPanel.add(JLabel("알아볼 편 수"))
        for (i in 1 .. 10){
            tryStartIndexInput.addItem(i)
            tryStopIndexInput.addItem(i)
        }
        tryIndexPanel.add(tryStartIndexInput)
        tryIndexPanel.add(tryStopIndexInput)

        tryStartIndexInput.addActionListener {
            val startIndexInt = tryStartIndexInput.selectedItem as Int
            val stopIndexMax = 10 - startIndexInt + 1
            tryStopIndexInput.removeAllItems()
            for (i in 1 .. stopIndexMax) {
                tryStopIndexInput.addItem(i)
            }
        }

        add(tryIndexPanel)
    }

    private fun addSeatTypePanel() {
        generalSeatButton.isSelected = true
        seatTypePanel.add(generalSeatButton)
        firstSeatButton.isSelected = true
        seatTypePanel.add(firstSeatButton)
        reserveHoldButton.isSelected = false
        seatTypePanel.add(reserveHoldButton)
        add(seatTypePanel)
    }
}