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

    public List<ChatMessage> getMessages(String sessionId) {
        List<Object> objects = redisTemplate.opsForList().range(PREFIX + sessionId, 0, -1);
        if (objects == null) return new ArrayList<>();
        return objects.stream()
                .map(obj -> (ChatMessage) obj)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void saveMessages(String sessionId, List<ChatMessage> messages) {
        redisTemplate.delete(PREFIX + sessionId); // 먼저 기존 메시지 삭제
        for (ChatMessage msg : messages) {
            redisTemplate.opsForList().rightPush(PREFIX + sessionId, msg);
        }
    }
}
