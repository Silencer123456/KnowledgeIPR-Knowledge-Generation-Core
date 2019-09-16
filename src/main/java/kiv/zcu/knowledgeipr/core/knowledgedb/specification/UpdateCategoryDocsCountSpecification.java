package kiv.zcu.knowledgeipr.core.knowledgedb.specification;

import java.util.ArrayList;
import java.util.List;

//Todo create common repository instead and run it there
public class UpdateCategoryDocsCountSpecification implements SqlSpecification {
    private String categoryName;
    private long totalDocs;

    public UpdateCategoryDocsCountSpecification(String categoryName, long totalDocs) {
        this.categoryName = categoryName;
    }

    @Override
    public SqlQuery toSqlQuery() {
        List<Object> parameters = new ArrayList<>();
        parameters.add(totalDocs);
        parameters.add(categoryName);
        return new SqlQuery(parameters,
                "UPDATE category SET totalDocuments = ?" +
                        "WHERE category.name = ?");
    }
}
