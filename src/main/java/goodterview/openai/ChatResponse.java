package goodterview.openai;

import lombok.Data;

import java.util.List;

// GPT 응답 구조
@Data
public class ChatResponse {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private Message message;

        @Data
        public static class Message {
            private String role;
            private String content;
        }
    }
}