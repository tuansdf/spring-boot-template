package org.tuanna.xcloneserver.modules.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.entities.User;
import org.tuanna.xcloneserver.mappers.CommonMapper;
import org.tuanna.xcloneserver.modules.user.dtos.UserDTO;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final CommonMapper commonMapper;
    private final UserRepository userRepository;

    @Override
    public UserDTO save(UserDTO userDTO) {
        return commonMapper.toDTO(userRepository.save(commonMapper.toEntity(userDTO)));
    }

    @Override
    public boolean existsByUsernameOrEmail(String username, String email) {
        return userRepository.existsByUsernameOrEmail(username, email);
    }

    @Override
    public UserDTO findOneById(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.map(commonMapper::toDTO).orElse(null);
    }

    @Override
    public UserDTO findOneByUsername(String username) {
        Optional<User> userOptional = userRepository.findTopByUsername(username);
        return userOptional.map(commonMapper::toDTO).orElse(null);
    }

    @Override
    public UserDTO findOneByEmail(String email) {
        Optional<User> userOptional = userRepository.findTopByEmail(email);
        return userOptional.map(commonMapper::toDTO).orElse(null);
    }

}
