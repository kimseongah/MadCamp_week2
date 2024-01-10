package com.example.madcamp_week2

data class BuskingResponse(
    val busking_list: List<Busking>,
    val position_list: List<Int>,
    val header_list: List<Int>,
    val beforeheader_list: List<Int>,
    val listvalue: List<Int>
)