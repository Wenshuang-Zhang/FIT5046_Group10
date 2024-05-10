package com.example.a1

import android.app.Application
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog

import java.time.format.DateTimeFormatter

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.room.Database
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow



//data class HistoryItem(val type: String, val kcal: Int, val trainingDate: String, val trainingTime: Int)
@Entity(tableName = "history_items")
data class HistoryItem @JvmOverloads constructor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val kcal: Int,
    val trainingDate: String,
    val trainingTime: Int
)

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryItem(item: HistoryItem)

    @Query("SELECT * FROM history_items")
    fun getAllHistoryItems(): Flow<List<HistoryItem>>
}

@Database(entities = [HistoryItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "history_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}


class HistoryRepository(private val historyDao: HistoryDao) {
    fun getAllHistoryItems(): Flow<List<HistoryItem>> {
        return historyDao.getAllHistoryItems()
    }

    suspend fun insertItem(item: HistoryItem) {
        historyDao.insertHistoryItem(item)
    }
}


class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: HistoryRepository
    private val _showDialog = mutableStateOf(false)
    val showDialog: State<Boolean> = _showDialog

    fun toggleDialog(show: Boolean) {
        _showDialog.value = show
    }

    init {
        val dao = AppDatabase.getDatabase(application).historyDao()
        repository = HistoryRepository(dao)
    }

    val allHistoryItems: LiveData<List<HistoryItem>> = repository.getAllHistoryItems().asLiveData()

    fun addHistoryItem(item: HistoryItem) {
        viewModelScope.launch {
            repository.insertItem(item)
        }
    }

    private val _historyItems = MutableLiveData<List<HistoryItem>>()
    val historyItems: LiveData<List<HistoryItem>> = _historyItems

    fun fetchHistoryItems() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("trainingHistory").document(uid).collection("cards")
            .get()
            .addOnSuccessListener { result ->
                val items = result.mapNotNull { doc ->
                    deserializeToHistoryItem(doc)
                }
                _historyItems.postValue(items)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }
    }
    private fun deserializeToHistoryItem(document: DocumentSnapshot): HistoryItem? {
        return if (document.exists()) {
            val type = document.getString("type") ?: "Unknown"
            val kcal = document.getLong("kcal")?.toInt() ?: 0
            val trainingDate = document.getString("trainingDate") ?: ""
            val trainingTime = document.getLong("trainingTime")?.toInt() ?: 0
            HistoryItem(0, type, kcal, trainingDate, trainingTime)
        } else null
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(navController: NavHostController, viewModel: HistoryViewModel) {
    //var showDialog by remember { mutableStateOf(false) }
    val historyItems = remember { mutableStateListOf<HistoryItem>() }

    val historyItems1 by viewModel.historyItems.observeAsState(emptyList())
    //val showDialog by viewModel.showDialog.observeAsState(false)
    val showDialog by viewModel.showDialog

    LaunchedEffect(key1 = true) {
        viewModel.fetchHistoryItems()
    }

    if (showDialog) {
        AddDialog(viewModel = viewModel, onDismiss = {
            viewModel.toggleDialog(false)
        })
    }




//    if (showDialog) {
//        AddDialog(historyItems) { showDialog = false }
//    }

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    val yyyyMMddFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val MMMyyyyFormatter = DateTimeFormatter.ofPattern("MMM yyyy")


    LaunchedEffect(key1 = uid) {
        if (uid != null) {
            db.collection("trainingHistory").document(uid).collection("cards").get().addOnSuccessListener { collection ->
                if (!collection.isEmpty) {
                    historyItems.clear()
                    for (document in collection.documents) {
                        val type = document.getString("type") ?: "Unknown"
                        val kcal = document.getLong("kcal")?.toInt() ?: 0
                        val trainingTime = document.getLong("trainingTime")?.toInt() ?: 0
                        val trainingDate =  document.getString("trainingDate") ?: "Unknown"

                        val newItem = HistoryItem(0, type, kcal, trainingDate, trainingTime)
                        historyItems.add(newItem)
                    }
                }
            }.addOnFailureListener { e ->
                Log.w("Firestorm", "Error fetching document", e)
            }
        }
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE4E4FC),
                            Color(0xFFFAE8E1),
                        )
                    )
                )
        ) {
            Text(
                text = "Activity",
                color = Color(0xFF151C57),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 0.dp)
            )

            Text(
                "Press the bottom icon to record your work out",
                fontSize = 16.sp,
                color = Color(0xFF8E91B9),
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 0.dp)
            )

            LazyColumn(contentPadding = PaddingValues(all = 16.dp)) {
                val groupedByMonth = historyItems1.groupBy {
                    //it.trainingDate.format(DateTimeFormatter.ofPattern("MMM yyyy"))
                    val date = LocalDate.parse(it.trainingDate, yyyyMMddFormatter)
                    date.format(MMMyyyyFormatter)

                }
                groupedByMonth.forEach { (month, itemsInMonth) ->
                    stickyHeader {
                        Text(text = month, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Gray)
                    }
                    items(itemsInMonth) { item ->
                        HistoryCard(item)
                    }
                }
            }
        }
        //FloatingActionButton
        FloatingActionButton(
            onClick = { viewModel.toggleDialog(true) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 15.dp, end = 16.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Add"
            )
        }
    }
}




@Composable
fun AddDialog(
    viewModel: HistoryViewModel,
    onDismiss: () -> Unit) {
    val trainingTypes = listOf("CYCLING", "HIIT", "YOGA", "TREADMILL", "PILATES")
    var selectedTrainingType by remember { mutableStateOf(trainingTypes[0]) }
    var time by remember { mutableStateOf("") }
    var trainingDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                var expanded by remember { mutableStateOf(false) }
                //val trainingTypes = listOf("CYCLING", "HIIT", "YOGA", "TREADMILL", "PILATES")

                Box(
                    modifier = Modifier
                        .background(Color(0xFF776EE3), shape = RoundedCornerShape(5.dp))
                        .height(28.dp)
                        .width(120.dp),
                    contentAlignment = Alignment.Center,
                ){
                    if (showDatePicker) {
                        DatePickerDialog(
                            date = trainingDate,
                            onDateSelected = { date ->
                                trainingDate = date
                                showDatePicker = false
                            },

                            onDismissRequest = { showDatePicker = false }
                        )
                    }

                    ClickableText(
                        text = AnnotatedString(trainingDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))),
                        onClick = { showDatePicker = true },
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                    )
                }



                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth()){
                    OutlinedTextField(
                        value = selectedTrainingType,
                        onValueChange = { selectedTrainingType = it },
                        label = { Text("Training Type") },
                        trailingIcon = {
                            Icon(Icons.Filled.ArrowDropDown, "Drop Down", Modifier.clickable { expanded = true })
                        },
                        readOnly = true
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        offset = DpOffset(x = 114.dp, y = 0.dp),
                        modifier = Modifier.align(Alignment.BottomStart)
                    ) {
                        trainingTypes.forEach { label ->
                            DropdownMenuItem(onClick = {
                                selectedTrainingType = label
                                expanded = false
                            }) {
                                Text(label)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = time,
                    onValueChange = { newValue ->
                        // convert the input to int
                        val num = newValue.toIntOrNull()
                        if (num != null && num in 0..1000) {
                            time = newValue
                        }
                        if (newValue.isEmpty()) {
                            time = ""
                        }
                    },
                    label = { Text("Training Time (Minutes)") },
                    modifier = Modifier.fillMaxWidth(),
                    //number keyboard
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                        if (selectedTrainingType.isNotEmpty() && time.isNotEmpty()) {
                        val trainingTime = time.toIntOrNull() ?: 0
                        val kcal = calculateKcal(selectedTrainingType, trainingTime)
                        val formattedDate = trainingDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        val newItem = HistoryItem(0, selectedTrainingType, kcal, formattedDate, trainingTime)
                        viewModel.addHistoryItem(newItem)
                        //historyItems.add(newItem)
                        uploadToFirebase(newItem)
                        onDismiss()
                        }
                    } ){
                        Text("Add", color = Color.White)
                    }
                }
            }
        }
    }
}

fun uploadToFirebase(newItem: HistoryItem) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    val db = FirebaseFirestore.getInstance()
    val data = hashMapOf(
        "type" to newItem.type,
        "kcal" to newItem.kcal,
        "trainingDate" to newItem.trainingDate,
        "trainingTime" to newItem.trainingTime
    )
    if (uid != null) {
        db.collection("trainingHistory").document(uid).collection("cards").add(data)
    }
}

@Composable
fun DatePickerDialog(
    date: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val currentYear = date.year
    val currentMonth = date.monthValue - 1 // Calendar.MONTH is zero-based in Java
    val currentDay = date.dayOfMonth

    // Remember a DatePickerDialog to avoid recreation on recompositions
    val datePickerDialog = remember {
        DatePickerDialog(context, { _: DatePicker, year: Int, month: Int, day: Int ->
            // Update the date when a new date is picked
            val newDate = LocalDate.of(year, month + 1, day)
            onDateSelected(newDate)
        }, currentYear, currentMonth, currentDay).apply {
            // Dismiss listener
            setOnDismissListener {
                onDismissRequest()
            }
            // Set the max date to the current date
            datePicker.maxDate = System.currentTimeMillis()
        }
    }

    // Handle dialog showing and updates
    LaunchedEffect(date) {
        datePickerDialog.updateDate(currentYear, currentMonth, currentDay)
        datePickerDialog.show()
    }
}


fun calculateKcal(trainingType: String, trainingTime: Int): Int {
    return when (trainingType) {
        "CYCLING" -> 12 * trainingTime
        "HIIT" -> 8 * trainingTime
        "YOGA" -> 3 * trainingTime
        "TREADMILL" -> 10 * trainingTime
        "PILATES" -> 6 * trainingTime
        else -> 0 //0
    }
}


@Composable
fun HistoryCard(item: HistoryItem) {
    val yyyyMMddFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val ddMMMyyyyFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    val kcal = calculateKcal(item.type, item.trainingTime)
    val formattedDate = LocalDate.parse(item.trainingDate, yyyyMMddFormatter).format(
        ddMMMyyyyFormatter)


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .heightIn(min = 50.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.type,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF7FB2EF)
                )
                Text(
                    text = formattedDate,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF7FB2EF)
                )
            }
            Text(
                text = "$kcal KCAL",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF151C57),
                fontSize = 20.sp
            )
            Text(
                text = "${item.trainingTime} Minutes",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF151C57),
                fontSize = 20.sp
            )
        }
    }
}
