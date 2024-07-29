package pl.smyk.customerservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import pl.smyk.customerservice.dto.ReservationDto;
import pl.smyk.customerservice.model.Reservation;

@Service
public class MailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String email;

    public void sendEmail(String to, Reservation reservation ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Przypomnienie o rezerwacji: " + reservation.getId());
        message.setText("Szanowni Państwo,\n\n" +
                "Chcielibyśmy przypomnieć o rezerwacji na film, który odbędzie się jutro o godzinie " +
                reservation.getSelectedPlayTime().getHour() + ":" +
                String.format("%02d", reservation.getSelectedPlayTime().getMinute()) + ".");
        message.setFrom(email);

        javaMailSender.send(message);
    }
}
