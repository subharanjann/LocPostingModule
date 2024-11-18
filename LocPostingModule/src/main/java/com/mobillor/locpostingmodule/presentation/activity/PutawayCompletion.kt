package com.mobillor.locpostingmodule.presentation.activity


import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent

import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.mobillor.goodsinwardmodule.data.apiclient.NewApiClient
import com.mobillor.locpostingmodule.R
import com.mobillor.locpostingmodule.data.model.DataResponseBinInfo
import com.mobillor.locpostingmodule.data.model.DataResponseItemInfo
import com.mobillor.locpostingmodule.data.model.DataResponseMappedToPallet
import com.mobillor.locpostingmodule.data.model.DataResponsePalletInfo
import com.mobillor.locpostingmodule.data.model.InputMappedtoPallet

import com.mobillor.locpostingmodule.data.repository.PalletPutawayRepositoryImpl
import com.mobillor.locpostingmodule.domain.useCase.GetLocationUseCase
import com.mobillor.locpostingmodule.domain.useCase.GetMappedToPalletUseCase
import com.mobillor.locpostingmodule.domain.useCase.PutawayPalletUseCase
import com.mobillor.locpostingmodule.presentation.composable.LocationHintForPallet
import com.mobillor.locpostingmodule.presentation.composable.LocationPostingLocationFloatingButtonForItemPutaway
import com.mobillor.locpostingmodule.presentation.composable.LocationPostingPalletInfoCard
import com.mobillor.locpostingmodule.presentation.composable.MappedToPalletInfoList
import com.mobillor.locpostingmodule.presentation.composable.TopNavigationBar
import com.mobillor.locpostingmodule.presentation.viewModel.PalletPutawayCompletionVm
import com.mobillor.locpostingmodule.presentation.viewModelFactory.PalletPutawayVmFactory
import com.mobillor.locpostingmodule.util.BaseActivity
import com.mobillor.locpostingmodule.util.Resource
import com.mobillor.themeresourcemodule.commonComposables.BasicBottomButton
import com.mobillor.themeresourcemodule.commonComposables.ui.theme.SEGMK3Theme
import org.json.JSONException
import org.json.JSONObject

class PutawayCompletion: BaseActivity() {
    private lateinit var viewModel : PalletPutawayCompletionVm
    private var locationCode = 0
    private var sourceCode = 0
    private var userID  = ""
    private var mappedData : List<DataResponseMappedToPallet> = emptyList()
    private var palletList: List<DataResponsePalletInfo>? = mutableListOf()
    private var itemList: List<DataResponseItemInfo>? = mutableListOf()
    private var binList: List<DataResponseBinInfo>? = mutableListOf()

    private var palletInput: InputMappedtoPallet = InputMappedtoPallet(0, emptyList(), 0, "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor()
        setUp()
        setContent{
            SEGMK3Theme {
            BinPutawayCompletionGreetings()
        }
        }
    }

    @Preview
    @Composable
    private fun BinPutawayCompletionGreetings(){
        Scaffold(

            topBar = {
                Spacer(modifier = Modifier.height(6.dp))
                TopNavigationBar(name = "Pallet Posting"){ finish() }},
            floatingActionButton = {
                LocationPostingLocationFloatingButtonForItemPutaway{

                    QRCodeInputFromMobileCamera()


            }},
            bottomBar = {
                BasicBottomButton(title = "Putaway") {
                    // dataFromRecycler = padapter.currentValueForGone()


                    initiatePutaway()
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        ) {innerPadding ->
            Column(Modifier.fillMaxSize().padding(innerPadding)){

                Row(
                    Modifier
                        .height(48.dp)
                        .padding(end = 8.dp)){
                    Spacer(Modifier.weight(1f))
                    LocationHintForPallet(viewModel)
                }
                LocationPostingPalletInfoCard(card = palletList!![0], convertDateFormat =palletList!![0].cd?.convertDateFormat() ,viewModel)
                MappedToPalletInfoList(viewModel)


            }
        }
    }

    private fun setUp() {
        val repository = PalletPutawayRepositoryImpl(NewApiClient.getLocPostingService())
        val locationUseCase = GetLocationUseCase(repository)
        val mappedDataUseCase = GetMappedToPalletUseCase(repository)
        val putawayUseCase = PutawayPalletUseCase(repository)
        viewModel = ViewModelProvider(this,
            PalletPutawayVmFactory(locationUseCase,mappedDataUseCase,putawayUseCase)
        )[PalletPutawayCompletionVm::class.java]

        if(!isInternetAvailable(this)){
            Toast.makeText(this,"Internet Unavailable", Toast.LENGTH_LONG).show()
        }

        userID = LocPostingStarterActivity.USER_ID
        Log.d("tagss",userID)
        getIntentData()
        observer()
    }
    private fun observer() {
        viewModel.locationDataResponseLiveData.observe(this) { it ->
            Log.d("tags", "observer hit")
            when(it){
                is Resource.Loading ->{       loadingPopup.show()}
                is Resource.Success ->{
                    loadingPopup.dismiss()
                    locationCode = it.data?.data?.get(0)?.locationId?:0
                }
                is Resource.Error ->{
                    loadingPopup.dismiss()
                    val errorMessage = try {
                        JSONObject(it.message ?: "").getString("msg")
                    }
                    catch (e: JSONException) { "An error occurred" }
                    Log.e("testing", "hhkj ${it.message}")
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()

                }
            }
        }
        viewModel.mappedToPalletResponseLiveData.observe(this) {it ->
            when(it){
                is Resource.Loading ->{
                    loadingPopup.show()
                }
                is Resource.Success ->{
                    loadingPopup.dismiss()
                    mappedData =   it.data?.data?: emptyList()
                    sourceCode = it.data?.data?.get(0)?.locationId?:0

                }
                is Resource.Error ->{
                    loadingPopup.dismiss()
                    val errorMessage = try {
                        JSONObject(it.message ?: "").getString("msg")
                    }
                    catch (e: JSONException) { "An error occurred" }
                    Log.e("testing", "hhkj ${it.message}")
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }

            }

        }
        viewModel.putawayPalletTransferResponseLiveData.observe(this) {it ->
            when(it){
                is Resource.Loading ->{
                    loadingPopup.show()
                }
                is Resource.Success ->{
                    loadingPopup.dismiss()
                    Toast.makeText(this, it.data?.msg?:"Success", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
                is Resource.Error   ->{
                    loadingPopup.dismiss()
                    val errorMessage = try {
                        JSONObject(it.message ?: "").getString("msg")
                    }
                    catch (e: JSONException) { "An error occurred" }
                    Log.e("testing", "hhkj ${it.message}")
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()

                }
            }
        }
    }

    private fun getIntentData() {
        val palletString = intent.getSerializableExtra("pallet")as? ArrayList<*>
        val itemString = intent.getSerializableExtra("item")as? ArrayList<*>
        val binString = intent.getSerializableExtra("bin")as? ArrayList<*>
        palletList =palletString?.filterIsInstance<DataResponsePalletInfo>()
        itemList = itemString?.filterIsInstance<DataResponseItemInfo>()
        binList= binString?.filterIsInstance<DataResponseBinInfo>()
        Log.e("taggss","$binString, bin : $binList")

        if(palletList?.get(0)?.palletId != null){
            viewModel.getMappedToPallet(LocPostingStarterActivity.USER_TOKEN,palletList!![0].palletId?:0)
        }


    }



    private fun QRCodeInputFromMobileCamera() {

        if (ContextCompat.checkSelfPermission(
                this@PutawayCompletion,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCamera()
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(this@PutawayCompletion, "Camera permission required", Toast.LENGTH_LONG).show()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun showCamera() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
        options.setPrompt("Scan QR code")
        options.setCameraId(0)
        options.setBeepEnabled(true)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(true)

        scanLoc.launch(options)
    }

    private val scanLoc = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
        run {
            if (result.contents != null) {
                val location = result.contents.toString()
                viewModel.getLocationCode(LocPostingStarterActivity.USER_TOKEN,location)
                loadingPopup.show()
            }
        }}


    private fun initiatePutaway() {
        if(locationCode != 0 ){
            if(!palletList.isNullOrEmpty()){
                palletInput.locationId = locationCode
                palletInput.userId = userID
                palletInput.sourceLocation = sourceCode
                val pickIdList : List<InputMappedtoPallet.Pallet> = viewModel.PicklistList.map {InputMappedtoPallet.Pallet(it) }
                palletInput.pallets =pickIdList
                //populatePalletIds()
                Log.d("tagss",palletInput.toString())
                viewModel.putawayPallet(LocPostingStarterActivity.USER_TOKEN,palletInput)

            }
            else{
                Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this,getString(R.string.scan_location),Toast.LENGTH_SHORT).show()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            showCamera()
        }
        else {
            ActivityCompat.requestPermissions(
                this@PutawayCompletion, arrayOf(
                    android.Manifest.permission.CAMERA,
                ), 101
            )
        }
    }
}