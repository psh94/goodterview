package goodterview.interview;

import goodterview.openai.ChatMessage;
import goodterview.openai.ChatResponse;
import goodterview.openai.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final OpenAiService openAiService;
    private final InterviewSessionService interviewSessionService;

    private static final Map<String, List<String>> LANGUAGE_QUESTIONS = Map.of(
            "Java", List.of(
                    "자기소개 부탁드립니다.",
                    "Java에서 OOP(객체지향)의 4대 특성을 설명해주세요.",
                    "Java와 Spring의 관계를 설명해주세요.",
                    "포트폴리오에 대한 간단한 설명 부탁드립니다."
            ),
            "Python", List.of(
                    "자기소개 부탁드립니다.",
                    "Python의 장점과 단점을 말씀해 주세요.",
                    "가장 즐겨 쓰는 Python 라이브러리는 무엇인가요?"
            )
            // 필요한 언어만 계속 추가!
    );

    // 면접 시작: system + user("자기소개 부탁드립니다.") 저장, assistant 메시지는 반환만
    public InterviewStartResponse startInterview(InterviewStartRequest request) {
        String sessionId = UUID.randomUUID().toString();
        String lang = Optional.ofNullable(request.getPreferredLanguage()).orElse("Java");
        List<String> questions = LANGUAGE_QUESTIONS.getOrDefault(lang, LANGUAGE_QUESTIONS.get("Java"));

        List<ChatMessage> messages = new ArrayList<>();

        String systemBasicPrompt = "너는 채용 면접관이다. 절대 어떤 경우에도 해설, 설명, 안내, 조언, 칭찬, 정보 제공, 답변, 힌트, 친절한 리액션, 사담을 하지 말고 반드시 한 번에 하나의 질문만 하라." +
                "설명·해설·조언·정보 제공은 규칙 위반이다. 질문 이외의 말은 규칙 위반이다. 규칙을 어길 경우 벌점을 받는다. 반드시 질문만 해라. 반드시 '?'로 끝나야 한다." +
                "지원자가 모르겠다고 해도 그에 대한 질문만 반복하지 말고 다음 준비된 질문(혹은 새로운 질문)을 해라.";

        messages.add(new ChatMessage("system", systemBasicPrompt + lang + " 개발자 기술 면접입니다."));
        messages.add(new ChatMessage("user", questions.get(0)));

        // Redis에 저장
        interviewSessionService.saveMessages(sessionId, messages);

        // 바로 첫 질문 반환
        return new InterviewStartResponse(sessionId, new ChatMessage("assistant", "자기소개 부탁드립니다."));
    }

    public ChatMessage askQuestion(InterviewRequest request) {
        String sessionId = request.getSessionId();
        List<ChatMessage> contextMessages = new ArrayList<>(interviewSessionService.getMessages(sessionId));
        String lang = Optional.ofNullable(request.getPreferredLanguage()).orElse("Java");
        List<String> questions = LANGUAGE_QUESTIONS.getOrDefault(lang, LANGUAGE_QUESTIONS.get("Java"));

        // 지금까지 user 답변 개수 == 다음에 던질 질문 인덱스
        long userCount = contextMessages.stream().filter(m -> "user".equals(m.getRole())).count();

        // 1) 미리 준비한 질문 남았으면 다음 질문 반환(assistant)
        if (userCount < questions.size() - 1) {
            contextMessages.add(new ChatMessage("user", request.getQuestion()));
            String nextQ = questions.get((int) userCount + 1); // 0: start에서 썼음
            ChatMessage assistantMsg = new ChatMessage("assistant", nextQ);
            contextMessages.add(assistantMsg);
            interviewSessionService.saveMessages(sessionId, contextMessages);
            return assistantMsg;
        }

        // 사용자 질문 추가
        contextMessages.add(new ChatMessage("user", request.getQuestion()));

        // GPT 호출 (예외/빈값 방지 처리)
        ChatResponse response = openAiService.chatWithFullResponse(contextMessages);

        if (response == null) {
            throw new IllegalStateException("OpenAI API 응답 오류");
        }
        if (response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new IllegalStateException("OpenAI API 응답 오류");
        }

        // GPT 답변 추출
        ChatResponse.Choice.Message aiMsg = response.getChoices().get(0).getMessage();
        ChatMessage assistantMessage = new ChatMessage(aiMsg.getRole(), aiMsg.getContent());

        // Redis에 전체 문맥 저장
        contextMessages.add(assistantMessage);
        interviewSessionService.saveMessages(sessionId, contextMessages);

        return assistantMessage;
    }
}
