package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.text.Normalizer;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import org.apache.tika.exception.TikaException;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.parser.txt.TXTParser;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.CharArraySet;


public class Indexer {

    private static IndexWriter writer;
    private static final String stopwords_path = "utils/stopwords.txt";

    Indexer(String index_directory_path) throws IOException, TikaException, SAXException{
        Path auxiliary_index_path = Paths.get(index_directory_path);

        Directory out_index_directory = FSDirectory.open(auxiliary_index_path);
        CharArraySet stopwords_updated = stopwords_List();//this.stopwords_List();
        RomanianAnalyzer ro_analyzer = new RomanianAnalyzer(stopwords_updated);
        IndexWriterConfig config = new IndexWriterConfig(ro_analyzer);

        writer = new IndexWriter(out_index_directory, config);

    }


    void index_documents(String dir_path) throws IOException, TikaException, SAXException{
        if (dir_path == null) System.out.println("Please insert a valid path");

        File folder = new File(dir_path);
        File[] listOfFiles = folder.listFiles();

        for (File f:listOfFiles){

            String extension = FileNameUtils.getExtension(f.getCanonicalPath());


            InputStream inputStream = new FileInputStream(f);

            BodyContentHandler contentHandler = new BodyContentHandler();

            Metadata metadata = new Metadata();

            ParseContext context = new ParseContext();

            switch(extension){

                case "pdf":

                    PDFParser parser_pdf = new PDFParser();
                    parser_pdf.parse(inputStream, contentHandler, metadata, context);
                    break;

                case "doc":
                case "docx":{
                    OOXMLParser parser_word = new OOXMLParser();
                    parser_word.parse(inputStream, contentHandler, metadata, context);
                    break;
                }

                case "txt":
                    TXTParser parser_txt = new TXTParser();
                    parser_txt.parse(inputStream, contentHandler, metadata, context);
                    break;

                
            }
            String text_norm = remove_diacritics(contentHandler.toString());
            Document doc_to_index = create_Doc(text_norm, f.getName(), f.getCanonicalPath());
            writer.addDocument(doc_to_index);
        }
        writer.close();
    }

    public static CharArraySet stopwords_List(){
        ArrayList<String> stopwords = new ArrayList<>();

        try {
            File sw_file = new File(stopwords_path);
            Scanner Reader = new Scanner(sw_file);
            while (Reader.hasNextLine()) {

                String word = Reader.nextLine();

                if (!word.contains("#")){
                    stopwords.add(word);
                    boolean is_norm = Normalizer.isNormalized(word, Normalizer.Form.NFD);

                    if (!is_norm) {
                        stopwords.add(remove_diacritics(word));
                    }
                }
            }
            Reader.close();
        }   catch (FileNotFoundException e) {
            System.out.println("Please insert a valid path");
            e.printStackTrace();
        }
        return new CharArraySet(stopwords, true);
    }

    public static String remove_diacritics(String text){
        return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }


    private Document create_Doc(String file_content, String file_name, String file_path) {
        Document document = new Document();

        document.add(new TextField("Content", file_content, Field.Store.YES));
        document.add(new StringField("FileName", file_name, Field.Store.YES));
        document.add(new StringField("FilePath", file_path, Field.Store.YES));

        return document;

    }

}

