package com.kiwi.combat;

import com.kiwi.features.combat.data.persistence.CombatLogPersistence;
import com.kiwi.features.combat.repositories.CombatLogRepository;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class CombatLogTestRepositoryInMemory implements CombatLogRepository {

    private final Map<Long, CombatLogPersistence> store = new LinkedHashMap<>();

    private long nextId = 1L;

    private long generateNextId() {
        return nextId++;
    }

    @Override
    public List<CombatLogPersistence> findByCombatIdOrderByIdAsc(Long combatId) {
        return store.values().stream()
                .filter(c -> Objects.equals(c.getCombatId(), combatId))
                .sorted(Comparator.comparing(CombatLogPersistence::getId))
                .toList();
    }

    @Override
    public void deleteByCombatId(Long combatId) {
        store.values().removeIf(c -> Objects.equals(c.getCombatId(), combatId));
    }

    @Override
    public Optional<CombatLogPersistence> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public <S extends CombatLogPersistence> S save(S entity) {
        return saveAndFlush(entity);
    }

    @Override
    public <S extends CombatLogPersistence> S saveAndFlush(S entity) {
        if (entity.getId() == null) {
            entity.setId(generateNextId());
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends CombatLogPersistence> List<S> saveAll(Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        for (S entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    @Override
    public boolean existsById(Long id) {
        return store.containsKey(id);
    }

    @Override
    public List<CombatLogPersistence> findAll() {
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

    @Override public void delete(CombatLogPersistence entity) {}
    @Override public void deleteById(Long id) {}
    @Override public void deleteAll() {}
    @Override public void deleteAll(Iterable<? extends CombatLogPersistence> entities) {}
    @Override public void deleteAllById(Iterable<? extends Long> longs) {}

    @Override public <S extends CombatLogPersistence> List<S> saveAllAndFlush(Iterable<S> entities) { return saveAll(entities); }

    @Override public List<CombatLogPersistence> findAllById(Iterable<Long> ids) { return List.of(); }

    @Override public List<CombatLogPersistence> findAll(Sort sort) { return findAll(); }
    @Override public Page<CombatLogPersistence> findAll(Pageable pageable) { return Page.empty(); }

    @Override public void deleteAllInBatch() {}
    @Override public void deleteAllInBatch(Iterable<CombatLogPersistence> entities) {}
    @Override public void deleteAllByIdInBatch(Iterable<Long> longs) {}

    @Override @Deprecated public CombatLogPersistence getOne(Long aLong) { return null; }
    @Override @Deprecated public CombatLogPersistence getById(Long aLong) { return null; }
    @Override public CombatLogPersistence getReferenceById(Long aLong) { return null; }

    @Override
    public <S extends CombatLogPersistence> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
    @Override
    public <S extends CombatLogPersistence> List<S> findAll(Example<S> example) { return List.of(); }
    @Override
    public <S extends CombatLogPersistence> List<S> findAll(Example<S> example, Sort sort) { return List.of(); }
    @Override
    public <S extends CombatLogPersistence> Page<S> findAll(Example<S> example, Pageable pageable) { return Page.empty(); }
    @Override
    public <S extends CombatLogPersistence> long count(Example<S> example) { return 0; }
    @Override
    public <S extends CombatLogPersistence> boolean exists(Example<S> example) { return false; }
    @Override
    public <S extends CombatLogPersistence, R> R findBy(
            Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction
    ) { return null; }
}
