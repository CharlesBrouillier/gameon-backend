package fr.gameon.service;

import fr.gameon.dto.PagedResponse;
import fr.gameon.entity.GameEntity;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface IGameService {
    public PagedResponse<GameEntity> getGames(int page, int size);
    public ResponseEntity<GameEntity> getGameById(Long id);
    public ResponseEntity<GameEntity> getGameBySlug(String slug);
    public PagedResponse<GameEntity> getGamesByFilters(Double minPrice, Double maxPrice, Integer minPlayers, Integer maxPlayers, Integer minDuration, Integer maxDuration, List<Integer> mechanisms, Integer mechanismsSize, int page, int size);
    public List<GameEntity> getGamesByMechanismId(String mechanismId);
    public List<GameEntity> findGamesWithMechanisms(String GameId);
    public ResponseEntity<GameEntity> createGame(GameEntity gameEntity);
    public ResponseEntity<GameEntity> updateGame(Long id, GameEntity gameEntity);
    public void deleteGame(Long id);
}
