package kiv.zcu.knowledgeipr.core.knowledgedb.mapper;

import kiv.zcu.knowledgeipr.api.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.core.knowledgedb.dto.QueryDto;
import kiv.zcu.knowledgeipr.core.model.search.Query;
import kiv.zcu.knowledgeipr.utils.SerializationUtils;

public class QueryToQueryDtoMapper implements Mapper<Query, QueryDto> {
    @Override
    public QueryDto map(Query query) throws ObjectSerializationException {
        return new QueryDto(query.hashCode(), SerializationUtils.serializeObject(query), "test");
    }
}
