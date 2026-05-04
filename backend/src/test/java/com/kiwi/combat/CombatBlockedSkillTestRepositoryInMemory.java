package com.kiwi.combat;

import com.kiwi.features.combat.data.persistence.CombatBlockedSkillKey;
import com.kiwi.features.combat.data.persistence.CombatBlockedSkillPersistence;
import com.kiwi.features.combat.repositories.CombatBlockedSkillRepository;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class CombatBlockedSkillTestRepositoryInMemory implements CombatBlockedSkillRepository {

    private final Map<CombatBlockedSkillKey, CombatBlockedSkillPersistence> store = new HashMap<>();

    @Override
    public List<CombatBlockedSkillPersistence> findById_CombatId(Long combatId) {
        return store.values().stream()
                .filter(e -> Objects.equals(e.getId().getCombatId(), combatId))
                .toList();
    }

    @Override
    public void deleteByIdCombatId(Long combatId) {
        store.entrySet().removeIf(e -> Objects.equals(e.getKey().getCombatId(), combatId));
    }

    @Override
    public Optional<CombatBlockedSkillPersistence> findById(CombatBlockedSkillKey key) {
        return Optional.ofNullable(store.get(key));
    }

    @Override
    public <S extends CombatBlockedSkillPersistence> S save(S entity) {
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends CombatBlockedSkillPersistence> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends CombatBlockedSkillPersistence> List<S> saveAll(Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        for (S entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    @Override
    public boolean existsById(CombatBlockedSkillKey key) {
        return store.containsKey(key);
    }

    @Override
    public List<CombatBlockedSkillPersistence> findAll() {
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

    @Override public void delete(CombatBlockedSkillPersistence entity) {}
    @Override public void deleteById(CombatBlockedSkillKey key) {}
    @Override public void deleteAll() {}
    @Override public void deleteAll(Iterable<? extends CombatBlockedSkillPersistence> entities) {}
    @Override public void deleteAllById(Iterable<? extends CombatBlockedSkillKey> keys) {}

    @Override public <S extends CombatBlockedSkillPersistence> List<S> saveAllAndFlush(Iterable<S> entities) { return saveAll(entities); }

    @Override public List<CombatBlockedSkillPersistence> findAllById(Iterable<CombatBlockedSkillKey> keys) { return List.of(); }

    @Override public List<CombatBlockedSkillPersistence> findAll(Sort sort) { return findAll(); }
    @Override public Page<CombatBlockedSkillPersistence> findAll(Pageable pageable) { return Page.empty(); }

    @Override public void deleteAllInBatch() {}
    @Override public void deleteAllInBatch(Iterable<CombatBlockedSkillPersistence> entities) {}
    @Override public void deleteAllByIdInBatch(Iterable<CombatBlockedSkillKey> keys) {}

    @Override @Deprecated public CombatBlockedSkillPersistence getOne(CombatBlockedSkillKey key) { return null; }
    @Override @Deprecated public CombatBlockedSkillPersistence getById(CombatBlockedSkillKey key) { return null; }
    @Override public CombatBlockedSkillPersistence getReferenceById(CombatBlockedSkillKey key) { return null; }

    @Override
    public <S extends CombatBlockedSkillPersistence> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
    @Override
    public <S extends CombatBlockedSkillPersistence> List<S> findAll(Example<S> example) { return List.of(); }
    @Override
    public <S extends CombatBlockedSkillPersistence> List<S> findAll(Example<S> example, Sort sort) { return List.of(); }
    @Override
    public <S extends CombatBlockedSkillPersistence> Page<S> findAll(Example<S> example, Pageable pageable) { return Page.empty(); }
    @Override
    public <S extends CombatBlockedSkillPersistence> long count(Example<S> example) { return 0; }
    @Override
    public <S extends CombatBlockedSkillPersistence> boolean exists(Example<S> example) { return false; }
    @Override
    public <S extends CombatBlockedSkillPersistence, R> R findBy(
            Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction
    ) { return null; }
}
