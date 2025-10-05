package co.edu.uniquindio.application.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Place place;

    @OneToOne
    @JoinColumn(nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private int rating; // 1-5

    @Column(nullable = false, length = 500)
    private String text;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(length = 500)
    private String hostReply;
}
