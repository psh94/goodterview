package goodterview.interview;

import goodterview.openai.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interview")
public class InterviewController {

    private final InterviewService interviewService;

    // 면접 시작 (언어 선택 + sessionId 입력 → 자기소개 질문)
    @PostMapping("/start")
    public ResponseEntity<InterviewStartResponse> start(@RequestBody InterviewStartRequest request) {
        InterviewStartResponse resp = interviewService.startInterview(request);
        return ResponseEntity.ok(resp);
    }


    // 이후 질문 응답
    @PostMapping
    public ResponseEntity<ChatMessage> ask(@RequestBody InterviewRequest request) {
        if (request.getSessionId() == null || request.getSessionId().isBlank()) {
            return ResponseEntity.badRequest().body(
                    new ChatMessage("system", "sessionId는 필수입니다.")
            );
        }
        if (request.getQuestion() == null || request.getQuestion().isBlank()) {
            return ResponseEntity.badRequest().body(
                    new ChatMessage("system", "question은 필수입니다.")
            );
        }

        ChatMessage answer = interviewService.askQuestion(request);
        return ResponseEntity.ok(answer);
    }
}

