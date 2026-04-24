package edu.ucsb.cs156.example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: pgunha This is a JPA entity that represents a UCSBOrganization
 *     <p>A UCSBOrganization is a dining commons at UCSB
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ucsbOrganization")
public class UCSBOrganization {
  @Id private String orgCode;

  private String orgTranslationShort;
  private String orgTranslation;
  private boolean inactive;
}
