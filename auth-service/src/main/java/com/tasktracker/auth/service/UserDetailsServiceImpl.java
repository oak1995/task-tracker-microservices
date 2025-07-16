package com.tasktracker.auth.service;

import com.tasktracker.auth.entity.User;
import com.tasktracker.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Реализация UserDetailsService для Spring Security
 * 
 * Загружает данные пользователя из БД по имени пользователя
 * Используется Spring Security для аутентификации
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    
    /**
     * Репозиторий для работы с пользователями
     */
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Загрузка пользователя по имени пользователя
     * 
     * Вызывается Spring Security при аутентификации
     * Поддерживает вход как по username, так и по email
     * 
     * @param usernameOrEmail имя пользователя или email
     * @return UserDetails данные пользователя
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        logger.debug("Loading user by username or email: {}", usernameOrEmail);
        
        // Ищем пользователя по username или email
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> {
                    logger.error("User not found: {}", usernameOrEmail);
                    return new UsernameNotFoundException("User not found: " + usernameOrEmail);
                });
        
        // Проверяем, активен ли пользователь
        if (!user.isEnabled()) {
            logger.error("User account is disabled: {}", usernameOrEmail);
            throw new UsernameNotFoundException("User account is disabled: " + usernameOrEmail);
        }
        
        logger.debug("User loaded successfully: {}", user.getUsername());
        
        // Возвращаем User (который реализует UserDetails)
        return user;
    }
} 