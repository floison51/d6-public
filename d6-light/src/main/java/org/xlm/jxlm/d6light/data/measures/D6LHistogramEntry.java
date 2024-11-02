package org.xlm.jxlm.d6light.data.measures;

/**
 * Histogram entry
 * @author Loison
 *
 */
public class D6LHistogramEntry {
	
	public enum HistoKeyEnum {
		nbDirectedLinksToCompo,
		nbDirectedLinksFromKit,
		nbComponentsBelongingToBusinessLotHierachy,
		nbComponentsBelongingToNotBusinessLotHierachy
	}
	
	private int id;

    private HistoKeyEnum histoKey;

	private long longValue;

	D6LHistogramEntry() {
		// For serialization
		super();
	}
	
	D6LHistogramEntry( int id, HistoKeyEnum histoKey, long longValue ) {
		super();
		this.id = id;
		this.histoKey = histoKey;
		this.longValue = longValue;
	}

	public long getLongValue() {
		return longValue;
	}

	public void setLongValue(int longValue) {
		this.longValue = longValue;
	}

	public HistoKeyEnum getHistoKey() {
		return histoKey;
	}

	public int getId() {
		return id;
	}

}
