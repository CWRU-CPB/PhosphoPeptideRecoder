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
 * Encapsulates a peptide record from an mzIdentML file. This is a non-
 * exhaustive implementation that only includes getters for parameters we
 * use to convert mzid files to tdv.
 * 
 * @author Sean Maxwell
 */
public class Peptide extends HashMap<String,String> {
    
    public Peptide(HashMap<String,String> atts) {
        this.putAll(atts);
    }
    
    public String identifier() {
        return this.get("id");
    }
    
    public String sequence() {
        return this.get("sequence");
    }
    
    public String aminoAcid(int index) {
        return String.format("%c",this.get("sequence").charAt(index));
    }
}
