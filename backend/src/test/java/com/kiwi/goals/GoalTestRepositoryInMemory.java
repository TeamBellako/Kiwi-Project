package com.kiwi.goals;

import com.kiwi.features.goals.controllers.UserGoalStatusRepository;
import com.kiwi.features.goals.data.GoalCategory;
import com.kiwi.features.goals.data.GoalStatus;
import com.kiwi.features.goals.data.GoalType;
import com.kiwi.features.goals.data.UserGoalStatusPersistence;
import com.kiwi.features.users.data.UsersPersistence;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

public class GoalTestRepositoryInMemory implements UserGoalStatusRepository {

    private final Map<Long, UserGoalStatusPersistence> store = new HashMap<>();
    private long nextId = 1L;

    @Override
    public UserGoalStatusPersistence save(UserGoalStatusPersistence entity) {
        if (entity.getId() == null) {
            entity.setId(nextId++);
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public UserGoalStatusPersistence saveAndFlush(UserGoalStatusPersistence entity) {
        return save(entity);
    }

    @Override
    public Optional<UserGoalStatusPersistence> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<UserGoalStatusPersistence> findByUserAndGoal_CategoryOrderByDateDesc(
            UsersPersistence user, GoalCategory category) {
        return store.values().stream()
                .filter(g -> g.getUser().getId().equals(user.getId()))
                .filter(g -> g.getGoal().getCategory() == category)
                .sorted(Comparator.comparing(UserGoalStatusPersistence::getDate).reversed())
                .toList();
    }

    @Override
    public List<UserGoalStatusPersistence> findByUserAndDateAndGoal_Category(
            UsersPersistence user, LocalDate date, GoalCategory category) {
        return store.values().stream()
                .filter(g -> g.getUser().getId().equals(user.getId()))
                .filter(g -> g.getDate().equals(date))
                .filter(g -> g.getGoal().getCategory() == category)
                .toList();
    }

    @Override
    public List<UserGoalStatusPersistence> findByUserAndStatusAndDateBeforeAndGoal_CategoryOrderByDateDesc(
            UsersPersistence user, GoalStatus status, LocalDate date, GoalCategory category) {
        return store.values().stream()
                .filter(g -> g.getUser().getId().equals(user.getId()))
                .filter(g -> g.getStatus() == status)
                .filter(g -> g.getDate().isBefore(date))
                .filter(g -> g.getGoal().getCategory() == category)
                .sorted(Comparator.comparing(UserGoalStatusPersistence::getDate).reversed())
                .toList();
    }

    @Override
    public List<UserGoalStatusPersistence> findByUserAndGoal_Category(
            UsersPersistence user, GoalCategory category) {
        return store.values().stream()
                .filter(g -> g.getUser().getId().equals(user.getId()))
                .filter(g -> g.getGoal().getCategory() == category)
                .toList();
    }

    @Override
    public Optional<UserGoalStatusPersistence> findByIdAndUser(Long id, UsersPersistence user) {
        return store.values().stream()
                .filter(g -> g.getId().equals(id))
                .filter(g -> g.getUser().getId().equals(user.getId()))
                .findFirst();
    }

    @Override
    public Optional<UserGoalStatusPersistence> findFirstByUserAndGoal_TypeOrderByDateDesc(
            UsersPersistence user, GoalType type) {
        return store.values().stream()
                .filter(g -> g.getUser().getId().equals(user.getId()))
                .filter(g -> g.getGoal().getType() == type)
                .max(Comparator.comparing(UserGoalStatusPersistence::getDate));
    }

    @Override
    public void deleteByUser_IdAndGoal_Id(Long userId, Long goalId) {
        store.values().removeIf(g ->
                g.getUser().getId().equals(userId) && g.getGoal().getId().equals(goalId));
    }

    @Override
    public List<UserGoalStatusPersistence> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteAll(Iterable<? extends UserGoalStatusPersistence> entities) {
        entities.forEach(e -> store.remove(e.getId()));
    }

    @Override
    public long count() {
        return store.size();
    }

    @Override
    public void flush() {}

    @Override
    public <S extends UserGoalStatusPersistence> List<S> saveAll(Iterable<S> entities) {
        List<S> saved = new ArrayList<>();
        entities.forEach(e -> saved.add((S) this.save(e)));
        return saved;
    }

    @Override
    public <S extends UserGoalStatusPersistence> List<S> saveAllAndFlush(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<UserGoalStatusPersistence> entities) {}

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {}

    @Override
    public void deleteAllInBatch() {}

    @Override
    public UserGoalStatusPersistence getOne(Long id) { return null; }

    @Override
    public UserGoalStatusPersistence getById(Long id) { return null; }

    @Override
    public UserGoalStatusPersistence getReferenceById(Long id) { return null; }

    @Override
    public boolean existsById(Long id) {
        return store.containsKey(id);
    }

    @Override
    public List<UserGoalStatusPersistence> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public void delete(UserGoalStatusPersistence entity) {
        store.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {}

    @Override
    public void deleteAll() {
        store.clear();
    }

    @Override
    public <S extends UserGoalStatusPersistence> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends UserGoalStatusPersistence> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends UserGoalStatusPersistence> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends UserGoalStatusPersistence> Page<S> findAll(Example<S> example, Pageable pageable) {
        return Page.empty();
    }

    @Override
    public <S extends UserGoalStatusPersistence> long count(Example<S> example) { return 0; }

    @Override
    public <S extends UserGoalStatusPersistence> boolean exists(Example<S> example) { return false; }

    @Override
    public <S extends UserGoalStatusPersistence, R> R findBy(
            Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<UserGoalStatusPersistence> findAll(Sort sort) {
        return findAll();
    }

    @Override
    public Page<UserGoalStatusPersistence> findAll(Pageable pageable) {
        return Page.empty();
    }
}
