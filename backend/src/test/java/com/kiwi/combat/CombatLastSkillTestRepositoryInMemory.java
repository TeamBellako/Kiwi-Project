package com.kiwi.combat;

import com.kiwi.features.combat.data.persistence.CombatLastSkillKey;
import com.kiwi.features.combat.data.persistence.CombatLastSkillPersistence;
import com.kiwi.features.combat.repositories.CombatLastSkillRepository;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class CombatLastSkillTestRepositoryInMemory implements CombatLastSkillRepository {

    private final Map<CombatLastSkillKey, CombatLastSkillPersistence> store = new HashMap<>();

    @Override
    public List<CombatLastSkillPersistence> findById_CombatId(Long combatId) {
        return store.values().stream()
                .filter(e -> Objects.equals(e.getId().getCombatId(), combatId))
                .toList();
    }

    @Override
    public void deleteByIdCombatId(Long combatId) {
        store.entrySet().removeIf(e -> Objects.equals(e.getKey().getCombatId(), combatId));
    }

    @Override
    public Optional<CombatLastSkillPersistence> findById(CombatLastSkillKey key) {
        return Optional.ofNullable(store.get(key));
    }

    @Override
    public <S extends CombatLastSkillPersistence> S save(S entity) {
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends CombatLastSkillPersistence> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends CombatLastSkillPersistence> List<S> saveAll(Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        for (S entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    @Override
    public boolean existsById(CombatLastSkillKey key) {
        return store.containsKey(key);
    }

    @Override
    public List<CombatLastSkillPersistence> findAll() {
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

    @Override public void delete(CombatLastSkillPersistence entity) {}
    @Override public void deleteById(CombatLastSkillKey key) {}
    @Override public void deleteAll() {}
    @Override public void deleteAll(Iterable<? extends CombatLastSkillPersistence> entities) {}
    @Override public void deleteAllById(Iterable<? extends CombatLastSkillKey> keys) {}

    @Override public <S extends CombatLastSkillPersistence> List<S> saveAllAndFlush(Iterable<S> entities) { return saveAll(entities); }

    @Override public List<CombatLastSkillPersistence> findAllById(Iterable<CombatLastSkillKey> keys) { return List.of(); }

    @Override public List<CombatLastSkillPersistence> findAll(Sort sort) { return findAll(); }
    @Override public Page<CombatLastSkillPersistence> findAll(Pageable pageable) { return Page.empty(); }

    @Override public void deleteAllInBatch() {}
    @Override public void deleteAllInBatch(Iterable<CombatLastSkillPersistence> entities) {}
    @Override public void deleteAllByIdInBatch(Iterable<CombatLastSkillKey> keys) {}

    @Override @Deprecated public CombatLastSkillPersistence getOne(CombatLastSkillKey key) { return null; }
    @Override @Deprecated public CombatLastSkillPersistence getById(CombatLastSkillKey key) { return null; }
    @Override public CombatLastSkillPersistence getReferenceById(CombatLastSkillKey key) { return null; }

    @Override
    public <S extends CombatLastSkillPersistence> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
    @Override
    public <S extends CombatLastSkillPersistence> List<S> findAll(Example<S> example) { return List.of(); }
    @Override
    public <S extends CombatLastSkillPersistence> List<S> findAll(Example<S> example, Sort sort) { return List.of(); }
    @Override
    public <S extends CombatLastSkillPersistence> Page<S> findAll(Example<S> example, Pageable pageable) { return Page.empty(); }
    @Override
    public <S extends CombatLastSkillPersistence> long count(Example<S> example) { return 0; }
    @Override
    public <S extends CombatLastSkillPersistence> boolean exists(Example<S> example) { return false; }
    @Override
    public <S extends CombatLastSkillPersistence, R> R findBy(
            Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction
    ) { return null; }
}
