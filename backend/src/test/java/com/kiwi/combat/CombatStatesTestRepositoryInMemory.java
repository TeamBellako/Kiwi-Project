package com.kiwi.combat;

import com.kiwi.features.combat.data.persistence.CombatStatePersistence;
import com.kiwi.features.combat.repositories.CombatStatesRepository;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class CombatStatesTestRepositoryInMemory implements CombatStatesRepository {

    private final Map<Long, CombatStatePersistence> data = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public List<CombatStatePersistence> findByIdIn(List<Long> ids) {
        return ids.stream()
                .map(data::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends CombatStatePersistence> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends CombatStatePersistence> List<S> saveAllAndFlush(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<CombatStatePersistence> entities) {
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
    public CombatStatePersistence getOne(Long aLong) {
        return getReferenceById(aLong);
    }

    @Override
    public CombatStatePersistence getById(Long aLong) {
        return getReferenceById(aLong);
    }

    @Override
    public CombatStatePersistence getReferenceById(Long aLong) {
        return data.get(aLong);
    }

    @Override
    public <S extends CombatStatePersistence> Optional<S> findOne(Example<S> example) {
        return findAll(example).stream().findFirst();
    }

    @Override
    public <S extends CombatStatePersistence> List<S> findAll(Example<S> example) {
        return data.values().stream()
                .filter(example.getProbeType()::isInstance)
                .map(example.getProbeType()::cast)
                .toList();
    }

    @Override
    public <S extends CombatStatePersistence> List<S> findAll(Example<S> example, Sort sort) {
        return findAll(example);
    }

    @Override
    public <S extends CombatStatePersistence> Page<S> findAll(Example<S> example, Pageable pageable) {
        List<S> content = findAll(example);
        return new PageImpl<>(content, pageable, content.size());
    }

    @Override
    public <S extends CombatStatePersistence> long count(Example<S> example) {
        return findAll(example).size();
    }

    @Override
    public <S extends CombatStatePersistence> boolean exists(Example<S> example) {
        return !findAll(example).isEmpty();
    }

    @Override
    public <S extends CombatStatePersistence, R> R findBy(
            Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends CombatStatePersistence> S save(S entity) {

        if (entity.getId() == null) {
            entity.setId(nextId.getAndIncrement());
        }

        data.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends CombatStatePersistence> List<S> saveAll(Iterable<S> entities) {

        List<S> result = new ArrayList<>();

        for (S entity : entities) {
            result.add(save(entity));
        }

        return result;
    }

    @Override
    public Optional<CombatStatePersistence> findById(Long aLong) {
        return Optional.ofNullable(data.get(aLong));
    }

    @Override
    public boolean existsById(Long aLong) {
        return data.containsKey(aLong);
    }

    @Override
    public List<CombatStatePersistence> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<CombatStatePersistence> findAllById(Iterable<Long> longs) {

        List<CombatStatePersistence> result = new ArrayList<>();

        for (Long id : longs) {
            CombatStatePersistence entity = data.get(id);

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
    public void delete(CombatStatePersistence entity) {

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
    public void deleteAll(Iterable<? extends CombatStatePersistence> entities) {

        for (CombatStatePersistence entity : entities) {
            delete(entity);
        }
    }

    @Override
    public void deleteAll() {
        data.clear();
    }

    @Override
    public List<CombatStatePersistence> findAll(Sort sort) {
        return findAll();
    }

    @Override
    public Page<CombatStatePersistence> findAll(Pageable pageable) {

        List<CombatStatePersistence> content = findAll();

        return new PageImpl<>(
                content,
                pageable,
                content.size()
        );
    }
}