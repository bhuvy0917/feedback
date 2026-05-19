
package com.example.feedback;

import jakarta.persistence.*;

@Entity
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String feedback;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getFeedback() { return feedback; }

    public void setName(String name) { this.name = name; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
