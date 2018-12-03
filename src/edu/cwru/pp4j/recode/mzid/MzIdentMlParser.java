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


import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A light-weight mzIdentML parser that loads a subset of the total information
 * stored in an mzIdentML file. This is non-exhaustive and only include the
 * functionality we need to read a file and convert it to tab-delimited values.
 * 
 * @author Sean Maxwell
 */
public class MzIdentMlParser extends DefaultHandler {
    private static final Logger logger = LogManager.getFormatterLogger(MzIdentMlParser.class.getName());
    private final StringBuilder buffer;
    private final HashMap<String,String> dbSequences;
    private final HashMap<String,String> identificationResultScans;
    private final HashMap<String,Peptide> peptides;
    private final HashMap<String,Identification> identifications;
    private final HashMap<String,ArrayList<Modification>> modifications;
    private final HashMap<String,Evidence> evidences;
    private final HashMap<String,ArrayList<String>> evidenceRefs;
    
    private Peptide currentPeptide;
    private Modification currentModification;
    private Identification currentIdentification;
    private ArrayList<String> currentEvidenceRefs;
    private ArrayList<Modification> currentModifications;
    private boolean parsingSequence;
    private boolean parsingModification;
    private boolean parsingIdentification;
    private String identficationResultId;
    
    
    private static class mzIdentMLErrorHandler implements ErrorHandler {

        private String getParseExceptionInfo(SAXParseException spe) {
            String systemId = spe.getSystemId();

            if (systemId == null) {
                systemId = "null";
            }

            String info = "URI=" + systemId + " Line=" 
                + spe.getLineNumber() + ": " + spe.getMessage();

            return info;
        }

        @Override
        public void warning(SAXParseException spe) throws SAXException {
            logger.warn(getParseExceptionInfo(spe));
        }

        @Override
        public void error(SAXParseException spe) throws SAXException {
            String message = getParseExceptionInfo(spe);
            logger.error(message);
            throw new SAXException(message);
        }

        @Override
        public void fatalError(SAXParseException spe) throws SAXException {
            String message = getParseExceptionInfo(spe);
            logger.fatal(message);
            throw new SAXException(message);
        }
    }
    
    private HashMap<String,String> newAttributeMap(Attributes atts) {
        HashMap<String,String> attributes = new HashMap();
        for(int i=0;i<atts.getLength();i++) {
            String name = atts.getQName(i);
            String value = atts.getValue(name);
            attributes.put(name, value);
        }
        return attributes;
    }
    
    @Override
    public void startDocument() {
        
    }
    
    @Override
    public void endDocument() {
        
    }
    
    @Override
    public void startElement(String namespaceURI,
                             String localName,
                             String qName, 
                             Attributes atts) throws SAXException {
        if(localName.equals("DBSequence")) {
            dbSequences.put(atts.getValue("id"),atts.getValue("accession"));
        }
        else if(localName.equals("Peptide")) {
            currentPeptide = new Peptide(newAttributeMap(atts));
            currentModifications = new ArrayList<>();
        }
        else if(localName.equals("PeptideEvidence")) {
            Evidence evidence = new Evidence(newAttributeMap(atts));
            evidences.put(evidence.identifier(),evidence);
        }
        else if(localName.equals("PeptideSequence")) {
            parsingSequence = true;
        }
        else if(localName.equals("Modification")) {
            parsingModification = true;
            currentModification = new Modification(newAttributeMap(atts));
        }
        else if(localName.equals("SpectrumIdentificationItem")) {
            parsingIdentification = true;
            currentIdentification = new Identification(newAttributeMap(atts));
            currentEvidenceRefs = new ArrayList();
        }
        else if(localName.equals("SpectrumIdentificationResult")) {
            identficationResultId = atts.getValue("id");
        }
        else if(localName.equals("PeptideEvidenceRef")) {
            if(parsingIdentification) {
                currentEvidenceRefs.add(atts.getValue("peptideEvidence_ref"));
            }
        }
        else if(localName.equals("cvParam")) {
            if(parsingModification) {
                currentModification.put(atts.getValue("name"),atts.getValue("value"));
            }
            else if(parsingIdentification) {
                currentIdentification.put(atts.getValue("name"),atts.getValue("value"));
            }
            else if(identficationResultId != null) {
                if(atts.getValue("name").equals("scan number(s)")) {
                    if(identifications.containsKey(identficationResultId)) {
                        throw new SAXException("Multiple cvParams for scan number???");
                    }
                    identificationResultScans.put(identficationResultId,atts.getValue("value"));
                }
            }
            else if(currentPeptide != null) {
                if(atts.getValue("value") != null) {
                    currentPeptide.put(atts.getValue("name"),atts.getValue("value"));
                }
                else {
                    currentPeptide.put(atts.getValue("name"),"true");
                }
            }
        }
        

    }
    
    @Override
    public void endElement(String namespaceURI, 
                           String localName, 
                           String qName) throws SAXException {
        if(localName.equals("Peptide")) {
            peptides.put(currentPeptide.get("id"),currentPeptide);
            modifications.put(currentPeptide.get("id"),currentModifications);
            currentPeptide = null;
            currentModifications = null;
        }
        else if(localName.equals("PeptideSequence")) {
            currentPeptide.put("sequence",buffer.toString());
            parsingSequence = false;
            buffer.delete(0, buffer.length());
        }
        else if(localName.equals("Modification")) {
            currentModifications.add(currentModification);
            parsingModification=false;
        }
        else if(localName.equals("SpectrumIdentificationItem")) {
            currentIdentification.put("SIR", identficationResultId);
            identifications.put(currentIdentification.get("id"), currentIdentification);
            evidenceRefs.put(currentIdentification.get("id"), currentEvidenceRefs);
            currentIdentification = null;
            currentEvidenceRefs = null;
            parsingIdentification = false;
        }
        else if(localName.equals("SpectrumIdentificationResult")) {
            identficationResultId = null;
        }
    }
    
    /**
     * Overrides the XMLReader DefaultHandler SAX event for character data.
     *
     * @param ch Array of chars that were parsed
     * @param start At what position in ch[] the character data begins
     * @param length At what position in ch[] the character data ends
     *
     * @throws SAXException if an XML parsing exception if thrown
     */
    @Override
    public void characters(char[] ch,
                           int start,
                           int length) throws SAXException {
        if(parsingSequence) {
            buffer.append(ch, start, length);
        }
    }
    
    public MzIdentMlParser() {
        dbSequences = new HashMap();
        peptides = new HashMap();
        modifications = new HashMap();
        evidences = new HashMap();
        evidenceRefs = new HashMap();
        identificationResultScans = new HashMap();
        identifications = new HashMap();
        identficationResultId = null;
        currentPeptide = null;
        parsingSequence = false;
        parsingModification = false;
        parsingIdentification = false;
        buffer = new StringBuilder();
    }
        
    public boolean load(String path) {
        XMLReader xr;
        InputSource r;
        
        /* Create a new XMLReader */
        try {
            xr = XMLReaderFactory.createXMLReader();
        }
        catch(Exception e) {
            logger.error("threw %s creating XMLReader -> %s",e.getClass().getName(),e.getMessage());
            return false;
        }
                
        /* Create a new FileReader object for the XMLReader to read from */
        try {
            /* Force UTF-8 Encoding */
            r = new InputSource(new InputStreamReader(new FileInputStream(path),"UTF-8"));
        }
        catch(FileNotFoundException | UnsupportedEncodingException e) {
            logger.error("threw %s creating InputStreamReader -> %s",e.getClass().getName(),e.getMessage());
            return false;
        }
        
        /* Parse the file */
        try {
            xr.setContentHandler(this);
            xr.setErrorHandler(new mzIdentMLErrorHandler());
            xr.parse(r);
        }
        catch(IOException | SAXException e) {
            logger.error("threw %s parsing document -> %s",e.getClass().getName(),e.getMessage());
            return false;
        }
        
        return true;
    }
    
    public Collection<Identification> identifications() {
        return this.identifications.values();
    }
        
    public Peptide peptide(String id) {
        return this.peptides.get(id);
    }
    
    public String accession(String id) {
        return this.dbSequences.get(id);
    }
        
    public List<Modification> modifications(String id) {
        return this.modifications.get(id);
    }
    
    public Evidence evidence(String id) {
        return this.evidences.get(id);
    }
    
    public List<String> evidenceRefs(String id) {
        return this.evidenceRefs.get(id);
    }
    
    public HashMap<String,String> getIdentificationResultScans() {
        return identificationResultScans;
    }
    
}
