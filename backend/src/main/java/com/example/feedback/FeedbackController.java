
package com.example.feedback;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin
public class FeedbackController {

    private final FeedbackRepository repo;

    public FeedbackController(FeedbackRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public Feedback save(@RequestBody Feedback feedback) {
        return repo.save(feedback);
    }

    @GetMapping
    public List<Feedback> getAll() {
        return repo.findAll();
    }
}
