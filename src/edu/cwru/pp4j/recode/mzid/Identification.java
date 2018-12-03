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
 * Encapsulates an identification record from an mzIdentML file. This is a
 * non-exhaustive implementation that includes getters for the elements we 
 * need to convert mzid files to tdv.
 * 
 * @author Sean Maxwell
 */
public class Identification extends HashMap<String,String> {
    public final String MASCOTSCORECVNAME="Mascot:score";
    public final String MSGFSCORECVNAME="MS-GF:RawScore";
    public final String MSGFQVALUECVNAME="MS-GF:QValue";
    public final String MASCOTEXPECTCVNAME="Mascot:expectation value";
    
    public Identification(HashMap<String,String> atts) {
        this.putAll(atts);
    }
    
    public double getDouble(String name) throws Exception {
        if(this.containsKey(name)) {
            return Double.parseDouble(this.get(name));
        }
        else {
            throw new Exception(String.format("EntityNotFound %s",name));
        }
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
    
    public boolean passThreshold() {
        return this.get("passThreshold").equals("true");
    }
        
    public double mascotScore() {
        try {
            return this.getDouble(this.MASCOTSCORECVNAME);
        }
        catch(Exception e) {
            return -1.0;
        }
    }
    
    public double mascotExpectationValue() {
        try {
            return this.getDouble(this.MASCOTEXPECTCVNAME);
        }
        catch(Exception e) {
            return -1.0;
        }
    }
    
    public double msgfScore() {
        try {
            return this.getDouble(this.MSGFSCORECVNAME);
        }
        catch(Exception e) {
            return -1.0;
        }
    }
    
    public double msgfQValue() {
        try {
            return this.getDouble(this.MSGFQVALUECVNAME);
        }
        catch(Exception e) {
            return -1.0;
        }
    }
    
    public double score() {
        if(this.containsKey(this.MASCOTSCORECVNAME)) {
            return this.mascotScore();
        }
        else if(this.containsKey(this.MSGFSCORECVNAME)) {
            return this.msgfScore();
        }
        else {
            return -1;
        }
    }
    
    public double qValue() {
        if(this.containsKey(this.MSGFQVALUECVNAME)) {
            return this.msgfQValue();
        }
        else {
            return 0.0;
        }
    }
    
    public double pValue() {
        if(this.containsKey(this.MASCOTEXPECTCVNAME)) {
            return this.mascotExpectationValue();
        }
        else {
            return 0.0;
        }
    }
    
    public int chargeState() {
        try {
            return this.getInteger("chargeState");
        }
        catch(Exception e) {
            return 0;
        }
    }
    
    public double massToCharge() {
        try {
            return this.getDouble("calculatedMassToCharge");
        }
        catch(Exception e) {
            return 0;
        }
    }
    
    public String identificationResultID() {
        try {
            return this.get("SIR");
        }
        catch(Exception e) {
            return "NULL";
        }
    }
    
    public boolean isMsgf() {
        return this.containsKey(this.MSGFQVALUECVNAME);
    }
    
    public int rank() {
        try {
            return this.getInteger("rank");
        }
        catch(Exception e) {
            return -1;
        }
    }

}
