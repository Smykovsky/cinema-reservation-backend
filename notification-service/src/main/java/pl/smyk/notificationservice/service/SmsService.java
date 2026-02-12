package pl.smyk.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.smsapi.api.SmsFactory;
import pl.smsapi.api.action.sms.SMSSend;
import pl.smsapi.exception.SmsapiException;
import pl.smyk.notificationservice.dto.SmsRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {
    private final SmsFactory smsFactory;

    public void sendSms(SmsRequest smsRequest) {
        try {
            SMSSend smsSend = smsFactory.actionSend(smsRequest.getPhoneNumber(), smsRequest.getMessage());
            smsSend.execute();
        } catch (SmsapiException e) {
            log.error(e.getMessage(), e);
        }
    }
}
