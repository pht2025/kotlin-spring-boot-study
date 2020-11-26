package com.pht.kotlinstudy.listener

import com.pht.kotlinstudy.Global
import com.pht.kotlinstudy.model.GlobalProperty
import com.pht.kotlinstudy.model.Message
import com.pht.kotlinstudy.model.Property
import com.pht.kotlinstudy.model.PropertyType
import com.pht.kotlinstudy.repository.GlobalPropertyRepository
import com.pht.kotlinstudy.repository.MessageRepository
import com.pht.kotlinstudy.scheduler.ScheduleTaskService
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ApplicationEventListener(
        val globalPropertyRepository: GlobalPropertyRepository,
        val messageRepository: MessageRepository,
        val scheduleTaskService: ScheduleTaskService
) : ApplicationListener<ApplicationStartedEvent> {

    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        val interval = globalPropertyRepository.findByKey(Global.KEY_INTERVAL).orElse(null)
        val day = globalPropertyRepository.findByKey(Global.KEY_DAY_BEFORE).orElse(null)
        val onOff = globalPropertyRepository.findByKey(Global.KEY_ON_OFF).orElse(null)
        var countMessage = messageRepository.findByKey(PropertyType.COUNT.name).orElse(null)
        var moneyMessage = messageRepository.findByKey(PropertyType.MONEY.name).orElse(null)
        var newUserMessage = messageRepository.findByKey(PropertyType.NEW_USER.name).orElse(null)

        if (interval == null) {
            val intervalProperty = GlobalProperty()
            intervalProperty.key = Global.KEY_INTERVAL
            intervalProperty.name = Global.KEY_INTERVAL
            intervalProperty.value = "30"
            globalPropertyRepository.save(intervalProperty)
        }
        if (day == null) {
            val dayProperty = GlobalProperty()
            dayProperty.key = Global.KEY_DAY_BEFORE
            dayProperty.name = Global.KEY_DAY_BEFORE
            dayProperty.value = "5"
            globalPropertyRepository.save(dayProperty)
        }
        if (onOff == null) {
            val onOffProperty = GlobalProperty()
            onOffProperty.key = Global.KEY_DAY_BEFORE
            onOffProperty.name = Global.KEY_DAY_BEFORE
            onOffProperty.value = GlobalProperty.OnOff.OFF.name
            globalPropertyRepository.save(onOffProperty)
        }

        if (countMessage == null) {
            val property = Property()
            property.key = PropertyType.COUNT.name
            property.name = PropertyType.COUNT.name
            property.value = "1,5,10"
            property.type = PropertyType.COUNT

            countMessage = Message()
            countMessage.key = PropertyType.COUNT.name
            countMessage.title = "${PropertyType.COUNT} Message"
            countMessage.message = "COUNT Message"
            countMessage.addProperty(property)
            messageRepository.save(countMessage)
        }
        if (moneyMessage == null) {
            val property = Property()
            property.key = PropertyType.MONEY.name
            property.name = PropertyType.MONEY.name
            property.value = "60000,200000"
            property.type = PropertyType.MONEY

            moneyMessage = Message()
            moneyMessage.key = PropertyType.MONEY.name
            moneyMessage.title = "${PropertyType.MONEY} Message"
            moneyMessage.message = "MONEY message"
            moneyMessage.addProperty(property)
            messageRepository.save(moneyMessage)
        }
        if (newUserMessage == null) {
            val property = Property()
            property.key = PropertyType.NEW_USER.name
            property.name = PropertyType.NEW_USER.name
            property.value = "true"
            property.type = PropertyType.NEW_USER

            newUserMessage = Message()
            newUserMessage.key = PropertyType.NEW_USER.name
            newUserMessage.title = "스포츠, 예측의 재미를 더하다!"
            newUserMessage.message = """
                스포츠, 예측의 재미를 더하다!
                【승부사온라인】 에 오신걸 환영합니다!


                ◆승부사온라인 뽀~너스 보상 안내◆
                ※ 보상은 메시지함에서 꼭! 수령해주세요 / 1인 1지급 

                ①  신규가입 : 30,000G
                ②  가이드 정독 (15초) : 30,000G 
                (미지급시, ≡ 서랍메뉴 클릭 → 가이드 → 15초 정독후 보상)
                ③  텔레그램 연동 : 30,000G
                (월~금 : 연동일 다음날 오후12시 / 금 오후 12시 이후,주말 : 차주 월요일 일괄지급)
                ④  목표베팅 60,000G 달성 : 1,000 P
                (아이템샵에서 20,000G 포함 스킨 구매 가능)
                ⑤  선물상자 (랜덤) : 24시간마다 오픈 가능
                ⑥  라이브베팅 인증이벤트 : 10,000G (라이브베팅 10,000G 이상 필수)

                ◆라이브 베팅 인증이벤트◆
                * 지급 : 라이브 베팅 10,000G이상 필수 / 1일 1회 참여가능 / 1인 1지급 / 자정 이후 일괄지급 
                * 참여방법 : 고객센터 1:1 문의로 아래 내용 꼭 남겨야 참여완료!
                ①  문의 제목에 "라이브 인증" 이라 적어주세요.  
                ②  내용에 “닉네임”을 꼭! 기재해 주세요.
                ③  라이브 베팅하신 캡처본 사진을 꼭! 첨부해 주세요.


                ◆텔레그램 연동◆
                ※베팅적중 알림 / 이벤트 소식 등을 쉽게 알 수 있어요!

                ①  텔레그램 메신저 다운받기
                ②  텔레그램 가입
                ③  검색창에 “승부사온라인” 검색
                ④  채널 입장 → 시작버튼 클릭!
                ⑤  고유번호 복사 → [승부사온라인] 게임이동 
                ⑥  게임내, 좌측 상단 서랍메뉴 → 텔레그램 알림설정
                ⑦  고유번호 입력 → 연동 → 끝!
                 
                ☞ 이용중 문의사항은 고객센터 <1:1 문의>를 이용해 주세요.
                당신에게 일어날 최고의 베팅은~ 승부사온라인에서 !
            """.trimIndent()
            newUserMessage.addProperty(property)
            messageRepository.save(newUserMessage)
        }

        if (onOff.value == GlobalProperty.OnOff.ON.name) {
            scheduleTaskService.startSendMessageTask(Duration.ofSeconds(interval.value?.toLong()!!))
        }
    }
}
