package com.mobillor.locpostingmodule.presentation.activity


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.mobillor.locpostingmodule.domain.useCase.GetItemByIdUseCase
import com.mobillor.locpostingmodule.domain.useCase.GetPalletByIdUseCase
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.mobillor.goodsinwardmodule.data.apiclient.NewApiClient
import com.mobillor.locpostingmodule.R
import com.mobillor.locpostingmodule.data.model.DataResponseBinInfo
import com.mobillor.locpostingmodule.data.model.DataResponseItemInfo
import com.mobillor.locpostingmodule.data.model.DataResponsePalletInfo
import com.mobillor.locpostingmodule.data.repository.PalletScanRepositoryImpl
import com.mobillor.locpostingmodule.domain.useCase.GetBinByIdUseCase

import com.mobillor.locpostingmodule.presentation.composable.ColorChangingRow
import com.mobillor.locpostingmodule.presentation.composable.TopNavigationBar
import com.mobillor.locpostingmodule.presentation.viewModel.PalletScanVm
import com.mobillor.locpostingmodule.presentation.viewModelFactory.PalletScanVmFactory
import com.mobillor.locpostingmodule.util.BaseActivity
import com.mobillor.locpostingmodule.util.Resource
import com.mobillor.themeresourcemodule.commonComposables.ScanHintCard

import com.mobillor.themeresourcemodule.commonComposables.ui.theme.SEGMK3Theme
import com.mobillor.themeresourcemodule.commonComposables.ui.theme.darkPurpleStuff
import com.mobillor.themeresourcemodule.commonComposables.ui.theme.lightPurpleStuff
import org.json.JSONException
import org.json.JSONObject

class LocPostingStarterActivity : BaseActivity() {
    private lateinit var viewModel: PalletScanVm
    private var baseURL : String =""
    private var userToken : String =""
    private var userIdD : String = ""

    private var typeSelected : Int = 1
    var status=""
    private var dataResponsePalletInfo = ArrayList<DataResponsePalletInfo>()
    private var dataResponseItemInfo = ArrayList<DataResponseItemInfo>()
    private var dataResponseBinInfo = ArrayList<DataResponseBinInfo>()

    private var intentOnce = true
    private var intentOnceToggle = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor()
        setUp()
        setContent{
            SEGMK3Theme {
                PalletscanActivityGreetings()
            }
        }
    }
    @Composable
    fun PalletscanActivityGreetings(){

        val isExpanded by viewModel.isExpanded
        val columnHeight by animateDpAsState(if (isExpanded) 500.dp else 0.dp)
        var currentStatus by remember { mutableStateOf("QR Code") }
        val deviceType = ""

        Scaffold(
            topBar = {
                TopNavigationBar(name = "Location Posting") { onBackPressed()}
            }
        ) {innerContent->



            Column(
                Modifier
                    .fillMaxSize()
                    .padding(innerContent)
                    .heightIn(min = 0.dp, max = columnHeight)
                    .animateContentSize()){
                if(isExpanded){
                    ScanHintCard("Scan your choice !")
                    ColorChangingRow(
                        onText1Click = {     typeSelected =1 },
                        onText2Click = {  typeSelected =3 },
                        onText3Click = {  typeSelected =2 },
                        viewModel
                    )
                }
            }

            Column(Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 56.dp, end = 16.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {

                    // Use combined composable function here
                    StatusAndScanButton(
                        currentStatus = currentStatus,
                        onStatusChange = { status ->
                            currentStatus = status
                            Log.e("checkingstatus2", currentStatus)
                        },
                        onScanButtonClick = {
                            handleScanButtonClick(status)
                        }, deviceType = deviceType, context = this@LocPostingStarterActivity
                    )

                    if ( !isExpanded) {

                        for (i in viewModel.PalletList.value?: emptyList()){
                            if (!dataResponsePalletInfo.contains(i)) {
                                dataResponsePalletInfo.add(i)
                                Log.e("tag",viewModel.PalletList.value.toString())
                            }
                        }

                        for(i in viewModel.ItemList.value?: emptyList()){
                            if (!dataResponseItemInfo.contains(i)) {
                                dataResponseItemInfo.add(i)
                            }
                        }
                        for(i in viewModel.BinList.value?: emptyList()){
                            if (!dataResponseBinInfo.contains(i)) {
                                dataResponseBinInfo.add(i)
                            }
                        }


                        Log.d("taggs","pallet : $dataResponsePalletInfo , item : itemString , bin : binString")


                        if (intentOnce){

                            when (typeSelected) {
                                1 -> {
                                    val i = Intent(this@LocPostingStarterActivity,PutawayCompletion::class.java)
                                    i.putExtra("pallet", dataResponsePalletInfo)
                                    i.putExtra("item", dataResponseItemInfo)
                                    i.putExtra("bin", dataResponseBinInfo)
                                    i.putExtra("currentstatus",currentStatus)
                                    startActivity(i)
                                    viewModel.toggleExpanded()
                                    intentOnceToggle = true

                                }
                                2 -> {
                                    val i = Intent(this@LocPostingStarterActivity,ItemPutawayCompletion::class.java)
                                    i.putExtra("pallet", dataResponsePalletInfo)
                                    i.putExtra("item", dataResponseItemInfo)
                                    i.putExtra("bin", dataResponseBinInfo)
                                    i.putExtra("currentstatus",currentStatus)
                                    startActivity(i)
                                    viewModel.toggleExpanded()
                                    intentOnceToggle = true
                                }
                                3 -> {
                                    val i = Intent(this@LocPostingStarterActivity,BinPutawayCompletion::class.java)
                                    i.putExtra("pallet", dataResponsePalletInfo)
                                    i.putExtra("item", dataResponseItemInfo)
                                    i.putExtra("bin", dataResponseBinInfo)
                                    i.putExtra("currentstatus",currentStatus)
                                    startActivity(i)
                                    viewModel.toggleExpanded()
                                    intentOnceToggle = true
                                }
                            }

                            intentOnce = intentOnceToggle

                        }


                    }
                }
            }
        }
    }
    @Composable
    fun StatusAndScanButton(
        currentStatus: String,
        onStatusChange: (String) -> Unit,
        onScanButtonClick: (String) -> Unit,
        deviceType: String,
        context: Context
    ) {
        var switchState by remember { mutableStateOf(currentStatus == "RFID") }
        val status = if (switchState) "RFID" else "QR Code"

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, bottom = 56.dp, end = 10.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Status Switch
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = "Mode: $status",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Switch(
                        checked = switchState,
                        onCheckedChange = { isChecked ->
                                switchState = isChecked
                                onStatusChange(if (isChecked) "RFID" else "QR Code")

                        }
                    )
                }

                // Bin Scan Button
                FloatingActionButton(
                    onClick = { onScanButtonClick(status) },
                    containerColor = lightPurpleStuff,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.binss),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(darkPurpleStuff),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
    private fun setUp() {
        baseURL = intent.getStringExtra("baseURL")?:""
        userToken = intent.getStringExtra("userToken")?:""
        userIdD =  intent.getStringExtra("userId")?:""
        BASE_URL = baseURL
        USER_TOKEN = userToken
        USER_ID = userIdD
        if(!isInternetAvailable(this)){
            Toast.makeText(this,"Internet Unavailable", Toast.LENGTH_LONG).show()
        }
        val repository = PalletScanRepositoryImpl(NewApiClient.getLocPostingService())
        val getPalletByIdUseCase= GetPalletByIdUseCase(repository)
        val getBinByIdUseCase= GetBinByIdUseCase(repository)
        val getItemByIdUseCase= GetItemByIdUseCase(repository)
        viewModel = ViewModelProvider(this,
            PalletScanVmFactory(getPalletByIdUseCase,getBinByIdUseCase,getItemByIdUseCase)
        )[PalletScanVm::class.java]

        //   getIntentData()
        //     setOnclick()
        observer()
    }
    private fun observer() {
        viewModel.ResponsePalletById.observe(this) {
            when(it){
                is Resource.Loading ->{
                    loadingPopup.show()
                }
                is Resource.Success ->{
                    loadingPopup.dismiss()
                    viewModel.toggleExpanded()
                    viewModel.addtoPalletList(it.data!!.data[0])
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

                else -> {
                    Log.e("ElseTrigger","ElseTrigger")}
            }
        }
        viewModel.ResponseBinById.observe(this ){
            when(it){
                is Resource.Loading ->{
                    loadingPopup.show()
                }
                is Resource.Success ->{
                    loadingPopup.dismiss()
                    viewModel.toggleExpanded()
                    viewModel.addtoBinList(it.data!!.data[0])
                }
                is Resource.Error ->{
                    loadingPopup.dismiss()
                    val errorMessage = try {
                        JSONObject(it.message ?: "").getString("msg")
                    }
                    catch (e: JSONException) { "An error occurred gg" }
                    Log.e("testing", "hhkj ${it.message}")
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }

                else -> {
                    Log.e("ElseTrigger","ElseTrigger")}
            }
        }
        viewModel.ResponseItemById.observe(this) {
            when(it){
                is Resource.Loading ->{
                    loadingPopup.show()
                }
                is Resource.Success ->{
                    loadingPopup.dismiss()
                    viewModel.toggleExpanded()
                    viewModel.addtoItemList(it.data!!.data[0])
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

                else -> {
                    Log.e("ElseTrigger","ElseTrigger")}
            }
        }
    }


    private fun handleScanButtonClick(currentStatus: String) {

            Log.e("checkingqmob",currentStatus)
            QRCodeInputFromMobileCamera()

    }

    private fun QRCodeInputFromMobileCamera() {

        if (ContextCompat.checkSelfPermission(this@LocPostingStarterActivity, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showCamera()
        }
        else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(
                this@LocPostingStarterActivity,
                "Camera permission required",
                Toast.LENGTH_LONG
            ).show()
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

        scanLauncher.launch(options)
    }
    private val scanLauncher = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
        run {
            if (result.contents != null) {
                val scannedId = result.contents.toString()
                Log.d("PalletScanRepositoryLogs","scanner called type = $typeSelected")
                viewModel.dataCaller(typeSelected,this@LocPostingStarterActivity.userToken,scannedId)

            } else {
                Toast.makeText(this@LocPostingStarterActivity, "Cancelled", Toast.LENGTH_LONG).show()
            }
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            showCamera()
        }
        else {
            ActivityCompat.requestPermissions(
                this@LocPostingStarterActivity, arrayOf(
                    android.Manifest.permission.CAMERA,
                ), 101
            )
        }
    }


    companion object{
        var BASE_URL = ""
        var USER_TOKEN = ""
        var USER_ID = ""
    }
}