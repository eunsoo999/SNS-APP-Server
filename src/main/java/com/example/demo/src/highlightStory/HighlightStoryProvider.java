package com.example.demo.src.highlightStory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HighlightStoryProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HighlightStoryDao highlightStoryDao;

    @Autowired
    public HighlightStoryProvider(HighlightStoryDao highlightStoryDao) {
        this.highlightStoryDao = highlightStoryDao;
    }

}
