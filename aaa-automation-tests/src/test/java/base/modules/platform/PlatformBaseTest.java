/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.platform;

import java.util.List;

import aaa.admin.metadata.security.PARMetaData.GeneralPARTab;
import aaa.admin.modules.security.par.IPAR;
import aaa.admin.modules.security.par.PAR;
import aaa.admin.modules.security.par.PARType;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;

public class PlatformBaseTest extends BaseTest {

    public String parNameAndCode1;
    public String parNameAndCode2;

    public synchronized List<String> create2PAR() {
        PAR par = ((PAR) PARType.PRODUCT_ACCESS_ROLE.get());
        List<String> roles = par.searchForRoles();
        if (roles.isEmpty()) {
            roles.add(createPAR());
            roles.add(createPAR());
        } else if (roles.size() == 1) {
            roles.add(createPAR());
        }
        parNameAndCode1 = roles.get(0);
        parNameAndCode2 = roles.get(1);
        return roles;
    }

    private String createPAR() {
        IPAR par = PARType.PRODUCT_ACCESS_ROLE.get();
        TestData tdPAR = testDataManager.par.get(PARType.PRODUCT_ACCESS_ROLE).getTestData("DataGather", "TestData");
        String parCode = tdPAR.getValue(GeneralPARTab.class.getSimpleName(), GeneralPARTab.ROLE_CODE.getLabel());
        String parName = tdPAR.getValue(GeneralPARTab.class.getSimpleName(), GeneralPARTab.ROLE_NAME.getLabel());
        par.create(tdPAR);
        return String.format("%s (%s)", parName, parCode);
    }

}
