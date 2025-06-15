package goodterview.interview;

import goodterview.openai.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewSessionService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "interview:";

    public void saveMessage(String sessionId, ChatMessage message) {
        redisTemplate.opsForList().rightPush(PREFIX + sessionId, message);
    }

    @SuppressWarnings("unchecked")
    public List<ChatMessage> getMessages(String sessionId) {
        List<Object> objects = redisTemplate.opsForList().range(PREFIX + sessionId, 0, -1);
        return objects == null ? new ArrayList<>() :
                objects.stream().map(obj -> (ChatMessage) obj)
                        .collect(Collectors.toCollection(ArrayList::new)); // ← 가변 리스트로 반환
    }

    public void clearSession(String sessionId) {
        redisTemplate.delete(PREFIX + sessionId);
    }
}
