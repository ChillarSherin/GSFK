package com.chillarcards.gsfk.interfaces

import com.chillarcards.gsfk.utills.CommonDBaseModel

interface IAdapterViewUtills {

    fun getAdapterPosition(Position: Int, ValueArray: ArrayList<CommonDBaseModel>, Mode: String?)
}