package com.valdirsantos714.backend.application.service;

import com.valdirsantos714.backend.adapters.in.dto.UserRequestDTO;
import com.valdirsantos714.backend.adapters.out.repository.UserRepositoryAdapter;
import com.valdirsantos714.backend.application.core.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepositoryAdapter userRepositoryAdapter;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
    }

    @Test
    @DisplayName("should save user with encoded password")
    void shouldSaveUserWithEncodedPassword() {
        // Given
        UserRequestDTO userDTO = new UserRequestDTO("Test User", "test@example.com", "plainPassword");
        User userMapped = new User();
        userMapped.setName("Test User");
        userMapped.setEmail("test@example.com");
        userMapped.setPassword("encodedPassword");

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepositoryAdapter.save(any(User.class))).thenReturn(userMapped);

        // When
        User result = userService.save(userDTO);

        // Then
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        verify(userRepositoryAdapter).save(any(User.class));
    }

    @Test
    @DisplayName("should find user by id")
    void shouldFindUserById() {
        // Given
        when(userRepositoryAdapter.findById(1L)).thenReturn(user);

        // When
        User result = userService.findById(1L);

        // Then
        assertEquals(1L, result.getId());
        assertEquals("Test User", result.getName());
    }

    @Test
    @DisplayName("should return list of all users")
    void shouldReturnListOfAllUsers() {
        // Given
        when(userRepositoryAdapter.findAll()).thenReturn(List.of(user));

        // When
        List<User> result = userService.findAll();

        // Then
        assertEquals(1, result.size());
        assertEquals("Test User", result.get(0).getName());
    }

    @Test
    @DisplayName("should update user")
    void shouldUpdateUser() {
        // Given
        UserRequestDTO userDTO = new UserRequestDTO("Updated User", "updated@example.com", "newPassword");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated User");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setPassword("newPassword");

        when(userRepositoryAdapter.update(eq(1L), any(User.class))).thenReturn(updatedUser);

        // When
        User result = userService.update(1L, userDTO);

        // Then
        assertEquals("Updated User", result.getName());
        assertEquals("updated@example.com", result.getEmail());
    }

    @Test
    @DisplayName("should delete user")
    void shouldDeleteUser() {
        // When
        userService.delete(1L);

        // Then
        verify(userRepositoryAdapter).delete(1L);
    }

    @Test
    @DisplayName("should find name by email")
    void shouldFindNameByEmail() {
        // Given
        when(userRepositoryAdapter.findNameByEmail("test@example.com")).thenReturn("Test User");

        // When
        String name = userService.findNameByEmail("test@example.com");

        // Then
        assertEquals("Test User", name);
    }
}