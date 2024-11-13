package org.example;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.util.*;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;



public class Main {

    private static final String INDEX_PATH_FILE = "indexPath.txt";


    public static void main(String[] args) throws TikaException, SAXException, IOException, ParseException{

        if (args.length == 3){
            if((args[0].equals("-index") || args[0].equals("--i")) && args[1].equals("-directory")) {

                System.out.println("Indexing stage..." + "\t");
                Indexer index = new Indexer(args[2]);
                index.index_documents(args[2]);
                System.out.println("Indexing documents..." + "\t");
                System.out.println("The file where the index was saved is: " + args[2] + "\t");

                storeIndexPath(args[2]);


            }

            else if((args[0].equals("-search") || args[0].equals("--s")) && args[1].equals("-query")){
                try{
                    String stored_index_path = getIndexPath(INDEX_PATH_FILE);

                    Searcher searcher = new Searcher(stored_index_path);

                    String query_without_diacritics = Indexer.remove_diacritics(args[2]);

                    searcher.find_query(query_without_diacritics);


                }catch (IndexNotFoundException e){
                    System.out.println("Please insert a valid Index path");
                    e.printStackTrace();
                }

            }

            else{
                System.out.println("Please insert valid options: [-index] [-directory] [<path to docs>] or [--i] [-directory] [<path to docs>] for Indexing");
                System.out.println("Please insert valid options: [-search] [-query] [<keyword>] or [--s] [-query] [<keyword>] for Searching");

            }

        }
        else{
            System.out.println("Invalid arguments: Please insert 3 arguments");
        }

    }

    private static void storeIndexPath(String indexPath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(INDEX_PATH_FILE))) {
            writer.write(indexPath);
        } catch (IOException e) {
            System.out.println("Failed to write index path to file.Please insert a valid filename");
            e.printStackTrace();
        }
    }

    private static String getIndexPath(String indexPath){
        StringBuilder data = new StringBuilder();
        try {
            File f = new File(indexPath);
            Scanner Reader = new Scanner(f);
            while (Reader.hasNextLine()) {
                data.append(Reader.nextLine());

            }
            Reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return data.toString();
    }

}