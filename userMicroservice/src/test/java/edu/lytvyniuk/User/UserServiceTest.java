package edu.lytvyniuk.User;

import edu.lytvyniuk.customException.DuplicateResourceException;
import edu.lytvyniuk.customException.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
    }

    @Test
    void findAll_shouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<User> users = userService.findAll();

        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
        assertEquals("testuser", users.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findByUsername_whenUserExists_shouldReturnUser() throws ResourceNotFoundException {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findByUsername("testuser");

        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
    }

    @Test
    void findByUsername_whenUserDoesNotExist_shouldThrowException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findByUsername("nonexistent"));
    }

    @Test
    void save_whenUserIsValid_shouldSaveUser() throws DuplicateResourceException {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.save(user);

        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void save_whenUsernameExists_shouldThrowException() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        assertThrows(DuplicateResourceException.class, () -> userService.save(user));
    }

    @Test
    void deleteById_whenUserExists_shouldDeleteUser() throws ResourceNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_whenUserDoesNotExist_shouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteById(1L));
    }
}
