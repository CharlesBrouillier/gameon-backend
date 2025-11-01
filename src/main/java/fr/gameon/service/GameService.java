package fr.gameon.service;

import fr.gameon.dto.PagedResponse;
import fr.gameon.entity.*;
import fr.gameon.exception.ResourceNotFoundException;
import fr.gameon.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GameService implements IGameService {

    private final GameRepository gameRepository;
    private final IllustratorRepository illustratorRepository;
    private final MechanismRepository mechanismRepository;
    private final AuthorRepository authorRepository;
    private final GameImageRepository gameImageRepository;
    private final PublisherRepository publisherRepository;

    public GameService(GameRepository gameRepository, IllustratorRepository illustratorRepository, MechanismRepository mechanismRepository, AuthorRepository authorRepository, GameImageRepository gameImageRepository, PublisherRepository publisherRepository) {
        this.gameRepository = gameRepository;
        this.illustratorRepository = illustratorRepository;
        this.mechanismRepository = mechanismRepository;
        this.authorRepository = authorRepository;
        this.gameImageRepository = gameImageRepository;
        this.publisherRepository = publisherRepository;
    }

    public PagedResponse<GameEntity> getGames(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GameEntity> resultPage = gameRepository.findAll(pageable);

        return new PagedResponse<>(resultPage.getContent(), resultPage.getTotalPages(), resultPage.getTotalElements(), resultPage.getSize());
    }

    public PagedResponse<GameEntity> getGamesByFilters(Double minPrice, Double maxPrice, Integer minPlayers, Integer maxPlayers, Integer minDuration, Integer maxDuration, List<Integer> mechanisms, Integer mechanismsSize, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GameEntity> resultPage = gameRepository.findGamesByFilters(minPrice, maxPrice, minPlayers, maxPlayers, minDuration, maxDuration, mechanisms, mechanismsSize, pageable);

        return new PagedResponse<>(resultPage.getContent(), resultPage.getTotalPages(), resultPage.getTotalElements(), resultPage.getSize());
    }

    public ResponseEntity<GameEntity> getGameById(Long id) {
        GameEntity _game = gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id : " + id));

        return ResponseEntity.ok(_game);
    }

    public ResponseEntity<GameEntity> getGameBySlug(String slug) {
        GameEntity _game = gameRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with slug : " + slug));

        return ResponseEntity.ok(_game);
    }

    public List<GameEntity> getGamesByMechanismId(String mechanismId) {
        return gameRepository.findGamesByMechanismId(mechanismId);
    }

    public List<GameEntity> findGamesWithMechanisms(String gameId) {
        return gameRepository.findGamesWithMechanisms(gameId);
    }

    @Transactional
    public ResponseEntity<GameEntity> createGame(GameEntity game){
        if (game.getMechanisms() != null) {
            Set<MechanismEntity> attachedMechanisms = game.getMechanisms().stream()
                    .map(mechanism -> mechanismRepository.findById(mechanism.getId())
                            .orElseThrow(() -> new EntityNotFoundException("Mechanism not found with id: " + mechanism.getId())))
                    .collect(Collectors.toSet());
            game.setMechanisms(attachedMechanisms);
        }

        if (game.getAuthors() != null) {
            Set<AuthorEntity> attachedAuthors = game.getAuthors().stream()
                    .map(author -> authorRepository.findById(author.getId())
                            .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + author.getId())))
                    .collect(Collectors.toSet());
            game.setAuthors(attachedAuthors);
        }

        if (game.getIllustrators() != null) {
            Set<IllustratorEntity> attachedIllustrators = game.getIllustrators().stream()
                    .map(illustrator -> illustratorRepository.findById(illustrator.getId())
                            .orElseThrow(() -> new EntityNotFoundException("Illustrator not found with id: " + illustrator.getId())))
                    .collect(Collectors.toSet());
            game.setIllustrators(attachedIllustrators);
        }

        if (game.getPublisher() != null) {
            PublisherEntity publisher = publisherRepository.findById(game.getPublisher().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Publisher not found with id: " + game.getPublisher().getId()));
            game.setPublisher(publisher);
        }

        if (game.getImageUrls() != null) {
            List<GameImageEntity> attachedImages = game.getImageUrls().stream()
                    .map(image -> gameImageRepository.findById(image.getId())
                            .orElseThrow(() -> new EntityNotFoundException("Image not found with id: " + image.getId())))
                    .collect(Collectors.toList());
            game.setImageUrls(attachedImages);
        }

        return new ResponseEntity<>(gameRepository.save(game), HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<GameEntity> updateGame(Long id, GameEntity game) {
        GameEntity _game = gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id : " + id));

        _game.setName(game.getName());
        _game.setSlug(game.getName().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", ""));
        _game.setDescription(game.getDescription());
        _game.setPrice(game.getPrice());
        _game.setReleaseDate(game.getReleaseDate());
        _game.setMinAge(game.getMinAge());
        _game.setMinPlayers(game.getMinPlayers());
        _game.setMaxPlayers(game.getMaxPlayers());
        _game.setMinDuration(game.getMinDuration());
        _game.setMaxDuration(game.getMaxDuration());
        _game.setEan(game.getEan());
        _game.setGlobalNote(game.getGlobalNote());
        _game.setYoutubeUrl(game.getYoutubeUrl());
        _game.setBgaUrl(game.getBgaUrl());

        return new ResponseEntity<>(gameRepository.save(_game), HttpStatus.OK);
    }

    @OnDelete(action = OnDeleteAction.CASCADE)
    @Transactional
    public void deleteGame(Long id) {
        GameEntity _game = gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with id: " + id));

        gameRepository.delete(_game);
    }
}
