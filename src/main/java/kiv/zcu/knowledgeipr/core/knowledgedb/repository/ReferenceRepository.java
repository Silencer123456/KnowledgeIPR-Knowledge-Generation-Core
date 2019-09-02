package kiv.zcu.knowledgeipr.core.knowledgedb.repository;

import kiv.zcu.knowledgeipr.core.knowledgedb.dto.ReferenceDto;
import kiv.zcu.knowledgeipr.core.knowledgedb.specification.Specification;

import java.util.List;


public class ReferenceRepository extends BasicRepository<ReferenceDto> {

    public ReferenceRepository() {
        super("reference");
    }

    @Override
    public long add(Iterable<ReferenceDto> items) {
        return 0;
    }

    @Override
    public void update(ReferenceDto item) {

    }

    @Override
    public void remove(ReferenceDto item) {

    }

    @Override
    public void remove(Specification specification) {

    }

    @Override
    public ReferenceDto getById(int id) {
        return null;
    }

    @Override
    public List<ReferenceDto> query(Specification specification) {
        return queryGeneric(specification, ReferenceDto.class);
    }
}
