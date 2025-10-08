package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.model.entity.Favorite;
import co.edu.uniquindio.application.model.entity.Place;
import co.edu.uniquindio.application.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByUserAndPlace(User user, Place place);
    void deleteByUserAndPlace(User user, Place place);
    List<Favorite> findByUser(User user);
    int countByPlace(Place place);
}
