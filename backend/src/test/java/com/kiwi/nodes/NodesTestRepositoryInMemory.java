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

    private final Map<Integer, NodesPersistence> nodeStore = new HashMap<>();
    private final Map<String, UserNodeStatusPersistence> statusStore = new HashMap<>();

    private long idSequence = 1;

    // NodesTestRepositoryInMemory
    @Override
    public List<NodesPersistence> findAll() { return new ArrayList<>(nodeStore.values()); }

    @Override
    public Optional<NodesPersistence> findById(Integer id) { return Optional.ofNullable(nodeStore.get(id)); }

    @Override
    public NodesPersistence saveAndFlush(NodesPersistence node) { nodeStore.put(node.getId(), node); return node; }

    @Override
    public List<NodesPersistence> findAllByNodeOrder(int nodeOrder) { return nodeStore.values().stream().filter(n -> n.getNodeOrder() == nodeOrder).collect(Collectors.toList()); }


    @Override
    public <S extends NodesPersistence> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }



    @Override
    public boolean existsById(Integer integer) {
        return false;
    }


    @Override
    public List<NodesPersistence> findAllById(Iterable<Integer> integers) {
        return List.of();
    }


    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Integer integer) {

    }


    @Override
    public void delete(NodesPersistence entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> integers) {

    }

    @Override
    public void deleteAll(Iterable<? extends NodesPersistence> entities) {

    }

    @Override
    public void deleteAll() {

    }

    public Optional<NodesPersistence> findById(int id) {
        return Optional.ofNullable(nodeStore.get(id));
    }


    @Override
    public <S extends NodesPersistence> S save(S entity) {
        return null;
    }

    // ---- USER NODE STATUS ----
    public Optional<UserNodeStatusPersistence> findUserStatus(int userId, int nodeId) {
        return Optional.ofNullable(statusStore.get(userId + "-" + nodeId));
    }

    public void saveUserStatus(UserNodeStatusPersistence s) {
        statusStore.put(s.getId().getUserId() + "-" + s.getId().getNodeId(), s);
    }


    @Override
    public void flush() {

    }

    @Override
    public <S extends NodesPersistence> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteInBatch(Iterable<NodesPersistence> entities) {
        NodesRepository.super.deleteInBatch(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<NodesPersistence> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Integer> integers) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public NodesPersistence getOne(Integer integer) {
        return null;
    }

    @Override
    public NodesPersistence getById(Integer integer) {
        return null;
    }

    @Override
    public NodesPersistence getReferenceById(Integer integer) {
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