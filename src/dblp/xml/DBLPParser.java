/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dblp.xml;

import java.io.BufferedWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.w3c.dom.DOMException;

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
            File fXmlFile = new File("data/dblp2.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            // List<String> types = Arrays.asList("incollection", "article", "inproceedings", "proceedings");
            List<String> types = Arrays.asList("incollection");
            for (String type : types) {
                extractElements(doc, type);
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

    private static void extractElements(Document doc, final String type) throws DOMException {
        //            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        NodeList nList = doc.getElementsByTagName(type);
        System.out.println(type + " " + nList.getLength());
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String title = eElement.getElementsByTagName("title").item(0).getTextContent();
                int titleId = titlesCollection.put(title);
                String year = eElement.getElementsByTagName("year").item(0).getTextContent();
                int yearId = yearsCollection.put(year);
                String booktitle = eElement.getElementsByTagName("booktitle").item(0).getTextContent();
                int confId = confsCollection.put(booktitle);
                title_conf.add(new Edge(titleId, confId));
//                title_conf.add(new Edge(confId, titleId));
                title_year.add(new Edge(titleId, yearId));
//                title_year.add(new Edge(yearId, titleId));
                final NodeList authors = eElement.getElementsByTagName("author");
                for (int i = 0; i < authors.getLength(); i++) {
                    String author_str = authors.item(i).getTextContent();
                    int authorId = authorsCollection.put(author_str);
                    title_author.add(new Edge(titleId, authorId));
//                    title_author.add(new Edge(authorId, authorId));
                }
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
