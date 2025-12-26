package com.kiwi.goals;

import com.kiwi.features.goals.data.GoalPersistence;
import com.kiwi.features.goals.data.GoalRepository;
import com.kiwi.features.users.data.UsersPersistence;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GoalTestRepositoryInMemory implements GoalRepository {

    private final Map<String, GoalPersistence> store = new HashMap<>();

    @Override
    public GoalPersistence save(GoalPersistence entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public GoalPersistence saveAndFlush(GoalPersistence entity) {
        return save(entity);
    }

    @Override
    public Optional<GoalPersistence> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<GoalPersistence> findByUserAndDate(UsersPersistence user, LocalDate date) {
        return store.values().stream()
                .filter(g -> g.getUser().getId().equals(user.getId()))
                .filter(g -> g.getDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public List<GoalPersistence> findByUserOrderByDateDesc(UsersPersistence user) {
        return store.values().stream()
                .filter(g -> g.getUser().getId().equals(user.getId()))
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<GoalPersistence> findByIdAndUser(String id, UsersPersistence user) {
        return store.values().stream()
                .filter(g -> g.getId().equals(id))
                .filter(g -> g.getUser().getId().equals(user.getId()))
                .findFirst();
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

    // Unused methods
    @Override
    public void flush() {}

    @Override
    public <S extends GoalPersistence> List<S> saveAll(Iterable<S> entities) {
        entities.forEach(this::save);
        return (List<S>) findAll();
    }

    @Override
    public <S extends GoalPersistence> List<S> saveAllAndFlush(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<GoalPersistence> entities) {}

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {}

    @Override
    public void deleteAllInBatch() {}

    @Override
    public GoalPersistence getOne(String s) {
        return null;
    }

    @Override
    public GoalPersistence getById(String s) {
        return null;
    }

    @Override
    public GoalPersistence getReferenceById(String s) {
        return null;
    }

    @Override
    public boolean existsById(String s) {
        return store.containsKey(s);
    }

    @Override
    public List<GoalPersistence> findAllById(Iterable<String> strings) {
        return List.of();
    }

    @Override
    public void deleteById(String s) {
        store.remove(s);
    }

    @Override
    public void delete(GoalPersistence entity) {
        store.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {}

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
    public <S extends GoalPersistence> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends GoalPersistence> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends GoalPersistence, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
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
