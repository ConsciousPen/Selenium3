package aaa.admin.modules.commission.bulkadjustment;

public enum BulkAdjustmentType {
    BULK_ADJUSTMENT("Bulk Adjustment", new BulkAdjustment());

    private String bulkAdjustmentType;
    private IBulkAdjustment bulkAdjustment;

    BulkAdjustmentType(String bulkAdjustmentType, IBulkAdjustment bulkAdjustment) {
        this.bulkAdjustmentType = bulkAdjustmentType;
        this.bulkAdjustment = bulkAdjustment;
    }

    public IBulkAdjustment get() {
        return bulkAdjustment;
    }

    public String getName() {
        return bulkAdjustmentType;
    }

    public String getKey() {
        return bulkAdjustment.getClass().getSimpleName();
    }
}
