package window.kris

import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*
import javax.swing.border.LineBorder

class LoginPanel : JPanel() {
    val loginOptionPanel = JPanel(GridLayout(1, 3))
    val loginOptionGroup = ButtonGroup()
    val loginWithCustomerNumber = JRadioButton("회원번호로 로그인")
    val loginWithEmail = JRadioButton("이메일로 로그인")
    val loginWithPhoneNumber = JRadioButton("전화번호로 로그인")

    val loginFormPanel = JPanel(GridLayout(4, 1))
    val idEnterHint = JLabel("로그인 방식을 먼저 선택하세요")
    val idField = JTextField()
    val passwordEnterHint = JLabel("비밀번호")
    val passwordField = JPasswordField()

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = LineBorder(Color(128, 128, 128))
        preferredSize = Dimension(0, 150)

        addRadioButton()
        addRadioButtonAction()
        addLoginForm()
    }

    private fun addRadioButton() {
        loginOptionGroup.add(loginWithCustomerNumber)
        loginOptionPanel.add(loginWithCustomerNumber)
        loginOptionGroup.add(loginWithEmail)
        loginOptionPanel.add(loginWithEmail)
        loginOptionGroup.add(loginWithPhoneNumber)
        loginOptionPanel.add(loginWithPhoneNumber)
        add(loginOptionPanel)
    }

    private fun addRadioButtonAction() {
        loginWithCustomerNumber.addActionListener {
            idEnterHint.text = "회원번호"
            idField.isEnabled = true
        }
        loginWithEmail.addActionListener {
            idEnterHint.text = "이메일"
            idField.isEnabled = true
        }
        loginWithPhoneNumber.addActionListener {
            idEnterHint.text = "전화번호"
            idField.isEnabled = true
        }
    }

    private fun addLoginForm() {
        loginFormPanel.add(idEnterHint)

        idField.isEnabled = false
        loginFormPanel.add(idField)
        loginFormPanel.add(passwordEnterHint)
        loginFormPanel.add(passwordField)
        add(loginFormPanel)
    }
}