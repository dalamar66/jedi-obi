package com.ldap.jedi;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Jaro;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.MongeElkan;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWatermanGotoh;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWatermanGotohWindowedAffine;
import uk.ac.shef.wit.simmetrics.similaritymetrics.TagLinkToken;

/**
 * File : JediFilterConstants.java 
 * Component : Version : 1.0 
 * Creation date : 2010-03-10 
 * Modification date : 2010-03-10
 */

public interface JediFilterConstants {

	public static AbstractStringMetric METRIC_JARO = new Jaro();
	public static AbstractStringMetric METRIC_JARO_WINKLER = new JaroWinkler();
	public static AbstractStringMetric METRIC_LEVENSHTEIN = new Levenshtein();
	public static AbstractStringMetric METRIC_MONGE_ELKAN = new MongeElkan();
	public static AbstractStringMetric METRIC_SMITH_WATERMAN_GOTOH = new SmithWatermanGotoh();
	public static AbstractStringMetric METRIC_SMITH_WATERMAN_GOTOH_WINDOWED_AFFINE = new SmithWatermanGotohWindowedAffine();
	public static AbstractStringMetric METRIC_TAG_LINK_TOKEN = new TagLinkToken();

}
