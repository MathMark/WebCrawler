package com.seo.crawler;

import com.seo.model.ConnectionResponse;

import java.util.Optional;

public interface ConnectionClient {
    Optional<ConnectionResponse> connect(String uri);
}
