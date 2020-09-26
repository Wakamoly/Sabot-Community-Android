package com.lucidsoftworksllc.sabotcommunity

import com.lucidsoftworksllc.sabotcommunity.others.Constants

class SliderUtilsDash {
    var sliderImageUrl: String? = null
        get() = Constants.BASE_URL + field
    var sliderTitle: String? = null
    var sliderDescription: String? = null
    var sliderID: String? = null
    var sliderType: String? = null
    var sliderTag: String? = null
    var sliderAdID: String? = null
}