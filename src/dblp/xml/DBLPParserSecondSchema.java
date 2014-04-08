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

public class DBLPParserSecondSchema {

    static DBLPCollection authorsCollection = new DBLPCollection();
    static DBLPCollection yearConfsCollection = new DBLPCollection();
    static DBLPCollection confsCollection = new DBLPCollection();
    static DBLPCollection titlesCollection = new DBLPCollection();
    static String title_author_path = "data/output2/title_author.txt";
    static String author_conf_path = "data/output2/author_conf.txt";
    static String title_year_path = "data/output2/title_year.txt";
    static String conf_year_path = "data/output2/conf_year.txt";
    static Writer title_author_writer;
    static Writer author_conf_writer;
    static Writer title_year_writer;
    static Writer conf_year_writer;

    public static void main(String argv[]) {

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
            titlesCollection.writeToFile("data/output2/title.txt");
            authorsCollection.writeToFile("data/output2/author.txt");
            confsCollection.writeToFile("data/output2/conf.txt");
            yearConfsCollection.writeToFile("data/output2/year-with-conf.txt");
            yearConfsCollection.writeToFileJustFirsToken("data/output2/year.txt");


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
}
