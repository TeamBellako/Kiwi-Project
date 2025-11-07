package com.kiwi.nodes;

import com.kiwi.features.nodes.controllers.UserNodeStatusRepository;
import com.kiwi.features.nodes.data.UserNodeStatusPersistence;
import com.kiwi.features.nodes.data.UserNodeStatusKey;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class UserNodeStatusTestRepositoryInMemory implements UserNodeStatusRepository {

    private final Map<UserNodeStatusKey, UserNodeStatusPersistence> store = new HashMap<>();

    @Override
    public Optional<UserNodeStatusPersistence> findByIdUserIdAndIdNodeId(int userId, int nodeId) {
        return Optional.ofNullable(store.get(new UserNodeStatusKey(userId, nodeId)));
    }

    @Override
    public <S extends UserNodeStatusPersistence> S save(S entity) {
        store.put(entity.getId(), entity);
        return entity;
    }

    public void saveUserStatus(UserNodeStatusPersistence s) {
        store.put(s.getId(), s);
    }

    @Override
    public <S extends UserNodeStatusPersistence> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<UserNodeStatusPersistence> findById(UserNodeStatusKey userNodeStatusKey) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(UserNodeStatusKey userNodeStatusKey) {
        return false;
    }

    public List<UserNodeStatusPersistence> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<UserNodeStatusPersistence> findAllById(Iterable<UserNodeStatusKey> userNodeStatusKeys) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(UserNodeStatusKey userNodeStatusKey) {

    }

    @Override
    public void delete(UserNodeStatusPersistence entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends UserNodeStatusKey> userNodeStatusKeys) {

    }

    @Override
    public void deleteAll(Iterable<? extends UserNodeStatusPersistence> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends UserNodeStatusPersistence> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends UserNodeStatusPersistence> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<UserNodeStatusPersistence> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UserNodeStatusKey> userNodeStatusKeys) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public UserNodeStatusPersistence getOne(UserNodeStatusKey userNodeStatusKey) {
        return null;
    }

    @Override
    public UserNodeStatusPersistence getById(UserNodeStatusKey userNodeStatusKey) {
        return null;
    }

    @Override
    public UserNodeStatusPersistence getReferenceById(UserNodeStatusKey userNodeStatusKey) {
        return null;
    }

    @Override
    public <S extends UserNodeStatusPersistence> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends UserNodeStatusPersistence> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends UserNodeStatusPersistence> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends UserNodeStatusPersistence> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends UserNodeStatusPersistence> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends UserNodeStatusPersistence> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends UserNodeStatusPersistence, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<UserNodeStatusPersistence> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<UserNodeStatusPersistence> findAll(Pageable pageable) {
        return null;
    }


}
