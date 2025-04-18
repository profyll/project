package com.example.project.service;


import com.example.project.vo.YoutubeItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@AllArgsConstructor
public class YouTubeApiService {
    private ObjectMapper objectMapper;

    public YoutubeItem[] findYoutubeVideoId(String q) throws JsonProcessingException {

        RestTemplate restTemplate = new RestTemplate();


        ResponseEntity<String> response = restTemplate.exchange("https://www.googleapis.com/youtube/v3/search?key=AIzaSyBKjXCAidMtNi8cqdkB49uE_kn2YI3QHVs&type=video&maxResults=2&q="+q,
                HttpMethod.GET,
                null,
                String.class
                );

        String json =objectMapper.readTree(response.getBody()).path("items").toString();


        return objectMapper.readValue(json, YoutubeItem[].class);
    }
}
