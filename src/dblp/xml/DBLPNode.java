/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dblp.xml;

import java.util.Objects;

/**
 *
 * @author aleyase2-admin
 */
public class DBLPNode {

    int id;
    String name;

    public DBLPNode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DBLPNode other = (DBLPNode) obj;
//        if (this.id != other.id) {
//            return false;
//        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

}
