package goodterview.interview;

import goodterview.openai.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InterviewStartResponse {
    private String sessionId;
    private ChatMessage firstMessage; // assistant 역할, content="자기소개 부탁드립니다."
}
