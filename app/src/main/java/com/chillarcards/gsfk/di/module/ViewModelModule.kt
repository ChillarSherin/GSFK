package com.chillarcards.gsfk.di.module

import com.chillarcards.gsfk.viewmodel.MobileViewModel
import com.chillarcards.gsfk.viewmodel.ReportViewModel
import com.chillarcards.gsfk.viewmodel.ScanViewModel
import org.koin.dsl.module


/**
 * @Author: Sherin Jaison
 * @Date: 01-11-2023
 * Chillar
 */
val viewModelModule = module {
    single {
        MobileViewModel(get(), get())
    }
    single {
        ScanViewModel(get(), get())
    }
    single {
        ReportViewModel(get(), get())
    }
}