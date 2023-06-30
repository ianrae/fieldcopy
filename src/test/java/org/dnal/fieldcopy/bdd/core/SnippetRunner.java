package org.dnal.fieldcopy.bdd.core;

public interface SnippetRunner {
//    void setConnectionProvider(ConnectionProvider connProvider);
    BDDSnippetResult execute(BDDSnippet snippet, BDDSnippetResult previousRes, SnippetContext ctx);
}
