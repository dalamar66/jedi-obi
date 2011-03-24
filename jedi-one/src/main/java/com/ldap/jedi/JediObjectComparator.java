package com.ldap.jedi;

import java.util.Comparator;

public class JediObjectComparator implements Comparator<JediObject> {

	public static final int DN = 0;
	public static final int RDN = 1;

	private int property;

	public JediObjectComparator(int property) {
		super();
		this.property = property;
	}

	public int compare(JediObject o1, JediObject o2) {
		int comparaison = -1;
		String sn1 = null;
		String sn2 = null;

		try {
			switch (property) {
				case DN:
					sn1 = (String) (((JediObject) o1).getCompleteDN());
					sn2 = (String) (((JediObject) o2).getCompleteDN());

					comparaison = sn1.compareTo(sn2);

					break;
				case RDN:
					sn1 = (String) (((JediObject) o1).getRDN());
					sn2 = (String) (((JediObject) o2).getRDN());

					comparaison = sn1.compareTo(sn2);

					break;
				default:
					break;
			}
		} catch (Exception ex) {
			// Comparaison impossible ....
		}

		return comparaison;
	}

}