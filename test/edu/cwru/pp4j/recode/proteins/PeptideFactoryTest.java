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

import java.util.List;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sean-m
 */
public class PeptideFactoryTest {
    
    public PeptideFactoryTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testChymotrypsin_simple() {
        System.out.println("Chymotrypsin - [FYW]");
        List<String> expResult = new ArrayList<>();
        expResult.add("AAAF");
        expResult.add("CCCY");
        expResult.add("DDDW");
        
        PeptideFactory pb = new PeptideFactory(false);
        pb.setProtease("Chymotrypsin");
        pb.setSequence("AAAFCCCYDDDW");
        pb.setMissedCleavages(0);
        pb.start();
        
        List<String> result = new ArrayList<>();
        List<Peptide> peptide = pb.GetNext(0);
        while(peptide != null) {
            for(int i=0;i<peptide.size();i++) {
                result.add(peptide.get(i).sequence());
            }
            peptide = pb.GetNext(0);
        }
        
        assertEquals(expResult,result); 
    }
    
    @Test
    public void testChymotrypsin_Expasy() {
        System.out.println("Chymotrypsin - Expasy");
        List<String> expResult = new ArrayList<>();
        expResult.add("AAAF");
        expResult.add("CCCY");
        expResult.add("DDDW");
        expResult.add("AAAFPCCCYPDDDWPEEEWM");
        
        PeptideFactory pb = new PeptideFactory(true);
        pb.setProtease("Chymotrypsin");
        pb.setSequence("AAAFCCCYDDDWAAAFPCCCYPDDDWPEEEWM");
        pb.setMissedCleavages(0);
        pb.start();
        
        List<String> result = new ArrayList<>();
        List<Peptide> peptide = pb.GetNext(0);
        while(peptide != null) {
            for(int i=0;i<peptide.size();i++) {
                result.add(peptide.get(i).sequence());
            }
            peptide = pb.GetNext(0);
        }
        
        assertEquals(expResult,result); 
    }
    
    @Test
    public void testAspN_Expasy() {
        System.out.println("Asp-N - Expasy");
        List<String> expResult = new ArrayList<>();
        expResult.add("TTTT");
        expResult.add("DTTTT");
        
        PeptideFactory pb = new PeptideFactory(true);
        pb.setProtease("AspN");
        pb.setSequence("TTTTDTTTT");
        pb.setMissedCleavages(0);
        pb.start();
        
        List<String> result = new ArrayList<>();
        List<Peptide> peptide = pb.GetNext(0);
        while(peptide != null) {
            for(int i=0;i<peptide.size();i++) {
                result.add(peptide.get(i).sequence());
            }
            peptide = pb.GetNext(0);
        }
        
        assertEquals(expResult,result); 
    }
    
    @Test
    public void testAspNGlu_Expasy() {
        System.out.println("Asp-N + Glu - Expasy");
        List<String> expResult = new ArrayList<>();
        expResult.add("TTTT");
        expResult.add("DTTTT");
        expResult.add("ETTT");
        
        PeptideFactory pb = new PeptideFactory(true);
        pb.setProtease("AspN/N->D");
        pb.setSequence("TTTTDTTTTETTT");
        pb.setMissedCleavages(0);
        pb.start();
        
        List<String> result = new ArrayList<>();
        List<Peptide> peptide = pb.GetNext(0);
        while(peptide != null) {
            for(int i=0;i<peptide.size();i++) {
                result.add(peptide.get(i).sequence());
            }
            peptide = pb.GetNext(0);
        }
        
        assertEquals(expResult,result); 
    }
    
    @Test
    public void testGluC_Expasy() {
        System.out.println("GluC - Expasy");
        List<String> expResult = new ArrayList<>();
        expResult.add("TTTE");
        expResult.add("TTT");
        
        PeptideFactory pb = new PeptideFactory(true);
        pb.setProtease("GluC");
        pb.setSequence("TTTETTT");
        pb.setMissedCleavages(0);
        pb.start();
        
        List<String> result = new ArrayList<>();
        List<Peptide> peptide = pb.GetNext(0);
        while(peptide != null) {
            for(int i=0;i<peptide.size();i++) {
                result.add(peptide.get(i).sequence());
            }
            peptide = pb.GetNext(0);
        }
        
        assertEquals(expResult,result); 
    }
    
    @Test
    public void testLysC_Expasy() {
        System.out.println("LysC- Expasy");
        List<String> expResult = new ArrayList<>();
        expResult.add("TTTK");
        expResult.add("TTT");
        
        PeptideFactory pb = new PeptideFactory(true);
        pb.setProtease("LysC");
        pb.setSequence("TTTKTTT");
        pb.setMissedCleavages(0);
        pb.start();
        
        List<String> result = new ArrayList<>();
        List<Peptide> peptide = pb.GetNext(0);
        while(peptide != null) {
            for(int i=0;i<peptide.size();i++) {
                result.add(peptide.get(i).sequence());
            }
            peptide = pb.GetNext(0);
        }
        
        assertEquals(expResult,result); 
    }
    
    @Test
    public void testTrypsin_Expasy() {
        System.out.println("Trypsin - Expasy");
        List<String> expResult = new ArrayList<>();
        expResult.add("TTTR");
        expResult.add("TTTK");
        expResult.add("TTTWK");
        expResult.add("PTTTMR");
        expResult.add("PTTTKPTTTRPTTTCKDTTTDKDTTTCKYTTTCKHTTTCRK");
        expResult.add("TTTR");
        expResult.add("RHTTTR");
        expResult.add("RR");
        expResult.add("TTT");
        
        PeptideFactory pb = new PeptideFactory(true);
        pb.setProtease("Trypsin");
        pb.setSequence("TTTRTTTKTTTWKPTTTMRPTTTKPTTTRPTTTCKDTTTDKDTTTCKYTTTCKHTTTCRKTTTRRHTTTRRRTTT");
        pb.setMissedCleavages(0);
        pb.start();
        
        List<String> result = new ArrayList<>();
        List<Peptide> peptide = pb.GetNext(0);
        while(peptide != null) {
            for(int i=0;i<peptide.size();i++) {
                result.add(peptide.get(i).sequence());
            }
            peptide = pb.GetNext(0);
        }
        
        assertEquals(expResult,result); 
    }
    
    @Test
    public void testTrypsin_RK() {
        System.out.println("Trypsin - [RK]");
        List<String> expResult = new ArrayList<>();
        expResult.add("TTTR");
        expResult.add("TTTK");
        expResult.add("TTTWK");
        expResult.add("PTTTMR");
        expResult.add("PTTTK");
        expResult.add("PTTTR");
        expResult.add("PTTTCK");
        expResult.add("DTTTDK");
        expResult.add("DTTTCK");
        expResult.add("YTTTCK");
        expResult.add("HTTTCR");
        expResult.add("K");
        expResult.add("TTTR");
        expResult.add("R");
        expResult.add("HTTTR");
        expResult.add("R");
        expResult.add("R");
        expResult.add("TTT");
        
        PeptideFactory pb = new PeptideFactory(false);
        pb.setProtease("Trypsin");
        pb.setSequence("TTTRTTTKTTTWKPTTTMRPTTTKPTTTRPTTTCKDTTTDKDTTTCKYTTTCKHTTTCRKTTTRRHTTTRRRTTT");
        pb.setMissedCleavages(0);
        pb.start();
        
        List<String> result = new ArrayList<>();
        List<Peptide> peptide = pb.GetNext(0);
        while(peptide != null) {
            for(int i=0;i<peptide.size();i++) {
                result.add(peptide.get(i).sequence());
            }
            peptide = pb.GetNext(0);
        }
        
        assertEquals(expResult,result); 
    }
    
    @Test
    public void testPepsin13_Expasy() {
        System.out.println("Pepsin ph=1.3 - Expasy");
        List<String> expResult = new ArrayList<>();
        expResult.add("TTTT");
        expResult.add("F");
        expResult.add("TTTRT");
        expResult.add("FTTTHT");
        expResult.add("FTTTKT");
        expResult.add("FTTTPTF");
        expResult.add("TTTTRF");
        expResult.add("TTTTF");
        expResult.add("PTTT");
        expResult.add("FTPTTT");
        
        PeptideFactory pb = new PeptideFactory(true);
        pb.setProtease("Pepsin, pH=1.3");
        pb.setSequence("TTTTFTTTRTFTTTHTFTTTKTFTTTPTFTTTTRFTTTTFPTTTFTPTTT");
        pb.setMissedCleavages(0);
        pb.start();
        
        List<String> result = new ArrayList<>();
        List<Peptide> peptide = pb.GetNext(0);
        while(peptide != null) {
            for(int i=0;i<peptide.size();i++) {
                result.add(peptide.get(i).sequence());
            }
            peptide = pb.GetNext(0);
        }
        
        assertEquals(expResult,result); 
    }
    
    @Test
    public void testPepsin13_FL() {
        System.out.println("Pepsin ph=1.3 - [FL]");
        List<String> expResult = new ArrayList<>();
        expResult.add("TTTT");
        expResult.add("F");
        expResult.add("TTTRT");
        expResult.add("F");
        expResult.add("TTTHT");
        expResult.add("F");
        expResult.add("TTTKT");
        expResult.add("F");
        expResult.add("TTTPT");
        expResult.add("F");
        expResult.add("TTTTR");
        expResult.add("F");
        expResult.add("TTTT");
        expResult.add("F");
        expResult.add("PTTT");
        expResult.add("F");
        expResult.add("TPTTT");
        
        PeptideFactory pb = new PeptideFactory(false);
        pb.setProtease("Pepsin, pH=1.3");
        pb.setSequence("TTTTFTTTRTFTTTHTFTTTKTFTTTPTFTTTTRFTTTTFPTTTFTPTTT");
        pb.setMissedCleavages(0);
        pb.start();
        
        List<String> result = new ArrayList<>();
        List<Peptide> peptide = pb.GetNext(0);
        while(peptide != null) {
            for(int i=0;i<peptide.size();i++) {
                result.add(peptide.get(i).sequence());
            }
            peptide = pb.GetNext(0);
        }
        
        assertEquals(expResult,result); 
    }
    
    @Test
    public void testPepsin20_Expasy() {
        System.out.println("Pepsin ph>2.0 - Expasy");
        List<String> expResult = new ArrayList<>();
        expResult.add("TTTT");
        expResult.add("W");
        expResult.add("TTTRT");
        expResult.add("WTTTHT");
        expResult.add("WTTTKT");
        expResult.add("WTTTPTW");
        expResult.add("TTTTRW");
        expResult.add("TTTTW");
        expResult.add("PTTT");
        expResult.add("WTPTTT");
        
        PeptideFactory pb = new PeptideFactory(true);
        pb.setProtease("Pepsin, pH=2.0");
        pb.setSequence("TTTTWTTTRTWTTTHTWTTTKTWTTTPTWTTTTRWTTTTWPTTTWTPTTT");
        pb.setMissedCleavages(0);
        pb.start();
        
        List<String> result = new ArrayList<>();
        List<Peptide> peptide = pb.GetNext(0);
        while(peptide != null) {
            for(int i=0;i<peptide.size();i++) {
                result.add(peptide.get(i).sequence());
            }
            peptide = pb.GetNext(0);
        }
        
        assertEquals(expResult,result); 
    }
    
    @Test
    public void testPepsin20_FLWY() {
        System.out.println("Pepsin ph>2.0 - [FLWY]");
        List<String> expResult = new ArrayList<>();
        expResult.add("TTTT");
        expResult.add("W");
        expResult.add("TTTRT");
        expResult.add("W");
        expResult.add("TTTHT");
        expResult.add("W");
        expResult.add("TTTKT");
        expResult.add("W");
        expResult.add("TTTPT");
        expResult.add("W");
        expResult.add("TTTTR");
        expResult.add("W");
        expResult.add("TTTT");
        expResult.add("W");
        expResult.add("PTTT");
        expResult.add("W");
        expResult.add("TPTTT");
        
        PeptideFactory pb = new PeptideFactory(false);
        pb.setProtease("Pepsin, pH=2.0");
        pb.setSequence("TTTTWTTTRTWTTTHTWTTTKTWTTTPTWTTTTRWTTTTWPTTTWTPTTT");
        pb.setMissedCleavages(0);
        pb.start();
        
        List<String> result = new ArrayList<>();
        List<Peptide> peptide = pb.GetNext(0);
        while(peptide != null) {
            for(int i=0;i<peptide.size();i++) {
                result.add(peptide.get(i).sequence());
            }
            peptide = pb.GetNext(0);
        }
        
        assertEquals(expResult,result); 
    }
    
    @Test
    public void testNon_specific() {
        System.out.println("Non-specific - Expasy");
        List<String> expResult = new ArrayList<>();
        expResult.add("A");
        expResult.add("B");
        expResult.add("C");
        expResult.add("D");
        
        PeptideFactory pb = new PeptideFactory(true);
        pb.setProtease("Non-specific");
        pb.setSequence("ABCD");
        pb.setMissedCleavages(0);
        pb.start();
        
        List<String> result = new ArrayList<>();
        List<Peptide> peptide = pb.GetNext(0);
        while(peptide != null) {
            for(int i=0;i<peptide.size();i++) {
                result.add(peptide.get(i).sequence());
            }
            peptide = pb.GetNext(0);
        }
        
        assertEquals(expResult,result); 
    }
}
