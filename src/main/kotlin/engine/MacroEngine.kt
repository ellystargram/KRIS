package engine

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.support.ui.Select
import window.kris.Kris
import window.kris.LoginPanel
import window.kris.TrainSelectPanel
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.swing.JTextArea

class MacroEngine {
    var driver: WebDriver? = null
//    var ticketTryThread: Thread? = null
    var goForIt = false
    private val launchEnvironment =
        "${System.getProperty("os.name").lowercase()}-${System.getProperty("os.arch").lowercase()}"
    private var engineOutput: JTextArea? = null

    private fun getWebDriver(browser: String): WebDriver? {
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
            else -> {
                outputWrite("E: 지원하지 않는 브라우저입니다. Chrome, Edge, Safari 중 하나를 선택해주세요.")
                null
            }
        }
    }

    private fun getTempFilePathFromResource(resourcePath: String): String {
        val inputStream: InputStream = object {}.javaClass.getResourceAsStream(resourcePath)
            ?: throw IllegalStateException("ChromeDriver not found at $resourcePath")
        val tempFile = File.createTempFile("driver/chrome", "")
        tempFile.deleteOnExit()
        Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        tempFile.setExecutable(true)
        return tempFile.absolutePath
    }

    private fun outputWrite(text: String) {
        if (engineOutput != null) {
            engineOutput!!.append("$text\n")
        }
    }

    private fun inputValidate(kris: Kris): Boolean {
        val id = kris.loginPanel!!.idField.text
        val password = kris.loginPanel!!.passwordField.password.toString()
        if (id.isBlank() || password.isBlank()) {
            outputWrite("E: 아이디와 비밀번호를 입력해주세요.")
            return false
        }

        val departureStation = kris.trainSelectPanel!!.departureStation.selectedItem as String
        val destinationStation = kris.trainSelectPanel!!.destinationStation.selectedItem as String
        if (departureStation == destinationStation) {
            outputWrite("E: 출발역과 도착역이 동일합니다.")
            return false
        }

        try {
            val departureDateString = kris.trainSelectPanel!!.departureDate.text
            val departureDate = LocalDate.parse(departureDateString, DateTimeFormatter.ISO_DATE)
            if (departureDate.isBefore(LocalDate.now())) {
                outputWrite("E: 출발일이 오늘 이전입니다.")
                return false
            }
        } catch (_: Exception) {
            outputWrite("E: 출발일 형식이 잘못되었습니다. YYYY-MM-DD 형식으로 입력해주세요.")
            return false
        }

        try {
            val startIndex = kris.trainSelectPanel!!.tryStartIndexInput.text.toInt()
            val endIndex = kris.trainSelectPanel!!.tryStopIndexInput.text.toInt()
            if (startIndex <= 0) {
                outputWrite("E: 시도 인덱스는 0 이상의 숫자로 입력해주세요.")
                return false
            }
            if (endIndex <= 0) {
                outputWrite("E: 시도 인덱스는 1 이상의 숫자로 입력해주세요.")
                return false
            }
        } catch (_: Exception) {
            outputWrite("E: 시도 인덱스 입력이 잘못되었습니다. 숫자로 입력해주세요.")
            return false
        }

        val genericSeatSelected = kris.trainSelectPanel!!.generalSeatButton.isSelected
        val firstSeatSelected = kris.trainSelectPanel!!.firstSeatButton.isSelected
        if (!genericSeatSelected && !firstSeatSelected) {
            outputWrite("E: 일반석 또는 특실 중 하나를 선택해주세요.")
            return false
        }

        val osName = System.getProperty("os.name").lowercase()
        val browser = (kris.browserSelectPanel!!.browserSelect.selectedItem as String).lowercase()
        if (!osName.contains("mac") && browser == "safari") {
            outputWrite("E: Safari 브라우저는 macOS 에서만 지원됩니다.")
            return false
        }
        return true
    }

    fun macroStartUp(kris: Kris) {
        if (engineOutput == null) {
            engineOutput = kris.engineControlPanel!!.engineOutput
            outputWrite("N: 자동화 엔진 결과 출력창 연결")
        }
        outputWrite("N: 입력 유효성 검사 시작")
        if (!inputValidate(kris)) {
            outputWrite("E: 입력 유효성 검사 실패")
            kris.engineControlPanel!!.goNoButton.text = "Engage"
            goForIt = false
            return
        }
        outputWrite("N: 입력 유효성 검사 성공")

        if (driver != null) {
            outputWrite("N: 기존 WebDriver 종료")
            driver!!.quit()
            driver = null
        }

        try {
            driver = getWebDriver(kris.browserSelectPanel!!.browserSelect.selectedItem as String)
        } catch (e: Exception) {
            outputWrite("E: WebDriver 초기화 실패 - ${e.message}")
            kris.engineControlPanel!!.goNoButton.text = "Engage"
            goForIt = false
            return
        }
        if (driver == null) {
            outputWrite("E: WebDriver 초기화 실패")
            kris.engineControlPanel!!.goNoButton.text = "Engage"
            goForIt = false
            return
        }
        outputWrite("N: WebDriver 초기화 성공")
        outputWrite("N: 로그인 시도")

        if (!login(kris.loginPanel!!)) {
            outputWrite("E: 로그인 실패 - 프로그램을 종료합니다.")
            kris.engineControlPanel!!.goNoButton.text = "Engage"
            goForIt = false
            driver!!.quit()
            driver = null
            return
        }

        if (!trainSet(kris.trainSelectPanel!!)) {
            outputWrite("E: 기차 선택 실패 - 프로그램을 종료합니다.")
            kris.engineControlPanel!!.goNoButton.text = "Engage"
            goForIt = false
            driver!!.quit()
            driver = null
            return
        }
    }

    private fun login(loginPanel: LoginPanel): Boolean {
        if (driver == null) {
            throw IllegalStateException("WebDriver is NULL!!")
        }

        driver!!.get("https://etk.srail.kr/cmc/01/selectLoginForm.do?pageId=TK0701000000")

        val id = loginPanel.idField.text
        val password = String(loginPanel.passwordField.password)
        if (loginPanel.loginWithCustomerNumber.isSelected) {
            driver!!.findElement(By.id("srchDvCd1")).click()
            driver!!.findElement(By.id("srchDvNm01")).sendKeys(id)
            driver!!.findElement(By.id("hmpgPwdCphd01")).sendKeys(password)
            driver!!.findElement(By.xpath("//*[@id=\"login-form\"]/fieldset/div[1]/div[2]/div[2]/div/div[2]/input"))
                .click()
        } else if (loginPanel.loginWithEmail.isSelected) {
            driver!!.findElement(By.id("srchDvCd2")).click()
            driver!!.findElement(By.id("srchDvNm02")).sendKeys(id)
            driver!!.findElement(By.id("hmpgPwdCphd02")).sendKeys(password)
            driver!!.findElement(By.xpath("//*[@id=\"login-form\"]/fieldset/div[1]/div[2]/div[3]/div/div[2]/input"))
                .click()
        } else if (loginPanel.loginWithPhoneNumber.isSelected) {
            driver!!.findElement(By.id("srchDvCd3")).click()
            driver!!.findElement(By.id("srchDvNm03")).sendKeys(id)
            driver!!.findElement(By.id("hmpgPwdCphd03")).sendKeys(password)
            driver!!.findElement(By.xpath("//*[@id=\"login-form\"]/fieldset/div[1]/div[2]/div[4]/div/div[2]/input"))
                .click()
        }

        Thread.sleep(1000) // 로그인 처리 대기

        if (driver!!.currentUrl != "https://etk.srail.kr/main.do") {
            outputWrite("E: 로그인 실패 - 아이디 또는 비밀번호가 잘못되었습니다.")
            return false
        }
        outputWrite("N: 로그인 성공")


        return true
    }

    private fun trainSet(trainSelectPanel: TrainSelectPanel): Boolean {
        if (driver == null) {
            throw IllegalStateException("WebDriver is NULL!!")
        }

        driver!!.get("https://etk.srail.kr/hpg/hra/01/selectScheduleList.do")

        Thread.sleep(2000) // 페이지 로딩 대기

        try {

            val departureStation = trainSelectPanel.departureStation.selectedItem as String
            val destinationStation = trainSelectPanel.destinationStation.selectedItem as String
            driver!!.findElement(By.id("dptRsStnCdNm")).sendKeys(departureStation)
            driver!!.findElement(By.id("arvRsStnCdNm")).sendKeys(destinationStation)

            val departureDateSelect = Select(driver!!.findElement(By.id("dptDt")))
            val departureDate = trainSelectPanel.departureDate.text.replace("-", "")
            departureDateSelect.selectByValue(departureDate)

            val departureTimeSelect = Select(driver!!.findElement(By.id("dptTm")))
            val departureTimeNum =
                (trainSelectPanel.departureTime.selectedItem as String).replace(Regex("[ 시]"), "").toInt()
            val departureTimeValue = "%02d0000".format(departureTimeNum)
            departureTimeSelect.selectByValue(departureTimeValue)

            driver!!.findElement(By.xpath("//*[@id=\"search_top_tag\"]/input")).click()
        } catch (_: Exception) {
            outputWrite("E: 기차 선택 실패")
            return false
        }

        return true
    }
}