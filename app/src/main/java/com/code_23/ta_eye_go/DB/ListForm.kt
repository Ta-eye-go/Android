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
    var id: String?,
    var startNodenm: String?,
    var endNodenm: String?,
    var guide_dog: Boolean?
){
    constructor(): this(null,null,null,false)
}

class driverlist(
    var id: String?,
    var startNodenm: String?,
    var endNodenm: String?,
    var guide_dog: String
){
    constructor(): this(null,null,null,"")
}

class bordinglist(
    var startNodenm: String?
){
    constructor(): this(null)
}

class getofflist(
    var endNodenm: String?
){
    constructor(): this(null)
}