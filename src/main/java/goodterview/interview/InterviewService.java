package goodterview.interview;

import goodterview.openai.ChatMessage;
import goodterview.openai.ChatRequest;
import goodterview.openai.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final WebClient openAiWebClient;
    private final InterviewSessionService sessionService;

    @Value("${openai.api.key}")
    private String apiKey;

    public String askQuestion(String sessionId, String userQuestion) {

        // 1. 기존 세션 메시지 조회
        List<ChatMessage> messages = sessionService.getMessages(sessionId);
        messages.forEach(System.out::println);

        // 2. 처음 시작이면 system 프롬프트 추가
        if (messages.isEmpty()) {
            messages.add(new ChatMessage("system", "You are a professional technical interviewer. Provide concise and relevant answers."));
        }

        // 3. 사용자의 질문 추가
        ChatMessage userMessage = new ChatMessage("user", userQuestion);
        messages.add(userMessage);

        // 4. GPT 요청 객체 구성
        ChatRequest request = new ChatRequest("gpt-4o", messages);

        // 5. GPT API 요청 및 응답 수신
        ChatResponse response = openAiWebClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();

        // 6. GPT 응답 추출
        String assistantReply = response.getChoices().get(0).getMessage().getContent();

        // 7. 질문/응답 Redis에 저장
        sessionService.saveMessage(sessionId, userMessage);
        sessionService.saveMessage(sessionId, new ChatMessage("assistant", assistantReply));

        // 8. 응답 반환
        return assistantReply;
    }
}
