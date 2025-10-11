package com.github.priyajitbera.carkg.service.api.service;

import java.util.List;

public interface SemanticOps<EMBEDDING_REQ, EMBEDDING_MODEL, SEMANTIC_SEARCH_MODEL> {

    EMBEDDING_MODEL embed(EMBEDDING_REQ request);

    List<SEMANTIC_SEARCH_MODEL> semanticSearch(String query);
}
