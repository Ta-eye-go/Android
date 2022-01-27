package com.code_23.ta_eye_go.ui.bookbus

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
data class ArriveResponse(
    @Element
    var header: ArriveHeader,

    @Element(name = "body")
    var body: ArriveBody?
)

@Xml(name = "header")
data class ArriveHeader(
    @PropertyElement
    var resultCode: String,

    @PropertyElement
    var resultMsg: String
)

@Xml
data class ArriveBody(
    @Element(name = "items")
    var items: ArriveItems,

    @PropertyElement
    var numOfRows: Int,

    @PropertyElement
    var pageNo: Int,

    @PropertyElement
    var totalCount: Int
)

@Xml
data class ArriveItems(
    @Element(name = "item")
    var item: List<ArriveDTO>?
)

@Xml
data class ArriveDTO(
    @PropertyElement(name = "nodeid")
    var nodeId: String,

    @PropertyElement(name = "nodenm")
    var nodeNm: String,

    @PropertyElement(name = "routeid")
    var routeId: String,

    @PropertyElement(name = "routeno")
    var routeNo: String,

    @PropertyElement(name = "routetp")
    var routeTp: String,

    @PropertyElement(name = "arrprevstationcnt")
    var arrPrevStationCnt: Int,

    @PropertyElement(name = "vehicletp")
    var vehicleTp: String,

    @PropertyElement(name = "arrtime")
    var arrTime: Long
)
