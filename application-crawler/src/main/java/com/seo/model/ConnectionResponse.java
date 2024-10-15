package com.seo.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
public class ConnectionResponse {
    private String htmlBody;
    private HttpStatus httpStatus;
    private List<ContentType> contentType;
    private boolean hasHtmlSource;
    
    public enum ContentType {
        APPLICATION_JSON("application/json"),
        APPLICATION_PDF("application/pdf"),
        APPLICATION_MSWORD("application/msword"),
        APPLICATION_SQL("application/sql"),
        APPLICATION_ZIP("application/zip"),
        APPLICATION_XML("application/xml"),
        TEXT_HTML("text/html"),
        IMG("img/jpg"),
        UNSUPPORTED_OR_UNKNOWN("unsupported/unknown");
        
        String name;
        
        private static final Map<String, ContentType> CONTENT_TYPE_MAP = new HashMap<>();
        
        static {
            for (ContentType type : ContentType.values()) {
                CONTENT_TYPE_MAP.put(type.name, type);
            }
        }
        
        ContentType(String name) {
            this.name = name;
        }
        
        public static ContentType fromString(String name) {
            ContentType contentType = CONTENT_TYPE_MAP.get(name);
            return Objects.requireNonNullElse(contentType, ContentType.UNSUPPORTED_OR_UNKNOWN);
        }
    }
}
