package com.kiwi.combat;

import com.kiwi.features.combat.data.persistence.CombatConfigPersistence;
import com.kiwi.features.combat.repositories.CombatConfigRepository;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class CombatConfigTestRepositoryInMemory implements CombatConfigRepository {

    private final Map<Long, CombatConfigPersistence> store = new HashMap<>();

    private long nextId = 1L;

    private long generateNextId() {
        return nextId++;
    }

    @Override
    public Optional<CombatConfigPersistence> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public <S extends CombatConfigPersistence> S save(S entity) {
        return saveAndFlush(entity);
    }

    @Override
    public <S extends CombatConfigPersistence> S saveAndFlush(S entity) {
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
    public List<CombatConfigPersistence> findAll() {
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

    @Override public void delete(CombatConfigPersistence entity) {}
    @Override public void deleteById(Long id) {}
    @Override public void deleteAll() {}
    @Override public void deleteAll(Iterable<? extends CombatConfigPersistence> entities) {}
    @Override public void deleteAllById(Iterable<? extends Long> longs) {}

    @Override public <S extends CombatConfigPersistence> List<S> saveAll(Iterable<S> entities) { return List.of(); }
    @Override public <S extends CombatConfigPersistence> List<S> saveAllAndFlush(Iterable<S> entities) { return List.of(); }

    @Override public List<CombatConfigPersistence> findAllById(Iterable<Long> ids) { return List.of(); }

    @Override public List<CombatConfigPersistence> findAll(Sort sort) { return findAll(); }
    @Override public Page<CombatConfigPersistence> findAll(Pageable pageable) { return Page.empty(); }

    @Override public void deleteAllInBatch() {}
    @Override public void deleteAllInBatch(Iterable<CombatConfigPersistence> entities) {}
    @Override public void deleteAllByIdInBatch(Iterable<Long> longs) {}

    @Override @Deprecated public CombatConfigPersistence getOne(Long aLong) { return null; }
    @Override @Deprecated public CombatConfigPersistence getById(Long aLong) { return null; }
    @Override public CombatConfigPersistence getReferenceById(Long aLong) { return null; }

    @Override
    public <S extends CombatConfigPersistence> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
    @Override
    public <S extends CombatConfigPersistence> List<S> findAll(Example<S> example) { return List.of(); }
    @Override
    public <S extends CombatConfigPersistence> List<S> findAll(Example<S> example, Sort sort) { return List.of(); }
    @Override
    public <S extends CombatConfigPersistence> Page<S> findAll(Example<S> example, Pageable pageable) { return Page.empty(); }
    @Override
    public <S extends CombatConfigPersistence> long count(Example<S> example) { return 0; }
    @Override
    public <S extends CombatConfigPersistence> boolean exists(Example<S> example) { return false; }
    @Override
    public <S extends CombatConfigPersistence, R> R findBy(
            Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction
    ) { return null; }
}
