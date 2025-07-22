package window.kris

import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.JPanel

class Kris : JFrame() {
    var operatorSelectPanel: OperatorSelectPanel? = null
    var loginPanel: LoginPanel? = null
    var trainSelectPanel: TrainSelectPanel? = null
    var engineControlPanel: EngineControlPanel? = null

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

        val junkSpace = JPanel()
        junkSpace.preferredSize = Dimension(800, 200)
//        junkSpace.background = Color(12, 12, 12) //Dark mode Stamp Color
        add(junkSpace)

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

    private fun attachEngineControlPanel() {
        if (engineControlPanel == null) {
            engineControlPanel = EngineControlPanel()
        }
        contentPane.add(engineControlPanel!!)
    }
}