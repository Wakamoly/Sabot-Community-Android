package com.lucidsoftworksllc.sabotcommunity;

class SliderUtilsDash {

    private String sliderImageUrl, sliderTitle, sliderDescription, sliderID, sliderType, sliderTag, sliderAdID;

    String getSliderImageUrl() {
        return Constants.BASE_URL+sliderImageUrl;
    }

    String getSliderDescription() {
        return sliderDescription;
    }

    String getSliderTitle() {
        return sliderTitle;
    }

    String getSliderID() {
        return sliderID;
    }

    String getSliderType() {
        return sliderType;
    }

    String getSliderTag() {
        return sliderTag;
    }


    void setSliderID(String sliderID) {
        this.sliderID = sliderID;
    }

    void setSliderImageUrl(String sliderImageUrl) {
        this.sliderImageUrl = sliderImageUrl;
    }

    void setSliderTitle(String sliderTitle) {
        this.sliderTitle = sliderTitle;
    }

    void setSliderDescription(String sliderDescription) {
        this.sliderDescription = sliderDescription;
    }

    void setSliderType(String sliderType) {
        this.sliderType = sliderType;
    }

    void setSliderTag(String sliderTag) {
        this.sliderTag = sliderTag;
    }

    public void setSliderAdID(String sliderAdID) {
        this.sliderAdID = sliderAdID;
    }

    public String getSliderAdID() {
        return sliderAdID;
    }
}