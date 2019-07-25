package kiv.zcu.knowledgeipr.core.database.specification;

public class QueryByHashCodeSpecification implements SqlSpecification {

    private int hash;

    public QueryByHashCodeSpecification(int hash) {
        this.hash = hash;
    }

    @Override
    public String toSqlQuery() {

        return String.format("SELECT * FROM %1$s WHERE `%2$s` = `%3$s`",
                "query",
                "hashCode",
                hash);
    }
}
