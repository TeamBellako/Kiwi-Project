package com.kiwi.combat;

import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import com.kiwi.features.combat.repositories.CombatRepository;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class CombatTestRepositoryInMemory implements CombatRepository {

    private final Map<Long, CombatPersistence> store = new HashMap<>();

    private long nextId = 1L;

    private long generateNextId() {
        return nextId++;
    }

    @Override
    public Optional<CombatPersistence> findByUserIdAndCombatConfigId(Long userId, Long combatConfigId) {
        return store.values().stream()
                .filter(c -> c.getUserId().equals(userId) && c.getCombatConfigId().equals(combatConfigId))
                .findFirst();
    }

    @Override
    public Optional<CombatPersistence> findFirstByUserIdAndCombatStatus(Long userId, CombatGeneralStatus combatStatus) {
        return store.values().stream()
                .filter(c -> c.getUserId().equals(userId) && c.getCombatStatus() == combatStatus)
                .findFirst();
    }

    @Override
    public Optional<CombatPersistence> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public <S extends CombatPersistence> S save(S entity) {
        return saveAndFlush(entity);
    }

    @Override
    public <S extends CombatPersistence> S saveAndFlush(S entity) {
        if (entity.getId() == null) {
            entity.setId(generateNextId());
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void delete(CombatPersistence entity) {
        if (entity != null && entity.getId() != null) {
            store.remove(entity.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return store.containsKey(id);
    }

    @Override
    public List<CombatPersistence> findAll() {
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

    @Override public void deleteAll() {}
    @Override public void deleteAll(Iterable<? extends CombatPersistence> entities) {}
    @Override public void deleteAllById(Iterable<? extends Long> longs) {}

    @Override public <S extends CombatPersistence> List<S> saveAll(Iterable<S> entities) { return List.of(); }
    @Override public <S extends CombatPersistence> List<S> saveAllAndFlush(Iterable<S> entities) { return List.of(); }

    @Override public List<CombatPersistence> findAllById(Iterable<Long> ids) { return List.of(); }

    @Override public List<CombatPersistence> findAll(Sort sort) { return findAll(); }
    @Override public Page<CombatPersistence> findAll(Pageable pageable) { return Page.empty(); }

    @Override public void deleteAllInBatch() {}
    @Override public void deleteAllInBatch(Iterable<CombatPersistence> entities) {}
    @Override public void deleteAllByIdInBatch(Iterable<Long> longs) {}

    @Override @Deprecated public CombatPersistence getOne(Long aLong) { return null; }
    @Override @Deprecated public CombatPersistence getById(Long aLong) { return null; }
    @Override public CombatPersistence getReferenceById(Long aLong) { return null; }

    @Override
    public <S extends CombatPersistence> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
    @Override
    public <S extends CombatPersistence> List<S> findAll(Example<S> example) { return List.of(); }
    @Override
    public <S extends CombatPersistence> List<S> findAll(Example<S> example, Sort sort) { return List.of(); }
    @Override
    public <S extends CombatPersistence> Page<S> findAll(Example<S> example, Pageable pageable) { return Page.empty(); }
    @Override
    public <S extends CombatPersistence> long count(Example<S> example) { return 0; }
    @Override
    public <S extends CombatPersistence> boolean exists(Example<S> example) { return false; }
    @Override
    public <S extends CombatPersistence, R> R findBy(
            Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction
    ) { return null; }
}
