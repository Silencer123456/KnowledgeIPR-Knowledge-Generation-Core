package kiv.zcu.knowledgeipr.core.database.repository.specification;

public interface SqlSpecification extends Specification {
    String toSqlQuery();
}
