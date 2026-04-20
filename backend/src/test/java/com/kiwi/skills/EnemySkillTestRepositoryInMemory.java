package com.kiwi.skills;

import com.kiwi.features.skills.controllers.EnemySkillRepository;
import com.kiwi.features.skills.data.persistence.EnemySkillKey;
import com.kiwi.features.skills.data.persistence.EnemySkillPersistence;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class EnemySkillTestRepositoryInMemory implements EnemySkillRepository {

    private final List<EnemySkillPersistence> store = new ArrayList<>();

    @Override
    public List<EnemySkillPersistence> findByEnemy_Id(Long enemyId) {
        return store.stream()
                .filter(e -> e.getEnemy().getId().equals(enemyId))
                .toList();
    }

    @Override
    public <S extends EnemySkillPersistence> S save(S entity) {
        store.add(entity);
        return entity;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends EnemySkillPersistence> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends EnemySkillPersistence> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<EnemySkillPersistence> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<EnemySkillKey> enemySkillKeys) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override @Deprecated
    public EnemySkillPersistence getOne(EnemySkillKey enemySkillKey) {
        return null;
    }

    @Override @Deprecated
    public EnemySkillPersistence getById(EnemySkillKey enemySkillKey) {
        return null;
    }

    @Override
    public EnemySkillPersistence getReferenceById(EnemySkillKey enemySkillKey) {
        return null;
    }

    @Override
    public <S extends EnemySkillPersistence> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends EnemySkillPersistence> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends EnemySkillPersistence> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends EnemySkillPersistence> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends EnemySkillPersistence> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends EnemySkillPersistence> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends EnemySkillPersistence, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends EnemySkillPersistence> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<EnemySkillPersistence> findById(EnemySkillKey enemySkillKey) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(EnemySkillKey enemySkillKey) {
        return false;
    }

    @Override
    public List<EnemySkillPersistence> findAll() {
        return List.of();
    }

    @Override
    public List<EnemySkillPersistence> findAllById(Iterable<EnemySkillKey> enemySkillKeys) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(EnemySkillKey enemySkillKey) {

    }

    @Override
    public void delete(EnemySkillPersistence entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends EnemySkillKey> enemySkillKeys) {

    }

    @Override
    public void deleteAll(Iterable<? extends EnemySkillPersistence> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<EnemySkillPersistence> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<EnemySkillPersistence> findAll(Pageable pageable) {
        return null;
    }
}
