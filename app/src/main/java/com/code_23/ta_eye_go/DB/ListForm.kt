package com.code_23.ta_eye_go.DB

import androidx.room.Entity


class ListForm (
    var id: String?,
    var guide_dog: Boolean,
    var cityCode: String?,
    var startNodenm: String?,
    var startNodeID: String?,
    var routeID: String?,
    var endNodenm: String?,
    var routeNo: String?,
    var endNodeID: String?
){
    constructor(): this(null, false,null,null,null,null,null,null,null)
}