/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.enums;

public final class CacheManagerEnums {

	private CacheManagerEnums() {}

	public enum CacheManagerTableColumns {
		CACHE_MANAGER("Cache manager"),
		CACHE_NAME	("Cache name"),
		NUMBER_OF_ELEMENTS("# of elements"),
		ACTION("Action");

		String id;

		CacheManagerTableColumns(String id) {
			this.id = id;
		}

		public String get() {
			return id;
		}
	}

	public enum CacheNameEnum {
		BASE_LOOKUP_CACHE("BaseLookupCache"),
		I18N_CACHE("I18NCache"),
		LOOKUP_CACHE("lookupCache"),
		PRODUCT_FACTORY("ProductFactory"),
		PRODUCT_FACTORY_INFO("ProductFactoryInfo"),
		SECURITY_CACHE("SecurityCache"),
		UI_RESOURCE_CACHE("UiResourceCache");

		String id;

		CacheNameEnum(String id) {
			this.id = id;
		}

		public String get() {
			return id;
		}
	}

	public enum CachedProjectNameTableColumns {
		CACHED_PROJECT_NAME("Cached project name"),
		ACTION("Action");

		String id;

		CachedProjectNameTableColumns(String id) {
			this.id = id;
		}

		public String get() {
			return id;
		}
	}
}
