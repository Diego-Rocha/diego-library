package io.diego.lib.reveng;

import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;

public class CustomReverseEngineeringStrategy extends DelegatingReverseEngineeringStrategy {

	public CustomReverseEngineeringStrategy(ReverseEngineeringStrategy delegate) {
		super(delegate);
	}

	@Override
	public boolean excludeColumn(TableIdentifier identifier, String columnName) {
		if (columnName.toLowerCase().startsWith("sys_period")) {
			return true;
		}
		return super.excludeColumn(identifier, columnName);
	}

}
