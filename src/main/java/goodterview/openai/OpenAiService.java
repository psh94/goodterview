package goodterview.openai;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.api.url}")
    private String openAiApiUrl;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(openAiApiUrl)
                .build();
    }

    // 응답 DTO 전체 반환 (ChatCompletionResponse)
    public ChatResponse chatWithFullResponse(List<ChatMessage> messages) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4o");
        body.put("messages", messages);

        return webClient.post()
                .header("Authorization", "Bearer " + openAiApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();
    }
}

