package kiv.zcu.knowledgeipr.core.database.specification;

/**
 * Abstracts a single SQL query
 */
public interface SqlSpecification extends Specification {
    /**
     * Generates the SqlQuery object containing the SQL query along
     * with other information
     *
     * @return Created SqlQuery object
     */
    SqlQuery toSqlQuery();
}
