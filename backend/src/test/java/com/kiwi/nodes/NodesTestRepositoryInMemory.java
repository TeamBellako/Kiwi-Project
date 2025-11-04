package com.kiwi.nodes;

import com.kiwi.features.nodes.controllers.NodesRepository;
import com.kiwi.features.nodes.data.NodesPersistence;
import com.kiwi.features.nodes.data.UserNodeStatusPersistence;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class NodesTestRepositoryInMemory implements NodesRepository {

    private final Map<Long, NodesPersistence> nodeStore = new HashMap<>();
    private final Map<String, UserNodeStatusPersistence> statusStore = new HashMap<>();

    private long idSequence = 1;

    @Override
    public <S extends NodesPersistence> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    // ---- NODE ----
    public List<NodesPersistence> findAll() {
        return new ArrayList<>(nodeStore.values());
    }

    @Override
    public List<NodesPersistence> findAllById(Iterable<Long> longs) {
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
    public void delete(NodesPersistence entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends NodesPersistence> entities) {

    }

    @Override
    public void deleteAll() {

    }

    public Optional<NodesPersistence> findById(Long id) {
        return Optional.ofNullable(nodeStore.get(id));
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    public NodesPersistence save(NodesPersistence entity) {
        if (entity.getId() == null) {
            entity.setId(idSequence++);
        }
        nodeStore.put(entity.getId(), entity);
        return entity;
    }

    // ---- USER NODE STATUS ----
    public Optional<UserNodeStatusPersistence> findUserStatus(Long userId, Long nodeId) {
        return Optional.ofNullable(statusStore.get(userId + "-" + nodeId));
    }

    public void saveUserStatus(UserNodeStatusPersistence s) {
        statusStore.put(s.getId().getUserId() + "-" + s.getId().getNodeId(), s);
    }


    @Override
    public void flush() {

    }

    @Override
    public <S extends NodesPersistence> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends NodesPersistence> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<NodesPersistence> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public NodesPersistence getOne(Long aLong) {
        return null;
    }

    @Override
    public NodesPersistence getById(Long aLong) {
        return null;
    }

    @Override
    public NodesPersistence getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends NodesPersistence> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends NodesPersistence> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends NodesPersistence> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends NodesPersistence> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends NodesPersistence> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends NodesPersistence> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends NodesPersistence, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<NodesPersistence> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<NodesPersistence> findAll(Pageable pageable) {
        return null;
    }
}