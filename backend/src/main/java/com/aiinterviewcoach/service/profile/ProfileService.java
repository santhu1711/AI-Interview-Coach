package com.aiinterviewcoach.service.profile;

import com.aiinterviewcoach.dto.request.UpdateProfileRequest;
import com.aiinterviewcoach.dto.response.ProfileResponse;
import com.aiinterviewcoach.entity.User;
import com.aiinterviewcoach.exception.ResourceNotFoundException;
import com.aiinterviewcoach.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {
    private final UserRepository userRepository;

    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ProfileResponse get(Long userId) {
        return toResponse(findUser(userId));
    }

    @Transactional
    public ProfileResponse update(Long userId, UpdateProfileRequest request) {
        User user = findUser(userId);
        user.setFullName(request.fullName().trim());
        return toResponse(userRepository.save(user));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user no longer exists."));
    }

    private static ProfileResponse toResponse(User user) {
        return new ProfileResponse(user.getId(), user.getFullName(), user.getEmail(), user.getCreatedAt());
    }
}
