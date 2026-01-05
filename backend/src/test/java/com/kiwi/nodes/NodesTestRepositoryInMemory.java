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
import java.util.stream.Collectors;

public class NodesTestRepositoryInMemory implements NodesRepository {

    private final Map<Long, NodesPersistence> nodeStore = new HashMap<>();

    @Override
    public <S extends NodesPersistence> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public List<NodesPersistence> findAll() {
        return new ArrayList<>(nodeStore.values());
    }

    @Override
    public List<NodesPersistence> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public Optional<NodesPersistence> findById(Long id) {
        return Optional.ofNullable(nodeStore.get(id));
    }

    @Override
    public NodesPersistence saveAndFlush(NodesPersistence node) {
        if (node.getId() == null) {
            node.setId(generateNextId());
        }
        nodeStore.put(node.getId(), node);
        return node;
    }

    @Override
    public boolean existsById(Long id) {
        return nodeStore.containsKey(id);
    }

    // Métodos irrelevantes para el test
    @Override public <S extends NodesPersistence> S save(S entity) { return (S) saveAndFlush(entity); }
    @Override public void flush() { }

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

    @Override public long count() { return nodeStore.size(); }

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

    private long nextId = 1;
    private long generateNextId() {
        return nextId++;
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