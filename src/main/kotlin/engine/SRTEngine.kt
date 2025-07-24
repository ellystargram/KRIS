package engine

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.Select
import window.kris.Kris
import window.kris.LoginPanel
import window.kris.TrainSelectPanel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SRTEngine : MacroEngine() {
    private fun inputValidate(): Boolean {
        val id = kris!!.loginPanel!!.idField.text
        val password = kris!!.loginPanel!!.passwordField.password.toString()
        if (id.isBlank() || password.isBlank()) {
            outputWrite("E: 아이디와 비밀번호를 입력해주세요.")
            return false
        }

        val departureStation = kris!!.trainSelectPanel!!.departureStation.selectedItem as String
        val destinationStation = kris!!.trainSelectPanel!!.destinationStation.selectedItem as String
        if (departureStation == destinationStation) {
            outputWrite("E: 출발역과 도착역이 동일합니다.")
            return false
        }

        try {
            val departureDateString = kris!!.trainSelectPanel!!.departureDate.text
            val departureDate = LocalDate.parse(departureDateString, DateTimeFormatter.ISO_DATE)
            if (departureDate.isBefore(LocalDate.now())) {
                outputWrite("E: 출발일이 오늘 이전입니다.")
                return false
            }
        } catch (_: Exception) {
            outputWrite("E: 출발일 형식이 잘못되었습니다. YYYY-MM-DD 형식으로 입력해주세요.")
            return false
        }

        val genericSeatSelected = kris!!.trainSelectPanel!!.generalSeatButton.isSelected
        val firstSeatSelected = kris!!.trainSelectPanel!!.firstSeatButton.isSelected
        val reserveHoldSelected = kris!!.trainSelectPanel!!.reserveHoldButton.isSelected
        if (!genericSeatSelected && !firstSeatSelected && !reserveHoldSelected) {
            outputWrite("E: 일반석 또는 특실, 예약대기 중 하나를 선택해주세요.")
            return false
        }

        val osName = System.getProperty("os.name").lowercase()
        val browser = (kris!!.browserSelectPanel!!.browserSelect.selectedItem as String).lowercase()
        if (!osName.contains("mac") && browser == "safari") {
            outputWrite("E: Safari 브라우저는 macOS 에서만 지원됩니다.")
            return false
        }
        return true
    }

    fun toggleMacro(kris: Kris) {
        this.kris = kris
        if (goForIt) { // Engine is running already
            engineCutOff()
        } else { //Engine is not running
            macroStartUp()
        }
    }

    fun macroStartUp() {
        if (goForIt) {
            outputWrite("N: 자동화 엔진 이미 작동중")
            return
        }
        goForIt = true
        if (engineOutput == null) {
            engineOutput = kris!!.engineControlPanel!!.engineOutput
            outputWrite("N: 자동화 엔진 결과 출력창 연결")
        }
        outputWrite("N: 입력 유효성 검사 시작")
        if (!inputValidate()) {
            outputWrite("E: 입력 유효성 검사 실패")
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
            driver = getWebDriver(kris!!.browserSelectPanel!!.browserSelect.selectedItem as String)
            driver!!.manage().window().maximize()
        } catch (e: Exception) {
            outputWrite("E: WebDriver 초기화 실패 - ${e.message}")
            goForIt = false
            return
        }
        if (driver == null) {
            outputWrite("E: WebDriver 초기화 실패")
            goForIt = false
            return
        }
        outputWrite("N: WebDriver 초기화 성공")
        outputWrite("N: 로그인 시도")

        if (!login(kris!!.loginPanel!!)) {
            outputWrite("E: 로그인 실패 - 프로그램을 종료합니다.")
            goForIt = false
            driver!!.quit()
            driver = null
            return
        }

        if (!trainSet(kris!!.trainSelectPanel!!)) {
            outputWrite("E: 기차 선택 실패 - 프로그램을 종료합니다.")
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
        Thread.sleep(2000) //로그인 사이트 대기

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

        Thread.sleep(2000) // 로그인 처리 대기

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
            val departureStationInput = driver!!.findElement(By.id("dptRsStnCdNm"))
            val destinationStationInput = driver!!.findElement(By.id("arvRsStnCdNm"))
            departureStationInput.clear()
            departureStationInput.sendKeys(departureStation)
            destinationStationInput.clear()
            destinationStationInput.sendKeys(destinationStation)

            val departureDateSelect = Select(driver!!.findElement(By.id("dptDt")))
            val departureDate = trainSelectPanel.departureDate.text.replace("-", "")
            departureDateSelect.selectByValue(departureDate)

            val departureTimeSelect = Select(driver!!.findElement(By.id("dptTm")))
            val departureTimeNum =
                (trainSelectPanel.departureTime.selectedItem as String).replace(Regex("[ 시]"), "").toInt()
            val departureTimeValue = "%02d0000".format(departureTimeNum)
            departureTimeSelect.selectByValue(departureTimeValue)

            trainLooking(trainSelectPanel)
        } catch (_: Exception) {
            outputWrite("E: 기차 선택 실패")
            return false
        }

        return true
    }

    private fun trainLooking(trainSelectPanel: TrainSelectPanel): Boolean {
        if (ticketTryThread != null) {
            outputWrite("E: 이미 티케팅 중입니다")
            return false
        }
        ticketTryThread = Thread() {
            var tryCount: Long = 0
            var successful = false
            while (goForIt) {
                val searchButton = driver!!.findElement(By.xpath("//*[@id=\"search_top_tag\"]/input"))
                (driver as JavascriptExecutor).executeScript("arguments[0].click();", searchButton)

                tryCount++
                outputWrite("N: ${tryCount}번째 시도")
                Thread.sleep(2000) //ListUpdate 대기

                val trainStartIndex = trainSelectPanel.tryStartIndexInput.selectedItem as Int
                val trainCount = trainSelectPanel.tryStopIndexInput.selectedItem as Int
                val genericSeatActive = trainSelectPanel.generalSeatButton.isSelected
                val firstSeatActive = trainSelectPanel.firstSeatButton.isSelected
                val reserveHoldActive = trainSelectPanel.reserveHoldButton.isSelected
                for (i in trainStartIndex until trainStartIndex + trainCount) {
                    if (genericSeatActive) {
                        val genericSeat =
                            driver!!.findElement(By.cssSelector("#result-form > fieldset > div.tbl_wrap.th_thead > table > tbody > tr:nth-child(${i}) > td:nth-child(7)"))
                        if (genericSeat.text.contains("예약하기")) {
                            val genericButton =
                                driver!!.findElement(By.xpath("//*[@id=\"result-form\"]/fieldset/div[6]/table/tbody/tr[${i}]/td[7]/a/span"))
                            successful = clickButton(genericButton)
                        }
                    }
                    if (firstSeatActive && !successful) {
                        val firstSeat =
                            driver!!.findElement(By.cssSelector("#result-form > fieldset > div.tbl_wrap.th_thead > table > tbody > tr:nth-child(${i}) > td:nth-child(6)"))
                        if (firstSeat.text.contains("예약하기")) {
                            val firstButton =
                                driver!!.findElement(By.xpath("//*[@id=\"result-form\"]/fieldset/div[6]/table/tbody/tr[${i}]/td[6]/a/span"))
                            successful = clickButton(firstButton)
                        }
                    }
                    if (reserveHoldActive && !successful) {
                        val reserveHold =
                            driver!!.findElement(By.cssSelector("#result-form > fieldset > div.tbl_wrap.th_thead > table > tbody > tr:nth-child(${i}) > td:nth-child(8)"))
                        if (reserveHold.text.contains("신청하기")) {
                            val reserveButton =
                                driver!!.findElement(By.xpath("//*[@id=\"result-form\"]/fieldset/div[6]/table/tbody/tr[${i}]/td[8]/a/span"))
                            successful = clickButton(reserveButton)
                        }
                    }
                    if (successful) break
                }

                if (successful) {
                    break
                }

                Thread.sleep(5000) //없을경우 새로고침 대기
            }
            if (goForIt) {
                outputWrite("N: 예매 성공, 가서 결제하세요")
            } else {
                outputWrite("N: 유저 요청에 의한 예매 중단됨")
            }
            engineCutOff()
            //EngineControlPanel에 값을 바꿀 방법 마련해야함
        }
        ticketTryThread!!.start()
        return true
    }

    private fun clickButton(button: WebElement): Boolean {
        (driver as JavascriptExecutor).executeScript("arguments[0].click();", button)
        Thread.sleep(5000)
        if (driver!!.findElements(By.id("isFalseGotoMain")).isNotEmpty()) {
            return true
        } else {
            outputWrite("N: 이미 선점된 좌석")
            driver!!.navigate().back()
            Thread.sleep(1000)
            return false
        }
    }

    private fun engineCutOff() {
        if (!goForIt) return
        driver?.quit()
        driver = null
        goForIt = false
    }
}