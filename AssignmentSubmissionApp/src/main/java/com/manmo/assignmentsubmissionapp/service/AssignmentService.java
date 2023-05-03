package com.manmo.assignmentsubmissionapp.service;

import com.manmo.assignmentsubmissionapp.domain.Assignment;
import com.manmo.assignmentsubmissionapp.domain.User;
import com.manmo.assignmentsubmissionapp.enums.AssignmentStatusEnum;
import com.manmo.assignmentsubmissionapp.enums.AuthorityEnum;
import com.manmo.assignmentsubmissionapp.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class AssignmentService {
    @Autowired
    private AssignmentRepository assignmentRepository;

    public Assignment save(User user) {
        Assignment assignment = new Assignment();
        assignment.setStatus(AssignmentStatusEnum.PENDING_SUBMISSION.getStatus());
        assignment.setNumber(findNextAssignmentToSubmit(user));
        assignment.setUser(user);
        return assignmentRepository.save(assignment);
    }

    private Integer findNextAssignmentToSubmit(User user) {
        Set<Assignment> assignmentsByUser = assignmentRepository.findByUser(user);
        if (assignmentsByUser == null) {
            return 1;
        }
        Optional<Integer> nextAssignmentNumOption = assignmentsByUser.stream()
                .sorted((a1, a2) -> {
                    if (a1.getNumber() == null) return 1;
                    if (a2.getNumber() == null) return 1;
                    return a2.getNumber().compareTo(a1.getNumber());
                })
                .map(assignment -> {
                    if (assignment.getNumber() == null) {
                        return 1;
                    }
                    return assignment.getNumber() + 1;
                })
                .findFirst();
        return nextAssignmentNumOption.orElse(1);
    }

    public Set<Assignment> findByUser(User user) {
        boolean hasCodeReviewerRole = user.getAuthorities()
                .stream()
                .filter(grantedAuthority -> AuthorityEnum.ROLE_CODE_REVIEWER.name().equals(grantedAuthority.getAuthority()))
                .count() > 0;
        if (hasCodeReviewerRole) {
            return assignmentRepository.findByCodeReviewer(user);
        } else {
            return assignmentRepository.findByUser(user);
        }
    }

    public Optional<Assignment> findById(Long assignmentId) {
        return assignmentRepository.findById(assignmentId);
    }

    public Assignment save(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }
}
