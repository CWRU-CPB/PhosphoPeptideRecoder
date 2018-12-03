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
package edu.cwru.pp4j.recode.mzid;

import java.util.HashMap;

/**
 * Encapsulates an evidence record from an mzIdentML file. This is a 
 * non-exhaustive implementation that is only includes getters for the 
 * properties that we use for converting mzid files to tdv.
 * 
 * @author Sean Maxwell
 */
public class Evidence extends HashMap<String,String>{
    
    public Evidence(HashMap<String,String> atts) {
        this.putAll(atts);
    }
    
    public int getInteger(String name) throws Exception {
        if(this.containsKey(name)) {
            return Integer.parseInt(this.get(name));
        }
        else {
            throw new Exception(String.format("EntityNotFound %s",name));
        }
    }
    
    public String identifier() {
        return this.get("id");
    }
    
    public String peptideRef() {
        return this.get("peptide_ref");
    }
    
    public String sequenceRef() {
        return this.get("dBSequence_ref");
    }
    
    public int start() {
        try {
            return this.getInteger("start");
        }
        catch(Exception e) {
            return -1;
        }
    }
    
    public int end() {
        try {
            return this.getInteger("end");
        }
        catch(Exception e) {
            return -1;
        }
    }
}
