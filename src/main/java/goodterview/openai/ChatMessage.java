package goodterview.openai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// GPT 요청 메시지 구조
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private String role;
    private String content;
}
