package com.kiwi.skills;

import com.kiwi.features.combat.data.persistence.CombatElementPersistence;
import com.kiwi.features.combat.repositories.CombatElementRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class CombatElementTestRepositoryInMemory implements CombatElementRepository {

    private final Map<Long, CombatElementPersistence> store = new HashMap<>();

    @Override
    public Optional<CombatElementPersistence> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public <S extends CombatElementPersistence> S save(S entity){
        CombatElementPersistence e = new CombatElementPersistence();
        e.setId(entity.getId());
        e.setName(entity.getName());
        store.put(entity.getId(), e);

        return entity;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends CombatElementPersistence> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends CombatElementPersistence> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<CombatElementPersistence> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public CombatElementPersistence getOne(Long aLong) {
        return null;
    }

    @Override
    public CombatElementPersistence getById(Long aLong) {
        return null;
    }

    @Override
    public CombatElementPersistence getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends CombatElementPersistence> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends CombatElementPersistence> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends CombatElementPersistence> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends CombatElementPersistence> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends CombatElementPersistence> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends CombatElementPersistence> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends CombatElementPersistence, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends CombatElementPersistence> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public List<CombatElementPersistence> findAll() {
        return List.of();
    }

    @Override
    public List<CombatElementPersistence> findAllById(Iterable<Long> longs) {
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
    public void delete(CombatElementPersistence entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends CombatElementPersistence> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<CombatElementPersistence> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<CombatElementPersistence> findAll(Pageable pageable) {
        return null;
    }

    // resto métodos vacíos
}