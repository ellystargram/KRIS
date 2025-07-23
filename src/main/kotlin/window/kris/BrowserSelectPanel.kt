package window.kris

import javax.swing.BoxLayout
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel

class BrowserSelectPanel: JPanel() {
    val browserSelect = JComboBox<String>()
    val browsers = listOf("Chrome", "Safari", "Edge", "Firefox")
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        for (browser in browsers) {
            browserSelect.addItem(browser)
        }
        add(JLabel("브라우저 지정"))
        add(browserSelect)
    }
}