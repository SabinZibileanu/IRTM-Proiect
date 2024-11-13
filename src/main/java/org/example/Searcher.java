package org.example;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;


public class Searcher {

    private static IndexSearcher searcher;
    private RomanianAnalyzer ro_analyzer = new RomanianAnalyzer(Indexer.stopwords_List());

    Searcher(String index_directory) throws IOException{
        Path auxiliary_index_path = Paths.get(index_directory);

        Directory search_index_directory = FSDirectory.open(auxiliary_index_path);

        IndexReader reader = DirectoryReader.open(search_index_directory);
        searcher = new IndexSearcher(reader);
    }

    void find_query (String input_query) throws ParseException, IOException{
        Query q = new QueryParser("Content", ro_analyzer).parse(input_query);

        int hitsPerPage = 5;

        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;
        StoredFields storedFields = searcher.storedFields();


        for(int i=0; i<hits.length; i++) {
            int docId = hits[i].doc;
            Document d = storedFields.document(docId);//searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("FileName") + "\t");
        }

    }


}


