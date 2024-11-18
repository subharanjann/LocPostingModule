package com.mobillor.locpostingmodule.presentation.activity

import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.mobillor.locpostingmodule.data.model.InputMappedtoBin
import com.mobillor.locpostingmodule.data.repository.BinPutawayRepositoryImpl
import com.mobillor.locpostingmodule.data.repository.PalletPutawayRepositoryImpl
import com.mobillor.locpostingmodule.domain.useCase.GetLocationUseCase
import com.mobillor.locpostingmodule.domain.useCase.GetMappedToBinUseCase
import com.mobillor.locpostingmodule.domain.useCase.GetPalletLocationUseCase
import com.mobillor.locpostingmodule.domain.useCase.PutawayBinUseCase
import com.mobillor.locpostingmodule.presentation.composable.LocationHintForBin
import com.mobillor.locpostingmodule.presentation.composable.LocationPostingBinInfoCard
import com.mobillor.locpostingmodule.presentation.composable.LocationPostingLocationFloatingButtonForItemPutaway
import com.mobillor.locpostingmodule.presentation.composable.LocationPostingPalletFloatingButtonForItemPutaway
import com.mobillor.locpostingmodule.presentation.composable.MappedToBinInfoList
import com.mobillor.locpostingmodule.presentation.composable.PalletHintForBin
import com.mobillor.locpostingmodule.presentation.composable.TopNavigationBar
import com.mobillor.locpostingmodule.presentation.viewModel.BinPutawayCompletionVm
import com.mobillor.locpostingmodule.presentation.viewModelFactory.BinPutawayVmFactory
import com.mobillor.locpostingmodule.util.BaseActivity
import com.mobillor.locpostingmodule.util.Resource
import com.mobillor.themeresourcemodule.commonComposables.BasicBottomButton
import com.mobillor.themeresourcemodule.commonComposables.ui.theme.SEGMK3Theme
import org.json.JSONException
import org.json.JSONObject

class BinPutawayCompletion : BaseActivity() {
    private lateinit var viewModel : BinPutawayCompletionVm
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            showCameraForLoc()
        }
        else {
            ActivityCompat.requestPermissions(
                this@BinPutawayCompletion, arrayOf(
                    android.Manifest.permission.CAMERA,
                ), 101
            )
        }
    }
    private val requestPermissionLauncherPalletSpecific = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            showCamera()
        }
        else {
            ActivityCompat.requestPermissions(
                this@BinPutawayCompletion, arrayOf(
                    android.Manifest.permission.CAMERA,
                ), 101
            )
        }
    }

    private var binList: List<DataResponseBinInfo>? = mutableListOf()
    private var dataFromRecycler : List<DataResponseBinInfo?> = mutableListOf()
    private var locationCode = 0
    private var sourceCode = 0
    private var palletCode = 0
    var scanned_info=""
    private var userID  = ""

    private var binInput: InputMappedtoBin = InputMappedtoBin( emptyList(),0, 0, "")

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
    @Composable
    private fun BinPutawayCompletionGreetings(){
        Scaffold(
            topBar = {
                TopNavigationBar(name = "Bin Posting"){ onBackPressed() }
            },
            floatingActionButton = {
                Column (horizontalAlignment = Alignment.End){
                    Row(){
                        LocationPostingLocationFloatingButtonForItemPutaway{
                                QRCodeInputFromMobileCameraLocation()
                        }
                        Column(Modifier.width(12.dp)) {

                        }
                    }
                    Row(){
                        LocationPostingPalletFloatingButtonForItemPutaway{
                                QRCodeInputFromMobileCameraPallet()
                            }
                        Column(Modifier.width(12.dp)) {

                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                }
            },
            bottomBar = {
                BasicBottomButton(title = "Putaway") {
                    //       dataFromRecycler = badapter.currentValueForGone()
                    Log.d("taggss", dataFromRecycler.toString())


                    initiatePutaway()
                }
            }
        ) {innPad ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(innPad)){

                Row(
                    Modifier
                        .height(48.dp)
                        .padding(end = 8.dp)){
                    Spacer(Modifier.weight(1f))
                    LocationHintForBin(viewModel)
                }
                Row(
                    Modifier
                        .height(48.dp)
                        .padding(end = 16.dp)){
                    Spacer(Modifier.weight(1f))
                    PalletHintForBin(viewModel)
                }
                LocationPostingBinInfoCard(card = binList?.get(0)?:DataResponseBinInfo("",0,"","","",0,0,0,0,0))
                MappedToBinInfoList(viewModel)


            }
        }

    }
    private fun setUp() {
        val binRepository = BinPutawayRepositoryImpl(NewApiClient.getLocPostingService())
        val palletRepository = PalletPutawayRepositoryImpl(NewApiClient.getLocPostingService())
        val getLocationUseCase= GetLocationUseCase(palletRepository)
        val getPalletLocationUseCase= GetPalletLocationUseCase(binRepository)
        val mappedToBinUseCase= GetMappedToBinUseCase(binRepository)
        val putawayBinUseCase= PutawayBinUseCase(binRepository)
        viewModel = ViewModelProvider(this, BinPutawayVmFactory(getLocationUseCase,getPalletLocationUseCase,mappedToBinUseCase,putawayBinUseCase) )[BinPutawayCompletionVm::class.java]

        if(!isInternetAvailable(this)){
            Toast.makeText(this,"Internet Unavailable",Toast.LENGTH_LONG).show()
        }
        userID = LocPostingStarterActivity.USER_ID
        getIntentData()
        observer()
    }
    private fun initiatePutaway() {
        if(locationCode != 0 ){
            if(!binList.isNullOrEmpty()){
                binInput.locationId = locationCode
                binInput.userId = userID
                binInput.sourceLocation = sourceCode
                populateBinsWithPalletId()
                Log.d("tagss",binInput.toString())
                viewModel.binLocationTransfer(LocPostingStarterActivity.USER_TOKEN,binInput)

            }
            else{
                Toast.makeText(this,"Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this,getString(R.string.scan_location), Toast.LENGTH_SHORT).show()
        }
    }
    private fun populateBinsWithPalletId() {
        binList?.let { list ->
            val bins = list.mapNotNull { bin ->
                val binInfo = dataFromRecycler.find { it?.binId == bin.binId }
                InputMappedtoBin.Bin(bin.binId ?:0, palletCode)

            }
            binInput.bins = bins
        }
    }
    private fun observer() {
        viewModel.locationDataResponseLiveData.observe(this) { response ->
            Log.d("tags", "observer hit")
            when (response) {
                is Resource.Loading -> {
                    loadingPopup.show()
                }

                is Resource.Success -> {
                    loadingPopup.dismiss()
                    locationCode = response.data?.data?.get(0)?.locationId ?: 0
                }

                is Resource.Error -> {
                    loadingPopup.dismiss()
                    val errorMessage = try {
                        JSONObject(response.message ?: "").getString("msg")
                    } catch (e: JSONException) {
                        "An error occurred"
                    }
                    Log.e("testing", "hhkj ${response.message}")
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }

            }
        }

        viewModel.palletCodeResponseLiveData.observe(this) { response ->
            //  binding.progressBar3.visibility = View.GONE
            when (response) {
                is Resource.Loading -> {
                    loadingPopup.show()
                }

                is Resource.Success -> {
                    loadingPopup.dismiss()
                    palletCode = response.data?.data?.get(0)?.palletId ?: 0
                }

                is Resource.Error -> {
                    loadingPopup.dismiss()
                    val errorMessage = try {
                        JSONObject(response.message ?: "").getString("msg")
                    } catch (e: JSONException) {
                        "An error occurred"
                    }
                    Log.e("testing", "hhkj ${response.message}")
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.mappedtoBin.observe(this) { response ->
            when (response) {
                is Resource.Loading -> {
                    loadingPopup.show()
                }

                is Resource.Success -> {
                    loadingPopup.dismiss()
                    sourceCode = response.data?.data?.get(0)?.locationId ?: 0
                }

                is Resource.Error -> {
                    loadingPopup.dismiss()
                    val errorMessage = try {
                        JSONObject(response.message ?: "").getString("msg")
                    } catch (e: JSONException) {
                        "An error occurred"
                    }
                    Log.e("testing", "hhkj ${response.message}")
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.binPutawayResponseLiveData.observe(this) { response ->
            when (response) {
                is Resource.Loading -> {
                    loadingPopup.show()
                }

                is Resource.Success -> {
                    loadingPopup.dismiss()
                    val msg = response.message ?: "info"

                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    updateLocalSelectionValues()
                    onBackPressed()
                    finish()
                }

                is Resource.Error -> {
                    loadingPopup.dismiss()
                    val errorMessage = try {
                        JSONObject(response.message ?: "").getString("msg")
                    } catch (e: JSONException) {
                        "An error occurred"
                    }
                    Log.e("testing", "hhkj ${response.message}")
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }

        }

    }
    private fun updateLocalSelectionValues() {
        dataFromRecycler.forEach { palletInfo ->
            if (palletInfo?.localSelectionValue == 1) {
                palletInfo.localSelectionValue = 2
            }
        }
    }

    private fun QRCodeInputFromMobileCameraLocation() {

        if (ContextCompat.checkSelfPermission(
                this@BinPutawayCompletion,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCameraForLoc()
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(
                this@BinPutawayCompletion,
                "Camera permission required",
                Toast.LENGTH_LONG
            ).show()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }
    private fun showCameraForLoc() {
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
                scanned_info = location
                viewModel.getLocationCode(LocPostingStarterActivity.USER_TOKEN,location)
                loadingPopup.show()
            }
        }
    }

    private fun QRCodeInputFromMobileCameraPallet() {

        if (ContextCompat.checkSelfPermission(
                this@BinPutawayCompletion,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCamera()
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(
                this@BinPutawayCompletion,
                "Camera permission required",
                Toast.LENGTH_LONG
            ).show()
        } else {
            requestPermissionLauncherPalletSpecific.launch(android.Manifest.permission.CAMERA)
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

        scanPallet.launch(options)
    }
    private val scanPallet = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
        run {
            if (result.contents != null) {
                val pallet = result.contents.toString()
                scanned_info=pallet
                viewModel.getPalletCode(LocPostingStarterActivity.USER_TOKEN,pallet)
            }
        }
    }

    private fun getIntentData() {
        val binString = intent.getSerializableExtra("bin")as? ArrayList<*>
        binList= binString?.filterIsInstance<DataResponseBinInfo>()
        Log.e("taggss","$binString, bin : $binList")
        //  setData()
        viewModel.getMappedtoBinData(LocPostingStarterActivity.USER_TOKEN,binList!![0].binId ?:0)
    }
}