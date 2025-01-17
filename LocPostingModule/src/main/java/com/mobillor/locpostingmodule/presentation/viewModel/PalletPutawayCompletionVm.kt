package com.mobillor.locpostingmodule.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobillor.locpostingmodule.domain.useCase.GetLocationUseCase
import com.mobillor.locpostingmodule.domain.useCase.GetMappedToPalletUseCase
import com.mobillor.locpostingmodule.domain.useCase.PutawayPalletUseCase
import com.mobillor.locpostingmodule.data.model.InputMappedtoPallet
import com.mobillor.locpostingmodule.data.model.ResponseLocationCode
import com.mobillor.locpostingmodule.data.model.ResponseMappedToPallet
import com.mobillor.locpostingmodule.data.model.ResponsePutawayPalletTransfer
import com.mobillor.locpostingmodule.util.Resource
import kotlinx.coroutines.launch

class PalletPutawayCompletionVm(
    private val getLocationUseCase: GetLocationUseCase,
    private val getMappedToPalletUseCase: GetMappedToPalletUseCase,
    private val putawayPalletUseCase: PutawayPalletUseCase
): ViewModel() {
    private val _locationDataResponseLiveData = MutableLiveData<Resource<ResponseLocationCode>>()
    val locationDataResponseLiveData: LiveData<Resource<ResponseLocationCode>>get() = _locationDataResponseLiveData

    private val _mappedToPalletResponseLiveData = MutableLiveData<Resource<ResponseMappedToPallet>>()
    val mappedToPalletResponseLiveData : LiveData<Resource<ResponseMappedToPallet>> get() =_mappedToPalletResponseLiveData

    private val _putawayPalletTransferResponseLiveData = MutableLiveData<Resource<ResponsePutawayPalletTransfer>>()
    val putawayPalletTransferResponseLiveData : LiveData<Resource<ResponsePutawayPalletTransfer>> get() = _putawayPalletTransferResponseLiveData

    var PicklistList = ArrayList<Int>()

    fun getLocationCode(token : String, scannedLoc: String) {
       viewModelScope.launch {
         val result =  getLocationUseCase(token,scannedLoc)
           _locationDataResponseLiveData.postValue(result)
       }
    }

    fun getMappedToPallet(token  : String,id : Int){
        viewModelScope.launch {
            val result = getMappedToPalletUseCase(token , id)
            _mappedToPalletResponseLiveData.postValue(result)
        }
    }
    fun putawayPallet(token : String, dataSet : InputMappedtoPallet){
        viewModelScope.launch {
            val result = putawayPalletUseCase(token , dataSet)
            _putawayPalletTransferResponseLiveData.postValue(result)
        }
    }






}