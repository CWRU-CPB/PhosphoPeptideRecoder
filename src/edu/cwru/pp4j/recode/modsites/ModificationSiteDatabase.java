/* Copyright 2018 Case Western Reserve University
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.cwru.pp4j.recode.modsites;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

/**
 * DAO for H2 databases created by ModificationSiteDatabaseImporter. Exposes
 * methods to query modification sites by protein accession and test existence
 * of individual sites.
 * 
 * @author Sean Maxwell
 */
public class ModificationSiteDatabase {
    private Connection connection;
    private PreparedStatement select;
    private PreparedStatement head;
    
    /**
     * Connect to a database containing modification sites.
     * @param db Database name
     * @throws Exception if the requested cannot be fulfilled.
     */
    public void connect(String db) throws Exception {
        Class.forName("org.h2.Driver");
        connection = DriverManager.
            getConnection("jdbc:h2:"+db+";IFEXISTS=TRUE", "sb4j_h2_user", "sb4j_h2_password");
        select = connection.prepareStatement("SELECT site FROM records WHERE accession=? AND site=?;");
        head = connection.prepareStatement("SELECT * FROM records WHERE accession=?;");
        
    }
    
    /**
     * Close any connection previously opened to a database.
     * @throws Exception if the request cannot be fulfilled.
     */
    public void disconnect() throws Exception {
        connection.close();
    }
    
    /**
     * Fetch a list of all modification sites in the databases associated with 
     * a protein.
     * @param accession Uniprot accession of protein
     * @return modification sites
     * @throws Exception if the request cannot be fulfilled.
     */
    public List<String> list(String accession) throws Exception {
        List<String> r = new ArrayList<>();
        head.setString(1,accession);
        try(ResultSet rs = head.executeQuery()) {
            while(rs.next()) {
                r.add(rs.getString("site"));
            }
        }
        return r;
    }
    
    /**
     * Test if a modification site exists in the database.
     * @param accession Uniprot protein accession
     * @param site The modification site
     * @return true for exists, false for does not exist.
     * @throws Exception if the request cannot be fulfilled.
     */
    public boolean exists(String accession, String site) throws Exception {
        select.setString(1, accession);
        select.setString(2, site);
        try(ResultSet rs = select.executeQuery()) {
            if(rs.first()) {
                return true;
            }
        }
        return false;
    }
}
