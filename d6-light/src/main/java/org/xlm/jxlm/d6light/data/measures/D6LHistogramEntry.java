package org.xlm.jxlm.d6light.data.measures;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

/**
 * Histogram entry
 * @author Loison
 *
 */
@Entity
public class D6LHistogramEntry {
	
	public enum HistoKeyEnum {
		nbDirectedLinksToCompo,
		nbDirectedLinksFromKit,
		nbComponentsBelongingToBusinessLotHierachy,
		nbComponentsBelongingToNotBusinessLotHierachy
	}
	
	@Id
	@SequenceGenerator( name="D6LMeasureSeq", sequenceName="seq_D6LMeasure", initialValue = 0, allocationSize=0)
	private int id;
	
	@ManyToOne
	@Enumerated
	@Basic(optional=false)
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
