package engine

import org.openqa.selenium.By
import org.openqa.selenium.UsernameAndPassword
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.Duration

class MacroEngine(val launchEnvironment: String) {
    var driver: WebDriver = getChromeDriver()

    init {
        println("SRT MACRO ENGINE STARTED")

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10))

        driver.navigate().to("https://etk.srail.co.kr/cmc/01/selectLoginForm.do")

        println("SRT MACRO ENGINE INITIALIZED")
    }

    private fun getChromeDriver(): WebDriver {
        val chromeDriverPath = getTempFilePathFromResource(
            when (launchEnvironment) {
                "mac os x-aarch64" -> {
                    println("MAC LAUNCH ENVIRONMENT: $launchEnvironment")
                    "/chromedriver/mac os x-aarch64/chromedriver"
                }

                "windows-x64" -> {
                    println("FUCKING WINDOWS LAUNCH ENVIRONMENT: $launchEnvironment")
                    "/chromedriver/windows-x64/chromedriver.exe"
                }

                else -> {
                    throw IllegalArgumentException("Not supported launch environment: $launchEnvironment")
                }
            }
        )

        System.setProperty("webdriver.chrome.driver", chromeDriverPath)

        return ChromeDriver()
    }

    private fun getTempFilePathFromResource(resourcePath: String): String {
        val inputStream: InputStream = object {}.javaClass.getResourceAsStream(resourcePath)
            ?: throw IllegalStateException("macOS ARM64 ChromeDriver not found at $resourcePath")
        val tempFile = File.createTempFile("chromedriver", "")
        tempFile.deleteOnExit()
        Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        tempFile.setExecutable(true)
        return tempFile.absolutePath
    }

    fun macroing(usernameAndPassword: UsernameAndPassword) {
        login(usernameAndPassword.username(), usernameAndPassword.password())
    }

    fun login(id: String, password: String){
        driver.findElement(By.id("srchDvNm01")).sendKeys(id)
        driver.findElement(By.id("hmpgPwdCphd01")).sendKeys(password)
        driver.findElement(By.xpath("//*[@id=\"login-form\"]/fieldset/div[1]/div[2]/div[2]/div/div[2]/input")).click()
    }

}