package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);
    boolean existsById(String id);
    Optional<User> findById(String id);
    List<User> findAll();
}
