package com.lucidsoftworksllc.sabotcommunity;

class MerchList {
    private final String id;
    private final String name;
    private final String options;
    private final String desc;
    private final String image;
    private final String price;
    private final String sale_price;
    private final String quantity;
    private final String sale_end;
    private final String active;

    public MerchList(String id, String name, String options, String desc, String image, String price, String sale_price, String quantity, String sale_end, String active) {
        this.id = id;
        this.name = name;
        this.options = options;
        this.desc = desc;
        this.image = image;
        this.price = price;
        this.sale_price = sale_price;
        this.quantity = quantity;
        this.sale_end = sale_end;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOptions() {
        return options;
    }

    public String getDesc() {
        return desc;
    }

    public String getImage() {
        return image;
    }

    public String getPrice() {
        return price;
    }

    public String getSale_price() {
        return sale_price;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getSale_end() {
        return sale_end;
    }

    public String getActive() {
        return active;
    }
}
