package engine

import org.openqa.selenium.By
import org.openqa.selenium.UsernameAndPassword
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.edge.EdgeDriver
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class MacroEngine(val launchEnvironment: String) {
    var driver: WebDriver? = null

    init {
        println("SRT MACRO ENGINE STARTED")

        println("SRT MACRO ENGINE INITIALIZED")
    }

    private fun getEdgeWebDriver(): WebDriver? {
        val fileName = if (System.getProperty("os.name").lowercase().contains("windows")) {
            "msedgedriver.exe"
        } else {
            "msedgedriver"
        }
        try {
            val edgeDriverPath = getTempFilePathFromResource("/edgedriver/$launchEnvironment/$fileName")
            System.setProperty("webdriver.edge.driver", edgeDriverPath)
            return EdgeDriver()
        } catch (e: Exception) {
            IllegalStateException("Edge driver for your system not fount", e)
        }
        return null
    }

    private fun getChromeWebDriver(): WebDriver? {
        val fileName = if (System.getProperty("os.name").lowercase().contains("windows")) {
            "chromedriver.exe"
        } else {
            "chromedriver"
        }
        try {
            val chromeDriverPath = getTempFilePathFromResource("/chromedriver/$launchEnvironment/$fileName")
            System.setProperty("webdriver.chrome.driver", chromeDriverPath)
            return ChromeDriver()
        } catch (e: Exception) {
            IllegalStateException("Chrome driver for your system not found", e)
        }
        return null
    }

    private fun getTempFilePathFromResource(resourcePath: String): String {
        val inputStream: InputStream = object {}.javaClass.getResourceAsStream(resourcePath)
            ?: throw IllegalStateException("ChromeDriver not found at $resourcePath")
        val tempFile = File.createTempFile("chromedriver", "")
        tempFile.deleteOnExit()
        Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        tempFile.setExecutable(true)
        return tempFile.absolutePath
    }

    fun macroing(usernameAndPassword: UsernameAndPassword) {
        login(usernameAndPassword.username(), usernameAndPassword.password())
    }

    fun login(id: String, password: String) {
        if (driver == null) {
            throw IllegalStateException("WebDriver is NULL!!")
        }
        driver!!.findElement(By.id("srchDvNm01")).sendKeys(id)
        driver!!.findElement(By.id("hmpgPwdCphd01")).sendKeys(password)
        driver!!.findElement(By.xpath("//*[@id=\"login-form\"]/fieldset/div[1]/div[2]/div[2]/div/div[2]/input")).click()
    }

}