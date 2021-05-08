package com.example.demo.src.highlightStory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/highlights")
public class HighlightStoryController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final HighlightStoryProvider highlightStoryProvider;

    @Autowired
    private final HighlightStoryService highlightStoryService;

    public HighlightStoryController(HighlightStoryProvider highlightStoryProvider, HighlightStoryService highlightStoryService) {
        this.highlightStoryProvider = highlightStoryProvider;
        this.highlightStoryService = highlightStoryService;
    }


}
