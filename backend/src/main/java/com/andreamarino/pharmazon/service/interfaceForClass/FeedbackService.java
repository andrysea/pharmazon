package com.andreamarino.pharmazon.service.interfaceForClass;

import java.util.List;
import com.andreamarino.pharmazon.dto.FeedbackDto;

public interface FeedbackService {
    FeedbackDto insertFeedbackDto(FeedbackDto feedbackDto);
    FeedbackDto updateFeedbackDto(FeedbackDto feedbackDto);
    void deleteFeedback(String code);
    List<FeedbackDto> getFeedbackList();
    List<FeedbackDto> getFeedbackListUser(String username);
}