package edu.berkeley.nlp.lm;

import java.io.Serializable;
import java.util.List;

/**
 * Default implementation of all NGramLanguageModel functionality except
 * getLogProb(int[] ngram, int startPos, int endPos)
 * 
 * @see getLogProb(int[] , int , int ) function.
 * 
 * @author adampauls
 * 
 * @param <W>
 */
public abstract class AbstractArrayEncodedNgramLanguageModel<W> implements ArrayEncodedNgramLanguageModel<W>, Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final int lmOrder;

	private final WordIndexer<W> wordIndexer;

	public AbstractArrayEncodedNgramLanguageModel(final int lmOrder, final WordIndexer<W> wordIndexer) {
		this.lmOrder = lmOrder;
		this.wordIndexer = wordIndexer;
	}

	@Override
	public int getLmOrder() {
		return lmOrder;
	}

	@Override
	public float scoreSentence(final List<W> sentence) {
		return ArrayEncodedNgramLanguageModel.DefaultImplementations.scoreSentence(sentence, this);
	}

	@Override
	public float getLogProb(final List<W> phrase) {
		return ArrayEncodedNgramLanguageModel.DefaultImplementations.getLogProb(phrase, this);
	}

	@Override
	public float getLogProb(final int[] ngram) {
		return ArrayEncodedNgramLanguageModel.DefaultImplementations.getLogProb(ngram, this);
	}

	@Override
	public WordIndexer<W> getWordIndexer() {
		return wordIndexer;
	}

	@Override
	public abstract float getLogProb(final int[] ngram, int startPos, int endPos);

}