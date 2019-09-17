package kiv.zcu.knowledgeipr.core.knowledgedb.repository;

import kiv.zcu.knowledgeipr.core.knowledgedb.specification.Specification;

import java.sql.SQLException;
import java.util.List;

// TODO: !!! Create generic repository
public interface IRepository<T> {
    /**
     * Adds an item to the repository.
     *
     * @param item - item to be inserted
     * @return id of the inserted record
     */
    long add(T item) throws SQLException;

    long add(Iterable<T> items) throws SQLException;

    void update(T item);

    void remove(T item);

    void remove(Specification specification);

    /**
     * Removes all data from the table in database
     */
    void removeAll();

    T getById(int id);

    List<T> query(Specification specification);
}
