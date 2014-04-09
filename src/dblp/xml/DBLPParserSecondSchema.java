/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dblp.xml;

import java.io.BufferedWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class DBLPParserSecondSchema extends DBLPParserSchema {

    static DBLPCollection authorsCollection = new DBLPCollection();
    static DBLPCollection yearConfsCollection = new DBLPCollection();
    static DBLPCollection confsCollection = new DBLPCollection();
    static DBLPCollection titlesCollection = new DBLPCollection();
    static String title_author_path = "data/output2/title_author.txt";
    static String author_conf_path = "data/output2/author_conf.txt";
    static String title_year_path = "data/output2/title_year.txt";
    static String conf_year_path = "data/output2/conf_year.txt";
    static final String title_path = "data/output2/title.txt";
    static final String author_path = "data/output2/author.txt";
    static final String conf_path = "data/output2/conf.txt";
    static final String year_with_conf_path = "data/output2/year-with-conf.txt";
    static final String year_path = "data/output2/year.txt";
    static Writer title_author_writer;
    static Writer author_conf_writer;
    static Writer title_year_writer;
    static Writer conf_year_writer;

    public static void main(String[] args) {
        filter();
    }

    public static void main2(String argv[]) {

        try {

            title_author_writer = new BufferedWriter(
                    new OutputStreamWriter(
                    new FileOutputStream(title_author_path), "utf-8"));
            author_conf_writer = new BufferedWriter(
                    new OutputStreamWriter(
                    new FileOutputStream(author_conf_path), "utf-8"));
            title_year_writer = new BufferedWriter(
                    new OutputStreamWriter(
                    new FileOutputStream(title_year_path), "utf-8"));
            conf_year_writer = new BufferedWriter(
                    new OutputStreamWriter(
                    new FileOutputStream(conf_year_path), "utf-8"));

            SAXBuilder builder = new SAXBuilder();
            String path = "data/dblp.xml";
            Document jdomDocument = builder.build(path);
            Element root = jdomDocument.getRootElement();

//            List<String> types = Arrays.asList("incollection", "article", "inproceedings", "proceedings");
            List<String> types = Arrays.asList("inproceedings");
            for (String type : types) {
                extractElements(root, type);
            }
            titlesCollection.writeToFile(title_path);
            authorsCollection.writeToFile(author_path);
            confsCollection.writeToFile(conf_path);
            yearConfsCollection.writeToFile(year_with_conf_path);

            yearConfsCollection.writeToFileJustFirsToken(year_path);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void extractElements(Element root, final String type) {
        try {
            List<Element> nList = root.getChildren(type);

            System.out.println(type + " " + nList.size());
            int index = 0;
            for (Element eElement : nList) {

                index++;
                if (index % 100 == 0) {
                    System.out.println("Node#: " + index);
                }
                String title = eElement.getChild("title").getText();
                int titleId = titlesCollection.get(title);
                String year = eElement.getChild("year").getText();
                String booktitle = eElement.getChild("booktitle").getText();
                int confId = confsCollection.get(booktitle);
                String yearConf = year + " " + booktitle;
                int yearConfId = yearConfsCollection.get(yearConf);
                conf_year_writer.write(confId + " " + yearConfId + "\n");
                title_year_writer.write(titleId + " " + yearConfId + "\n");
                final List<Element> authors = eElement.getChildren("author");
                for (Element author : authors) {
                    String author_str = author.getText();
                    int authorId = authorsCollection.get(author_str);
                    title_author_writer.write(titleId + " " + authorId + "\n");
                    author_conf_writer.write(authorId + " " + confId + "\n");
                }

            }
            title_author_writer.close();
            author_conf_writer.close();
            title_year_writer.close();
            conf_year_writer.close();
        } catch (IOException ex) {
            Logger.getLogger(DBLPParserSecondSchema.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void filter() {
        DBLPParserSchema parser = new DBLPParserFirstSchema();
        final List<Integer> topAuthors = parser.getTopAuthors(topAuthorCount, title_author_path);
        System.out.println("top authors# :" + topAuthorCount);

        final List<Edge> title_author = parser.readEdgeFile(title_author_path);
        final List<Edge> title_year = parser.readEdgeFile(title_year_path);
        final List<Edge> author_conf = parser.readEdgeFile(author_conf_path);
        final List<Edge> conf_year = parser.readEdgeFile(conf_year_path);

        final List<Integer> titles = parser.getNeighbourEntities(topAuthors, title_author, 0);

        final List<Integer> authors = parser.getNeighbourEntities(titles, title_author, 1);
        final List<Integer> years = parser.getNeighbourEntities(titles, title_year, 1);
        final List<Integer> confs = parser.getNeighbourEntities(authors, author_conf, 1);

        System.out.println("title#:" + titles.size());
        System.out.println("authors#:" + authors.size());
        System.out.println("conf#:" + confs.size());
        System.out.println("year#:" + years.size());

        final Map<Integer, String> title_nodes = parser.readNodeFile(title_path);
        final Map<Integer, String> author_nodes = parser.readNodeFile(author_path);
        final Map<Integer, String> year_nodes = parser.readNodeFile(year_path);
        final Map<Integer, String> conf_nodes = parser.readNodeFile(conf_path);

        parser.writeNodesToFile(titles, title_nodes, "data/output_filter2/title.txt");
        parser.writeNodesToFile(authors, author_nodes, "data/output_filter2/author.txt");
        parser.writeNodesToFile(confs, conf_nodes, "data/output_filter2/conf.txt");
        parser.writeNodesToFile(years, year_nodes, "data/output_filter2/year.txt");

        parser.writeEdgesToFile(titles, title_author, 0, "data/output_filter2/title_author.txt");
        parser.writeEdgesToFile(titles, title_year, 0, "data/output_filter2/title_year.txt");
        parser.writeEdgesToFile(authors, author_conf, 0, "data/output_filter2/author_conf.txt");
        parser.writeEdgesToFile(confs, conf_year, 0, "data/output_filter2/conf_year.txt");

    }
}
