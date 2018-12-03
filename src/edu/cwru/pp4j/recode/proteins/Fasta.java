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
package edu.cwru.pp4j.recode.proteins;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

/**
 * Encapsulates access to FASTA format files.
 *
 * @author Sean Maxwell
 */
public class Fasta {
    private Map<String,String> sequences;
    
    /**
     * Test a protein accession to determine if it is a valid Uniprot accession.
     * @param accession Protein accession to test
     * @return true if the accession is a valid Uniprot accession and false
     * otherwise.
     */
    public final boolean isUniprot(String accession) {
        return accession.matches("[OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2}");
    }
    
    /**
     * Parse the protein identifier(accession number) from the accession line
     * of a FASTA database. It supports two formats that cover most scenarios:
     * <br>
     * <ul>
     * <li>&gt;{ACCESSION}</li>
     * <li>&gt;sp|{ACCESSION} {DESCRIPTION}</li>
     * </ul>
     * 
     * @param line The accession line to parse
     * 
     * @return Accession number parsed from the line
     */
    public final String parseAccession(String line) {
        if(line.contains("|")) {
            String[] parts = line.split("\\|");
            return parts[1].trim();
        }
        else {
            return line.trim();
        }
    }
    
    /**
     * Constructor initializes a new database and populates it with the content
     * of the file located at the argument path.
     * 
     * @param path location of a FASTA protein sequence file
     * @param enforceUniprotAccession When set to true, the constructor will
     * throw an Exception if the accession line does not contain a valid 
     * Uniprot accession number.
     * 
     * @throws Exception if the request cannot be fulfilled. 
     */
    public Fasta(String path, boolean enforceUniprotAccession) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        String accessionLine = null;
        StringBuilder sequence = new StringBuilder();
        sequences = new HashMap<>();
        
        while((line=br.readLine()) != null) {
            /* Be flexible, and ignore blank lines */
            if(line.equals("")) continue;
            
            /* Start of new sequence */
            if(line.startsWith(">")) {
                if(accessionLine != null) {
                    /* If not a Uniprot accession, terminate here */
                    if(isUniprot(accessionLine) || !enforceUniprotAccession) {
                        sequences.put(accessionLine,sequence.toString());
                    }
                    else {
                        throw new Exception(String.format("Encountered non-Uniprot accession %s parsing database",accessionLine));
                    }
                }
                accessionLine = parseAccession(line.substring(1));
                sequence.delete(0, sequence.length());
            }
            
            /* Extension of amino acid sequence */
            else {
                sequence.append(line.toUpperCase());
            }
            
        }
        
        if(accessionLine != null) {
            if(isUniprot(accessionLine) || !enforceUniprotAccession) {
                sequences.put(accessionLine,sequence.toString());
            }
            else {
                throw new Exception(String.format("Encountered non-Uniprot accession %s parsing database",accessionLine));
            }
        }
        
        /* If database in empty, it was invalid */
        if(sequences.isEmpty()) {
            throw new Exception("The database contained no protein sequences");
        }
        
        br.close();
    }
    
    /**
     * Constructor initializes a new database and populates it with the content
     * of the file located at the argument path.
     * 
     * @param path location of a FASTA protein sequence file
     * 
     * @throws Exception if the request cannot be fulfilled. 
     */
    public Fasta(String path) throws Exception {
        this(path,true);
    }
    
    /**
     * Retrieve the protein sequence corresponding to a protein accession.
     * @param accession Protein accession identify sequence of interest
     * @return Protein amino acid sequence.
     */
    public String getSequence(String accession) {
        return sequences.get(accession);
    }
    
    /**
     * Retrieve the map of all accessions to sequences.
     * @return all accessions and sequences.
     */
    public Map<String,String> getSequences() {
        return sequences;
    }
    
    /**
     * Retrieve all accessions from database.
     * @return all protein accessions.
     */
    public Set<String> getAccessions() {
        return sequences.keySet();
    }
    
    /**
     * Report how many sequences are stored in the object.
     * @return Number of sequences stored in object.
     */
    public int size() {
        return sequences.size();
    }
    
}
