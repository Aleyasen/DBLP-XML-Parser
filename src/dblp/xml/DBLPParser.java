/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dblp.xml;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.util.IteratorIterable;

public class DBLPParser {

    static DBLPCollection authorsCollection = new DBLPCollection();
    static DBLPCollection yearsCollection = new DBLPCollection();
    static DBLPCollection confsCollection = new DBLPCollection();
    static DBLPCollection titlesCollection = new DBLPCollection();
    static List<Edge> title_year = new ArrayList<>();
    static List<Edge> title_conf = new ArrayList<>();
    static List<Edge> title_author = new ArrayList<>();

    public static void main(String argv[]) {

        try {

            SAXBuilder builder = new SAXBuilder();
            String path = "data/dblp2.xml";
            Document jdomDocument = builder.build(path);
            Element root = jdomDocument.getRootElement();

//            List<String> types = Arrays.asList("incollection", "article", "inproceedings", "proceedings");
            List<String> types = Arrays.asList("inproceedings");
            for (String type : types) {
                extractElements(root, type);
            }
            titlesCollection.writeToFile("data/output/title.txt");
            authorsCollection.writeToFile("data/output/author.txt");
            confsCollection.writeToFile("data/output/conf.txt");
            yearsCollection.writeToFile("data/output/year.txt");
            writeEdgesToFile(title_author, "data/output/title_author.txt");
            writeEdgesToFile(title_conf, "data/output/title_conf.txt");
            writeEdgesToFile(title_year, "data/output/title_year.txt");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void extractElements(Element root, final String type) {
        //            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        List<Element> nList = root.getChildren(type);
//        System.out.println(nList);
        System.out.println(type + " " + nList.size());
        int index = 0;
        for (Element eElement : nList) {
            index++;
            if (index % 100 == 0) {
                System.out.println("Node#: " + index);
            }
            String title = eElement.getChild("title").getText();
            int titleId = titlesCollection.put(title);
            String year = eElement.getChild("year").getText();
            int yearId = yearsCollection.put(year);
            String booktitle = eElement.getChild("booktitle").getText();
            int confId = confsCollection.put(booktitle);
            title_conf.add(new Edge(titleId, confId));
//                title_conf.add(new Edge(confId, titleId));
            title_year.add(new Edge(titleId, yearId));
//                title_year.add(new Edge(yearId, titleId));
            final List<Element> authors = eElement.getChildren("author");
            for (Element author : authors) {
                String author_str = author.getText();
                int authorId = authorsCollection.put(author_str);
                title_author.add(new Edge(titleId, authorId));
//                    title_author.add(new Edge(authorId, authorId));
            }
        }
    }

    public static void writeEdgesToFile(List<Edge> edges, String path) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));
            for (Edge edge : edges) {
                writer.write(edge.node1 + " " + edge.node2 + "\n");
            }
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
