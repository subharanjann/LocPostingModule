package com.mobillor.locpostingmodule.presentation.composable

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults.cardElevation
import com.mobillor.locpostingmodule.presentation.viewModel.BinPutawayCompletionVm
import com.mobillor.locpostingmodule.presentation.viewModel.ItemPutawayCompletionVm
import com.mobillor.locpostingmodule.presentation.viewModel.PalletPutawayCompletionVm
import com.mobillor.locpostingmodule.presentation.viewModel.PalletScanVm
import com.mobillor.locpostingmodule.R
import com.mobillor.locpostingmodule.data.model.DataResponseBinInfo
import com.mobillor.locpostingmodule.data.model.DataResponseItemInfo
import com.mobillor.locpostingmodule.data.model.DataResponseMappedToPallet
import com.mobillor.locpostingmodule.data.model.DataResponsePalletInfo
import com.mobillor.locpostingmodule.data.model.ResponseMappedToBin
import com.mobillor.themeresourcemodule.commonComposables.BasicScanButton
import com.mobillor.themeresourcemodule.commonComposables.CurvedBoxWithDottedBorder
import com.mobillor.themeresourcemodule.commonComposables.DashedLine
import com.mobillor.themeresourcemodule.commonComposables.TextStyles
import com.mobillor.themeresourcemodule.commonComposables.VerticalDashedLine
import com.mobillor.themeresourcemodule.commonComposables.ui.theme.SEGMK3Theme
import com.mobillor.themeresourcemodule.commonComposables.ui.theme.darkIndigo
import com.mobillor.themeresourcemodule.commonComposables.ui.theme.lightIndigo
import com.mobillor.themeresourcemodule.commonComposables.ui.theme.lightPurpleStuff
import com.mobillor.themeresourcemodule.commonComposables.ui.theme.primaryColor
import com.mobillor.themeresourcemodule.commonComposables.ui.theme.primaryColorAccent
import com.mobillor.themeresourcemodule.commonComposables.ui.theme.secondaryColorAccent
import com.mobillor.themeresourcemodule.commonComposables.ui.theme.white
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale


@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    SEGMK3Theme {
        Greeting3()
    }
}
@Composable
fun Greeting3() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 48.dp, 0.dp, 0.dp)
    ){
        val context = LocalContext.current
        LocationHint()
        PalletHint("hellow world")
        BinHint()

        Row(){
            LocationPostingLocationFloatingButton(){
                Toast.makeText(context,"location clicki", Toast.LENGTH_SHORT).show()
            }
            LocationPostingPalletFloatingButton(){
                Toast.makeText(context,"pallet clicki", Toast.LENGTH_SHORT).show()
            }
            LocationPostingBinFloatingButton(){
                Toast.makeText(context,"bin clicki", Toast.LENGTH_SHORT).show()
            }
            BasicScanButton {

            }
        }

    }

}

//Cards
@Composable
fun LocationPostingBinInfoCard(card: DataResponseBinInfo) {

    val hint = if (card.binType.isNullOrEmpty()) "Info Unavailable"
    else card.binType
    Card (
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .padding(8.dp)){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // First Column (left side content)
            Column(
                modifier = Modifier
                    .weight(1f) // Takes as much space as possible
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Bin Code ",
                        style = TextStyles.smallMediumNormalTextStyle,
                        modifier = Modifier
                            .width(80.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = ":",
                        style = TextStyles.smallMediumNormalTextStyle,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Text(
                        text = card.binCode ?:"NA",
                        style = TextStyles.smallMediumNormalTextStyle
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)) {
                    Text(
                        text = "Id",
                        style = TextStyles.smallMediumNormalTextStyle,
                        modifier = Modifier
                            .width(80.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = ":",
                        style = TextStyles.smallMediumNormalTextStyle,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Text(
                        text = (card.binId ?:0).toString(),
                        style = TextStyles.smallMediumNormalTextStyle
                    )
                }
            }

            // Second Column (right side content with image)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Top

            ) {
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp, 16.dp, 4.dp)
        ){
            DashedLine()
        }
        Row(
            modifier = Modifier
                .padding(16.dp,4.dp,16.dp,8.dp)
        ){
            Text(
                text = "Type",
                style = TextStyles.smallNormalTextStyle,
                modifier = Modifier
                    .width(80.dp)

            )
            Text(
                text = hint,
                style = TextStyles.smallNormalTextStyle,
                modifier = Modifier
                    .width(200.dp)

            )
        }
    }


}

@Composable
fun MappedToBinInfoList(viewModel: BinPutawayCompletionVm){
    val state by viewModel.mappedtoBin.observeAsState()
    val list = state?.data?.data?: emptyList()
    LazyColumn(
        Modifier
            .fillMaxSize()
            .heightIn(0.dp, 400.dp)
            .padding(8.dp)) {
            items(list){ card ->
              LocationPostingPostingMappedToBin("Suid", "Qty",card)

            }
    }
    Spacer(modifier = Modifier.height(32.dp))

}

@Composable
fun MappedToPalletInfoList(viewModel: PalletPutawayCompletionVm){
    val state by viewModel.mappedToPalletResponseLiveData.observeAsState()
    val list = state?.data?.data?: emptyList()
    LazyColumn(
        Modifier
            .fillMaxSize()
            .heightIn(0.dp, 400.dp)
            .padding(8.dp)) {
        items(list){ card ->
            LocationPostingPostingMappedToPallet("Suid", "Qty",card)

        }
    }
    Spacer(modifier = Modifier.height(32.dp))

}
//pallet
@Composable
fun LocationPostingPalletInfoCard(card: DataResponsePalletInfo, convertDateFormat: String?, vm : PalletPutawayCompletionVm) {
    if(!vm.PicklistList.contains(card.palletId)) {vm.PicklistList.add(card.palletId?:0)}
    Card (
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .padding(8.dp)){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 14.dp, 16.dp, 4.dp)
        ) {
            // First Column (left side content)
            Column(
                modifier = Modifier
                    .weight(1f) // Takes as much space as possible
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Pallet Name ",
                        style = TextStyles.smallMediumNormalTextStyle,
                        modifier = Modifier
                            .width(80.dp)
                    )
                    Text(
                        text = ":",
                        style = TextStyles.smallMediumNormalTextStyle,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                    Text(
                        text = card.palletName ?:"NA",
                        style = TextStyles.smallMediumNormalTextStyle
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                    Text(
                        text = "Code",
                        style = TextStyles.smallMediumNormalTextStyle,
                        modifier = Modifier
                            .width(80.dp)
                    )
                    Text(
                        text = ":",
                        style = TextStyles.smallMediumNormalTextStyle,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Text(
                        text = card.palletCode?:"NA",
                        style = TextStyles.smallMediumNormalTextStyle
                    )
                }
            }
//
//            // Second Column (right side content with image)
//            Column(
//                horizontalAlignment = Alignment.End,
//                verticalArrangement = Arrangement.Top
//
//            ) {
//                Checkbox(
//                    checked = false,
//                    modifier = Modifier
//                        .size(20.dp),
//                    onCheckedChange = { isChecked ->
//
//                    })
//            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp, 16.dp, 4.dp)
        ){
            DashedLine()
        }
        Row(
            modifier = Modifier
                .padding(16.dp,4.dp,16.dp,8.dp)
        ){
            Text(
                text = "CD",
                style = TextStyles.smallNormalGreyTextStyle,
                modifier = Modifier
                    .width(40.dp)

            )
            Text(
                text = convertDateFormat.toString(),
                style = TextStyles.smallNormalTextStyle,
                modifier = Modifier
                    .width(200.dp)

            )
        }
    }


}


//item
@Composable
fun LocationPostingItemInfoCard(card: DataResponseItemInfo) {
    Card (
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .padding(8.dp)){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // First Column (left side content)
            Column(
                modifier = Modifier
                    .weight(1f) // Takes as much space as possible
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "SUID ",
                        style = TextStyles.smallMediumNormalTextStyle,
                        modifier = Modifier
                            .width(80.dp)
                            .padding(end = 4.dp)

                    )
                    Text(
                        text = ":",
                        style = TextStyles.smallMediumNormalTextStyle,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                    )
                    Text(
                        text = card.suid ?:"NA",
                        style = TextStyles.smallMediumNormalTextStyle
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)) {
                    Text(
                        text = "ItemCode",
                        style = TextStyles.smallMediumNormalTextStyle,
                        modifier = Modifier
                            .width(80.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = ":",
                        style = TextStyles.smallMediumNormalTextStyle,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Text(
                        text = card.itemCode ?:"NA",
                        style = TextStyles.smallMediumNormalTextStyle
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)) {
                    Text(
                        text = "Asn Code",
                        style = TextStyles.smallMediumNormalTextStyle,
                        modifier = Modifier
                            .width(80.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = ":",
                        style = TextStyles.smallMediumNormalTextStyle,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Text(
                        text = card.asnCode?:"NA",
                        style = TextStyles.smallMediumNormalTextStyle
                    )
                }
            }

            // Second Column (right side content with image)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Top

            ) {
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp, 16.dp, 4.dp)
        ){
            DashedLine()
        }
        Row(
            modifier = Modifier
                .padding(16.dp,4.dp,16.dp,8.dp)
        ){
            Text(
                text = "Loc Code",
                style = TextStyles.smallNormalTextStyle,
                modifier = Modifier
                    .width(80.dp)

            )
            Text(
                text = card.locationCode?:"NA",
                style = TextStyles.smallNormalTextStyle,
                modifier = Modifier
                    .width(200.dp)

            )
        }
    }

}

@Composable
fun LocationPostingItemInfoListForItemPutaway(list:List<DataResponseItemInfo>){

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(bottom = 4.dp)) {
        if(!list.isNullOrEmpty())
            items(list){card ->
                LocationPostingItemInfoCard(card)
            }
    }

}




//second screen
@Composable
fun LocationPostingPostingMappedToBin(
    labelA: String,
    labelB: String,
    card: ResponseMappedToBin.DataResponseMappedToBin
){
    Card (
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .padding(horizontal =  8.dp, vertical = 1.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // First Column (left side content)
            Column(
                modifier = Modifier
                    .weight(1f) // Takes as much space as possible
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = labelA,
                        style = TextStyles.smallMediumNormalTextStyle,
                        modifier = Modifier
                            .width(70.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = ":",
                        style = TextStyles.smallMediumNormalTextStyle,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Text(
                        text = card.suid ?:"NA",
                        style = TextStyles.smallMediumNormalTextStyle
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)) {
                    Text(
                        text = labelB,
                        style = TextStyles.smallMediumNormalTextStyle,
                        modifier = Modifier
                            .width(70.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = ":",
                        style = TextStyles.smallMediumNormalTextStyle,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Text(
                        text = (card.qty ?:0.0).toString(),
                        style = TextStyles.smallMediumNormalTextStyle
                    )
                }
            }

        }
    }

}
@Composable
fun LocationPostingPostingMappedToPallet(
    labelA: String,
    labelB: String,
    card: DataResponseMappedToPallet,

    ){
    Card (
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .padding(horizontal =  8.dp, vertical = 1.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // First Column (left side content)
            Column(
                modifier = Modifier
                    .weight(1f) // Takes as much space as possible
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = labelA,
                        style = TextStyles.smallNormalGreyTextStyle,
                        modifier = Modifier
                            .width(70.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = ":",
                        style = TextStyles.smallNormalTextStyle,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Text(
                        text = card.suid ?:"NA",
                        style = TextStyles.smallNormalTextStyle
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)) {
                    Text(
                        text = labelB,
                        style = TextStyles.smallNormalGreyTextStyle,
                        modifier = Modifier
                            .width(70.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = ":",
                        style = TextStyles.smallNormalTextStyle,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Text(
                        text = (card.qty?: 0.0).toString(),
                        style = TextStyles.smallNormalTextStyle
                    )
                }
            }

        }
    }

}

//hint

@Composable
fun LocationHint(){
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White,),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .padding(8.dp),
        shape = MaterialTheme.shapes.small

    ){
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.locationlogo),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Black),
                modifier = Modifier
                    .size(18.dp),

                contentScale = ContentScale.Fit
            )
            Text(text = "Scan Location!",
                style = TextStyles.smallMediumNormalTextStyle,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
        }
    }

}

@Composable
fun LocationHintForItem(viewModel: ItemPutawayCompletionVm) {
    val state by viewModel.locationDataResponseLiveData.observeAsState()
    val data = state?.data?.data?.get(0)?.locationCode?:"Scan Location!!"
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White,),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .padding(8.dp),
        shape = MaterialTheme.shapes.small

    ){
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.locationlogo),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Black),
                modifier = Modifier
                    .size(18.dp),

                contentScale = ContentScale.Fit
            )
            Text(text = data,
                style = TextStyles.smallWildcardTextStyle,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
        }
    }

}
@Composable
fun LocationHintForBin(viewModel: BinPutawayCompletionVm) {
    val state by viewModel.locationDataResponseLiveData.observeAsState()
    val data = state?.data?.data?.get(0)?.locationCode?:"Scan Location!!"
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White,),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .padding(8.dp),
        shape = MaterialTheme.shapes.small

    ){
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.locationlogo),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Black),
                modifier = Modifier
                    .size(18.dp),

                contentScale = ContentScale.Fit
            )
            Text(text = data,
                style = TextStyles.smallWildcardTextStyle,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
        }
    }

}
@Composable
fun LocationHintForPallet(viewModel: PalletPutawayCompletionVm) {
    val state by viewModel.locationDataResponseLiveData.observeAsState()
    val data = state?.data?.data?.get(0)?.locationCode?:"Scan Location!!"
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White,),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .padding(8.dp),
        shape = MaterialTheme.shapes.small

    ){
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.locationlogo),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Black),
                modifier = Modifier
                    .size(18.dp),

                contentScale = ContentScale.Fit
            )
            Text(text = data,
                style = TextStyles.smallWildcardTextStyle,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
        }
    }

}
@Composable
fun PalletHint(hint : String){
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .padding(horizontal = 0.dp),
        shape = MaterialTheme.shapes.small

    ){
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.pallet),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Black),
                modifier = Modifier
                    .size(18.dp),

                contentScale = ContentScale.Fit
            )
            Text(text = hint,
                style = TextStyles.smallMediumNormalTextStyle,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
        }
    }

}
@Composable
fun PalletHintForItem( viewModel: ItemPutawayCompletionVm){
    val state by viewModel.palletCodeResponseLiveData.observeAsState()
    val data = state?.data?.data?.get(0)?.palletCode?:"Scan Pallet"
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .padding(horizontal = 0.dp),
        shape = MaterialTheme.shapes.small

    ){
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.pallet),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Black),
                modifier = Modifier
                    .size(18.dp),

                contentScale = ContentScale.Fit
            )
            Text(text = data,
                style = TextStyles.smallWildcardTextStyle,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
        }
    }

}
@Composable
fun PalletHintForBin( viewModel: BinPutawayCompletionVm){
    val state by viewModel.palletCodeResponseLiveData.observeAsState()
    val data = state?.data?.data?.get(0)?.palletCode?:"Scan Pallet"
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .padding(horizontal = 0.dp),
        shape = MaterialTheme.shapes.small

    ){
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.pallet),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Black),
                modifier = Modifier
                    .size(18.dp),

                contentScale = ContentScale.Fit
            )
            Text(text = data,
                style = TextStyles.smallWildcardTextStyle,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
        }
    }

}
@Composable
fun BinHint(){
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White,),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .padding(8.dp),
        shape = MaterialTheme.shapes.small

    ){
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.palletbox),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Black),
                modifier = Modifier
                    .size(18.dp),

                contentScale = ContentScale.Fit
            )
            Text(text = "Scan Pallet!",
                style = TextStyles.smallMediumNormalTextStyle,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
        }
    }

}
@Composable
fun BinHintForItem(viewModel: ItemPutawayCompletionVm) {
    val state by viewModel.binCodeResponseLiveData.observeAsState()
    val data = state?.data?.data?.get(0)?.binCode?:"Bin Info"
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White,),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .padding(0.dp),
        shape = MaterialTheme.shapes.small

    ){
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.palletbox),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Black),
                modifier = Modifier
                    .size(18.dp),

                contentScale = ContentScale.Fit
            )
            Text(text = data,
                style = TextStyles.smallWildcardTextStyle,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
        }
    }

}
@Composable
fun TopNavigationBar(name : String,OnBackClick : ()->Unit) {

    Card(
        colors = CardDefaults.cardColors(containerColor = primaryColor),
        elevation = cardElevation(8.dp),
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .height(60.dp)
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        )
        {

            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "",

                modifier = Modifier
                    .size(40.dp)
                    .padding(start = 16.dp)
                    .clickable(onClick = OnBackClick)
            )
            Spacer(modifier = Modifier
                .height(20.dp)
                .weight(1f))

            Text(
                text = name,
                style = TextStyles.whiteTitleTextStyle
            )

            Spacer(modifier = Modifier.height(20.dp).weight(1f))
                Image(
                    painter = painterResource(id = com.mobillor.themeresourcemodule.R.drawable.homeicon2),
                    contentDescription = "",

                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 16.dp),
                    colorFilter = ColorFilter.tint(primaryColor)                 )


        }

    }

}
//buttons



@Composable
fun  LocationPostingLocationFloatingButton(onClick: () -> Unit){
    FloatingActionButton(

        modifier = Modifier
            .size(56.dp)
            .padding(4.dp)
            .background(lightPurpleStuff),
        onClick = onClick) {
        Image(
            painter = painterResource(id = R.drawable.scanlocation),
            contentDescription = "",
            colorFilter = ColorFilter.tint(Color.Black),
            modifier = Modifier
                .size(24.dp)

        )
    }
}
@Composable
fun  LocationPostingLocationFloatingButtonForItemPutaway(onClick: () -> Unit){
    FloatingActionButton(

        modifier = Modifier
            .size(56.dp)
            .padding(4.dp),
        containerColor = lightIndigo,
        contentColor = darkIndigo,
        onClick = onClick) {
        Image(
            painter = painterResource(id = R.drawable.scanlocation),
            contentDescription = "",
            colorFilter = ColorFilter.tint(Color.Black),
            modifier = Modifier
                .size(24.dp)

        )
    }
}

@Composable
fun  LocationPostingPalletFloatingButton(onClick: () -> Unit){
    FloatingActionButton(

        modifier = Modifier
            .size(56.dp)
            .padding(4.dp)
            .background(lightPurpleStuff),
        onClick = onClick) {
        Image(
            painter = painterResource(id = R.drawable.pallet),
            contentDescription = "",
            colorFilter = ColorFilter.tint(Color.Black),
            modifier = Modifier
                .size(24.dp)

        )
    }
}
@Composable
fun  LocationPostingPalletFloatingButtonForItemPutaway(onClick: () -> Unit){
    FloatingActionButton(

        modifier = Modifier
            .size(56.dp)
            .padding(4.dp),
        containerColor = primaryColorAccent,
        contentColor = darkIndigo,
        onClick = onClick) {
        Image(
            painter = painterResource(id = R.drawable.pallet),
            contentDescription = "",
            colorFilter = ColorFilter.tint(Color.Black),
            modifier = Modifier
                .size(24.dp)

        )
    }
}

@Composable
fun  LocationPostingBinFloatingButton(onClick: () -> Unit){
    FloatingActionButton(

        modifier = Modifier
            .size(56.dp)
            .padding(4.dp)
            .background(lightPurpleStuff),
        onClick = onClick) {
        Image(
            painter = painterResource(id = R.drawable.palletbox),
            contentDescription = "",
            colorFilter = ColorFilter.tint(Color.Black),
            modifier = Modifier
                .size(24.dp)

        )
    }
}
@Composable
fun  LocationPostingBinFloatingButtonForItemPutaway(onClick: () -> Unit){
    FloatingActionButton(

        modifier = Modifier
            .size(56.dp)
            .padding(4.dp),
        containerColor = secondaryColorAccent,
        contentColor = darkIndigo,
        onClick = onClick) {
        Image(
            painter = painterResource(id = R.drawable.palletbox),
            contentDescription = "",
            colorFilter = ColorFilter.tint(Color.Black),
            modifier = Modifier
                .size(24.dp)

        )
    }
}
@Composable
fun ColorChangingRow(
    onText1Click: () -> Unit = {},
    onText2Click: () -> Unit = {},
    onText3Click: () -> Unit = {},
    viewModel: PalletScanVm
) {
    CurvedBoxWithDottedBorder(64.dp,16.dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        val color1 by viewModel::color1
        val color2 by viewModel::color2
        val color3 by viewModel::color3

        Text(
            text = "Pallet",
            color = color1,
            style = TextStyles.smallMediumNormalTextStyle,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .clickable {
                    viewModel.changeColors(exceptIndex = 1)
                    onText1Click()
                }
        )
        VerticalDashedLine(
            Modifier
                .fillMaxHeight()
                .align(Alignment.CenterVertically)
        )
        Text(
            text = "Bin",
            color = color2,
            style = TextStyles.smallMediumNormalTextStyle,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .clickable {
                    viewModel.changeColors(exceptIndex = 2)
                    onText2Click()
                }
        )
        VerticalDashedLine(
            Modifier
                .fillMaxHeight()
                .align(Alignment.CenterVertically)
        )
        Text(
            text = "Item",
            color = color3,
            style = TextStyles.smallMediumNormalTextStyle,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .clickable {
                    viewModel.changeColors(exceptIndex = 3)
                    onText3Click()
                }
        )
    }}
}
