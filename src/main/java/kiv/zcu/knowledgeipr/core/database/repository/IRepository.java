package kiv.zcu.knowledgeipr.core.database.repository;

import kiv.zcu.knowledgeipr.core.database.specification.Specification;

import java.sql.Connection;
import java.util.List;

// TODO: !!!CHANGE connection parameters
public interface IRepository<T> {
    /**
     * Adds an item to the repository.
     *
     * @param item - item to be inserted
     * @return id of the inserted record
     */
    long add(Connection connection, T item);

    long add(Connection connection, Iterable<T> items);

    void update(Connection connection, T item);

    void remove(Connection connection, T item);

    void remove(Connection connection, Specification specification);

    void removeAll(Connection connection);

    T getById(int id);

    List<T> query(Connection connection, Specification specification);
}
