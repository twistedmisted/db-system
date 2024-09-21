package ua.zxz.multydbsysytem.service.mail.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ua.zxz.multydbsysytem.dto.PasswordMessageInfo;
import ua.zxz.multydbsysytem.service.mail.MailService;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private static final String PASSWORD_CHANGE_SUBJECT = "Зміна паролю";
    private static final String PASSWORD_CHANGED_TEXT = """
            Вітаємо, шановний %s %s!
            
            Ваш пароль було успішно змінено на новий. Усі активні сеанси були деактивовані.
            
            Новий пароль - %s
            
            Рекомендуємо змінити його на Ваш у профілі.
            
            Дякуємо за увагу.""";

    @Value("${spring.mail.username}")
    private String FROM;

    private final JavaMailSender mailSender;

    @Override
    public void sendRestorePasswordMessage(PasswordMessageInfo messageInfo) {
        log.debug("Sending message to {} about changing password", messageInfo.getEmail());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(PASSWORD_CHANGE_SUBJECT);
        message.setFrom(FROM);
        message.setTo(messageInfo.getEmail());
        message.setText(String.format(PASSWORD_CHANGED_TEXT,
                messageInfo.getSurname(), messageInfo.getName(), messageInfo.getPassword()));
        mailSender.send(message);
    }
}
