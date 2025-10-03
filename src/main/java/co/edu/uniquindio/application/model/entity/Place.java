package co.edu.uniquindio.application.model.entity;

import co.edu.uniquindio.application.model.enums.Service;
import co.edu.uniquindio.application.model.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Lob
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private float price;

    @ElementCollection
    private List<String> images;

    @ElementCollection
    private Set<Service> services;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
}
