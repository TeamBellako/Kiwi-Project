package com.kiwi.combat;

import com.kiwi.features.combat.data.persistence.CombatActiveStatusPersistence;
import com.kiwi.features.combat.repositories.CombatActiveStatusesRepository;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class CombatActiveStatusesTestRepositoryInMemory implements CombatActiveStatusesRepository {

    private final Map<Long, CombatActiveStatusPersistence> data = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public List<CombatActiveStatusPersistence> findByCombatId(Long combatId) {

        return data.values().stream()
                .filter(status -> Objects.equals(status.getCombatId(), combatId))
                .toList();
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends CombatActiveStatusPersistence> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends CombatActiveStatusPersistence> List<S> saveAllAndFlush(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<CombatActiveStatusPersistence> entities) {
        deleteAll(entities);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {
        deleteAllById(longs);
    }

    @Override
    public void deleteAllInBatch() {
        deleteAll();
    }

    @Override
    public CombatActiveStatusPersistence getOne(Long aLong) {
        return getReferenceById(aLong);
    }

    @Override
    public CombatActiveStatusPersistence getById(Long aLong) {
        return getReferenceById(aLong);
    }

    @Override
    public CombatActiveStatusPersistence getReferenceById(Long aLong) {
        return data.get(aLong);
    }

    @Override
    public <S extends CombatActiveStatusPersistence> Optional<S> findOne(Example<S> example) {
        return findAll(example).stream().findFirst();
    }

    @Override
    public <S extends CombatActiveStatusPersistence> List<S> findAll(Example<S> example) {

        return data.values().stream()
                .filter(example.getProbeType()::isInstance)
                .map(example.getProbeType()::cast)
                .toList();
    }

    @Override
    public <S extends CombatActiveStatusPersistence> List<S> findAll(Example<S> example, Sort sort) {
        return findAll(example);
    }

    @Override
    public <S extends CombatActiveStatusPersistence> Page<S> findAll(Example<S> example, Pageable pageable) {

        List<S> content = findAll(example);

        return new PageImpl<>(content, pageable, content.size());
    }

    @Override
    public <S extends CombatActiveStatusPersistence> long count(Example<S> example) {
        return findAll(example).size();
    }

    @Override
    public <S extends CombatActiveStatusPersistence> boolean exists(Example<S> example) {
        return !findAll(example).isEmpty();
    }

    @Override
    public <S extends CombatActiveStatusPersistence, R> R findBy(
            Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {

        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends CombatActiveStatusPersistence> S save(S entity) {

        if (entity.getId() == null) {
            entity.setId(nextId.getAndIncrement());
        }

        data.put(entity.getId(), entity);

        return entity;
    }

    @Override
    public <S extends CombatActiveStatusPersistence> List<S> saveAll(Iterable<S> entities) {

        List<S> result = new ArrayList<>();

        for (S entity : entities) {
            result.add(save(entity));
        }

        return result;
    }

    @Override
    public Optional<CombatActiveStatusPersistence> findById(Long aLong) {
        return Optional.ofNullable(data.get(aLong));
    }

    @Override
    public boolean existsById(Long aLong) {
        return data.containsKey(aLong);
    }

    @Override
    public List<CombatActiveStatusPersistence> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<CombatActiveStatusPersistence> findAllById(Iterable<Long> longs) {

        List<CombatActiveStatusPersistence> result = new ArrayList<>();

        for (Long id : longs) {

            CombatActiveStatusPersistence entity = data.get(id);

            if (entity != null) {
                result.add(entity);
            }
        }

        return result;
    }

    @Override
    public long count() {
        return data.size();
    }

    @Override
    public void deleteById(Long aLong) {
        data.remove(aLong);
    }

    @Override
    public void delete(CombatActiveStatusPersistence entity) {

        if (entity != null && entity.getId() != null) {
            data.remove(entity.getId());
        }
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

        for (Long id : longs) {
            data.remove(id);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends CombatActiveStatusPersistence> entities) {

        for (CombatActiveStatusPersistence entity : entities) {
            delete(entity);
        }
    }

    @Override
    public void deleteAll() {
        data.clear();
    }

    @Override
    public List<CombatActiveStatusPersistence> findAll(Sort sort) {
        return findAll();
    }

    @Override
    public Page<CombatActiveStatusPersistence> findAll(Pageable pageable) {

        List<CombatActiveStatusPersistence> content = findAll();

        return new PageImpl<>(
                content,
                pageable,
                content.size()
        );
    }
}