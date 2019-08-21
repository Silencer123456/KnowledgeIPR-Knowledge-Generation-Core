package kiv.zcu.knowledgeipr.core.database.specification;

import java.util.ArrayList;
import java.util.List;

public class RecordsWithConfirmedCategorySpecification implements SqlSpecification {

    private String categoryName;

    public RecordsWithConfirmedCategorySpecification(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public SqlQuery toSqlQuery() {
        List<Object> parameters = new ArrayList<>();
        parameters.add(categoryName);
        return new SqlQuery(parameters,
                "SELECT ref.* FROM reference as ref " +
                        "INNER JOIN categoryreferences " +
                        "ON categoryreferences.referenceId = ref.referenceId " +
                        "INNER JOIN category " +
                        "ON category.categoryId = categoryreferences.categoryId " +
                        "WHERE category.name = ?");
    }
}
