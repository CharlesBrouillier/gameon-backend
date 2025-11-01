package fr.gameon.repository;

import fr.gameon.entity.GameEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<GameEntity, Long> {

    @Query("SELECT g FROM GameEntity g " +
            "JOIN g.mechanisms m " +
            "WHERE (:minPrice IS NULL OR g.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR g.price <= :maxPrice) " +
            "AND (:minPlayers IS NULL OR :maxPlayers IS NULL OR (g.minPlayers <= :maxPlayers AND g.maxPlayers >= :minPlayers)) " +
            "AND (:minPlayers IS NULL OR :minPlayers >= g.minPlayers) " +
            "AND (:maxPlayers IS NULL OR :maxPlayers <= g.maxPlayers) " +
            "AND (:minDuration IS NULL OR g.minDuration >= :minDuration) " +
            "AND (:maxDuration IS NULL OR g.maxDuration <= :maxDuration) " +
            "AND (:mechanisms IS NULL OR (SELECT COUNT(m2) FROM g.mechanisms m2 WHERE m2.id IN :mechanisms) = :mechanismsSize) " +
            "GROUP BY g")
    Page<GameEntity> findGamesByFilters(@Param("minPrice") Double minPrice,
                                        @Param("maxPrice") Double maxPrice,
                                        @Param("minPlayers") Integer minPlayers,
                                        @Param("maxPlayers") Integer maxPlayers,
                                        @Param("minDuration") Integer minDuration,
                                        @Param("maxDuration") Integer maxDuration,
                                        @Param("mechanisms") List<Integer> mechanisms,
                                        @Param("mechanismsSize") Integer mechanismsSize,
                                        Pageable pageable);

    @Query("SELECT g FROM GameEntity g JOIN g.mechanisms m WHERE m.id = :mechanismId")
    List<GameEntity> findGamesByMechanismId(@Param("mechanismId") String mechanismId);

    @Query("SELECT g FROM GameEntity g JOIN g.mechanisms m " +
            "WHERE m IN (SELECT m2 FROM GameEntity g2 JOIN g2.mechanisms m2 WHERE g2.id = :gameId) " +
            "AND g.id != :gameId")
    List<GameEntity> findGamesWithMechanisms(@Param("gameId") String gameId);

    @Query("SELECT g FROM GameEntity g WHERE g.slug = :slug")
    Optional<GameEntity> findBySlug(@Param("slug") String slug);
}