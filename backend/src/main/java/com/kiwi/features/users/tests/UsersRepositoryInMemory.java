package com.kiwi.features.users.tests;

import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.features.users.data.UsersPersistence;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class UsersRepositoryInMemory implements UsersRepository {
    private final HashMap<String, UsersPersistence> users = new HashMap<String, UsersPersistence>();

    @Override
    public <S extends UsersPersistence> S saveAndFlush(S entity) {
        return (S) users.put(entity.getEmail().value(), entity);
    }

    @Override
    public Optional<UsersPersistence> findByEmail(String email) {
        return Optional.ofNullable(users.get(email));
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.containsKey(email);
    }

    @Override
    public void deleteByEmail(String email) {
        if (!existsByEmail(email)) throw new IllegalArgumentException("User not found");
        
        users.remove(email);
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends UsersPersistence> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<UsersPersistence> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

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
        return null;
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
    public <S extends UsersPersistence> S save(S entity) {
        return null;
    }

    @Override
    public <S extends UsersPersistence> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<UsersPersistence> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<UsersPersistence> findAll() {
        return List.of();
    }

    @Override
    public List<UsersPersistence> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(UsersPersistence entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends UsersPersistence> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<UsersPersistence> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<UsersPersistence> findAll(Pageable pageable) {
        return null;
    }
}
