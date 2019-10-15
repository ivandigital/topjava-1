package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private Map<Integer, User> repository = new ConcurrentHashMap<>();
    private AtomicInteger idCounter = new AtomicInteger(0);
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);

    // static initializer
    {
        Arrays.asList(
                new User("Ivan Ivanov", "ivanos@mail.ru", "ii123456", Role.ROLE_ADMIN),
                new User("Петр Петров", "petrov@mail.ru", "pp123456", Role.ROLE_USER),
                new User("Fedor Fedorov", "fedorov@mail.ru", "ff123456", Role.ROLE_USER),
                new User("Сидор Сидоров", "sidorov@mail.ru", "ss123456", Role.ROLE_USER),
                new User("Маша Машина", "mashina@mail.ru", "mm123456", Role.ROLE_USER)
        ).forEach(this::save);
    }

    @Override
    public boolean delete(int id) {
        log.debug("delete {}", id);
        if (repository.containsKey(id)) {
            Object rezult = repository.remove(id);
            return (rezult != null);
        }
        else {
            log.warn("Cannot find user [id={}}]", id);
            return false;
        }
    }

    @Override
    public User save(User user) {
        log.debug("save {}", user);
        if (user.isNew()) {
            int newId = idCounter.incrementAndGet();
            log.debug("creating new user with id {} ...", newId);
            user.setId(newId);
            repository.put(newId, user);
        }
        else {
            log.debug("updating existing user...");
            repository.put(user.getId(), user);
        }
        return user;
    }

    @Override
    public User get(int id) {
        log.info("get {}", id);
        return repository.get(id);
    }

    @Override
    public List<User> getAll() {
        log.info("getAll");
        return repository.values().stream()
                .sorted(Comparator.comparing(User::getName))
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList()
        );
    }

    @Override
    public User getByEmail(String email) {
        log.info("getByEmail {}", email);
        if (email == null)
            return null;
        //end if
        return repository.values().stream()
                .filter(user -> email.equalsIgnoreCase(user.getEmail()))
                .findFirst()
                .orElse(null)
        ;
    }

}
