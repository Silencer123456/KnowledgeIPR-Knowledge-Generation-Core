package kiv.zcu.knowledgeipr.core.database.specification;

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
//
//        return String.format("SELECT * FROM %1$s WHERE `%2$s` = `%3$s`",
//                "query",
//                "hashCode",
//                hash);
    }
}
