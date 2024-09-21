package ua.zxz.multydbsysytem.service;

import ua.zxz.multydbsysytem.dto.UserDto;

public interface UserService {

    UserDto getById(long id);

    UserDto getByUsername(String username);

    void saveUser(UserDto userDto);

    void updateUser(UserDto userDto);
}
