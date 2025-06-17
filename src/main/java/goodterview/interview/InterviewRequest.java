package goodterview.interview;

import lombok.Data;

@Data
public class InterviewRequest {
    private String sessionId;
    private String preferredLanguage;
    private String question;
}

