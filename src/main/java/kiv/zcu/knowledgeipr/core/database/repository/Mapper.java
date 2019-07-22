package kiv.zcu.knowledgeipr.core.database.repository;

public interface Mapper<From, To> {
    To map(From from);
}
