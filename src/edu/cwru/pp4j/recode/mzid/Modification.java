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
 * Encapsulates a modification record from an mzIdentML file. This is a non-
 * exhaustive implementation that only includes getters for parameters we need
 * to convert mzid files to tdv.
 * 
 * @author Sean Maxwell
 */
public class Modification extends HashMap<String,String> {
    
    public Modification(HashMap<String,String> atts) {
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
    
    public int location() {
        try {
            return this.getInteger("location");
        }
        catch(Exception e) {
            return -1;
        }
    }
    
    public boolean isPhospho() {
        return this.containsKey("Phospho");
    }
    
}
