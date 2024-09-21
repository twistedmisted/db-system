package ua.zxz.multydbsysytem.service.mail;

import ua.zxz.multydbsysytem.dto.PasswordMessageInfo;

public interface MailService {

    void sendRestorePasswordMessage(PasswordMessageInfo messageInfo);
}
