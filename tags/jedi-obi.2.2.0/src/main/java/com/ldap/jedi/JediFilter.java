package com.ldap.jedi;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;

public class JediFilter {

	private HashMap<String, Object> mapFilter = new HashMap<String, Object>();

	public static final String ALIAS = "alias";
	public static final String PATH = "path";
	public static final String ATTRIBUTES = "attributes";

	public static final String GUID = "guid";
	public static final String DN = "dn";

	public static final String FILTER = "filter";
	public static final String SUBTREE = "subtree";
	public static final String PAGESIZE = "pageSize";
	public static final String LIMITATION = "limitation";
	public static final String SORTED = "sorted";

	public static final String APPROXIMATION = "approximation";
	public static final String INDICE = "indice";
	public static final String ATTRIBUTE = "attribute";
	public static final String METRIC = "metric";

	public void setAlias(String alias) {
		mapFilter.put(ALIAS, alias);
	}

	public String getAlias() {
		return (String) mapFilter.get(ALIAS);
	}

	public void setPath(String path) {
		mapFilter.put(PATH, path);
	}

	public String getPath() {
		return (String) mapFilter.get(PATH);
	}

	public void setAttributesList(List<String> list) {
		mapFilter.put(ATTRIBUTES, list);
	}

	@SuppressWarnings("unchecked")
	public List<String> getAttributesList() {
		return (List<String>) mapFilter.get(ATTRIBUTES);
	}

	public void setGuid(byte[] guid) {
		mapFilter.put(GUID, guid);
	}

	public byte[] getGuid() {
		return (byte[]) mapFilter.get(GUID);
	}

	public void setDn(String dn) {
		mapFilter.put(DN, dn);
	}

	public String getDn() {
		return (String) mapFilter.get(DN);
	}

	public void setFilter(String filter) {
		mapFilter.put(FILTER, filter);
	}

	public String getFilter() {
		return (String) mapFilter.get(FILTER);
	}

	public void setSubtree(Boolean subtree) {
		mapFilter.put(SUBTREE, subtree);
	}

	public Boolean getSubtree() {
		return (Boolean) mapFilter.get(SUBTREE);
	}

	public void setPageSize(Integer size) {
		mapFilter.put(PAGESIZE, size);
	}

	public Integer getPageSize() {
		return (Integer) mapFilter.get(PAGESIZE);
	}

	public void setLimitation(Integer limitation) {
		mapFilter.put(LIMITATION, limitation);
	}

	public Integer getLimitation() {
		return (Integer) mapFilter.get(LIMITATION);
	}

	public void setSorted(Comparator<JediObject> comp) {
		mapFilter.put(SORTED, comp);
	}

	@SuppressWarnings("unchecked")
	public Comparator<JediObject> getSorted() {
		return (Comparator<JediObject>) mapFilter.get(SORTED);
	}

	public void setApproximation(String approximation) {
		mapFilter.put(APPROXIMATION, approximation);
	}

	public String getApproximation() {
		return (String) mapFilter.get(APPROXIMATION);
	}

	public void setIndice(Integer indice) {
		mapFilter.put(INDICE, indice);
	}

	public Integer getIndice() {
		return (Integer) mapFilter.get(INDICE);
	}

	public void setAttribute(String attribute) {
		mapFilter.put(ATTRIBUTE, attribute);
	}

	public String getAttribute() {
		return (String) mapFilter.get(ATTRIBUTE);
	}

	public void setMetric(AbstractStringMetric metric) {
		mapFilter.put(METRIC, metric);
	}

	public AbstractStringMetric getMetric() {
		return (AbstractStringMetric) mapFilter.get(METRIC);
	}

}