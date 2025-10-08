package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.place.FavoriteDTO;
import java.util.List;

public interface FavoriteService {
    void addFavorite(Long placeId, String userEmail) throws Exception;
    void removeFavorite(Long placeId, String userEmail) throws Exception;
    List<FavoriteDTO> getUserFavorites(String userEmail) throws Exception;
    int getFavoriteCount(Long placeId) throws Exception;
}
