package com.mkh.mobilemall.bean;


/**
 * Created by xiniu_wutao on 15/7/1.
 */
public class ItemForRetail {
    private Long id;
    private Long tenantId;
    private String classType;
    private String number;
    private String name;
    private String type;
    private String basis;
    private String quickCode;
    private String shortName;
    private String description;
    private Long brandId;
    private Long categoryId;
    private String uom;
    private String inventoryType;
    private Boolean isInventory;
    private Boolean isExpense;
    private Boolean isActive;
    private Boolean isBySerial;
    private String pricingMethod;
    private Boolean isAllOuOpened;
    private String keywords;
    private Long parentId;
    private Long pictureId;
    private Double validityTerm;
    private Long accountId;
    private Long detailId;
    private Long centerId;
    private Long regionId;
    private Long productId;
    private Long projectId;
    private Long interUnitId;
    private Boolean isDiscount;
    private Boolean isNegotiatePrice;
    private Boolean isPoint;
    private Long pointValue;
    private Double retailPrice;
    private Double shopPrice;
    private Double memberPrice;
    private Boolean isOnRetail;

    public ItemForRetail() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getClassType() {
        return this.classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return this.name;
    }

    public void setQuickCode(String quickCode) {
        this.quickCode = quickCode;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getBrandId() {
        return this.brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getUom() {
        return this.uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public Boolean getIsInventory() {
        return this.isInventory;
    }

    public void setIsInventory(Boolean isInventory) {
        this.isInventory = isInventory;
    }

    public Boolean getIsExpense() {
        return this.isExpense;
    }

    public void setIsExpense(Boolean isExpense) {
        this.isExpense = isExpense;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsBySerial() {
        return this.isBySerial;
    }

    public void setIsBySerial(Boolean isBySerial) {
        this.isBySerial = isBySerial;
    }

    public String getPricingMethod() {
        return this.pricingMethod;
    }

    public void setPricingMethod(String pricingMethod) {
        this.pricingMethod = pricingMethod;
    }

    public Boolean getIsAllOuOpened() {
        return this.isAllOuOpened;
    }

    public void setIsAllOuOpened(Boolean isAllOuOpened) {
        this.isAllOuOpened = isAllOuOpened;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getPictureId() {
        return this.pictureId;
    }

    public void setPictureId(Long pictureId) {
        this.pictureId = pictureId;
    }

    public Double getValidityTerm() {
        return this.validityTerm;
    }

    public void setValidityTerm(Double validityTerm) {
        this.validityTerm = validityTerm;
    }

    public Long getAccountId() {
        return this.accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getDetailId() {
        return this.detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public Long getCenterId() {
        return this.centerId;
    }

    public void setCenterId(Long centerId) {
        this.centerId = centerId;
    }

    public Long getRegionId() {
        return this.regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getProductId() {
        return this.productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getInterUnitId() {
        return this.interUnitId;
    }

    public void setInterUnitId(Long interUnitId) {
        this.interUnitId = interUnitId;
    }

    public Boolean getIsDiscount() {
        return this.isDiscount;
    }

    public void setIsDiscount(Boolean isDiscount) {
        this.isDiscount = isDiscount;
    }

    public Boolean getIsNegotiatePrice() {
        return this.isNegotiatePrice;
    }

    public void setIsNegotiatePrice(Boolean isNegotiatePrice) {
        this.isNegotiatePrice = isNegotiatePrice;
    }

    public Boolean getIsPoint() {
        return this.isPoint;
    }

    public void setIsPoint(Boolean isPoint) {
        this.isPoint = isPoint;
    }

    public Long getPointValue() {
        return this.pointValue;
    }

    public void setPointValue(Long pointValue) {
        this.pointValue = pointValue;
    }

    public Double getRetailPrice() {
        return this.retailPrice;
    }

    public void setRetailPrice(Double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public Double getShopPrice() {
        return this.shopPrice;
    }

    public void setShopPrice(Double shopPrice) {
        this.shopPrice = shopPrice;
    }

    public Double getMemberPrice() {
        return this.memberPrice;
    }

    public void setMemberPrice(Double memberPrice) {
        this.memberPrice = memberPrice;
    }

    public Boolean getIsOnRetail() {
        return this.isOnRetail;
    }

    public void setIsOnRetail(Boolean isOnRetail) {
        this.isOnRetail = isOnRetail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBasis() {
        return this.basis;
    }

    public void setBasis(String basis) {
        this.basis = basis;
    }

    public String getQuickCode() {
        return this.quickCode;
    }

    public String getInventoryType() {
        return this.inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }


}
