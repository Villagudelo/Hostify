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
    
    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private int maxGuests;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User host;

    // Método para obtener la imagen principal
    public String getMainImage() {
        return (images != null && !images.isEmpty()) ? images.get(0) : null;
    }

    // Método para obtener el rating (puedes implementar la lógica real después)
    public Double getRating() {
        // Por ahora retorna null o un valor fijo, luego puedes calcular el promedio de comentarios
        return null;
    }  

}
