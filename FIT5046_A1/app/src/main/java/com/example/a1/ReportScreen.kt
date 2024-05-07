package com.example.a1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import android.app.DatePickerDialog
import android.widget.Toast


import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.viewinterop.AndroidView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.common.io.Files.append

fun getLastSevenDays(): List<String> {

    val dates = mutableListOf<String>()
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // 包括今天在内，向后获取7天的日期
    for (i in 0 until 7) {
        dates.add(dateFormat.format(calendar.time))
        calendar.add(Calendar.DATE, -1) // 往前回溯一天
    }

    return dates
}


fun loadKcalForDate(userId: String, db: FirebaseFirestore, date: String, onKcalReceived: (Int) -> Unit) {
    db.collection("trainingHistory").document(userId).collection("cards")
        .whereEqualTo("trainingDate", date)
        .get()
        .addOnSuccessListener { documents ->
            var totalKcal = 0
            for (document in documents) {
                val kcal = document.data["kcal"] as? Number
                totalKcal += kcal?.toInt() ?: 0
            }
            onKcalReceived(totalKcal)
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error fetching kcal for date $date: ", e)
            onKcalReceived(0) // 处理失败情况
        }
}
fun loadTrainingTimeForDate(userId: String, db: FirebaseFirestore, date: String, onKcalReceived: (Int) -> Unit) {
    db.collection("trainingHistory").document(userId).collection("cards")
        .whereEqualTo("trainingDate", date)
        .get()
        .addOnSuccessListener { documents ->
            var totalKcal = 0
            for (document in documents) {
                val kcal = document.data["trainingTime"] as? Number
                totalKcal += kcal?.toInt() ?: 0
            }
            onKcalReceived(totalKcal)
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error fetching kcal for date $date: ", e)
            onKcalReceived(0) // 处理失败情况
        }
}

@Composable
fun ReportScreen(navController: NavHostController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance()
    val dates = getLastSevenDays()
    val kcalData = remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    val context = LocalContext.current
    val averageKcal = remember { mutableStateOf(0) }
    val totalKcal = mutableListOf<Int>()
    val averageTime = remember { mutableStateOf(0) }
    val totalTime = mutableListOf<Int>()
    val trainingTimeData = remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    dates.forEach { date ->
        LaunchedEffect(date) {
            if (userId != null) {
                loadKcalForDate(userId, db, date) { kcal ->
                    totalKcal.add(kcal)
                    kcalData.value = kcalData.value.toMutableMap().apply { put(date, kcal) }
                    averageKcal.value = totalKcal.sum() / totalKcal.size
                }
            }
        }
    }
    dates.forEach { date ->
        LaunchedEffect(key1 = date) {
            if (userId != null) {
                loadTrainingTimeForDate(userId, db, date) { time ->
                    totalTime.add(time)
                    val currentData = trainingTimeData.value.toMutableMap()
                    currentData[date] = time
                    trainingTimeData.value = currentData
                    averageTime.value = totalTime.sum() / totalTime.size
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE4E4FC),
                        Color(0xFFFAE8E1)
                    )
                )
            )
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    ,


                ) {

                //top bar
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Weekly Report",
                        modifier = Modifier.weight(1f),
                        color = Color(0xFF151C57),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    //8E91B9
                    }
                    //Spacer(modifier = Modifier.padding(horizontal = 40.dp))//gap
                }
                Spacer(modifier = Modifier.height(40.dp))
                val text1 = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFFB0C4DE), fontSize = 15.sp, fontWeight = FontWeight.Bold)) {
                        append("       Average ")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFF151C57), fontSize = 20.sp, fontWeight = FontWeight.Bold)) { // 假设你想要突出显示平均值
                        append("${averageKcal.value} kcal")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFFB0C4DE), fontSize = 15.sp, fontWeight = FontWeight.Bold)) {
                        append(" per day")
                    }
                }
                //report 1 text
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text=  text1,
                                color = Color(0xFF151C57),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                            )

                            Spacer(modifier = Modifier.padding(horizontal = 50.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.export),
                                contentDescription = "Share",
                                tint = Color(0xFFA2A8BE)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp)) //gap

                        // report 1 card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp, vertical = 2.dp),

                            shape = RoundedCornerShape(15.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White  //
                            )
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()  // 确保图表充满水平空间
                                    .height(250.dp)
                                    .background(Color.Transparent)
                            ) {
                                KcalBarChart(kcalData = kcalData.value)
// Placeholder for future chart
                            }

                            }

                            Spacer(modifier = Modifier.height(22.dp))//gap
                        val text2 = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color(0xFFB0C4DE), fontSize = 15.sp, fontWeight = FontWeight.Bold)) {
                                append("       Average ")
                            }
                            withStyle(style = SpanStyle(color = Color(0xFF151C57), fontSize = 20.sp, fontWeight = FontWeight.Bold)) { // 假设你想要突出显示平均值
                                append("${averageTime.value} hours")
                            }
                            withStyle(style = SpanStyle(color = Color(0xFFB0C4DE), fontSize = 15.sp, fontWeight = FontWeight.Bold)) {
                                append(" per day")
                            }
                        }
                        //report 2 text
                        Row(

                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = text2,
                                color = Color(0xFF151C57),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.padding(horizontal = 50.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.export),
                                contentDescription = "Share",
                                tint = Color(0xFFA2A8BE)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))//gap

                        // report 2 card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp, vertical = 2.dp),

                            shape = RoundedCornerShape(15.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White  //
                            )
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()  // 确保图表充满水平空间
                                    .height(250.dp)
                                    .background(Color.Transparent)
                            ) {
                                TimeBarChart(trainingTimeData = trainingTimeData.value)
// Placeholder for future chart
                            }
                        }

                        // Placeholder for the other chart

                    }
                }
            }






@Composable
fun KcalBarChart(kcalData: Map<String, Int>) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(), // 确保视图使用所有可用的高度
        factory = { context ->
            BarChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,

                )
            }
        },
        update = { barChart ->
            setupBarChartData(barChart, kcalData)
        }
    )
}
@Composable
fun TimeBarChart(trainingTimeData: Map<String, Int>) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(), // 确保视图使用所有可用的高度
        factory = { context ->
            BarChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,

                    )
            }
        },
        update = { barChart ->
            setupTrainBarChartData(barChart, trainingTimeData)
        }
    )
}

private fun setupBarChartData(barChart: BarChart, kcalData: Map<String, Int>) {
    val entries = kcalData.keys.sorted().mapIndexed { index, date ->
        BarEntry(index.toFloat(), kcalData[date]?.toFloat() ?: 0f)
    }
    val dataSet = BarDataSet(entries, "Kcal Burned").apply{
        color = ColorTemplate.getHoloBlue()
        setColor(Color(0xFF6FCB76).toArgb())
        setDrawValues(false)
    }
    val barData = BarData(dataSet)
    barData.barWidth = 0.2f  // 正确设置柱子宽度

    // 应用这个包含宽度设置的 barData 到图表
    barChart.data = barData
    barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
        override fun onValueSelected(e: Entry?, h: Highlight?) {
            if (e is BarEntry) {
                val dataSetIndex = h?.dataSetIndex
                val dataSet = barChart.data.getDataSetByIndex(dataSetIndex ?: 0) as BarDataSet
                dataSet.setDrawValues(true)
                barChart.invalidate()
            }
        }

        override fun onNothingSelected() {
            barChart.data.dataSets.forEach { dataSet ->
                (dataSet as BarDataSet).setDrawValues(false)
            }
            barChart.invalidate()
        }
    })
    barChart.axisLeft.textColor = Color(0xFF8A2BE2).toArgb()
    barChart.axisLeft.textSize = 7f
    barChart.legend.isEnabled = false
    barChart.setExtraOffsets(10f, 0f, 10f, 20f) // 设置额外的偏移以减少边界
    barChart.axisLeft.axisMinimum = 0f
    barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
    barChart.xAxis.valueFormatter = IndexAxisValueFormatter(kcalData.keys.sorted())
    barChart.description.isEnabled = false
    barChart.animateY(500)
    barChart.invalidate()
    barChart.axisRight.isEnabled = false
    barChart.axisLeft.isEnabled = false
    barChart.xAxis.setDrawGridLines(false)
    barChart.axisLeft.setDrawGridLines(false)
    val indexToDateMap = kcalData.keys.sorted().mapIndexed { index, date -> index to date }.toMap()

    barChart.xAxis.apply {
        valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val dateString = indexToDateMap[value.toInt()] // 从映射中获取日期
                return dateString?.split("-")?.last() ?: ""
            }
        }
        textSize = 5f
        textColor = Color(0xFF151C57).toArgb()
        setDrawAxisLine(false)
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        setLabelCount(kcalData.size, false) // 确保所有标签都显示并且均匀分布
        //labelRotationAngle = -45f // 根据需要调整标签旋转角度以避免重叠
    }
}

private fun setupTrainBarChartData(barChart: BarChart, trainingTimeData: Map<String, Int>) {
    val entries = trainingTimeData.keys.sorted().mapIndexed { index, date ->
        BarEntry(index.toFloat(), trainingTimeData[date]?.toFloat() ?: 0f)
    }
    val dataSet = BarDataSet(entries, "Training time").apply{
        color = ColorTemplate.getHoloBlue()
        setColor(Color(0xFF6FCB76).toArgb())
        setDrawValues(false)
    }
    val barData = BarData(dataSet)
    barData.barWidth = 0.2f  // 正确设置柱子宽度

    // 应用这个包含宽度设置的 barData 到图表
    barChart.data = barData
    barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
        override fun onValueSelected(e: Entry?, h: Highlight?) {
            if (e is BarEntry) {
                val dataSetIndex = h?.dataSetIndex
                val dataSet = barChart.data.getDataSetByIndex(dataSetIndex ?: 0) as BarDataSet
                dataSet.setDrawValues(true)
                barChart.invalidate()
            }
        }

        override fun onNothingSelected() {
            barChart.data.dataSets.forEach { dataSet ->
                (dataSet as BarDataSet).setDrawValues(false)
            }
            barChart.invalidate()
        }
    })
    barChart.axisLeft.textColor = Color(0xFF8A2BE2).toArgb()
    barChart.axisLeft.textSize = 7f
    barChart.legend.isEnabled = false
    barChart.setExtraOffsets(10f, 0f, 10f, 20f) // 设置额外的偏移以减少边界
    barChart.axisLeft.axisMinimum = 0f
    barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
    barChart.xAxis.valueFormatter = IndexAxisValueFormatter(trainingTimeData.keys.sorted())
    barChart.description.isEnabled = false
    barChart.animateY(500)
    barChart.invalidate()
    barChart.axisRight.isEnabled = false
    barChart.axisLeft.isEnabled = false
    barChart.xAxis.setDrawGridLines(false)
    barChart.axisLeft.setDrawGridLines(false)
    val indexToDateMap = trainingTimeData.keys.sorted().mapIndexed { index, date -> index to date }.toMap()

    barChart.xAxis.apply {
        valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val dateString = indexToDateMap[value.toInt()] // 从映射中获取日期
                return dateString?.split("-")?.last() ?: ""
            }
        }
        textSize = 5f
        textColor = Color(0xFF151C57).toArgb()
        setDrawAxisLine(false)
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        setLabelCount(trainingTimeData.size, false) // 确保所有标签都显示并且均匀分布
        //labelRotationAngle = -45f // 根据需要调整标签旋转角度以避免重叠
    }
}
fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(context, { _, year, month, dayOfMonth ->
        val selectedDate = Calendar.getInstance().apply {
            set(year, month, dayOfMonth)
        }
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate.time)
        onDateSelected(formattedDate)
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
}


//test@gmail.com