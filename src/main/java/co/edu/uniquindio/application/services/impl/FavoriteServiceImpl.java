package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.place.FavoriteDTO;
import co.edu.uniquindio.application.model.entity.Favorite;
import co.edu.uniquindio.application.model.entity.Place;
import co.edu.uniquindio.application.model.entity.User;
import co.edu.uniquindio.application.repositories.FavoriteRepository;
import co.edu.uniquindio.application.repositories.PlaceRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    @Override
    public void addFavorite(Long placeId, String userEmail) throws Exception {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Place place = placeRepository.findById(placeId).orElseThrow();
        if (!favoriteRepository.existsByUserAndPlace(user, place)) {
            favoriteRepository.save(Favorite.builder().user(user).place(place).build());
        }
    }

    @Override
    public void removeFavorite(Long placeId, String userEmail) throws Exception {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Place place = placeRepository.findById(placeId).orElseThrow();
        favoriteRepository.deleteByUserAndPlace(user, place);
    }

    @Override
    public List<FavoriteDTO> getUserFavorites(String userEmail) throws Exception {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        return favoriteRepository.findByUser(user)
                .stream()
                .map(fav -> new FavoriteDTO(
                        fav.getPlace().getId(),
                        fav.getPlace().getTitle(),
                        fav.getPlace().getCity(),
                        fav.getPlace().getMainImage()
                ))
                .toList();
    }

    @Override
    public int getFavoriteCount(Long placeId) throws Exception {
        Place place = placeRepository.findById(placeId).orElseThrow();
        return favoriteRepository.countByPlace(place);
    }
}
