/**
 * Copyright (c) 2015, JULIE Lab.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public License (LGPL) v3.0
 */

package de.julielab.jcore.consumer.bionlp09event.utils;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Writer;

import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;
import org.apache.uima.fit.factory.JCasBuilder;
import org.apache.uima.fit.factory.JCasFactory;

import de.julielab.jcore.consumer.bionlpformat.utils.EntityWriter;
import de.julielab.jcore.types.EntityMention;

public class EntityWriterTest {
	private static final String ENTITY_T13 = "T13	Entity 322 330	tyrosine\n";
	
	private static final String DOCUMENT_TEXT = "Interferons inhibit activation of STAT6 by interleukin 4 in human monocytes by inducing SOCS-1 gene expression. \n" + 
												"Interferons (IFNs) inhibit induction by IL-4 of multiple genes in human monocytes. However, the mechanism by which IFNs mediate this inhibition has not been defined. IL-4 activates gene expression by inducing tyrosine phosphorylation, homodimerization, and nuclear translocation of the latent transcription factor, STAT6 (signal transducer and activator of transcription-6). STAT6-responsive elements are characteristically present in the promoters of IL-4-inducible genes. Because STAT6 activation is essential for IL-4-induced gene expression, we examined the ability of type I and type II IFNs to regulate activation of STAT6 by IL-4 in primary human monocytes. Pretreatment of monocytes with IFN-beta or IFN-gamma, but not IL-1, IL-2, macrophage colony-stimulating factor, granulocyte/macrophage colony-stimulating factor, IL-6, or transforming growth factor beta suppressed activation of STAT6 by IL-4. This inhibition was associated with decreased tyrosine phosphorylation and nuclear translocation of STAT6 and was not evident unless the cells were preincubated with IFN for at least 1 hr before IL-4 stimulation. Furthermore, inhibition by IFN could be blocked by cotreatment with actinomycin D and correlated temporally with induction of the JAK/STAT inhibitory gene, SOCS-1. Forced expression of SOCS-1 in a macrophage cell line, RAW264, markedly suppressed trans-activation of an IL-4-inducible reporter as well as IL-6- and IFN-gamma-induced reporter gene activity. These findings demonstrate that IFNs inhibit IL-4-induced activation of STAT6 and STAT6-dependent gene expression, at least in part, by inducing expression of SOCS-1.";
	
	private JCas cas;
	private EntityWriter entityWriter;
	private Writer writer;
	private EntityMention entityT13;

	@Before
	public void setUp() throws Exception{
		cas = JCasFactory.createJCas("src/test/resources/types/jcore-semantics-biology-types");
		
		writer = createMock(Writer.class);
		entityWriter = new EntityWriter(writer, DOCUMENT_TEXT);
				
		entityT13 = new EntityMention(cas);
		entityT13.setBegin(322);
		entityT13.setEnd(330);
		entityT13.setId("T13");
	}

	@Test
	public void testWriteEntity() throws Exception{
		writer.write(ENTITY_T13);
		replay(writer);

		entityWriter.writeEntity(entityT13);
		
		verify(writer);
	}
	
	@Test
	public void testIsWritten() throws Exception{
		assertFalse(entityWriter.isWritten(entityT13));
		writer.write(ENTITY_T13);
		replay(writer);
		
		entityWriter.writeEntity(entityT13);
		assertTrue(entityWriter.isWritten(entityT13));
	}
	
	@Test
	public void testClose() throws IOException{
		writer.close();
		replay(writer);
		
		entityWriter.close();
		verify(writer);
	}

}
