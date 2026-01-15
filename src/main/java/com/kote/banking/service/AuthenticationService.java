package com.kote.banking.service;

import com.kote.banking.dto.AuthenticationRequestDto;
import com.kote.banking.dto.AuthenticationResponseDto;
import com.kote.banking.entity.User;
import com.kote.banking.entity.enums.UserRole;
import com.kote.banking.mapper.UserMapper;
import com.kote.banking.repository.UserRepository;
import com.kote.banking.security.CustomUserDetails;
import com.kote.banking.security.JwtService;
import com.kote.banking.security.TenantContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public AuthenticationService(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder,
                                 UserMapper userMapper,
                                 JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthenticationResponseDto register(AuthenticationRequestDto dto){
        User user = new User();
        userRepository.findByEmail(dto.getEmail()).ifPresent((e)->{
            throw new RuntimeException(e.getEmail() + " already exists!");
        });
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setRole(UserRole.ROLE_USER);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);

        String tenant = TenantContext.getTenant();
        String token = jwtService.generateToken(new CustomUserDetails(user), tenant);

        return AuthenticationResponseDto.builder()
                .token(token)
                .id(user.getId())
                .build();
    }

    public AuthenticationResponseDto login(AuthenticationRequestDto requestDto){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword()));
        User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow();
        String tenant = TenantContext.getTenant();
        String token = jwtService.generateToken(new CustomUserDetails(user), tenant);
        return AuthenticationResponseDto.builder().token(token).build();
    }
}