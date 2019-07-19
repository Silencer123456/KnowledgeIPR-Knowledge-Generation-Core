package kiv.zcu.knowledgeipr.core.database.repository;

public interface SqlSpecification extends Specification {
    String toSqlQuery();
}
