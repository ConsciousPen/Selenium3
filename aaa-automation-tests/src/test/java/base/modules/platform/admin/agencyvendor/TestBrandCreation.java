package base.modules.platform.admin.agencyvendor;

import org.testng.annotations.Test;

import aaa.admin.metadata.agencyvendor.BrandMetaData.BrandTab;
import aaa.admin.modules.agencyvendor.AgencyVendorType;
import aaa.admin.modules.agencyvendor.brand.Brand;
import aaa.admin.modules.agencyvendor.brand.BrandType;
import aaa.admin.pages.agencyvendor.BrandPage;
import aaa.main.enums.AdminConstants;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.Button;

/**
 * @author Rokas Lazdauskas
 * @name Test Brand creation
 * @scenario
 * 1. Open Admin app
 * 2. Create / Update Brand
 * @details
 */
public class TestBrandCreation extends BaseTest {
    private BrandType brandType = new BrandType();
    private Brand brand = new Brand();
    private TestData tdBrand = testDataManager.agency.get(AgencyVendorType.BRAND);

    @Test(groups = {"7.2_All_UC_AccessBrandTab", "6.2.2_Add/EditBrand", "6.2.1_All_AddNewBrandHyperlink"})
    @TestInfo(component = "Platform.Admin")
    public void testBrandCreation() {

        adminApp().open();

        //create brand type
        String brandTypeName = tdBrand.getValue("BrandType", "TestData", BrandTab.class.getSimpleName(), BrandTab.ADD_BRAND_TYPE.getLabel(), BrandTab.AddBrandTypeDialog.BRAND_TYPE_NAME.getLabel());
        brandType.create(tdBrand.getTestData("BrandType", "TestData"));

        //check warnings
        log.info("TEST: Create Brand");
        brand.create(tdBrand.getTestData("DataGather", "TestData_Empty"));
        BrandPage.assetListAddBrand.getWarning(BrandTab.AddBrandDialog.BRAND_CODE.getLabel()).verify.value("Code is required");
        BrandPage.assetListAddBrand.getWarning(BrandTab.AddBrandDialog.BRAND_NAME.getLabel()).verify.value("Name is required");
        BrandPage.assetListAddBrand.getControl(BrandTab.AddBrandDialog.BUTTON_CANCEL.getLabel(), Button.class).click();

        //create
        String brandName = tdBrand.getValue("DataGather", "TestData", BrandTab.class.getSimpleName(), BrandTab.ADD_BRAND.getLabel(), BrandTab.AddBrandDialog.BRAND_NAME.getLabel());
        String brandCode = tdBrand.getValue("DataGather", "TestData", BrandTab.class.getSimpleName(), BrandTab.ADD_BRAND.getLabel(), BrandTab.AddBrandDialog.BRAND_CODE.getLabel());
        brand.create(tdBrand.getTestData("DataGather", "TestData"));
        BrandPage.tableBrands.getRow(AdminConstants.AdminBrandsTable.CODE, brandCode).verify.present();

        //check fields
        brand.update().start(brandName);
        BrandPage.assetListAddBrand.getControl(BrandTab.AddBrandDialog.BRAND_CODE.getLabel()).verify.enabled(false);
        BrandPage.assetListAddBrand.getControl(BrandTab.AddBrandDialog.BRAND_NAME.getLabel()).verify.enabled(false);
        BrandPage.assetListAddBrand.getControl(BrandTab.AddBrandDialog.BRAND_TYPE.getLabel()).verify.enabled();
        BrandPage.assetListAddBrand.getControl(BrandTab.AddBrandDialog.BRAND_EFFECTIVE_DATE.getLabel()).verify.enabled();
        BrandPage.assetListAddBrand.getControl(BrandTab.AddBrandDialog.BRAND_EXPIRATION_DATE.getLabel()).verify.enabled();
        BrandPage.assetListAddBrand.getControl(BrandTab.AddBrandDialog.UNDERWRITING_COMPANIES.getLabel()).verify.enabled();
        BrandPage.assetListAddBrand.getControl(BrandTab.AddBrandDialog.BUTTON_CANCEL.getLabel(), Button.class).click();
        BrandPage.tableBrands.getRow(AdminConstants.AdminBrandsTable.CODE, brandCode).verify.present();

        //update
        brand.update().perform(tdBrand.getTestData("DataGather", "TestData_UpdateBrandType").adjust(
                TestData.makeKeyPath(BrandTab.class.getSimpleName(), BrandTab.ADD_BRAND.getLabel(), BrandTab.AddBrandDialog.BRAND_TYPE.getLabel()), brandTypeName), brandName);
        log.info("TEST: Update Brand (Brand Code = " + brandCode + "; Brand Name = " + brandName + " Brand Type Name = " + brandTypeName);
        BrandPage.tableBrands.getRow(AdminConstants.AdminBrandsTable.NAME, brandName).getCell(AdminConstants.AdminBrandsTable.TYPE).verify.equals(brandTypeName);
    }
}
