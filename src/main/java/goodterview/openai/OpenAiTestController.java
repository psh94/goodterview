package goodterview.openai;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class OpenAiTestController {

    private final OpenAiService openAiService;

    @PostMapping
    public ResponseEntity<String> testOpenAi(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        String answer = openAiService.chat(question);
        return ResponseEntity.ok(answer);
    }
}

