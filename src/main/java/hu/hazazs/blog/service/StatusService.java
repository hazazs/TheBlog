package hu.hazazs.blog.service;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties(prefix = "service")
public class StatusService {
    private Map<String, String> status;
    public Map<String, String> getStatus() {
        return status;
    }
    public void setStatus(Map<String, String> status) {
        this.status = status;
    }
}