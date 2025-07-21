import engine.MacroEngine
import org.openqa.selenium.UsernameAndPassword
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPasswordField
import javax.swing.JTextField

fun main() {
//    XWindow("KRIS")

    val launchEnvironment = System.getProperty("os.name").lowercase() + "-" + System.getProperty("os.arch").lowercase()
    val macroEngine = MacroEngine(launchEnvironment)

    val testWindow = JFrame("test-window")
    testWindow.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    testWindow.setSize(800, 600)
    testWindow.setLocationRelativeTo(null)

    val idField = JTextField()
    idField.text = "id here" // Default username for testing
    val passwordField = JPasswordField()
    passwordField.text = "password here" // Default password for testing
    val goButton = JButton()

    testWindow.add(idField, "North")
    testWindow.add(passwordField, "South")
    testWindow.add(goButton, "Center")

    goButton.addActionListener {
        val username = idField.text
        val password = String(passwordField.password)
        val usernameAndPassword = UsernameAndPassword(username, password)

        macroEngine.macroing(usernameAndPassword)
    }


    testWindow.isVisible = true


}