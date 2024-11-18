package com.mobillor.locpostingmodule.presentation.viewModel



import androidx.compose.runtime.State
import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobillor.locpostingmodule.domain.useCase.GetItemByIdUseCase
import com.mobillor.locpostingmodule.domain.useCase.GetPalletByIdUseCase
import com.mobillor.locpostingmodule.data.model.DataResponseBinInfo
import com.mobillor.locpostingmodule.data.model.DataResponseItemInfo
import com.mobillor.locpostingmodule.data.model.DataResponsePalletInfo
import com.mobillor.locpostingmodule.data.model.ResponseBinInfo
import com.mobillor.locpostingmodule.data.model.ResponseItemInfo
import com.mobillor.locpostingmodule.data.model.ResponsePalletInfo
import com.mobillor.locpostingmodule.domain.useCase.GetBinByIdUseCase

import com.mobillor.locpostingmodule.util.Resource
import com.mobillor.themeresourcemodule.commonComposables.ui.theme.darkGrey
import com.mobillor.themeresourcemodule.commonComposables.ui.theme.primaryColorAccent
import kotlinx.coroutines.launch

class PalletScanVm(private val getPalletByIdUseCase: GetPalletByIdUseCase,
                   private val getBinByIdUseCase: GetBinByIdUseCase,
                   private val getItemByIdUseCase: GetItemByIdUseCase
) : ViewModel(){
    private val _ResponsePalletById = MutableLiveData<Resource<ResponsePalletInfo>>()
    val ResponsePalletById : LiveData<Resource<ResponsePalletInfo>> get() =  _ResponsePalletById
    private val _PalletList = MutableLiveData<ArrayList<DataResponsePalletInfo>>(arrayListOf())
    val PalletList : LiveData<ArrayList<DataResponsePalletInfo>> get() =  _PalletList

    private val _ResponseBinById = MutableLiveData<Resource<ResponseBinInfo>>()
    val ResponseBinById : LiveData<Resource<ResponseBinInfo>> get() =  _ResponseBinById
    private val _BinList = MutableLiveData<ArrayList<DataResponseBinInfo>>(arrayListOf())
    val BinList : LiveData<ArrayList<DataResponseBinInfo>> get() =  _BinList

    private val _ResponseItemById = MutableLiveData<Resource<ResponseItemInfo>>()
    val ResponseItemById : LiveData<Resource<ResponseItemInfo>> get() =  _ResponseItemById
    private val _ItemList = MutableLiveData<ArrayList<DataResponseItemInfo>>(arrayListOf())
    val ItemList : LiveData<ArrayList<DataResponseItemInfo>> get() =  _ItemList

    //jet Pack compose
    private val _isExpanded = mutableStateOf(true)
    val isExpanded: State<Boolean> = _isExpanded




    fun dataCaller(typeSelected: Int, token : String,scannedId: String) {
        when (typeSelected) {
                1 -> {
                    getPalletById(token,scannedId)
                }
                2 -> {
                    getItemById(token,scannedId)
                }
                3 -> {
                    getBinById(token,scannedId)
                }
        }

    }
    fun getPalletById(token : String,scannedId: String){
        viewModelScope.launch {
            val result = getPalletByIdUseCase(token,scannedId)
            _ResponsePalletById.postValue(result)
        }

    }
    fun addtoPalletList(input :DataResponsePalletInfo){
        val currentList = _PalletList.value ?: arrayListOf()
        currentList.add(input)
        _PalletList.value = currentList
    }
    fun getBinById(token : String,scannedId: String){
        viewModelScope.launch {
            val result = getBinByIdUseCase(token,scannedId)
            _ResponseBinById.postValue(result)
        }
    }
    fun addtoBinList(input :DataResponseBinInfo){
        val currentList = _BinList.value ?: arrayListOf()
        currentList.add(input)
        _BinList.value = currentList
    }
    fun getItemById(token : String,scannedId: String){
        viewModelScope.launch {
            val result = getItemByIdUseCase(token,scannedId)
            _ResponseItemById.postValue(result)
        }
    }
    fun addtoItemList(input :DataResponseItemInfo){
        val currentList = _ItemList.value ?: arrayListOf()
        currentList.add(input)
        _ItemList.value = currentList
    }


    //color stuff
    var color1 by mutableStateOf(Color.Black)
    var color2 by mutableStateOf(Color.Black)
    var color3 by mutableStateOf(Color.Black)

    fun changeColors(exceptIndex: Int) {
        when (exceptIndex) {
            1 -> {
                color1 = primaryColorAccent
                color2 = darkGrey
                color3 = darkGrey
            }
            2 -> {
                color1 = darkGrey
                color2 = primaryColorAccent
                color3 = darkGrey
            }
            3 -> {
                color1 = darkGrey
                color2 = darkGrey
                color3 = primaryColorAccent
            }
        }
    }

    fun resetColors() {
        color1 = Color.Black
        color2 = Color.Black
        color3 = Color.Black
    }

    fun toggleExpanded() {
        _isExpanded.value = !_isExpanded.value
    }

}