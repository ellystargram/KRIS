package window.kris

import javax.swing.*

class OperatorSelectPanel : JPanel() {
    val operatorRadioButtonPanel = JPanel()
    val operatorRadioButtonGroup = ButtonGroup()
    val sr = JRadioButton("SR")
    val korail = JRadioButton("KORAIL")
    val operatorSelectHint = JLabel()

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
//        border = LineBorder(Color(128, 128, 128))

        addOperatorSelectHint()
        addOperatorRadioButton()
    }

    private fun addOperatorSelectHint() {
        operatorSelectHint.text = "운영사 선택"
        operatorSelectHint.alignmentX = CENTER_ALIGNMENT
        add(operatorSelectHint)
    }

    private fun addOperatorRadioButton() {
        operatorRadioButtonPanel.layout = BoxLayout(operatorRadioButtonPanel, BoxLayout.X_AXIS)

        operatorRadioButtonGroup.add(sr)
        operatorRadioButtonGroup.add(korail)

        operatorRadioButtonPanel.add(sr)
        operatorRadioButtonPanel.add(korail)

        sr.isSelected = true
        korail.isEnabled = false
        add(operatorRadioButtonPanel)
    }
}
