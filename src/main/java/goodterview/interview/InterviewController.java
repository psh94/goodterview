package goodterview.interview;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interview")
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/{sessionId}")
    public ResponseEntity<String> ask(
            @PathVariable String sessionId,
            @RequestBody Map<String, String> request) {

        String question = request.get("question");

        // 예외 처리 (선택)
        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("질문이 비어있습니다.");
        }

        String answer = interviewService.askQuestion(sessionId, question);
        return ResponseEntity.ok(answer);
    }
}
