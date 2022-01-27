package com.code_23.ta_eye_go.ui.bookbus

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
data class Station(
    @Element
    var header: Header,

    @Element
    var body: Body?
)

@Xml(name = "header")
data class Header(
    @PropertyElement
    var resultCode: String,

    @PropertyElement
    var resultMsg: String
)

@Xml
data class Body(
    @Element(name = "items")
    var items: Items,

    @PropertyElement
    var numOfRows: Int,

    @PropertyElement
    var pageNo: Int,

    @PropertyElement
    var totalCount: Int
)

@Xml
data class Items(
    @Element
    var item: List<StationDTO>
)

@Xml(name = "item")
data class StationDTO(
    @PropertyElement(name = "citycode")
    var cityCode: String?,

    @PropertyElement(name = "gpslati")
    var latitude: String?,

    @PropertyElement(name = "gpslong")
    var longitude: String?,

    @PropertyElement(name = "nodeid")
    var nodeId: String?,

    @PropertyElement(name = "nodenm")
    var nodeName: String?,

    @PropertyElement(name = "nodeno")
    var nodeNumber: String?
)