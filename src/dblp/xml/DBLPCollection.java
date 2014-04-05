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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aleyase2-admin
 */
public class DBLPCollection {

    Map<DBLPNode, Integer> countMap;
    Map<String, Integer> idMap;

    public int size() {
        return countMap.size();
    }

    public DBLPCollection() {
        countMap = new HashMap<>();
        idMap = new HashMap<>();
    }

    public int put(String key) {
        DBLPNode node = null;
        if (!idMap.containsKey(key)) {
            int id = countMap.size() + 1;
            node = new DBLPNode(id, key);
            idMap.put(key, id);
            countMap.put(node, 0);
        } else {
            node = new DBLPNode(idMap.get(key), key);
        }
        int val = countMap.get(node);
        countMap.put(node, val + 1);
        return idMap.get(key);
    }

    public void print() {
        for (DBLPNode key : countMap.keySet()) {
            System.out.println(key.id + " " + key.name + " > " + countMap.get(key));
        }
    }

    public void writeToFile(String path) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));
            for (DBLPNode key : countMap.keySet()) {
                writer.write(key.id + " " + key.name + "\n");
            }
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
