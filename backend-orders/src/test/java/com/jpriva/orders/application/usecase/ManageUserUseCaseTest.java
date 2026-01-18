package com.jpriva.orders.application.usecase;

import com.jpriva.orders.application.dto.UserDto;
import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.UserErrorCodes;
import com.jpriva.orders.domain.model.User;
import com.jpriva.orders.domain.model.vo.Role;
import com.jpriva.orders.domain.ports.repository.UserRepository;
import com.jpriva.orders.domain.ports.security.JwtServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ManageUserUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtServicePort jwtService;

    @InjectMocks
    private ManageUserUseCase manageUserUseCase;

    private UserDto.CreateRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new UserDto.CreateRequest(
                "test@example.com",
                "password123",
                "Test User",
                "123456789",
                "123 Test St",
                "EXTERNAL"
        );
    }

    @Test
    void registerUser_shouldCreateAndSaveUser_whenEmailIsNew() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User user = inv.getArgument(0);
            return User.builder()
                    .id(UUID.randomUUID())
                    .email(user.getEmail())
                    .passwordHash(user.getPasswordHash())
                    .fullName(user.getFullName())
                    .phone(user.getPhone())
                    .address(user.getAddress())
                    .role(user.getRole())
                    .createdAt(LocalDateTime.now())
                    .build();
        });

        UserDto.Response result = manageUserUseCase.registerUser(createRequest);

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(createRequest.email());
        assertThat(result.fullName()).isEqualTo(createRequest.fullName());
    }

    @Test
    void registerUser_shouldThrowException_whenEmailAlreadyExists() {
        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .email(createRequest.email())
                .passwordHash("p")
                .fullName("n")
                .role(Role.EXTERNAL)
                .build();
        when(userRepository.findByEmail(createRequest.email())).thenReturn(Optional.of(existingUser));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> manageUserUseCase.registerUser(createRequest))
                .withMessage("User with email " + createRequest.email() + " already exists");
    }

    @Test
    void loginUser_shouldReturnToken_whenCredentialsAreValid() {
        UserDto.LoginRequest loginRequest = new UserDto.LoginRequest("test@example.com", "password123");
        User user = User.builder()
                .id(UUID.randomUUID())
                .email(loginRequest.email())
                .passwordHash("encodedPassword")
                .fullName("Test User")
                .role(Role.EXTERNAL)
                .build();
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        lenient().when(jwtService.generateToken(any(Map.class), any(User.class))).thenReturn("fake.jwt.token");

        UserDto.TokenResponse result = manageUserUseCase.loginUser(loginRequest);

        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo("fake.jwt.token");
        assertThat(result.tokenType()).isEqualTo("Bearer");
    }

    @Test
    void loginUser_shouldThrowException_whenCredentialsAreInvalid() {
        UserDto.LoginRequest loginRequest = new UserDto.LoginRequest("test@example.com", "wrongpassword");
        User user = User.builder()
                .id(UUID.randomUUID())
                .email(loginRequest.email())
                .passwordHash("encodedPassword")
                .fullName("Test User")
                .role(Role.EXTERNAL)
                .build();
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThatExceptionOfType(DomainException.class)
                .isThrownBy(() -> manageUserUseCase.loginUser(loginRequest))
                .extracting(DomainException::getCode)
                .isEqualTo(UserErrorCodes.USER_CREDENTIALS_INVALID.getCode());
    }

    @Test
    void getUser_shouldReturnUser_whenFound() {
        User user = User.builder().id(UUID.randomUUID()).email("e").passwordHash("p").fullName("f").role(Role.EXTERNAL).build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Optional<UserDto.Response> result = manageUserUseCase.getUser(user.getId());

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(user.getId());
    }

    @Test
    void getUser_shouldReturnEmpty_whenNotFound() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        Optional<UserDto.Response> result = manageUserUseCase.getUser(UUID.randomUUID());

        assertThat(result).isNotPresent();
    }

    @Test
    void getUserByEmail_shouldReturnUser_whenFound() {
        User user = User.builder().id(UUID.randomUUID()).email("test@example.com").passwordHash("p").fullName("f").role(Role.EXTERNAL).build();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Optional<UserDto.Response> result = manageUserUseCase.getUserByEmail(user.getEmail());

        assertThat(result).isPresent();
        assertThat(result.get().email()).isEqualTo(user.getEmail());
    }

    @Test
    void getUserByEmail_shouldReturnEmpty_whenNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        Optional<UserDto.Response> result = manageUserUseCase.getUserByEmail("nonexistent@example.com");

        assertThat(result).isNotPresent();
    }
}
