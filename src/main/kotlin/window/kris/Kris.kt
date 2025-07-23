package window.kris

import engine.MacroEngine
import java.awt.Dimension
import java.awt.event.MouseAdapter
import javax.swing.BoxLayout
import javax.swing.JFrame

class Kris : JFrame() {
    var operatorSelectPanel: OperatorSelectPanel? = null
    var loginPanel: LoginPanel? = null
    var trainSelectPanel: TrainSelectPanel? = null
    var browserSelectPanel: BrowserSelectPanel? = null
    var engineControlPanel: EngineControlPanel? = null

    val macroEngine = MacroEngine()

    init {
        title = "KRIS"
        size = Dimension(400, 600)
        isResizable = false
        defaultCloseOperation = DISPOSE_ON_CLOSE
        setLocationRelativeTo(null)
        contentPane.layout = BoxLayout(this.contentPane, BoxLayout.Y_AXIS)

        attachOperatorSelectPanel()
        attachLoginPanel()
        attachTrainSelectPanel()
        attachBrowserSelectPanel()
        attachEngineControlPanel()

        isVisible = true
    }

    private fun attachOperatorSelectPanel() {
        if (operatorSelectPanel == null) {
            operatorSelectPanel = OperatorSelectPanel()
        }
        contentPane.add(operatorSelectPanel)
    }

    private fun attachLoginPanel() {
        if (loginPanel == null) {
            loginPanel = LoginPanel()
        }
        contentPane.add(loginPanel!!)
    }

    private fun attachTrainSelectPanel() {
        if (trainSelectPanel == null) {
            trainSelectPanel = TrainSelectPanel()
        }
        contentPane.add(trainSelectPanel!!)
    }

    private fun attachBrowserSelectPanel() {
        if (browserSelectPanel == null) {
            browserSelectPanel = BrowserSelectPanel()
        }
        contentPane.add(browserSelectPanel!!)
    }

    private fun attachEngineControlPanel() {
        if (engineControlPanel == null) {
            engineControlPanel = EngineControlPanel()
        }
        contentPane.add(engineControlPanel!!)

        engineControlPanel!!.goNoButton.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent?) {
                macroEngine.goForIt = !macroEngine.goForIt
                engineControlPanel!!.goNoButton.text = if (macroEngine.goForIt) "Stop" else "Engage"
                if (macroEngine.goForIt) {
                    macroEngine.macroStartUp(this@Kris)
                }
            }
        })
    }
}