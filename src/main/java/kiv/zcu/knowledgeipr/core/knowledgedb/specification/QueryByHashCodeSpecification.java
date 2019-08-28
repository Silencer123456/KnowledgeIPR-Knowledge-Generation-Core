package kiv.zcu.knowledgeipr.core.knowledgedb.specification;

import java.util.ArrayList;
import java.util.List;

public class QueryByHashCodeSpecification implements SqlSpecification {

    private int hash;

    public QueryByHashCodeSpecification(int hash) {
        this.hash = hash;
    }

    @Override
    public SqlQuery toSqlQuery() {
        List<Object> parameters = new ArrayList<>();
        parameters.add(hash);
        return new SqlQuery(parameters, "SELECT * FROM query WHERE hashCode = ?");
    }
}
