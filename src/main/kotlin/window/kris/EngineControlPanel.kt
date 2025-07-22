package window.kris

import java.awt.Color
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.border.LineBorder

class EngineControlPanel : JPanel() {
    val goNoButton = JLabel("Engage")
    val engineOutput = JTextField()

    init {
        border = LineBorder(Color(128, 128, 128))
        layout = BoxLayout(this, BoxLayout.X_AXIS)
        preferredSize = Dimension(0, 50)

        engineOutput.isEnabled = false
        add(engineOutput)

        goNoButton.border = LineBorder(Color(128, 128, 128))
        goNoButton.isOpaque = false
        goNoButton.preferredSize = Dimension(50, 50)
        add(goNoButton)
    }
}