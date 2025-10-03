package co.edu.uniquindio.application.model.entity;

import co.edu.uniquindio.application.model.enums.Role;
import co.edu.uniquindio.application.model.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 15)
    private String phone;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 200, nullable = false)
    private String password;

    @Column(length = 200)
    private String photoUrl;

    @Column(nullable = false)
    private LocalDate dateBirth;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;


}
