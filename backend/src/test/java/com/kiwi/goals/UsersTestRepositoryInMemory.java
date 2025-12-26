package com.kiwi.goals;

import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.features.users.data.UsersPersistence;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class UsersTestRepositoryInMemory implements UsersRepository {

    private final Map<Long, UsersPersistence> store = new HashMap<>();
    private long idSequence = 1;

    @Override
    public UsersPersistence save(UsersPersistence entity) {
        if (entity.getId() == null) {
            entity.setId(idSequence++);
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public UsersPersistence saveAndFlush(UsersPersistence entity) {
        return save(entity);
    }

    @Override
    public Optional<UsersPersistence> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<UsersPersistence> findByEmail(String email) {
        return store.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        return store.values().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }

    @Override
    public void deleteByEmail(String email) {
        store.values().removeIf(u -> u.getEmail().equals(email));
    }

    @Override
    public List<UsersPersistence> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public long count() {
        return store.size();
    }

    // Unused methods
    @Override
    public void flush() {}

    @Override
    public <S extends UsersPersistence> List<S> saveAll(Iterable<S> entities) {
        entities.forEach(this::save);
        return (List<S>) findAll();
    }

    @Override
    public <S extends UsersPersistence> List<S> saveAllAndFlush(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<UsersPersistence> entities) {}

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {}

    @Override
    public void deleteAllInBatch() {}

    @Override
    public UsersPersistence getOne(Long aLong) {
        return null;
    }

    @Override
    public UsersPersistence getById(Long aLong) {
        return null;
    }

    @Override
    public UsersPersistence getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public boolean existsById(Long aLong) {
        return store.containsKey(aLong);
    }

    @Override
    public List<UsersPersistence> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public void deleteById(Long aLong) {
        store.remove(aLong);
    }

    @Override
    public void delete(UsersPersistence entity) {
        store.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {}

    @Override
    public void deleteAll(Iterable<? extends UsersPersistence> entities) {}

    @Override
    public void deleteAll() {
        store.clear();
    }

    @Override
    public <S extends UsersPersistence> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends UsersPersistence> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends UsersPersistence> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends UsersPersistence> Page<S> findAll(Example<S> example, Pageable pageable) {
        return Page.empty();
    }

    @Override
    public <S extends UsersPersistence> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends UsersPersistence> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends UsersPersistence, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<UsersPersistence> findAll(Sort sort) {
        return findAll();
    }

    @Override
    public Page<UsersPersistence> findAll(Pageable pageable) {
        return Page.empty();
    }
}
