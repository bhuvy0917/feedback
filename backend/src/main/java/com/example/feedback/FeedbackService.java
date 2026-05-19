package com.example.feedback;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    /**
     * Save feedback with validation
     * @param feedback the feedback to save
     * @return saved feedback
     * @throws FeedbackNotFoundException if feedback text is empty
     */
    public Feedback saveFeedback(Feedback feedback) {
        validateFeedback(feedback);
        return feedbackRepository.save(feedback);
    }

    /**
     * Get all feedback entries
     * @return list of all feedback
     * @throws FeedbackNotFoundException if no feedback exists
     */
    public List<Feedback> getAllFeedback() {
        List<Feedback> feedbackList = feedbackRepository.findAll();
        if (feedbackList.isEmpty()) {
            throw new FeedbackNotFoundException(
                "FEEDBACK_NOT_FOUND",
                "No feedback entries found in the system"
            );
        }
        return feedbackList;
    }

    /**
     * Get feedback by ID
     * @param id the feedback ID
     * @return feedback if found
     * @throws FeedbackNotFoundException if feedback not found
     */
    public Feedback getFeedbackById(Long id) {
        return feedbackRepository.findById(id)
            .orElseThrow(() -> new FeedbackNotFoundException(
                "FEEDBACK_NOT_FOUND",
                "Feedback with ID " + id + " not found"
            ));
    }

    /**
     * Delete feedback by ID
     * @param id the feedback ID to delete
     * @throws FeedbackNotFoundException if feedback not found
     */
    public void deleteFeedback(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new FeedbackNotFoundException(
                "FEEDBACK_NOT_FOUND",
                "Cannot delete. Feedback with ID " + id + " not found"
            );
        }
        feedbackRepository.deleteById(id);
    }

    /**
     * Update feedback
     * @param id the feedback ID
     * @param updatedFeedback the updated feedback data
     * @return updated feedback
     * @throws FeedbackNotFoundException if feedback not found or invalid
     */
    public Feedback updateFeedback(Long id, Feedback updatedFeedback) {
        Feedback existingFeedback = getFeedbackById(id);
        
        validateFeedback(updatedFeedback);
        
        existingFeedback.setName(updatedFeedback.getName());
        existingFeedback.setFeedback(updatedFeedback.getFeedback());
        
        return feedbackRepository.save(existingFeedback);
    }

    /**
     * Validate feedback data
     * @param feedback the feedback to validate
     * @throws FeedbackNotFoundException if validation fails
     */
    private void validateFeedback(Feedback feedback) {
        if (feedback == null) {
            throw new FeedbackNotFoundException(
                "FEEDBACK_EMPTY",
                "Feedback object cannot be null"
            );
        }

        if (feedback.getFeedback() == null || feedback.getFeedback().trim().isEmpty()) {
            throw new FeedbackNotFoundException(
                "FEEDBACK_TEXT_EMPTY",
                "Feedback text cannot be empty"
            );
        }

        if (feedback.getName() == null || feedback.getName().trim().isEmpty()) {
            throw new FeedbackNotFoundException(
                "FEEDBACK_NAME_EMPTY",
                "Name cannot be empty"
            );
        }

        if (feedback.getFeedback().length() > 1000) {
            throw new FeedbackNotFoundException(
                "FEEDBACK_TEXT_TOO_LONG",
                "Feedback text cannot exceed 1000 characters"
            );
        }

        if (feedback.getName().length() > 100) {
            throw new FeedbackNotFoundException(
                "FEEDBACK_NAME_TOO_LONG",
                "Name cannot exceed 100 characters"
            );
        }
    }

    /**
     * Count total feedback entries
     * @return count of feedback
     */
    public long countFeedback() {
        return feedbackRepository.count();
    }
}

// Made with Bob
