package kiv.zcu.knowledgeipr.core.knowledgedb.mapper;

public interface Mapper<From, To> {
    To map(From from) throws Exception;
}
