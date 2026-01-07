package com.valdirsantos714.backend.adapters.out.repository;

import com.valdirsantos714.backend.adapters.out.repository.entity.UserEntity;
import com.valdirsantos714.backend.adapters.out.repository.mapper.UserMapper;
import com.valdirsantos714.backend.application.core.domain.User;
import com.valdirsantos714.backend.application.core.domain.enums.UserRole;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    private final Long TEST_ID = 1L;
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_NAME = "Test User";

    @Test
    @DisplayName("should save user successfully")
    void shouldSaveUserSuccessfully() {
        // Given
        User user = new User();
        user.setName(TEST_NAME);
        user.setEmail(TEST_EMAIL);
        user.setPassword("password");
        user.setRole(UserRole.USER);
        user.setIncomes(Collections.emptyList());
        user.setExpenses(Collections.emptyList());

        UserEntity userEntity = UserMapper.toUserEntity(user);
        userEntity.setId(TEST_ID);
        userEntity.setRole(user.getRole());

        when(userJpaRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // When
        User savedUser = userRepositoryAdapter.save(user);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getName()).isEqualTo(TEST_NAME);
        assertThat(savedUser.getEmail()).isEqualTo(TEST_EMAIL);
        verify(userJpaRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("should find all users successfully")
    void shouldFindAllUsersSuccessfully() {
        // Given
        UserEntity userEntity = new UserEntity();
        userEntity.setId(TEST_ID);
        userEntity.setName(TEST_NAME);
        userEntity.setEmail(TEST_EMAIL);
        userEntity.setRole(UserRole.USER);
        List<UserEntity> userEntities = Collections.singletonList(userEntity);

        when(userJpaRepository.findAll()).thenReturn(userEntities);

        // When
        List<User> users = userRepositoryAdapter.findAll();

        // Then
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo(TEST_NAME);
        verify(userJpaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("should find user by id successfully")
    void shouldFindUserByIdSuccessfully() {
        // Given
        UserEntity userEntity = new UserEntity();
        userEntity.setId(TEST_ID);
        userEntity.setName(TEST_NAME);
        userEntity.setEmail(TEST_EMAIL);
        userEntity.setRole(UserRole.USER);

        when(userJpaRepository.findById(TEST_ID)).thenReturn(Optional.of(userEntity));

        // When
        User foundUser = userRepositoryAdapter.findById(TEST_ID);

        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(TEST_ID);
        assertThat(foundUser.getName()).isEqualTo(TEST_NAME);
        verify(userJpaRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when finding non-existent user by id")
    void shouldThrowExceptionWhenFindingNonExistentUserById() {
        // Given
        when(userJpaRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> userRepositoryAdapter.findById(TEST_ID));
        verify(userJpaRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Given
        User user = new User();
        user.setName("Updated Name");
        user.setEmail("updated@example.com");
        user.setPassword("newpassword");
        user.setRole(UserRole.ADMIN);
        user.setIncomes(Collections.emptyList());
        user.setExpenses(Collections.emptyList());

        UserEntity existingUserEntity = new UserEntity();
        existingUserEntity.setId(TEST_ID);
        existingUserEntity.setName(TEST_NAME);
        existingUserEntity.setEmail(TEST_EMAIL);
        existingUserEntity.setPassword("oldpassword");
        existingUserEntity.setRole(UserRole.USER);

        when(userJpaRepository.findById(TEST_ID)).thenReturn(Optional.of(existingUserEntity));
        when(userJpaRepository.save(any(UserEntity.class))).thenReturn(existingUserEntity);

        // When
        User updatedUser = userRepositoryAdapter.update(TEST_ID, user);

        // Then
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo(user.getName());
        assertThat(updatedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
        verify(userJpaRepository, times(1)).findById(TEST_ID);
        verify(userJpaRepository, times(1)).save(existingUserEntity);
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when updating non-existent user")
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        // Given
        User user = new User();
        when(userJpaRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> userRepositoryAdapter.update(TEST_ID, user));
        verify(userJpaRepository, times(1)).findById(TEST_ID);
        verify(userJpaRepository, never()).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Given
        doNothing().when(userJpaRepository).deleteById(TEST_ID);

        // When
        userRepositoryAdapter.delete(TEST_ID);

        // Then
        verify(userJpaRepository, times(1)).deleteById(TEST_ID);
    }

    @Test
    @DisplayName("should find user details by email successfully")
    void shouldFindUserDetailsByEmailSuccessfully() {
        // Given
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(TEST_EMAIL);
        userEntity.setPassword("encodedPassword");
        userEntity.setRole(UserRole.USER);

        when(userJpaRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(userEntity));

        // When
        UserDetails userDetails = userRepositoryAdapter.findByEmail(TEST_EMAIL);

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(TEST_EMAIL);
        verify(userJpaRepository, times(1)).findByEmail(TEST_EMAIL);
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when finding user details by non-existent email")
    void shouldThrowExceptionWhenFindingUserDetailsByNonExistentEmail() {
        // Given
        when(userJpaRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> userRepositoryAdapter.findByEmail(TEST_EMAIL));
        verify(userJpaRepository, times(1)).findByEmail(TEST_EMAIL);
    }

    @Test
    @DisplayName("should find user name by email successfully")
    void shouldFindUserNameByEmailSuccessfully() {
        // Given
        when(userJpaRepository.findNameByEmail(TEST_EMAIL)).thenReturn(TEST_NAME);

        // When
        String userName = userRepositoryAdapter.findNameByEmail(TEST_EMAIL);

        // Then
        assertThat(userName).isEqualTo(TEST_NAME);
        verify(userJpaRepository, times(1)).findNameByEmail(TEST_EMAIL);
    }
}
