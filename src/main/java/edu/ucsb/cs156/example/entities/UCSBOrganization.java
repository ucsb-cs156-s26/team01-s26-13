package edu.ucsb.cs156.example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: pgunhal
 * This is a JPA entity that represents a UCSBOrganization, a student organization at UCSB.  It is used to store the results from the UCSB API for student organizations.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ucsborganizations")
public class UCSBOrganization {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private String orgCode;

  private String orgTranslationShort;
  private String orgTranslation;
  private boolean inactive;
}
