package com.zerobin.zerobin_backend.service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zerobin.zerobin_backend.dto.ComplaintDto;
import com.zerobin.zerobin_backend.entity.Complaint;
import com.zerobin.zerobin_backend.entity.User;
import com.zerobin.zerobin_backend.repository.ComplaintRepository;
import com.zerobin.zerobin_backend.repository.UserRepository;

@Service

public class ComplaintService {
    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    public ComplaintDto saveComplaint(ComplaintDto dto, String email) {
        Complaint complaint = new Complaint();
        // Find user by email and set userId
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found for email: " + email);
        }
        complaint.setUserId(userOpt.get().getId());
        complaint.setText(dto.getText());
        complaint.setLocation(dto.getLocation());
        complaint.setSentiment(dto.getSentiment());
        complaint.setConfidence(dto.getConfidence());
        complaint.setReasoning(dto.getReasoning());
        complaint.setTimestamp(dto.getTimestamp());
        Complaint saved = complaintRepository.save(complaint);
        return toDto(saved);
    }

    public List<ComplaintDto> getComplaintsByUser(Long userId) {
        return complaintRepository.findByUserId(userId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<ComplaintDto> getAllComplaints() {
        return complaintRepository.findAll()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private ComplaintDto toDto(Complaint complaint) {
        ComplaintDto dto = new ComplaintDto();
        dto.setComplaintId(complaint.getComplaintId());
        dto.setUserId(complaint.getUserId());
        dto.setText(complaint.getText());
        dto.setLocation(complaint.getLocation());
        dto.setSentiment(complaint.getSentiment());
        dto.setConfidence(complaint.getConfidence());
        dto.setReasoning(complaint.getReasoning());
        dto.setTimestamp(complaint.getTimestamp());
        return dto;
    }
}
