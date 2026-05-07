package com.kiwi.combat;

import com.kiwi.features.combat.data.persistence.CombatBarkTriggerPersistence;
import com.kiwi.features.combat.repositories.CombatBarkTriggerRepository;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class CombatBarkTriggerTestRepositoryInMemory implements CombatBarkTriggerRepository {

    private final Map<Long, CombatBarkTriggerPersistence> store = new HashMap<>();
    private long sequence = 0;

    @Override
    public List<CombatBarkTriggerPersistence> findByCombatConfigId(Long combatConfigId) {
        return store.values().stream()
                .filter(t -> Objects.equals(t.getCombatConfigId(), combatConfigId))
                .toList();
    }

    @Override
    public Optional<CombatBarkTriggerPersistence> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public <S extends CombatBarkTriggerPersistence> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(++sequence);
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends CombatBarkTriggerPersistence> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends CombatBarkTriggerPersistence> List<S> saveAll(Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        for (S entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    @Override public boolean existsById(Long id) { return store.containsKey(id); }
    @Override public List<CombatBarkTriggerPersistence> findAll() { return new ArrayList<>(store.values()); }
    @Override public long count() { return store.size(); }

    // ============================================================================================
    // UNUSED METHODS
    // ============================================================================================

    @Override public void flush() {}
    @Override public void delete(CombatBarkTriggerPersistence entity) {}
    @Override public void deleteById(Long id) {}
    @Override public void deleteAll() {}
    @Override public void deleteAll(Iterable<? extends CombatBarkTriggerPersistence> entities) {}
    @Override public void deleteAllById(Iterable<? extends Long> ids) {}

    @Override public <S extends CombatBarkTriggerPersistence> List<S> saveAllAndFlush(Iterable<S> entities) { return saveAll(entities); }
    @Override public List<CombatBarkTriggerPersistence> findAllById(Iterable<Long> ids) { return List.of(); }
    @Override public List<CombatBarkTriggerPersistence> findAll(Sort sort) { return findAll(); }
    @Override public Page<CombatBarkTriggerPersistence> findAll(Pageable pageable) { return Page.empty(); }
    @Override public void deleteAllInBatch() {}
    @Override public void deleteAllInBatch(Iterable<CombatBarkTriggerPersistence> entities) {}
    @Override public void deleteAllByIdInBatch(Iterable<Long> ids) {}
    @Override @Deprecated public CombatBarkTriggerPersistence getOne(Long id) { return null; }
    @Override @Deprecated public CombatBarkTriggerPersistence getById(Long id) { return null; }
    @Override public CombatBarkTriggerPersistence getReferenceById(Long id) { return null; }
    @Override public <S extends CombatBarkTriggerPersistence> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
    @Override public <S extends CombatBarkTriggerPersistence> List<S> findAll(Example<S> example) { return List.of(); }
    @Override public <S extends CombatBarkTriggerPersistence> List<S> findAll(Example<S> example, Sort sort) { return List.of(); }
    @Override public <S extends CombatBarkTriggerPersistence> Page<S> findAll(Example<S> example, Pageable pageable) { return Page.empty(); }
    @Override public <S extends CombatBarkTriggerPersistence> long count(Example<S> example) { return 0; }
    @Override public <S extends CombatBarkTriggerPersistence> boolean exists(Example<S> example) { return false; }
    @Override public <S extends CombatBarkTriggerPersistence, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
}
