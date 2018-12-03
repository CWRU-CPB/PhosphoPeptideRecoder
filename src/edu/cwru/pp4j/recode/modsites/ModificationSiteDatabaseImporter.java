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

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.CallableStatement;

/**
 * Imports a tab-delimited file of cross references into an H2 database. The
 * source file must contain two columns with Uniprot protein accession in first
 * column and modification site in second column. Empty lines are allowed.
 * 
 * @author Sean Maxwell
 */
public class ModificationSiteDatabaseImporter {
    /**
     * Reads the tab-delimited file specified by the argument database name,
     * parsing the rows and adding to an H2 database with the same name.
     * @param source File containing modifications
     * @param db Database name for resulting H2 database
     * @return true on success, false if an error occurs.
     */
    public static boolean importDatabase(String source, String db) {
        try {
            Class.forName("org.h2.Driver");
            Connection connection = DriverManager.
                getConnection("jdbc:h2:"+db, "sb4j_h2_user", "sb4j_h2_password");
            CallableStatement destroy = connection.prepareCall("DROP TABLE records;");
            CallableStatement create = connection.prepareCall("CREATE TABLE records(accession VARCHAR(32), site VARCHAR(12), PRIMARY KEY(accession,site));");
            
            /* Remove any existing table */
            try {
                destroy.executeUpdate();
            }
            catch(Exception e) {
                // The table does not already exist
            }
            connection.commit();
            
            /* Create the new table */
            create.executeUpdate();
            connection.commit();
            
            /* Prepare insert/select statement after dropping/creating table */
            CallableStatement insert = connection.prepareCall("INSERT INTO records VALUES(?,?);");
            
            String line = null;
            try(BufferedReader br = new BufferedReader(new FileReader(source))) {

                /* Read header line */
                br.readLine();

                /* Read annotations */
                while((line=br.readLine()) != null) {
                    String[] tokens = line.split("\t");
                    if(tokens.length < 2) continue;

                    String accession = tokens[0];
                    String site = tokens[1];
                    
                    try {
                        insert.setString(1, accession);
                        insert.setString(2, site);
                        insert.executeUpdate();
                    }
                    
                    /* Exception on duplicate keyts */
                    catch(Exception e) {
                        System.out.printf("Dropping duplicate entry %s,%s\n",accession,site);
                    }
                }   
                br.close();
            }
            connection.commit();
            connection.close();
            
            return true;
        }
        catch(Exception e) {
            System.out.printf("Error importing database -> %s\n",e.getMessage());
            e.printStackTrace(System.out);
            return false;
        }
    }
}
