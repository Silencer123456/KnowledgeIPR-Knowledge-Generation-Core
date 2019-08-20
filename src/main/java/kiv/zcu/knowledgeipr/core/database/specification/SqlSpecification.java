package kiv.zcu.knowledgeipr.core.database.specification;

public interface SqlSpecification extends Specification {
    SqlQuery toSqlQuery();
}
