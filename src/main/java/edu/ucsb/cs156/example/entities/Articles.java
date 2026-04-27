package edu.ucsb.cs156.example.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity(name = "articles")
public class Articles {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String title;
  private String url;
  private String explanation;
  private String email;
  private LocalDateTime dateAdded;
}
