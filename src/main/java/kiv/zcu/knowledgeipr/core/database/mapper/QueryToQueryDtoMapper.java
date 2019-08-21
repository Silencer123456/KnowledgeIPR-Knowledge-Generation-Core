package kiv.zcu.knowledgeipr.core.database.mapper;

import kiv.zcu.knowledgeipr.core.database.dto.QueryDto;
import kiv.zcu.knowledgeipr.core.search.Query;
import kiv.zcu.knowledgeipr.core.utils.SerializationUtils;
import kiv.zcu.knowledgeipr.rest.errorhandling.ObjectSerializationException;

public class QueryToQueryDtoMapper implements Mapper<Query, QueryDto> {
    @Override
    public QueryDto map(Query query) throws ObjectSerializationException {
        return new QueryDto(query.hashCode(), SerializationUtils.serializeObject(query), "test");
    }
}
