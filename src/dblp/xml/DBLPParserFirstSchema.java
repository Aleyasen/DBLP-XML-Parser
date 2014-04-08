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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class DBLPParserFirstSchema {

    static DBLPCollection authorsCollection = new DBLPCollection();
    static DBLPCollection yearsCollection = new DBLPCollection();
    static DBLPCollection confsCollection = new DBLPCollection();
    static DBLPCollection titlesCollection = new DBLPCollection();
    static String title_author_path = "data/output1/title_author.txt";
    static String title_conf_path = "data/output1/title_conf.txt";
    static String title_year_path = "data/output1/title_year.txt";
    static Writer title_year_writer;
    static Writer title_conf_writer;
    static Writer title_author_writer;

    public static void main(String argv[]) {

        try {
            title_year_writer = new BufferedWriter(
                    new OutputStreamWriter(
                    new FileOutputStream(title_year_path), "utf-8"));

            title_conf_writer = new BufferedWriter(
                    new OutputStreamWriter(
                    new FileOutputStream(title_conf_path), "utf-8"));

            title_author_writer = new BufferedWriter(
                    new OutputStreamWriter(
                    new FileOutputStream(title_author_path), "utf-8"));

            SAXBuilder builder = new SAXBuilder();
            String path = "data/dblp.xml";
            Document jdomDocument = builder.build(path);
            Element root = jdomDocument.getRootElement();

//            List<String> types = Arrays.asList("incollection", "article", "inproceedings", "proceedings");
            List<String> types = Arrays.asList("inproceedings");
            for (String type : types) {
                extractElements(root, type);
            }
            titlesCollection.writeToFile("data/output1/title.txt");
            authorsCollection.writeToFile("data/output1/author.txt");
            confsCollection.writeToFile("data/output1/conf.txt");
            yearsCollection.writeToFile("data/output1/year.txt");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void extractElements(Element root, final String type) {
        try {
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
                int titleId = titlesCollection.get(title);
                String year = eElement.getChild("year").getText();
                int yearId = yearsCollection.get(year);
                String booktitle = eElement.getChild("booktitle").getText();
                int confId = confsCollection.get(booktitle);
                title_conf_writer.write(titleId + " " + confId + "\n");
                title_year_writer.write(titleId + " " + yearId + "\n");
                final List<Element> authors = eElement.getChildren("author");
                for (Element author : authors) {
                    String author_str = author.getText();
                    int authorId = authorsCollection.get(author_str);
                    title_author_writer.write(titleId + " " + authorId + "\n");
                }

            }
            title_author_writer.close();
            title_conf_writer.close();
            title_year_writer.close();
        } catch (IOException ex) {
            Logger.getLogger(DBLPParserFirstSchema.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
