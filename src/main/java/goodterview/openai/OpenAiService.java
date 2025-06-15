package goodterview.openai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final WebClient openAiWebClient;

    public String chat(String userMessage) {
        ChatRequest request = new ChatRequest(
                "gpt-4o",
                List.of(
                        new ChatMessage("system", "You are a helpful interviewer."),
                        new ChatMessage("user", userMessage)
                )
        );

        ChatResponse response = openAiWebClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();

        return response.getChoices().get(0).getMessage().getContent();
    }
}
