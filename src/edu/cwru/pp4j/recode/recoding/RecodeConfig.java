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
package edu.cwru.pp4j.recode.recoding;

import java.util.HashMap;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;

/**
 * Encapsulates all parameters for controlling the recoding of protein 
 * modification sites. Exposes setters and getters for all parameters using
 * a functional approach where getters return the object.
 * 
 * @author Sean Maxwell
 */
public class RecodeConfig {
    private final HashMap<Enum,String> strings;
    private final HashMap<Enum,Integer> integers;
    
    public RecodeConfig() {
        strings = new HashMap<>();
        integers = new HashMap<>();
        integers.put(RecodeParams.STRICTDIGEST, 0);
        integers.put(RecodeParams.DIGESTONLY,0);
    }
    
    public RecodeConfig setMissedCleavages(int i) {
        integers.put(RecodeParams.MISSEDCLEAVAGES, i);
        return this;
    }
    
    public int getMissedCleavages() {
        return integers.get(RecodeParams.MISSEDCLEAVAGES);
    }
    
    public RecodeConfig setMaxModifications(int i) {
        integers.put(RecodeParams.MAXMODIFICATIONS, i);
        return this;
    }
    
    public int getMaxModifications() {
        return integers.get(RecodeParams.MAXMODIFICATIONS);
    }
    
    public RecodeConfig setMinPeptideLength(int i) {
        integers.put(RecodeParams.MINPEPTIDELENGTH, i);
        return this;
    }
    
    public int getMinPeptideLength() {
        return integers.get(RecodeParams.MINPEPTIDELENGTH);
    }
    
    public RecodeConfig setMaxPeptideLength(int i) {
        integers.put(RecodeParams.MAXPEPTIDELENGTH, i);
        return this;
    }
    
    public int getMaxPeptideLength() {
        return integers.get(RecodeParams.MAXPEPTIDELENGTH);
    }
    
    public RecodeConfig setProtease(String s) {
        strings.put(RecodeParams.PROTEASE, s);
        return this;
    }
    
    public String getProtease() {
        return strings.get(RecodeParams.PROTEASE);
    }
    
    public RecodeConfig setStrictDigest(boolean b) {
        if(b) {
            integers.put(RecodeParams.STRICTDIGEST, 1);
        }
        else {
            integers.put(RecodeParams.STRICTDIGEST, 0);
        }
        return this;
    }
    
    public boolean getStrictDigest() {
        return integers.get(RecodeParams.STRICTDIGEST)==1;
    }
    
    public RecodeConfig setDatabase(String s) {
        strings.put(RecodeParams.DATABASE, s);
        return this;
    }
    
    public String getDatabase() {
        return strings.get(RecodeParams.DATABASE);
    }
    
    public RecodeConfig setModSiteDatabase(String s) {
        strings.put(RecodeParams.MODSITEDATABASE, s);
        return this;
    }
    
    public String getModSiteDatabase() {
        return strings.get(RecodeParams.MODSITEDATABASE);
    }
    
    public RecodeConfig setOutputName(String s) {
        strings.put(RecodeParams.OUTPUTNAME, s);
        return this;
    }
    
    public String getOutputName() {
        return strings.get(RecodeParams.OUTPUTNAME);
    }
    
    public RecodeConfig setDigestOnly(boolean b) {
        int value = b ? 1 : 0;
        integers.put(RecodeParams.DIGESTONLY,value);
        return this;
    }
    
    public boolean getDigestOnly() {
        return integers.get(RecodeParams.DIGESTONLY)==1;
    }
    
    public static void save(RecodeConfig rcc, String path) throws Exception {
        try(FileWriter fw = new FileWriter(path)) {
        
            /* Write out Integer valued parameters */
            for(Enum parameter : RecodeParams.values()) {
                if(rcc.integers.containsKey(parameter)) {
                    fw.write(String.format("%s=%d\n",parameter.toString(),rcc.integers.get(parameter)));
                }
            }

            /* Write out String valued parameters */
            for(Enum parameter : RecodeParams.values()) {
                if(rcc.strings.containsKey(parameter)) {
                    fw.write(String.format("%s=%s\n",parameter.toString(),rcc.strings.get(parameter)));
                }
            }

            fw.close();
        }
    }
    
    public static RecodeConfig load(String path) throws Exception {
        RecodeConfig rcc = new RecodeConfig();
        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while((line=br.readLine()) != null) {
                String[] tokens = line.split("=");
                Enum parameter = RecodeParams.valueOf(tokens[0]);
                try {
                    Integer v = Integer.parseInt(tokens[1]);
                    rcc.integers.put(parameter,v);
                }
                catch(Exception e) {
                    rcc.strings.put(parameter,tokens[1]);
                }
            }
        }
        return rcc;
    }
}
