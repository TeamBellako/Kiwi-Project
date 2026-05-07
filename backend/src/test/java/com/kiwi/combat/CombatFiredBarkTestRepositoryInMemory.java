package com.kiwi.combat;

import com.kiwi.features.combat.data.persistence.CombatFiredBarkKey;
import com.kiwi.features.combat.data.persistence.CombatFiredBarkPersistence;
import com.kiwi.features.combat.repositories.CombatFiredBarkRepository;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class CombatFiredBarkTestRepositoryInMemory implements CombatFiredBarkRepository {

    private final Map<CombatFiredBarkKey, CombatFiredBarkPersistence> store = new HashMap<>();

    @Override
    public List<CombatFiredBarkPersistence> findById_CombatId(Long combatId) {
        return store.values().stream()
                .filter(e -> Objects.equals(e.getId().getCombatId(), combatId))
                .toList();
    }

    @Override
    public void deleteByIdCombatId(Long combatId) {
        store.entrySet().removeIf(e -> Objects.equals(e.getKey().getCombatId(), combatId));
    }

    @Override
    public Optional<CombatFiredBarkPersistence> findById(CombatFiredBarkKey key) {
        return Optional.ofNullable(store.get(key));
    }

    @Override
    public <S extends CombatFiredBarkPersistence> S save(S entity) {
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends CombatFiredBarkPersistence> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends CombatFiredBarkPersistence> List<S> saveAll(Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        for (S entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    @Override public boolean existsById(CombatFiredBarkKey key) { return store.containsKey(key); }
    @Override public List<CombatFiredBarkPersistence> findAll() { return new ArrayList<>(store.values()); }
    @Override public long count() { return store.size(); }

    // ============================================================================================
    // UNUSED METHODS
    // ============================================================================================

    @Override public void flush() {}
    @Override public void delete(CombatFiredBarkPersistence entity) {}
    @Override public void deleteById(CombatFiredBarkKey key) {}
    @Override public void deleteAll() {}
    @Override public void deleteAll(Iterable<? extends CombatFiredBarkPersistence> entities) {}
    @Override public void deleteAllById(Iterable<? extends CombatFiredBarkKey> keys) {}

    @Override public <S extends CombatFiredBarkPersistence> List<S> saveAllAndFlush(Iterable<S> entities) { return saveAll(entities); }
    @Override public List<CombatFiredBarkPersistence> findAllById(Iterable<CombatFiredBarkKey> keys) { return List.of(); }
    @Override public List<CombatFiredBarkPersistence> findAll(Sort sort) { return findAll(); }
    @Override public Page<CombatFiredBarkPersistence> findAll(Pageable pageable) { return Page.empty(); }
    @Override public void deleteAllInBatch() {}
    @Override public void deleteAllInBatch(Iterable<CombatFiredBarkPersistence> entities) {}
    @Override public void deleteAllByIdInBatch(Iterable<CombatFiredBarkKey> keys) {}
    @Override @Deprecated public CombatFiredBarkPersistence getOne(CombatFiredBarkKey key) { return null; }
    @Override @Deprecated public CombatFiredBarkPersistence getById(CombatFiredBarkKey key) { return null; }
    @Override public CombatFiredBarkPersistence getReferenceById(CombatFiredBarkKey key) { return null; }
    @Override public <S extends CombatFiredBarkPersistence> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
    @Override public <S extends CombatFiredBarkPersistence> List<S> findAll(Example<S> example) { return List.of(); }
    @Override public <S extends CombatFiredBarkPersistence> List<S> findAll(Example<S> example, Sort sort) { return List.of(); }
    @Override public <S extends CombatFiredBarkPersistence> Page<S> findAll(Example<S> example, Pageable pageable) { return Page.empty(); }
    @Override public <S extends CombatFiredBarkPersistence> long count(Example<S> example) { return 0; }
    @Override public <S extends CombatFiredBarkPersistence> boolean exists(Example<S> example) { return false; }
    @Override public <S extends CombatFiredBarkPersistence, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
}
