package base.modules.platform.admin.agencyvendor;

import org.testng.annotations.Test;

import aaa.admin.metadata.agencyvendor.BrandMetaData;
import aaa.admin.modules.agencyvendor.AgencyVendorType;
import aaa.admin.modules.agencyvendor.brand.BrandType;
import aaa.admin.modules.agencyvendor.brand.defaulttabs.BrandTab;
import aaa.admin.pages.agencyvendor.BrandPage;
import aaa.main.enums.AdminConstants;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Rokas Lazdauskas
 * @name Test Brand Type creation
 * @scenario
 * 1. Open Admin app
 * 2. Create / Update Brand Type
 * @details
 */
public class TestBrandTypeCreation extends BaseTest {

    private BrandType brandType = new BrandType();
    private TestData tdBrand = testDataManager.agency.get(AgencyVendorType.BRAND);

    @Test(groups = {"7.2_All_UC_Add/EditBrandType"})
    @TestInfo(component = "Platform.Admin")
    public void testBrandTypeCreation() {

        adminApp().open();

        log.info("TEST: Create Brand Type");

        //create
        String brandTypeName =
                tdBrand.getValue("BrandType", "TestData", BrandTab.class.getSimpleName(), BrandMetaData.BrandTab.ADD_BRAND_TYPE.getLabel(),
                        BrandMetaData.BrandTab.AddBrandTypeDialog.BRAND_TYPE_NAME.getLabel());
        String brandTypeCode =
                tdBrand.getValue("BrandType", "TestData", BrandTab.class.getSimpleName(), BrandMetaData.BrandTab.ADD_BRAND_TYPE.getLabel(),
                        BrandMetaData.BrandTab.AddBrandTypeDialog.BRAND_TYPE_CODE.getLabel());
        brandType.create(tdBrand.getTestData("BrandType", "TestData"));
        BrandPage.tableBrandType.getRow(AdminConstants.AdminBrandsTypeTable.NAME, brandTypeName).verify.present();

        //cancel
        brandType.update().start(brandTypeName);
        BrandPage.assetListAddBrandType.getControl(BrandMetaData.BrandTab.AddBrandTypeDialog.BRAND_TYPE_CODE.getLabel()).verify.enabled(false);
        BrandPage.assetListAddBrandType.getControl(BrandMetaData.BrandTab.AddBrandTypeDialog.BRAND_TYPE_NAME.getLabel()).verify.enabled();
        BrandTab.buttonCancel.click();
        BrandPage.tableBrandType.getRow(AdminConstants.AdminBrandsTypeTable.NAME, brandTypeName).verify.present();

        //check errors 
        brandType.update().perform(tdBrand.getTestData("BrandType", "TestData_EmptyName"), brandTypeName);
        BrandPage.assetListAddBrandType.getWarning(BrandMetaData.BrandTab.AddBrandTypeDialog.BRAND_TYPE_NAME.getLabel()).verify.value("Brand Type Name is required");

        //update brand type name
        brandTypeName =
                tdBrand.getValue("BrandType", "TestData_UpdateName", BrandTab.class.getSimpleName(), BrandMetaData.BrandTab.ADD_BRAND_TYPE.getLabel(),
                        BrandMetaData.BrandTab.AddBrandTypeDialog.BRAND_TYPE_NAME.getLabel());
        brandType.update().getView().fill(tdBrand.getTestData("BrandType", "TestData_UpdateName"));
        brandType.update().submit();
        BrandPage.tableBrandType.getRow(AdminConstants.AdminBrandsTypeTable.NAME, brandTypeName).verify.present();
        log.info("TEST: Update Brand Type (Brand Type Code = " + brandTypeCode + "; Brand Type Name = " + brandTypeName + ")");
    }
}
