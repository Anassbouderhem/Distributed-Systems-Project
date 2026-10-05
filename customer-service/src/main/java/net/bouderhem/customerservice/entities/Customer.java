package net.bouderhem.customerservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@NoArgsConstructor @AllArgsConstructor  @Builder
@Getter @Setter
public class Customer {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String email;

}
