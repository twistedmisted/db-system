package ua.zxz.multydbsysytem.service.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ua.zxz.multydbsysytem.dto.PasswordMessageInfo;
import ua.zxz.multydbsysytem.entity.UserEntity;
import ua.zxz.multydbsysytem.repository.UserRepository;
import ua.zxz.multydbsysytem.service.mail.MailService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestorePasswordService {

    private final UserRepository userRepository;
    private final MailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public void restorePasswordByEmail(String username) {
        log.debug("Restoring password for user with username = [{}]", username);
        if (!existsByEmail(username)) {
            log.debug("The user with username = [{}] does not exist", username);
            throw new ResponseStatusException(NOT_FOUND, "Користувача з даною поштою не знайдено. " +
                    "Переконайтеся в правильності вводу.");
        }
        log.debug("Generating and saving new password");
        UserEntity userEntity = userRepository.findByUsername(username).get();
        String newPassword = generateNewPassword();
        saveNewPassword(userEntity, newPassword);
        jwtService.invalidateTokenByUsername(username);
        sendMessageToUser(userEntity, newPassword);
    }

    private boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private String generateNewPassword() {
        String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
        String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
        String numbers = RandomStringUtils.randomNumeric(2);
        String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
        String totalChars = RandomStringUtils.randomAlphanumeric(2);
        String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
                .concat(numbers)
                .concat(specialChar)
                .concat(totalChars);
        List<Character> pwdChars = combinedChars.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(pwdChars);
        return pwdChars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private void saveNewPassword(UserEntity userEntity, String newPassword) {
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);
    }

    private void sendMessageToUser(UserEntity user, String newPassword) {
        emailService.sendRestorePasswordMessage(PasswordMessageInfo.builder()
                .name(user.getFirstName())
                .surname(user.getLastName())
                .email(user.getEmail())
                .password(newPassword)
                .build());
    }
}
