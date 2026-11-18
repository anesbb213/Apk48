package com.algeria.worldguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// لوحة الألوان الفاخرة: الأزرق الداكن الملكي والذهبي
val RoyalDarkBlue = Color(0xFF0A192F)
val CardDarkBlue = Color(0xFF112240)
val AccentGold = Color(0xFFFFD700)
val SoftGold = Color(0xFFE6C200)
val TextLight = Color(0xFFF8F9FA)
val TextMuted = Color(0xFFA8B2D1)

data class Wilaya(
    val code: String,
    val name: String,
    val region: String,
    val dairaCount: Int,
    val communeCount: Int,
    val distanceFromAlgiers: Int,
    val description: String
)

data class Country(
    val name: String,
    val flag: String,
    val capital: String,
    val continent: String,
    val currency: String,
    val language: String,
    val info: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                DalilApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DalilApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedWilaya by remember { mutableStateOf<Wilaya?>(null) }
    var selectedCountry by remember { mutableStateOf<Country?>(null) }

    val wilayas = remember { getAll58Wilayas() }
    val countries = remember { getWorldCountries() }

    Scaffold(
        containerColor = RoyalDarkBlue,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "دليل الجزائر والعالم",
                        color = AccentGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = CardDarkBlue
                ),
                navigationIcon = {
                    if (selectedWilaya != null || selectedCountry != null) {
                        IconButton(onClick = {
                            selectedWilaya = null
                            selectedCountry = null
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = AccentGold
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (selectedWilaya == null && selectedCountry == null) {
                NavigationBar(
                    containerColor = CardDarkBlue,
                    contentColor = AccentGold
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0; searchQuery = "" },
                        icon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
                        label = { Text("ولايات الجزائر (58)", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RoyalDarkBlue,
                            selectedTextColor = AccentGold,
                            indicatorColor = AccentGold,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1; searchQuery = "" },
                        icon = { Icon(Icons.Default.Public, contentDescription = null) },
                        label = { Text("دول العالم", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RoyalDarkBlue,
                            selectedTextColor = AccentGold,
                            indicatorColor = AccentGold,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2; searchQuery = "" },
                        icon = { Icon(Icons.Default.HistoryEdu, contentDescription = null) },
                        label = { Text("تاريخ ورؤساء", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RoyalDarkBlue,
                            selectedTextColor = AccentGold,
                            indicatorColor = AccentGold,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(RoyalDarkBlue, Color(0xFF061020))
                    )
                )
        ) {
            when {
                selectedWilaya != null -> {
                    WilayaDetailScreen(wilaya = selectedWilaya!!)
                }
                selectedCountry != null -> {
                    CountryDetailScreen(country = selectedCountry!!)
                }
                selectedTab == 0 -> {
                    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                        SearchBarField(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            placeholder = "ابحث باسم الولاية أو الرقم (مثلاً: وهران أو 31)..."
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        val filtered = wilayas.filter {
                            it.name.contains(searchQuery, ignoreCase = true) ||
                                    it.code.contains(searchQuery)
                        }
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(filtered) { wilaya ->
                                WilayaCard(wilaya = wilaya, onClick = { selectedWilaya = wilaya })
                            }
                        }
                    }
                }
                selectedTab == 1 -> {
                    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                        SearchBarField(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            placeholder = "ابحث عن دولة أو عاصمة..."
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        val filtered = countries.filter {
                            it.name.contains(searchQuery, ignoreCase = true) ||
                                    it.capital.contains(searchQuery, ignoreCase = true)
                        }
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(filtered) { country ->
                                CountryCard(country = country, onClick = { selectedCountry = country })
                            }
                        }
                    }
                }
                selectedTab == 2 -> {
                    AlgeriaHistoryScreen()
                }
            }
        }
    }
}

@Composable
fun SearchBarField(query: String, onQueryChange: (String) -> Unit, placeholder: String) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = TextMuted, fontSize = 15.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AccentGold) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = null, tint = AccentGold)
                }
            }
        },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentGold,
            unfocusedBorderColor = SoftGold.copy(alpha = 0.5f),
            focusedTextColor = TextLight,
            unfocusedTextColor = TextLight,
            cursorColor = AccentGold,
            focusedContainerColor = CardDarkBlue,
            unfocusedContainerColor = CardDarkBlue
        ),
        singleLine = true
    )
}

@Composable
fun WilayaCard(wilaya: Wilaya, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardDarkBlue),
        border = BorderStroke(1.dp, SoftGold.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(AccentGold, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = wilaya.code,
                        color = RoyalDarkBlue,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = wilaya.name,
                        color = TextLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )
                    Text(
                        text = "${wilaya.region} • ${wilaya.dairaCount} دوائر • ${wilaya.communeCount} بلدية",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
            Text(
                text = "${wilaya.distanceFromAlgiers} كم",
                color = AccentGold,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun WilayaDetailScreen(wilaya: Wilaya) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDarkBlue),
                border = BorderStroke(1.5.dp, AccentGold),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ولاية رقم ${wilaya.code}",
                        color = AccentGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = wilaya.name,
                        color = TextLight,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = wilaya.region,
                        color = TextMuted,
                        fontSize = 16.sp
                    )
                }
            }
        }
        item {
            InfoRowCard(title = "عدد الدوائر الإدارية", value = "${wilaya.dairaCount} دائرة")
        }
        item {
            InfoRowCard(title = "عدد البلديات", value = "${wilaya.communeCount} بلدية")
        }
        item {
            InfoRowCard(
                title = "المسافة عن الجزائر العاصمة",
                value = if (wilaya.distanceFromAlgiers == 0) "العاصمة (0 كم)" else "${wilaya.distanceFromAlgiers} كيلومتر"
            )
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDarkBlue),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "نبذة تعريفية وتاريخية",
                        color = AccentGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = wilaya.description,
                        color = TextLight,
                        fontSize = 16.sp,
                        lineHeight = 26.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CountryCard(country: Country, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardDarkBlue),
        border = BorderStroke(1.dp, SoftGold.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = country.flag, fontSize = 34.sp)
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = country.name,
                        color = TextLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )
                    Text(
                        text = "العاصمة: ${country.capital} • ${country.continent}",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }
            }
            Text(
                text = country.currency,
                color = AccentGold,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun CountryDetailScreen(country: Country) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDarkBlue),
                border = BorderStroke(1.5.dp, AccentGold),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = country.flag, fontSize = 54.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = country.name,
                        color = AccentGold,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(text = country.continent, color = TextMuted, fontSize = 16.sp)
                }
            }
        }
        item { InfoRowCard(title = "العاصمة", value = country.capital) }
        item { InfoRowCard(title = "العملة الرسمية", value = country.currency) }
        item { InfoRowCard(title = "اللغة الرسمية", value = country.language) }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDarkBlue),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "معلومات عامة",
                        color = AccentGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = country.info,
                        color = TextLight,
                        fontSize = 16.sp,
                        lineHeight = 26.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AlgeriaHistoryScreen() {
    val presidents = listOf(
        Pair("أحمد بن بلة (1963 - 1965)", "أول رئيس للجمهورية الجزائرية الديمقراطية الشعبية بعد الاستقلال."),
        Pair("هواري بومدين (1965 - 1978)", "قائد مجلس الثورة ورئيس الجمهورية، عُرف ببناء الدولة الحديثة والتأميمات الكبرى."),
        Pair("الشاذلي بن جديد (1979 - 1992)", "شهدت فترته الانفتاح السياسي والتعددية الحزبية ودستور 1989."),
        Pair("محمد بوضياف (1992)", "أحد مفجري الثورة التحريرية ورئيس المجلس الأعلى للدولة."),
        Pair("علي كافي (1992 - 1994)", "مجاهد ورئيس المجلس الأعلى للدولة."),
        Pair("اليمين زروال (1994 - 1999)", "أول رئيس منتخب في ظل التعددية عام 1995."),
        Pair("عبد العزيز بوتفليقة (1999 - 2019)", "تولى الرئاسة لأربع عهدات وشهدت فترته الوئام المدني والمصالحة الوطنية."),
        Pair("عبد المجيد تبون (2019 - إلى اليوم)", "رئيس الجمهورية الجزائرية المنتخب، أطلق مشاريع إصلاحية وتنموية شاملة.")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "رؤساء الجزائر منذ الاستقلال",
                color = AccentGold,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
        items(presidents) { (name, desc) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDarkBlue),
                border = BorderStroke(1.dp, SoftGold.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = name,
                        color = AccentGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = desc,
                        color = TextLight,
                        fontSize = 15.sp,
                        lineHeight = 23.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRowCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardDarkBlue),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, color = TextMuted, fontSize = 15.sp)
            Text(text = value, color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

fun getAll58Wilayas(): List<Wilaya> = listOf(
    Wilaya("01", "أدرار", "الجنوب الغربي", 6, 16, 1400, "جوهرة توات وعاصمة الجنوب الغربي، تشتهر بالقصور الصحراوية ونظام الفقارة العريق."),
    Wilaya("02", "الشلف", "الوسط الغربي", 13, 35, 200, "تقع في سهل الشلف الخصب، وتتميز بشريطها الساحلي الجميل وزراعة الحمضيات."),
    Wilaya("03", "الأغواط", "السهوب", 10, 24, 400, "بوابة الصحراء، تشتهر ببساتين النخيل وتاريخها الثقافي والفني الأصيل."),
    Wilaya("04", "أم البواقي", "الشرق", 12, 29, 500, "تقع في قلب الأوراس، وتتميز بطابعها الفلاحي وتاريخها الثوري العريق."),
    Wilaya("05", "باتنة", "الأوراس", 21, 61, 430, "عاصمة الأوراس، تضم آثار تيمقاد الرومانية وجبال الشليا ومعقل الثورة التحريرية."),
    Wilaya("06", "بجاية", "القبائل", 19, 52, 250, "عاصمة الحماديين، تجمع بين البحر والجبل وتضم قمة قورايا وشلالات كفريدة."),
    Wilaya("07", "بسكرة", "الزيبان", 10, 27, 400, "عروس الزيبان، تشتهر بإنتاج أجود أنواع التمور (دقلة نور) والحمامات المعدنية."),
    Wilaya("08", "بشار", "الساورة", 6, 11, 1150, "عاصمة الساورة، بوابة الصحراء الكبرى وتتميز بكثبان تاغيت الذهبية."),
    Wilaya("09", "البليدة", "الوسط", 10, 25, 50, "مدينة الورود، تقع عند سفح جبال الشريعة وتشتهر ببساتين البرتقال."),
    Wilaya("10", "البويرة", "الوسط", 12, 45, 120, "تقع عند سفح جبال جرجرة الشامخة، وتعد همزة وصل بين الوسط والشرق."),
    Wilaya("11", "تمنراست", "الهقار", 3, 5, 1950, "عاصمة الهقار والأهقار، تضم جبال الأسكرام ذات أجمل شروق وغروب شمس في العالم."),
    Wilaya("12", "تبسة", "الشرق", 12, 28, 630, "مدينة التاريخ العريق على الحدود الشرقية، تشتهر بمعالمها الرومانية والبيزنطية."),
    Wilaya("13", "تلمسان", "الغرب", 20, 53, 540, "لؤلؤة المغرب العربي وعاصمة الزيانيين، غنية بالآثار الإسلامية والطبيعة الخلابة."),
    Wilaya("14", "تيارت", "الهضاب العليا", 14, 42, 340, "عاصمة الرستميين قديماً، تشتهر بتربية الخيول العربية الأصيلة والسهول الفلاحية."),
    Wilaya("15", "تيزي وزو", "القبائل", 21, 67, 100, "قلب منطقة القبائل، تشتهر بصناعة الفخار والحلي الفضية وجبال جرجرة."),
    Wilaya("16", "الجزائر", "الوسط", 13, 57, 0, "البهجة وعاصمة البلاد السياسية والاقتصادية، تضم القصبة العتيقة ومقام الشهيد."),
    Wilaya("17", "الجلفة", "السهوب", 12, 36, 300, "عاصمة السهوب الجزائرية، تشتهر بكرم الضيافة وتربية المواشي والنقوش الصخرية."),
    Wilaya("18", "جيجل", "الشرق", 11, 28, 360, "جوهرة الساحل الشرقي، تشتهر بكهوفها العجيبة وشواطئها الفيروزية وغاباتها الكثيفة."),
    Wilaya("19", "سطيف", "الشرق", 20, 60, 300, "عاصمة الهضاب العليا، تشتهر بعين الفوارة وآثار جميلة الرومانية وحيويتها التجارية."),
    Wilaya("20", "سعيدة", "الغرب", 6, 16, 430, "مدينة المياه والينابيع المعدنية، وتتميز بطبيعتها الجبلية والسهبية."),
    Wilaya("21", "سكيكدة", "الشرق", 13, 38, 470, "روسيكادا التاريخية، مدينة ساحلية سياحية وصناعية كبرى في الشرق الجزائري."),
    Wilaya("22", "سيدي بلعباس", "الغرب", 15, 52, 440, "مدينة الفنون والسهول الخصبة، تعد قطباً فلاحياً وصناعياً مهماً في الغرب."),
    Wilaya("23", "عنابة", "الشرق", 6, 12, 600, "بونة الجميلة ومدينة العناب، تشتهر بشواطئ سرايدي وآثار هيبون."),
    Wilaya("24", "قالمة", "الشرق", 10, 34, 530, "مدينة الحمامات المعدنية كحمام دباغ والمسرح الروماني العريق."),
    Wilaya("25", "قسنطينة", "الشرق", 6, 12, 430, "مدينة الجسور المعلقة وعاصمة الشرق الجزائري، من أعرق مدن العالم."),
    Wilaya("26", "المدية", "الوسط", 19, 64, 90, "عاصمة التيطري التاريخية، تشتهر بمناخها المعتدل وبساتين الفواكه."),
    Wilaya("27", "مستغانم", "الغرب", 10, 32, 355, "لؤلؤة المتوسط في الغرب الجزائري، تشتهر بشواطئها الذهبية والمسرح والفنون."),
    Wilaya("28", "المسيلة", "الحضنة", 15, 47, 240, "عاصمة الحضنة، تضم قلعة بني حماد التاريخية المصنفة تراثاً عالمياً."),
    Wilaya("29", "معسكر", "الغرب", 16, 47, 360, "عاصمة الأمير عبد القادر ومهد المقاومة الشعبية، تشتهر بسهول غريس."),
    Wilaya("30", "ورقلة", "الجنوب الشرقي", 6, 10, 800, "عاصمة الواحات والذهب الأسود، تشتهر بمتحفها الصحراوي وقصورها العتيقة."),
    Wilaya("31", "وهران", "الغرب", 9, 26, 430, "الباهية وعاصمة الغرب الجزائري، تشتهر بقلعة سانتا كروز وواجهة البحر الساحرة."),
    Wilaya("32", "البيض", "الجنوب الغربي", 8, 22, 590, "تقع في الأطلس الصحراوي، وتشتهر بمناظرها الجبلية وتراثها البدوي الأصيل."),
    Wilaya("33", "إليزي", "الطاسيلي", 2, 4, 1750, "موطن حظيرة الطاسيلي ناجر أكبر متحف مفتوح للرسوم الصخرية في العالم."),
    Wilaya("34", "برج بوعريريج", "الشرق", 10, 34, 220, "عاصمة الإلكترونيك في الجزائر، وتتميز بموقعها الاستراتيجي في الهضاب."),
    Wilaya("35", "بومرداس", "الوسط", 9, 32, 50, "ولاية ساحلية جميلة شرق العاصمة، تشتهر بشواطئها وجامعاتها العلمية."),
    Wilaya("36", "الطارف", "الشرق", 7, 24, 680, "جوهرة الشرق الخضراء، تضم محمية القالة الطبيعية وبحيراتها النادرة."),
    Wilaya("37", "تندوف", "الجنوب الغربي", 1, 2, 1800, "أقصى الجنوب الغربي، بوابة الجزائر نحو موريتانيا وغرب إفريقيا."),
    Wilaya("38", "تيسمسيلت", "الونشريس", 8, 22, 260, "تضم الحظيرة الوطنية للأرز بثنية الحد وتتميز بغاباتها الكثيفة."),
    Wilaya("39", "الوادي", "وادي سوف", 10, 22, 630, "مدينة الألف قبة وقبة، تشتهر بجمال كثبانها الرملية وزراعتها الصحراوية."),
    Wilaya("40", "خنشلة", "الأوراس", 8, 21, 540, "عاصمة الشاوية والأوراس الشرقي، موطن الملكة ديهيا وغابات الأرز."),
    Wilaya("41", "سوق أهراس", "الشرق", 10, 26, 640, "طاغاست التاريخية، تشتهر بغاباتها الكثيفة وآثار مادور وخميسة."),
    Wilaya("42", "تيبازة", "الوسط", 10, 28, 70, "مدينة الآثار الرومانية المطلة على البحر الأبيض المتوسط وقبر الرومية."),
    Wilaya("43", "ميلة", "الشرق", 13, 32, 390, "تضم أقدم مسجد في الجزائر (مسجد سيدي غانم) وسد بني هارون العملاق."),
    Wilaya("44", "عين الدفلى", "الوسط", 14, 36, 145, "تقع في حوض الشلف الخصب، وتشتهر بإنتاجها الزراعي المتنوع."),
    Wilaya("45", "النعامة", "الجنوب الغربي", 7, 12, 680, "تشتهر بواحات تيوت والمغرار وبكثبان عين الصفراء الذهبية."),
    Wilaya("46", "عين تموشنت", "الغرب", 8, 28, 500, "ولاية ساحلية وسياحية بامتياز في الغرب الجزائري، تشتهر بشواطئ رشقون."),
    Wilaya("47", "غرداية", "وادي ميزاب", 9, 10, 600, "عاصمة وادي ميزاب، تشتهر بقصورها السبعة وهندستها المعمارية الفريدة."),
    Wilaya("48", "غليزان", "الغرب", 13, 38, 300, "تقع في سهل مينا الخصب، وتعد همزة وصل حيوية بين الوسط والغرب."),
    Wilaya("49", "تيميمون", "الجنوب", 4, 10, 1200, "الواحة الحمراء، تشتهر بقصورها الطينية الحمراء ومهرجان أهليل التراثي."),
    Wilaya("50", "برج باجي مختار", "أقصى الجنوب", 1, 2, 2200, "بوابة الجزائر الكبرى نحو عمق القارة الإفريقية ودولة مالي."),
    Wilaya("51", "أولاد جلال", "الزيبان", 2, 6, 440, "تشتهر بسلالة أغنام أولاد جلال الأصيلة وبساتين النخيل."),
    Wilaya("52", "بني عباس", "الساورة", 6, 10, 1250, "واحة الساورة الساحرة، تشتهر بقصرها العتيق وسط النخيل والكثبان."),
    Wilaya("53", "عين صالح", "تيديكلت", 2, 3, 1280, "تقع في قلب الصحراء الجزائرية، وتعد نقطة التقاء قوافل الجنوب."),
    Wilaya("54", "عين قزام", "أقصى الجنوب", 1, 2, 2350, "أقصى نقطة في الجنوب الجزائري على الحدود مع النيجر."),
    Wilaya("55", "تقرت", "وادي ريغ", 4, 11, 660, "عاصمة وادي ريغ، تشتهر بواحات النخيل الكثيفة وتاريخ سلطنة تقرت."),
    Wilaya("56", "جانت", "الطاسيلي", 1, 2, 2150, "عروس الطاسيلي، تشتهر بتكويناتها الصخرية الفريدة ومهرجان السبيبة."),
    Wilaya("57", "المغير", "وادي ريغ", 2, 8, 530, "تشتهر بإنتاج التمور وشط ملغيغ أخفض نقطة في الجزائر."),
    Wilaya("58", "المنيعة", "الجنوب", 2, 3, 870, "لؤلؤة الواحات، تشتهر بقصرها العتيق وبحيراتها المائية العذبة وسط الصحراء.")
)

fun getWorldCountries(): List<Country> = listOf(
    Country("الجزائر", "🇩🇿", "الجزائر", "إفريقيا", "الدينار الجزائري (DZD)", "العربية والأمازيغية", "أكبر دولة في إفريقيا والعالم العربي مساحةً، تتميز بتنوع تضاريسها من الساحل إلى الصحراء الكبرى."),
    Country("المملكة العربية السعودية", "🇸🇦", "الرياض", "آسيا", "الريال السعودي (SAR)", "العربية", "مهد الإسلام وتضم الحرمين الشريفين في مكة المكرمة والمدينة المنورة."),
    Country("مصر", "🇪🇬", "القاهرة", "إفريقيا", "الجنيه المصري (EGP)", "العربية", "أرض الحضارة الفرعونية العريقة ونهر النيل وقناة السويس."),
    Country("فلسطين", "🇵🇸", "القدس", "آسيا", "العملات المتداولة", "العربية", "أرض الأنبياء وتضم المسجد الأقصى المبارك أولى القبلتين وثالث الحرمين الشريفين."),
    Country("تونس", "🇹🇳", "تونس", "إفريقيا", "الدينار التونسي (TND)", "العربية", "تقع في شمال إفريقيا وتشتهر بآثار قرطاج ومدينة القيروان التاريخية."),
    Country("المغرب", "🇲🇦", "الرباط", "إفريقيا", "الدرهم المغربي (MAD)", "العربية والأمازيغية", "يقع في أقصى شمال غرب إفريقيا ويطل على المحيط الأطلسي والبحر المتوسط."),
    Country("موريتانيا", "🇲🇷", "نواكشوط", "إفريقيا", "الأوقية الموريتانية (MRU)", "العربية", "بلد المليون شاعر، يجمع بين الثقافة العربية والإفريقية وتشتهر بمدينة شنقيط."),
    Country("الإمارات العربية المتحدة", "🇦🇪", "أبوظبي", "آسيا", "الدرهم الإماراتي (AED)", "العربية", "اتحاد يضم سبع إمارات وتتميز بنهضتها العمرانية والتقنية الحديثة."),
    Country("قطر", "🇶🇦", "الدوحة", "آسيا", "الريال القطري (QAR)", "العربية", "تقع في شبه الجزيرة العربية وتشتهر بكونها مركزاً إعلامياً ورياضياً عالمياً."),
    Country("تركيا", "🇹🇷", "أنقرة", "آسيا / أوروبا", "الليرة التركية (TRY)", "التركية", "تربط بين قارتي آسيا وأوروبا وتتميز بتاريخها العثماني العريق."),
    Country("فرنسا", "🇫🇷", "باريس", "أوروبا", "اليورو (EUR)", "الفرنسية", "تقع في أوروبا الغربية وتشتهر ببرج إيفل ومتحف اللوفر."),
    Country("إسبانيا", "🇪🇸", "مدريد", "أوروبا", "اليورو (EUR)", "الإسبانية", "تقع في شبه الجزيرة الإيبيرية وتضم معالم الأندلس التاريخية الخالدة."),
    Country("إيطاليا", "🇮🇹", "روما", "أوروبا", "اليورو (EUR)", "الإيطالية", "مهد الإمبراطورية الرومانية وعصر النهضة الأوروبية."),
    Country("ألمانيا", "🇩🇪", "برلين", "أوروبا", "اليورو (EUR)", "الألمانية", "أكبر اقتصاد في أوروبا وتشتهر بصناعاتها الهندسية المتطورة."),
    Country("المملكة المتحدة", "🇬🇧", "لندن", "أوروبا", "الجنيه الإسترليني (GBP)", "الإنجليزية", "تتكون من إنجلترا واسكتلندا وويلز وأيرلندا الشمالية."),
    Country("الولايات المتحدة الأمريكية", "🇺🇸", "واشنطن", "أمريكا الشمالية", "الدولار الأمريكي (USD)", "الإنجليزية", "تتكون من 50 ولاية وتعد من أكبر دول العالم مساحة واقتصاداً."),
    Country("كندا", "🇨🇦", "أوتاوا", "أمريكا الشمالية", "الدولار الكندي (CAD)", "الإنجليزية والفرنسية", "ثاني أكبر دولة في العالم من حيث المساحة وتشتهر بطبيعتها وبحيراتها."),
    Country("اليابان", "🇯🇵", "طوكيو", "آسيا", "الين الياباني (JPY)", "اليابانية", "كوكب التكنولوجيا والابتكار وثقافة الساموراي العريقة."),
    Country("الصين", "🇨🇳", "بكين", "آسيا", "اليوان الصيني (CNY)", "الصينية", "موطن سور الصين العظيم وأحد أقدم الحضارات وأكبر القوى الصناعية."),
    Country("البرازيل", "🇧🇷", "برازيليا", "أمريكا الجنوبية", "الريال البرازيلي (BRL)", "البرتغالية", "أكبر دولة في أمريكا الجنوبية وتضم غابات الأمازون المطيرة.")
)
