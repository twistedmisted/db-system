package ua.zxz.multydbsysytem.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.zxz.multydbsysytem.dto.UserDto;
import ua.zxz.multydbsysytem.exception.WrongDataException;
import ua.zxz.multydbsysytem.mapper.impl.UserMapper;
import ua.zxz.multydbsysytem.repository.UserRepository;
import ua.zxz.multydbsysytem.service.UserService;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto getById(long id) {
        return userMapper.entityToDto(userRepository.findById(id)
                .orElseThrow(() -> new WrongDataException("User not found by id = [" + id + "]")));
    }

    @Override
    public UserDto getByUsername(String username) {
        return userMapper.entityToDto(userRepository.findByUsername(username)
                .orElseThrow(() -> new WrongDataException("User not found by username = [" + username + "]")));
    }

    @Override
    @Transactional
    public void saveUser(UserDto userDto) {
        if (existsByUsername(userDto.getUsername())) {
            log.info("The user with username = [{}] already exists", userDto.getUsername());
            throw new WrongDataException("The user with such username already exists");
        } else if (existsByEmail(userDto.getEmail())) {
            log.info("The user with email = [{}] already exists", userDto.getEmail());
            throw new WrongDataException("The user with such email already exists");
        }
        String password = userDto.getPassword();
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(userMapper.dtoToEntity(userDto));
        jdbcTemplate.update("CREATE USER " + userDto.getUsername() +
                " IDENTIFY BY '" + password + "'");
    }

    @Override
    public void updateUser(UserDto userDto) {
        if (Objects.isNull(userDto.getId())) {
            log.info("The user id is null");
            throw new WrongDataException("The user id is null");
        } else if (!existsByUsername(userDto.getUsername())) {
            log.info("The user with username = [{}] does not exist", userDto.getUsername());
            throw new WrongDataException("The user with such username does not exist");
        } else if (!existsByEmail(userDto.getEmail())) {
            log.info("The user with email = [{}] does not exist", userDto.getEmail());
            throw new WrongDataException("The user with such email does not exist");
        }
        userRepository.save(userMapper.dtoToEntity(userDto));
    }

    private boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    private boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
