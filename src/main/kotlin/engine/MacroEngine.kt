package engine

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.safari.SafariDriver
import window.kris.Kris
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import javax.swing.JTextArea

open class MacroEngine {
    protected var driver: WebDriver? = null
    protected var ticketTryThread: Thread? = null
    var goForIt = false
    private val launchEnvironment =
        "${System.getProperty("os.name").lowercase()}-${System.getProperty("os.arch").lowercase()}"
    protected var engineOutput: JTextArea? = null
    protected var kris: Kris? = null

    protected fun getWebDriver(browser: String): WebDriver? {
        val browser = browser.lowercase()
        if (browser == "safari") {
            return SafariDriver()
        }
        val fileName = if (System.getProperty("os.name").lowercase().contains("windows")) {
            "${browser}driver.exe"
        } else {
            "${browser}driver"
        }
        try {
            val driverPath = getTempFilePathFromResource("/driver/$browser/$launchEnvironment/$fileName")
            System.setProperty("webdriver.$browser.driver", driverPath)
        } catch (_: Exception) {
            outputWrite("E: $launchEnvironment 전용 $browser 드라이버를 찾을 수 없습니다.")
        }
        return when (browser) {
            "chrome" -> ChromeDriver()
            "edge" -> EdgeDriver()
            "firefox" -> FirefoxDriver()
            else -> {
                outputWrite("E: 지원하지 않는 브라우저입니다. Chrome, Edge, Safari 중 하나를 선택해주세요.")
                null
            }
        }
    }

    protected fun getTempFilePathFromResource(resourcePath: String): String {
        val inputStream: InputStream = object {}.javaClass.getResourceAsStream(resourcePath)
            ?: throw IllegalStateException("ChromeDriver not found at $resourcePath")
        val tempFile = File.createTempFile("driver/chrome", "")
        tempFile.deleteOnExit()
        Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        tempFile.setExecutable(true)
        return tempFile.absolutePath
    }

    protected fun outputWrite(text: String) {
        if (engineOutput != null) {
            engineOutput!!.append("$text\n")
        }
    }
}