package com.code_23.ta_eye_go.DB

import androidx.room.ColumnInfo
import androidx.room.Entity


class ListForm (
    var cityCode: String?,
    var endNodeID: String?,
    var endNodenm: String?,
    var guide_dog: Any,
    var id: String?,
    var routeID: String?,
    var routeNo: String?,
    var startNodeID: String?,
    var startNodenm: String?
){
    constructor(): this("", "","",false,"","","","","")
}

class booklist(
    var no: Any,
    var startNodenm: String?,
    var startNodeID: String?,
    var endNodenm: String?,
    var endNodeID: String?,
    var routeNo: String?,
    var routeID: String?
){
    constructor(): this(0,null,null,null,null,null,null)
}