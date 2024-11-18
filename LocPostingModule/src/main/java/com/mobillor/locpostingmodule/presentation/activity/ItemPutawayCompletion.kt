package com.mobillor.locpostingmodule.presentation.activity

import android.app.Dialog
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.mobillor.goodsinwardmodule.data.apiclient.NewApiClient
import com.mobillor.locpostingmodule.R
import com.mobillor.locpostingmodule.data.model.DataResponseItemInfo
import com.mobillor.locpostingmodule.data.model.InputMappedtoItem
import com.mobillor.locpostingmodule.data.repository.BinPutawayRepositoryImpl
import com.mobillor.locpostingmodule.data.repository.ItemPutawayRepositoryImpl
import com.mobillor.locpostingmodule.data.repository.PalletPutawayRepositoryImpl
import com.mobillor.locpostingmodule.domain.useCase.GetBinLocationUseCase
import com.mobillor.locpostingmodule.domain.useCase.GetLocationUseCase
import com.mobillor.locpostingmodule.domain.useCase.GetPalletLocationUseCase
import com.mobillor.locpostingmodule.domain.useCase.PutawayItemUseCase
import com.mobillor.locpostingmodule.presentation.composable.BinHintForItem
import com.mobillor.locpostingmodule.presentation.composable.LocationHintForItem
import com.mobillor.locpostingmodule.presentation.composable.LocationPostingBinFloatingButtonForItemPutaway
import com.mobillor.locpostingmodule.presentation.composable.LocationPostingItemInfoListForItemPutaway
import com.mobillor.locpostingmodule.presentation.composable.LocationPostingLocationFloatingButtonForItemPutaway
import com.mobillor.locpostingmodule.presentation.composable.LocationPostingPalletFloatingButtonForItemPutaway
import com.mobillor.locpostingmodule.presentation.composable.PalletHintForItem
import com.mobillor.locpostingmodule.presentation.composable.TopNavigationBar
import com.mobillor.locpostingmodule.presentation.viewModel.ItemPutawayCompletionVm
import com.mobillor.locpostingmodule.presentation.viewModelFactory.ItemPutawayVmFactory
import com.mobillor.locpostingmodule.util.BaseActivity
import com.mobillor.locpostingmodule.util.Resource
import com.mobillor.themeresourcemodule.commonComposables.BasicBottomButton
import com.mobillor.themeresourcemodule.commonComposables.ui.theme.SEGMK3Theme
import org.json.JSONException
import org.json.JSONObject

class ItemPutawayCompletion : BaseActivity() {
    private lateinit var viewModel : ItemPutawayCompletionVm

    private var itemList: List<DataResponseItemInfo>? = mutableListOf()
    private var locationCode = 0
    private var sourceCode = 0
    private var palletCode = 0
    private var binCode = 0
    private var userID  = ""

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            showCameraForLoc()
        }
        else {
            ActivityCompat.requestPermissions(
                this@ItemPutawayCompletion, arrayOf(
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
                this@ItemPutawayCompletion, arrayOf(
                    android.Manifest.permission.CAMERA,
                ), 101
            )
        }
    }
    private val requestPermissionLauncherBinSpecific = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            showCameraBin()
        }
        else {
            ActivityCompat.requestPermissions(
                this@ItemPutawayCompletion, arrayOf(
                    android.Manifest.permission.CAMERA,
                ), 101
            )
        }
    }


    var location_scanning=false
    var bin_scanning=false
    var pallet_scanning=false

    private var dataFromRecycler : List<DataResponseItemInfo> = mutableListOf()
    private var itemInput: InputMappedtoItem = InputMappedtoItem(locationCode, emptyList(), "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor()
        setUp()
        setContent{ SEGMK3Theme {
            ItemPutawayCompletionGreetings()
        }}
    }

    @Composable
    private fun ItemPutawayCompletionGreetings(){
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 40.dp)){
            TopNavigationBar(name = "Item Posting") {
                onBackPressed()
            }
            Row(
                Modifier
                    .height(48.dp)
                    .padding(end = 8.dp)){
                Spacer(Modifier.weight(1f))
                LocationHintForItem(viewModel)
            }
            Row(
                Modifier
                    .height(48.dp)
                    .padding(end = 16.dp)){
                Spacer(Modifier.weight(1f))
                PalletHintForItem(viewModel)
                Spacer(modifier = Modifier.width(4.dp))
                BinHintForItem(viewModel)
            }
            LocationPostingItemInfoListForItemPutaway(itemList?: emptyList())


        }
        Column(
            Modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, end = 0.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Column (
                    horizontalAlignment = Alignment.End){
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
                    Row(){
                        LocationPostingBinFloatingButtonForItemPutaway{

                                QRCodeInputFromMobileCameraBin()
                            }
                        Column(Modifier.width(12.dp)) {

                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    BasicBottomButton(title = "Putaway") {

                        dataFromRecycler = itemList!!
                        Log.d("taggss", dataFromRecycler.toString())

                        initiatePutaway()
                    }
                }
            }
        }
    }

    private fun setUp() {
        val repository = ItemPutawayRepositoryImpl(NewApiClient.getLocPostingService())
        val palletRepository = PalletPutawayRepositoryImpl(NewApiClient.getLocPostingService())
        val binRepository = BinPutawayRepositoryImpl(NewApiClient.getLocPostingService())
        val getLocationUseCase= GetLocationUseCase(palletRepository)
        val getPalletLocationUseCase = GetPalletLocationUseCase(binRepository)
        val getBinLocationUseCase = GetBinLocationUseCase(repository)
        val putawayItemUseCase = PutawayItemUseCase(repository)
        viewModel = ViewModelProvider(this,
            ItemPutawayVmFactory(getLocationUseCase,getPalletLocationUseCase,getBinLocationUseCase,putawayItemUseCase)
        )[ItemPutawayCompletionVm::class.java]

        if(!isInternetAvailable(this)){
            Toast.makeText(this,"Internet Unavailable",Toast.LENGTH_LONG).show()
        }

        userID = LocPostingStarterActivity.USER_ID
        getIntentData()
        observer()
    }
    private fun observer() {
        viewModel.locationDataResponseLiveData.observe(this) { response ->
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
        viewModel.binCodeResponseLiveData.observe(this) { response ->
            loadingPopup.show()
            when (response) {
                is Resource.Loading -> {
                    loadingPopup.show()
                }

                is Resource.Success -> {
                    loadingPopup.dismiss()
                    binCode = response.data?.data?.get(0)?.binId ?: 0
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
        viewModel.itemPutawayResponseLiveData.observe(this) { response ->
            loadingPopup.show()
            when (response) {
                is Resource.Loading -> {
                    loadingPopup.show()
                }

                is Resource.Success -> {
                    loadingPopup.dismiss()
                    val msg = response.message
                    Toast.makeText(this, response.data?.msg?:"putaway successful", Toast.LENGTH_SHORT).show()
                    updateLocalSelectionValues()
                    //  iadapter = PutawayItemInfoAdapter(this, dataFromRecycler)
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

    private fun getIntentData() {
        val itemString = intent.getSerializableExtra("item")as? ArrayList<*>
        itemList = itemString?.filterIsInstance<DataResponseItemInfo>()
        sourceCode = itemList!![0].locationId ?:0
        Log.e("taggss","$ bin : $itemList")
    }


    private fun initiatePutaway() {
        if(locationCode != 0 ){
            if(!itemList.isNullOrEmpty()){
                itemInput.locationId = locationCode
                itemInput.userId = userID

                populateSuids()
                Log.d("tagss",itemInput.toString())
                viewModel.itemPutaway(LocPostingStarterActivity.USER_TOKEN, itemInput)

            }
            else{
                Toast.makeText(this,"Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this,getString(R.string.scan_location), Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateSuids() {
        itemList?.let { list ->
            val suids = list.mapNotNull { dataResponseItemInfo ->
                val binInfo = dataFromRecycler.find { it.suid == dataResponseItemInfo.suid }
                if (binInfo?.localSelectionValue == 0) {
                    InputMappedtoItem.Suid(
                        binId = binCode,
                        palletId = palletCode,
                        srcLocationId = dataResponseItemInfo.locationId ?:0,
                        suid = dataResponseItemInfo.suid ?:""
                    )
                } else {
                    null
                }
            }
            itemInput.suids = suids
        }
    }
    private fun QRCodeInputFromMobileCameraLocation() {

        if (ContextCompat.checkSelfPermission(
                this@ItemPutawayCompletion,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCameraForLoc()
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(
                this@ItemPutawayCompletion,
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
                viewModel.getLocationCode(LocPostingStarterActivity.USER_TOKEN, location)
                loadingPopup.show()

            }
        }
    }

    private fun QRCodeInputFromMobileCameraPallet() {

        if (ContextCompat.checkSelfPermission(
                this@ItemPutawayCompletion,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCamera()
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(
                this@ItemPutawayCompletion,
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
                viewModel.getPalletCode(LocPostingStarterActivity.USER_TOKEN ,pallet)
                loadingPopup.show()
            }
        }
    }

    private fun QRCodeInputFromMobileCameraBin() {

        if (ContextCompat.checkSelfPermission(
                this@ItemPutawayCompletion,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCameraBin()
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(
                this@ItemPutawayCompletion,
                "Camera permission required",
                Toast.LENGTH_LONG
            ).show()
        } else {
            requestPermissionLauncherBinSpecific.launch(android.Manifest.permission.CAMERA)
        }
    }
    private fun showCameraBin() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
        options.setPrompt("Scan QR code")
        options.setCameraId(0)
        options.setBeepEnabled(true)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(true)

        scanBin.launch(options)
    }
    private val scanBin = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
        run {
            if (result.contents != null) {
                val bin = result.contents.toString()
                viewModel.getBinCode(LocPostingStarterActivity.USER_TOKEN,bin)
                loadingPopup.show()
            }
        }
    }

}