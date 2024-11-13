# Information Retrieval and Text Mining Project 1: Retrieval System for Romanian language
# Student: Zibileanu Sabin, Group: 512

The Retrieval System for Romanian language is based on two key components: an indexer and a searcher, which will both be presented.

# 1. The Indexer
The Lucene library uses the inverted index algorithm(Indexer), which is used for mapping terms to the documents that contain them. It is a fast and efficient algorithm due to the fact that it provides a method that looks for a document or a set of documents that contain certain terms.

In the project, the Indexer component presents multiple functionalities: 
 - Indexing documents: the content from every pdf, doc, docx or txt file that is in the files directory, will be read and then there will be created a Document that contains the file name, its path and its content. The file name and the path will not be tokenized, while the content will.

 - Updating the stopwords list: In the project the Analyzer that was chosen to be used was the **RomanianAnalyzer**. This Analyzer contains the default stopwords for Romanian language, words such as: "această", "și" etc. However, it is important to keep in mind that the Romanian language uses diacritics and, for example the words "si" and "și" are different and also need to be included in the stopwords list. With that in mind, the list of stopwords has also been updated so that it also contains the stopwords without diacritics.

 - Removing the diacritics: This is a text normalization step, that removes the diacritics from the content.


# 2. The Searcher
The second key part of the project is the Searcher, which will help "retrieve" the information that is being searched. What is important to mention here, is that the Analyzer used in the Searcher will also contain the updated list of stopwords that was created in the Indexer so that consistency is ensured.
The Searcher will display (if found) the top 5 most relevant documents for the input query
