package window

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel

class XWindow() : JFrame() {

    val titlePanel = JPanel()
    val titleLabel = JLabel()
    val buttonPanel = JPanel()
    val exitButton = JLabel()

    val contentPanel = JPanel()

    init {
        setLocationRelativeTo(null)
        size = Dimension(800, 600)
        defaultCloseOperation = DISPOSE_ON_CLOSE

        attachTitlePanel()
        attachContentPanel()

        isVisible = true
    }

    constructor(title: String) : this() {
        this.title = title
        titleLabel.text = title
    }

    private fun attachTitlePanel() {
        titlePanel.layout = BorderLayout()
        titlePanel.preferredSize = Dimension(800, 50)
        titlePanel.background = Color(0, 0, 0)

        titleLabel.horizontalAlignment = JLabel.CENTER
        titleLabel.foreground = Color(255, 255, 255)
        titlePanel.add(titleLabel, BorderLayout.CENTER)

        buttonPanel.layout = GridLayout(1, 3)
        buttonPanel.add(exitButton)

        this.add(titlePanel, BorderLayout.NORTH)
    }

    private fun attachContentPanel() {
        contentPanel.background = Color(64, 64, 64)
        this.add(contentPanel, BorderLayout.CENTER)
    }

}