package de.julielab.jcore.consumer.entityevaluator;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;

import de.julielab.jcore.consumer.entityevaluator.EntityEvaluatorConsumer.OffsetMode;
import de.julielab.jcore.consumer.entityevaluator.EntityEvaluatorConsumer.OffsetScope;
import de.julielab.jcore.utility.index.JCoReTreeMapAnnotationIndex;

public class OffsetsColumn extends Column {

	private OffsetMode offsetMode;
	private JCoReTreeMapAnnotationIndex<Long, ? extends Annotation> sentenceIndex;
	private OffsetScope offsetScope;
	private Map<Annotation, TreeMap<Integer, Integer>> numWsMaps;
	private TreeMap<Integer, Integer> documentNumWsMap;

	public OffsetsColumn(TreeMap<Integer, Integer> numWsMap, OffsetMode offsetMode) {
		this.documentNumWsMap = numWsMap;
		this.offsetMode = offsetMode;
		this.offsetScope = OffsetScope.Document;
		this.numWsMaps = new HashMap<>();
	}

	public OffsetsColumn(JCoReTreeMapAnnotationIndex<Long, ? extends Annotation> sentenceIndex, OffsetMode offsetMode) {
		this.sentenceIndex = sentenceIndex;
		this.offsetMode = offsetMode;
		this.offsetScope = OffsetScope.Sentence;
		this.numWsMaps = new HashMap<>();
	}

	public OffsetsColumn(OffsetMode offsetMode) {
		this.offsetMode = offsetMode;
		this.offsetScope = OffsetScope.Document;
		this.numWsMaps = new HashMap<>();
	}

	@Override
	public String getValue(TOP a) {
		Annotation an = (Annotation) a;
		TreeMap<Integer, Integer> numWsMap = documentNumWsMap;
		int annotationOffset = 0;

		if (offsetScope == OffsetScope.Sentence) {
			Annotation s = sentenceIndex.get(an);
			if (this.offsetMode == OffsetMode.NonWsCharacters)
				numWsMap = getNumWsMapForSentence(s);
			annotationOffset = s.getBegin();
		}

		return getOffsets(an, numWsMap, annotationOffset);
	}

	/**
	 * Creates and caches whitespace maps for sentence annotations.
	 * 
	 * @param s
	 *            The sentence for which a whitespace map is requested.
	 * @return A map that can be queried for each position of the sentence text
	 *         how many whitespaces before that position exist.
	 */
	private TreeMap<Integer, Integer> getNumWsMapForSentence(Annotation s) {
		TreeMap<Integer, Integer> numWsMap = numWsMaps.get(s);
		if (numWsMap == null) {
			numWsMap = EntityEvaluatorConsumer.createNumWsMap(s.getCoveredText());
			numWsMaps.put(s, numWsMap);
		}
		return numWsMap;
	}

	private String getOffsets(Annotation an, TreeMap<Integer, Integer> numWsMap, int annotationOffset) {
		int beginOffset;
		int endOffset;
		switch (offsetMode) {
		case CharacterSpan:
			beginOffset = an.getBegin() - annotationOffset;
			endOffset = an.getEnd() - annotationOffset;
			break;
		case NonWsCharacters:
			// for both offsets, subtract the number of preceding
			// white spaces up to the respective offset
			beginOffset = (an.getBegin() - annotationOffset) - numWsMap.floorEntry(an.getBegin()).getValue();
			// we even have to subtract one more because we count
			// actual characters while UIMA counts spans
			endOffset = (an.getEnd() - annotationOffset) - numWsMap.floorEntry(an.getEnd()).getValue() - 1;
			break;
		default:
			throw new IllegalArgumentException("Offset mode \"" + offsetMode + "\" is currently unsupported.");
		}
		String begin = String.valueOf(beginOffset);
		String end = String.valueOf(endOffset);
		return begin + "\t" + end;
	}

	public void reset() {
		numWsMaps.clear();
	}

}