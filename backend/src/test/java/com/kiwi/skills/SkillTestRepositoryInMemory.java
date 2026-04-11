package com.kiwi.skills;

import com.kiwi.features.skills.controllers.SkillRepository;
import com.kiwi.features.skills.data.persistence.SkillPersistence;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class SkillTestRepositoryInMemory implements SkillRepository {

    private final Map<Long, SkillPersistence> store = new HashMap<>();

    private long nextId = 1L;

    private long generateNextId() {
        return nextId++;
    }

    @Override
    public Optional<SkillPersistence> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public <S extends SkillPersistence> S save(S entity) {
        return saveAndFlush(entity);
    }

    @Override
    public <S extends SkillPersistence> S saveAndFlush(S entity) {
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
    public List<SkillPersistence> findAll() {
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

    @Override public void deleteById(Long id) {}
    @Override public void delete(SkillPersistence entity) {}

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override public void deleteAll() {}
    @Override public void deleteAll(Iterable<? extends SkillPersistence> entities) {}

    @Override public <S extends SkillPersistence> List<S> saveAll(Iterable<S> entities) { return List.of(); }
    @Override public <S extends SkillPersistence> List<S> saveAllAndFlush(Iterable<S> entities) { return List.of(); }

    @Override public List<SkillPersistence> findAllById(Iterable<Long> ids) { return List.of(); }

    @Override public List<SkillPersistence> findAll(Sort sort) { return findAll(); }
    @Override public Page<SkillPersistence> findAll(Pageable pageable) { return Page.empty(); }

    @Override public void deleteAllInBatch() {}

    @Override @Deprecated
    public SkillPersistence getOne(Long aLong) {
        return null;
    }

    @Override @Deprecated
    public SkillPersistence getById(Long aLong) {
        return null;
    }

    @Override public void deleteAllInBatch(Iterable<SkillPersistence> entities) {}
    @Override public void deleteAllByIdInBatch(Iterable<Long> longs) {}


    @Override public SkillPersistence getReferenceById(Long aLong) { return null; }

    @Override
    public <S extends SkillPersistence> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends SkillPersistence> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends SkillPersistence> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends SkillPersistence> Page<S> findAll(Example<S> example, Pageable pageable) {
        return Page.empty();
    }

    @Override
    public <S extends SkillPersistence> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends SkillPersistence> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends SkillPersistence, R> R findBy(
            Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction
    ) {
        return null;
    }

    @Override
    public List<SkillPersistence> findByIdIn(List<Long> ids) {
        return List.of();
    }
}
