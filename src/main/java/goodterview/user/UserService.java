package goodterview.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(String username, String rawPassword) {
        String encodedPw = passwordEncoder.encode(rawPassword);
        userRepository.save(User.builder()
                .username(username)
                .password(encodedPw)
                .role("ROLE_USER")
                .build());
    }
}
