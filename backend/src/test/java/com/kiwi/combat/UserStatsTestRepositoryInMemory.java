package com.kiwi.combat;

import com.kiwi.features.combat.data.persistence.UserStatsPersistence;
import com.kiwi.features.combat.repositories.UserStatsRepository;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class UserStatsTestRepositoryInMemory implements UserStatsRepository {

    private final Map<Long, UserStatsPersistence> store = new HashMap<>();

    @Override
    public Optional<UserStatsPersistence> findById(Long userId) {
        return Optional.ofNullable(store.get(userId));
    }

    @Override
    public <S extends UserStatsPersistence> S save(S entity) {
        return saveAndFlush(entity);
    }

    @Override
    public <S extends UserStatsPersistence> S saveAndFlush(S entity) {
        store.put(entity.getUserId(), entity);
        return entity;
    }

    @Override
    public boolean existsById(Long userId) {
        return store.containsKey(userId);
    }

    @Override
    public List<UserStatsPersistence> findAll() {
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

    @Override public void delete(UserStatsPersistence entity) {}
    @Override public void deleteById(Long id) {}
    @Override public void deleteAll() {}
    @Override public void deleteAll(Iterable<? extends UserStatsPersistence> entities) {}
    @Override public void deleteAllById(Iterable<? extends Long> longs) {}

    @Override public <S extends UserStatsPersistence> List<S> saveAll(Iterable<S> entities) { return List.of(); }
    @Override public <S extends UserStatsPersistence> List<S> saveAllAndFlush(Iterable<S> entities) { return List.of(); }

    @Override public List<UserStatsPersistence> findAllById(Iterable<Long> ids) { return List.of(); }

    @Override public List<UserStatsPersistence> findAll(Sort sort) { return findAll(); }
    @Override public Page<UserStatsPersistence> findAll(Pageable pageable) { return Page.empty(); }

    @Override public void deleteAllInBatch() {}
    @Override public void deleteAllInBatch(Iterable<UserStatsPersistence> entities) {}
    @Override public void deleteAllByIdInBatch(Iterable<Long> longs) {}

    @Override @Deprecated public UserStatsPersistence getOne(Long aLong) { return null; }
    @Override @Deprecated public UserStatsPersistence getById(Long aLong) { return null; }
    @Override public UserStatsPersistence getReferenceById(Long aLong) { return null; }

    @Override
    public <S extends UserStatsPersistence> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
    @Override
    public <S extends UserStatsPersistence> List<S> findAll(Example<S> example) { return List.of(); }
    @Override
    public <S extends UserStatsPersistence> List<S> findAll(Example<S> example, Sort sort) { return List.of(); }
    @Override
    public <S extends UserStatsPersistence> Page<S> findAll(Example<S> example, Pageable pageable) { return Page.empty(); }
    @Override
    public <S extends UserStatsPersistence> long count(Example<S> example) { return 0; }
    @Override
    public <S extends UserStatsPersistence> boolean exists(Example<S> example) { return false; }
    @Override
    public <S extends UserStatsPersistence, R> R findBy(
            Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction
    ) { return null; }
}
