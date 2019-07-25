package kiv.zcu.knowledgeipr.core.database.mapper;

public interface Mapper<From, To> {
    To map(From from) throws Exception;
}
