package com.zerobin.zerobin_backend.service;

import com.zerobin.zerobin_backend.dto.user.UserAdviceRequest;
import com.zerobin.zerobin_backend.entity.User;
import com.zerobin.zerobin_backend.entity.user.UserAdvice;
import com.zerobin.zerobin_backend.repository.UserAdviceRepository;
import com.zerobin.zerobin_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAdviceService {

    private final UserAdviceRepository adviceRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserAdvice create(String userEmail, UserAdviceRequest req) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserAdvice advice = UserAdvice.builder()
                .title(req.getTitle())
                .adviceText(req.getAdviceText())
                .score(req.getScore())
                .createdAt(LocalDateTime.now())
                .createdBy(user)
                .build();
        return adviceRepository.save(advice);
    }

    @Transactional(readOnly = true)
    public List<UserAdvice> listMine(String userEmail) {
        return adviceRepository.findByCreatedByEmailOrderByCreatedAtDesc(userEmail);
    }

    @Transactional(readOnly = true)
    public Optional<UserAdvice> getLatestMine(String userEmail) {
        return adviceRepository.findTopByCreatedByEmailOrderByCreatedAtDesc(userEmail);
    }
}
