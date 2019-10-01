package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces;

import kiv.zcu.knowledgeipr.core.knowledgedb.dto.ReferenceDto;
import kiv.zcu.knowledgeipr.core.model.search.Search;

import java.util.List;

/**
 *
 *
 * Interface which should be implemented by concrete data searchers specific
 * to some data storage.
 *
 */
public interface IDataSearcher<T extends IDbRecord> {

    /**
     * Searches for document records identified by the references.
     *
     * @param references - The list of references containing urls of the documents to retrieve
     * @return - The list of documents from Mongo associated with the references
     */
    List<T> searchByReferences(List<ReferenceDto> references, Search search);
}
