package com.kiwi.goals;

import com.kiwi.features.goals.data.SuggestedGoalPersistence;
import com.kiwi.features.goals.data.SuggestedGoalRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class SuggestedGoalTestRepositoryInMemory implements SuggestedGoalRepository {

    private final Map<String, SuggestedGoalPersistence> store = new HashMap<>();

    @Override
    public SuggestedGoalPersistence save(SuggestedGoalPersistence entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public SuggestedGoalPersistence saveAndFlush(SuggestedGoalPersistence entity) {
        return save(entity);
    }

    @Override
    public Optional<SuggestedGoalPersistence> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<SuggestedGoalPersistence> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteAll(Iterable<? extends SuggestedGoalPersistence> entities) {
        entities.forEach(e -> store.remove(e.getId()));
    }

    @Override
    public long count() {
        return store.size();
    }

    // Unused methods
    @Override
    public void flush() {}

    @Override
    public <S extends SuggestedGoalPersistence> List<S> saveAll(Iterable<S> entities) {
        entities.forEach(this::save);
        return (List<S>) findAll();
    }

    @Override
    public <S extends SuggestedGoalPersistence> List<S> saveAllAndFlush(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<SuggestedGoalPersistence> entities) {}

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {}

    @Override
    public void deleteAllInBatch() {}

    @Override
    public SuggestedGoalPersistence getOne(String s) {
        return null;
    }

    @Override
    public SuggestedGoalPersistence getById(String s) {
        return null;
    }

    @Override
    public SuggestedGoalPersistence getReferenceById(String s) {
        return null;
    }

    @Override
    public boolean existsById(String s) {
        return store.containsKey(s);
    }

    @Override
    public List<SuggestedGoalPersistence> findAllById(Iterable<String> strings) {
        return List.of();
    }

    @Override
    public void deleteById(String s) {
        store.remove(s);
    }

    @Override
    public void delete(SuggestedGoalPersistence entity) {
        store.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {}

    @Override
    public void deleteAll() {
        store.clear();
    }

    @Override
    public <S extends SuggestedGoalPersistence> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends SuggestedGoalPersistence> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends SuggestedGoalPersistence> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends SuggestedGoalPersistence> Page<S> findAll(Example<S> example, Pageable pageable) {
        return Page.empty();
    }

    @Override
    public <S extends SuggestedGoalPersistence> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends SuggestedGoalPersistence> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends SuggestedGoalPersistence, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<SuggestedGoalPersistence> findAll(Sort sort) {
        return findAll();
    }

    @Override
    public Page<SuggestedGoalPersistence> findAll(Pageable pageable) {
        return Page.empty();
    }
}
