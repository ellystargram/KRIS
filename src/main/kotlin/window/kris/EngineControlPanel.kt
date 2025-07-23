package window.kris

import java.awt.Color
import java.awt.Dimension
import javax.swing.*
import javax.swing.border.LineBorder

class EngineControlPanel : JPanel() {
    val goNoButton = JLabel("Engage")
    val engineOutput = JTextArea()
    val engineOutputScrollPane = JScrollPane(engineOutput)

    init {
        border = LineBorder(Color(128, 128, 128))
        layout = BoxLayout(this, BoxLayout.X_AXIS)
        preferredSize = Dimension(0, 50)

        engineOutput.isEnabled = false
        engineOutput.lineWrap = true
        engineOutput.wrapStyleWord = true
        engineOutputScrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        engineOutputScrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        add(engineOutputScrollPane)

        goNoButton.border = LineBorder(Color(128, 128, 128))
        goNoButton.isOpaque = false
        goNoButton.preferredSize = Dimension(50, 50)
        add(goNoButton)
    }
}