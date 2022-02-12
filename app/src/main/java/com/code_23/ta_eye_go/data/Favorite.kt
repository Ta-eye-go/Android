package com.code_23.ta_eye_go.data

data class Favorite(
    var favoriteNm : String, // 즐겨찾기 이름
    var startSttnNm : String, // 출발 정류장 이름
    var startSttnID : String, // 출발 정류장 id
    var destination : String, // 도착 정류장 이름
    var destinationID : String, // 도착 정류장 id
    var busNm : String //버스 번호
)