/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.product;

import aaa.admin.modules.product.moratorium.Moratorium;

public enum ProductType {
	MORATORIUM("Moratorium", new Moratorium());

	private String schemeName;

	private String productType;
	private IProduct product;

	ProductType(String productType, IProduct product) {
		this.productType = productType;
		this.product = product;
	}

	public String getName() {
		return productType;
	}

	public String getKey() {
		return product.getClass().getSimpleName();
	}

	public IProduct get() {
		return product;
	}
}
