package kiv.zcu.knowledgeipr.core.database.repository;

import kiv.zcu.knowledgeipr.core.database.specification.Specification;

import java.util.List;

// TODO: !!!CHANGE connection parameters
public interface IRepository<T> {
    /**
     * Adds an item to the repository.
     *
     * @param item - item to be inserted
     * @return id of the inserted record
     */
    long add(T item);

    long add(Iterable<T> items);

    void update(T item);

    void remove(T item);

    void remove(Specification specification);

    void removeAll();

    T getById(int id);

    List<T> query(Specification specification);
}
