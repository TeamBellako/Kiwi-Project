package com.kiwi.combat;

import com.kiwi.features.combat.data.persistence.EnemyPersistence;
import com.kiwi.features.combat.repositories.EnemyRepository;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class EnemyTestRepositoryInMemory implements EnemyRepository {

    private final Map<Long, EnemyPersistence> store = new HashMap<>();

    private long nextId = 1L;

    private long generateNextId() {
        return nextId++;
    }

    @Override
    public Optional<EnemyPersistence> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public <S extends EnemyPersistence> S save(S entity) {
        return saveAndFlush(entity);
    }

    @Override
    public <S extends EnemyPersistence> S saveAndFlush(S entity) {
        if (entity.getId() == null) {
            entity.setId(generateNextId());
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public boolean existsById(Long id) {
        return store.containsKey(id);
    }

    @Override
    public List<EnemyPersistence> findAll() {
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

    @Override public void delete(EnemyPersistence entity) {}
    @Override public void deleteById(Long id) {}
    @Override public void deleteAll() {}
    @Override public void deleteAll(Iterable<? extends EnemyPersistence> entities) {}
    @Override public void deleteAllById(Iterable<? extends Long> longs) {}

    @Override public <S extends EnemyPersistence> List<S> saveAll(Iterable<S> entities) { return List.of(); }
    @Override public <S extends EnemyPersistence> List<S> saveAllAndFlush(Iterable<S> entities) { return List.of(); }

    @Override public List<EnemyPersistence> findAllById(Iterable<Long> ids) { return List.of(); }

    @Override public List<EnemyPersistence> findAll(Sort sort) { return findAll(); }
    @Override public Page<EnemyPersistence> findAll(Pageable pageable) { return Page.empty(); }

    @Override public void deleteAllInBatch() {}
    @Override public void deleteAllInBatch(Iterable<EnemyPersistence> entities) {}
    @Override public void deleteAllByIdInBatch(Iterable<Long> longs) {}

    @Override @Deprecated public EnemyPersistence getOne(Long aLong) { return null; }
    @Override @Deprecated public EnemyPersistence getById(Long aLong) { return null; }
    @Override public EnemyPersistence getReferenceById(Long aLong) { return null; }

    @Override
    public <S extends EnemyPersistence> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
    @Override
    public <S extends EnemyPersistence> List<S> findAll(Example<S> example) { return List.of(); }
    @Override
    public <S extends EnemyPersistence> List<S> findAll(Example<S> example, Sort sort) { return List.of(); }
    @Override
    public <S extends EnemyPersistence> Page<S> findAll(Example<S> example, Pageable pageable) { return Page.empty(); }
    @Override
    public <S extends EnemyPersistence> long count(Example<S> example) { return 0; }
    @Override
    public <S extends EnemyPersistence> boolean exists(Example<S> example) { return false; }
    @Override
    public <S extends EnemyPersistence, R> R findBy(
            Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction
    ) { return null; }
}
