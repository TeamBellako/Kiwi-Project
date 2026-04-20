package com.kiwi.goals;

import com.kiwi.features.goals.controllers.GoalRepository;
import com.kiwi.features.goals.data.GoalCategory;
import com.kiwi.features.goals.data.GoalPersistence;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class GoalDefinitionRepositoryInMemory implements GoalRepository {

    private final Map<Long, GoalPersistence> store = new HashMap<>();
    private long nextId = 1L;

    @Override
    public GoalPersistence save(GoalPersistence entity) {
        if (entity.getId() == null) {
            entity.setId(nextId++);
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public GoalPersistence saveAndFlush(GoalPersistence entity) {
        return save(entity);
    }

    @Override
    public Optional<GoalPersistence> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<GoalPersistence> findByCategory(GoalCategory category) {
        return store.values().stream()
                .filter(g -> g.getCategory() == category)
                .toList();
    }

    @Override
    public List<GoalPersistence> findTwoRandom() {
        List<GoalPersistence> all = new ArrayList<>(store.values());
        Collections.shuffle(all);
        return all.stream().limit(2).toList();
    }

    @Override
    public List<GoalPersistence> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteAll(Iterable<? extends GoalPersistence> entities) {
        entities.forEach(e -> store.remove(e.getId()));
    }

    @Override
    public long count() {
        return store.size();
    }

    @Override
    public void flush() {}

    @Override
    public <S extends GoalPersistence> List<S> saveAll(Iterable<S> entities) {
        List<S> saved = new ArrayList<>();
        entities.forEach(e -> saved.add((S) this.save(e)));
        return saved;
    }

    @Override
    public <S extends GoalPersistence> List<S> saveAllAndFlush(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<GoalPersistence> entities) {}

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {}

    @Override
    public void deleteAllInBatch() {}

    @Override
    public GoalPersistence getOne(Long id) { return null; }

    @Override
    public GoalPersistence getById(Long id) { return null; }

    @Override
    public GoalPersistence getReferenceById(Long id) { return null; }

    @Override
    public boolean existsById(Long id) {
        return store.containsKey(id);
    }

    @Override
    public List<GoalPersistence> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public void delete(GoalPersistence entity) {
        store.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {}

    @Override
    public void deleteAll() {
        store.clear();
    }

    @Override
    public <S extends GoalPersistence> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends GoalPersistence> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends GoalPersistence> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends GoalPersistence> Page<S> findAll(Example<S> example, Pageable pageable) {
        return Page.empty();
    }

    @Override
    public <S extends GoalPersistence> long count(Example<S> example) { return 0; }

    @Override
    public <S extends GoalPersistence> boolean exists(Example<S> example) { return false; }

    @Override
    public <S extends GoalPersistence, R> R findBy(
            Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<GoalPersistence> findAll(Sort sort) {
        return findAll();
    }

    @Override
    public Page<GoalPersistence> findAll(Pageable pageable) {
        return Page.empty();
    }
}
