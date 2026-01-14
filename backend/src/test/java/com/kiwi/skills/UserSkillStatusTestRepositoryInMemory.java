package com.kiwi.skills;

import com.kiwi.features.skills.controllers.UserSkillStatusRepository;
import com.kiwi.features.skills.data.*;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserSkillStatusTestRepositoryInMemory implements UserSkillStatusRepository {

    private final Map<UserSkillStatusKey, UserSkillStatusPersistence> store = new HashMap<>();

    @Override
    public Optional<UserSkillStatusPersistence> findByIdUserIdAndIdSkillId(Long userId, Long skillId) {
        return Optional.ofNullable(store.get(new UserSkillStatusKey(userId, skillId)));
    }

    @Override
    public List<UserSkillStatusPersistence> findByIdUserId(Long userId) {
        return store.values().stream()
                .filter(us -> us.getId().getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<UserSkillStatusPersistence> findByIdUserIdAndDeckSlotNot(
            Long userId,
            int deckSlot
    ) {
        return store.values().stream()
                .filter(us ->
                        us.getId().getUserId().equals(userId) &&
                                us.getDeckSlot() != deckSlot
                )
                .toList();
    }

    @Override
    public <S extends UserSkillStatusPersistence> S save(S entity) {
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends UserSkillStatusPersistence> S saveAndFlush(S entity) {
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<UserSkillStatusPersistence> findById(UserSkillStatusKey key) {
        return Optional.ofNullable(store.get(key));
    }

    @Override
    public boolean existsById(UserSkillStatusKey key) {
        return store.containsKey(key);
    }

    @Override
    public List<UserSkillStatusPersistence> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public long count() {
        return store.size();
    }

    // ============================================================================================
    // UNUSED METHODS
    // ============================================================================================

    @Override public void flush() {}

    @Override public void deleteById(UserSkillStatusKey key) {}
    @Override public void delete(UserSkillStatusPersistence entity) {}
    @Override public void deleteAll() {}
    @Override public void deleteAll(Iterable<? extends UserSkillStatusPersistence> entities) {}
    @Override public void deleteAllById(Iterable<? extends UserSkillStatusKey> keys) {}

    @Override public <S extends UserSkillStatusPersistence> List<S> saveAll(Iterable<S> entities) { return List.of(); }
    @Override public <S extends UserSkillStatusPersistence> List<S> saveAllAndFlush(Iterable<S> entities) { return List.of(); }

    @Override public List<UserSkillStatusPersistence> findAllById(Iterable<UserSkillStatusKey> keys) { return List.of(); }

    @Override public List<UserSkillStatusPersistence> findAll(Sort sort) { return findAll(); }
    @Override public Page<UserSkillStatusPersistence> findAll(Pageable pageable) { return Page.empty(); }

    @Override public void deleteAllInBatch() {}
    @Override public void deleteAllInBatch(Iterable<UserSkillStatusPersistence> entities) {}
    @Override public void deleteAllByIdInBatch(Iterable<UserSkillStatusKey> keys) {}

    @Override public UserSkillStatusPersistence getOne(UserSkillStatusKey key) { return null; }
    @Override public UserSkillStatusPersistence getById(UserSkillStatusKey key) { return null; }
    @Override public UserSkillStatusPersistence getReferenceById(UserSkillStatusKey key) { return null; }

    @Override
    public <S extends UserSkillStatusPersistence> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends UserSkillStatusPersistence> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends UserSkillStatusPersistence> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends UserSkillStatusPersistence> Page<S> findAll(Example<S> example, Pageable pageable) {
        return Page.empty();
    }

    @Override
    public <S extends UserSkillStatusPersistence> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends UserSkillStatusPersistence> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends UserSkillStatusPersistence, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

}
