package edu.berkeley.nlp.lm;

import java.io.Serializable;

import edu.berkeley.nlp.lm.map.ContextEncodedNgramMap;
import edu.berkeley.nlp.lm.util.Annotations.OutputParameter;
import edu.berkeley.nlp.lm.values.ProbBackoffPair;
import edu.berkeley.nlp.lm.values.ProbBackoffValueContainer;

/**
 * Language model implementation which uses Katz-style backoff computation.
 * 
 * @author adampauls
 * 
 * @param <W>
 */
public class ContextEncodedProbBackoffLm<W> extends AbstractContextEncodedNgramLanguageModel<W> implements ContextEncodedNgramLanguageModel<W>, Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final ContextEncodedNgramMap<ProbBackoffPair> map;

	private final ProbBackoffValueContainer values;

	/**
	 * Fixed constant returned when computing the log probability for an n-gram
	 * whose last word is not in the vocabulary. Note that this is different
	 * from the log prob of the <code>unk</code> tag probability.
	 * 
	 */
	private final float oovWordLogProb;

	public ContextEncodedProbBackoffLm(final int lmOrder, final WordIndexer<W> wordIndexer, final ContextEncodedNgramMap<ProbBackoffPair> map,
		final ConfigOptions opts) {
		super(lmOrder, wordIndexer);
		oovWordLogProb = (float) opts.unknownWordLogProb;
		this.map = map;
		this.values = (ProbBackoffValueContainer) map.getValues();

	}
	
	

	@Override
	public float getLogProb(final long contextOffset, final int contextOrder, final int word, @OutputParameter final LmContextInfo outputContext) {
		final ContextEncodedNgramMap<ProbBackoffPair> localMap = map;
		int currContextOrder = contextOrder;
		long currContextOffset = contextOffset;
		float backoffSum = 0.0f;
		while (true) {
			final long offset = localMap.getOffset(currContextOffset, currContextOrder, word);
			final int ngramOrder = currContextOrder + 1;
			final float prob = offset < 0 ? Float.NaN : values.getProb(ngramOrder, offset);
			if (offset >= 0 && !Float.isNaN(prob)) {
				setOutputContext(outputContext, offset, ngramOrder);
				return backoffSum + prob;
			} else if (currContextOrder >= 0) {
				final long backoffIndex = currContextOffset;
				final float backOff = backoffIndex < 0 ? 0.0f : values.getBackoff(currContextOrder, backoffIndex);
				backoffSum += (Float.isNaN(backOff) ? 0.0f : backOff);
				currContextOrder--;
				currContextOffset = currContextOrder < 0 ? 0 : values.getContextOffset(currContextOffset, currContextOrder + 1);
			} else {
				return oovWordLogProb;
			}
		}
	}

	@Override
	public LmContextInfo getOffsetForNgram(final int[] ngram, final int startPos, final int endPos) {
		return map.getOffsetForNgram(ngram, startPos, endPos);
	}

	@Override
	public int[] getNgramForOffset(final long contextOffset, final int contextOrder, final int word) {
		return map.getNgramForOffset(contextOffset, contextOrder, word);
	}

	/**
	 * @param outputContext
	 * @param offset
	 * @param ngramOrder
	 */
	private void setOutputContext(final LmContextInfo outputContext, final long offset, final int ngramOrder) {
		if (outputContext != null) {
			if (ngramOrder == lmOrder - 1) {
				final long suffixOffset = values.getContextOffset(offset, ngramOrder);
				outputContext.offset = suffixOffset;
				outputContext.order = ngramOrder - 1;
			} else {
				outputContext.offset = offset;
				outputContext.order = ngramOrder;

			}
		}
	}

	

}