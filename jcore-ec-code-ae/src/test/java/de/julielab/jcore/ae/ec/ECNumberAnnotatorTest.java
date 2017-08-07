package de.julielab.jcore.ae.ec;

import static org.junit.Assert.*;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.julielab.jcore.ae.ecn.ECNumberAnnotator;
import de.julielab.jcore.types.Enzyme;

public class ECNumberAnnotatorTest {
	@Test
	public void simpleTest() throws Exception {
		// This test delivers a brief text with one EC number. We expect a
		// single Enzyme annotation for this number with the appropriate values
		// set.
		JCas jcas = JCasFactory.createJCas("de.julielab.jcore.types.jcore-semantics-biology-types");
		jcas.setDocumentText("Acetylesterase has number EC 3.1.1.6");
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(ECNumberAnnotator.class);
		ae.process(jcas);
		Enzyme enzyme = JCasUtil.selectSingle(jcas, Enzyme.class);
		assertEquals("3.1.1.6", enzyme.getSpecificType());
		assertEquals(26, enzyme.getBegin());
		assertEquals(36, enzyme.getBegin());
	}
}