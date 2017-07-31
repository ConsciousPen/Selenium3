package com.exigen.ipb.etcsa.utils;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public class LocalDateTimeSupplier implements Supplier<LocalDateTime>{

	@Override
	public LocalDateTime get() {
		return TimeSetterUtil.getInstance().getCurrentTime();
	}
	
}
