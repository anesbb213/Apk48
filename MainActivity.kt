package com.mousoua.algeriaworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

private val Navy = Color(0xFF073B78)
private val Blue = Color(0xFF087FC5)
private val Gold = Color(0xFFFFD56A)
private val Cream = Color(0xFFF4F7FC)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.layoutDirection = android.view.View.LAYOUT_DIRECTION_RTL
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Navy, secondary = Gold, background = Cream)) {
                EncyclopediaApp()
            }
        }
    }
}

data class Entry(val name: String, val detail: String, val extra: String = "")
data class Section(val title: String, val subtitle: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val entries: List<Entry>)

private val wilayaNames = listOf(
    "أدرار","الشلف","الأغواط","أم البواقي","باتنة","بجاية","بسكرة","بشار","البليدة","البويرة",
    "تمنراست","تبسة","تلمسان","تيارت","تيزي وزو","الجزائر","الجلفة","جيجل","سطيف","سعيدة",
    "سكيكدة","سيدي بلعباس","عنابة","قالمة","قسنطينة","المدية","مستغانم","المسيلة","معسكر","ورقلة",
    "وهران","البيض","إليزي","برج بوعريريج","بومرداس","الطارف","تندوف","تيسمسيلت","الوادي","خنشلة",
    "سوق أهراس","تيبازة","ميلة","عين الدفلى","النعامة","عين تموشنت","غرداية","غليزان",
    "تيميمون","برج باجي مختار","أولاد جلال","بني عباس","عين صالح","عين قزام","تقرت","جانت","المغير","المنيعة"
)

private val wilayaNotes = mapOf(
    "أدرار" to "ولاية صحراوية في الجنوب الغربي، تشتهر بالقصور والواحات ونظام الفقارات.",
    "الشلف" to "ولاية في الشمال الغربي، يمر بها وادي الشلف وتضم مناطق زراعية وساحلية.",
    "الأغواط" to "ولاية من الهضاب العليا وبوابة نحو الصحراء.",
    "أم البواقي" to "ولاية تقع في الشرق الجزائري ضمن منطقة الهضاب العليا.",
    "باتنة" to "ولاية الأوراس، وتجاورها مواقع أثرية مهمة مثل تيمقاد.",
    "بجاية" to "ولاية ساحلية في منطقة القبائل، تضم جبال جرجرة وميناء بجاية.",
    "بسكرة" to "تعرف ببوابة الصحراء وبواحات النخيل وإنتاج التمور.",
    "بشار" to "ولاية في الجنوب الغربي تضم بشار وبني عباس ومناطق صحراوية.",
    "البليدة" to "ولاية قريبة من العاصمة وتشتهر بالبساتين وسهل متيجة.",
    "البويرة" to "ولاية جبلية تضم مناطق من جبال جرجرة.",
    "تمنراست" to "ولاية في أقصى الجنوب، وتضم جبال الهقار.",
    "تبسة" to "ولاية شرقية حدودية ذات تاريخ ومواقع أثرية.",
    "تلمسان" to "ولاية تاريخية في الغرب، عرفت بمكانتها العلمية والفنية.",
    "تيارت" to "ولاية من الهضاب الغربية.",
    "تيزي وزو" to "ولاية في منطقة القبائل الكبرى.",
    "الجزائر" to "عاصمة الجمهورية الجزائرية ومركزها السياسي والإداري.",
    "الجلفة" to "ولاية واسعة في وسط البلاد ضمن الهضاب العليا.",
    "جيجل" to "ولاية ساحلية شرقية تتميز بالغابات والخلجان.",
    "سطيف" to "ولاية شرقية مهمة تجارياً وجامعياً، وبها موقع جميلة الأثري.",
    "سعيدة" to "ولاية داخلية في الغرب الجزائري.",
    "سكيكدة" to "ولاية ساحلية شرقية تضم ميناءً ومناطق سياحية.",
    "سيدي بلعباس" to "ولاية في الغرب الجزائري.",
    "عنابة" to "مدينة ساحلية شرقية ومركز صناعي وجامعي.",
    "قالمة" to "ولاية شرقية تشتهر بالحمامات المعدنية والمواقع التاريخية.",
    "قسنطينة" to "مدينة الجسور المعلقة وعاصمة ثقافية في الشرق.",
    "المدية" to "ولاية داخلية جنوب العاصمة ضمن الأطلس التلي.",
    "مستغانم" to "ولاية ساحلية في الغرب معروفة بشواطئها وتراثها.",
    "المسيلة" to "ولاية من الهضاب العليا تضم منطقة بوسعادة.",
    "معسكر" to "ولاية في الغرب ارتبط تاريخها بالأمير عبد القادر.",
    "ورقلة" to "ولاية صحراوية مهمة في الجنوب الشرقي.",
    "وهران" to "مدينة ساحلية كبرى في الغرب ومركز ثقافي واقتصادي.",
    "البيض" to "ولاية من الهضاب العليا الغربية.",
    "إليزي" to "ولاية صحراوية شاسعة في الجنوب الشرقي.",
    "برج بوعريريج" to "ولاية في الهضاب العليا الشرقية.",
    "بومرداس" to "ولاية ساحلية شرق العاصمة.",
    "الطارف" to "ولاية حدودية ساحلية في أقصى الشرق.",
    "تندوف" to "ولاية صحراوية في أقصى الجنوب الغربي.",
    "تيسمسيلت" to "ولاية داخلية في الشمال الغربي.",
    "الوادي" to "ولاية واحات في الجنوب الشرقي تشتهر بالهندسة الصحراوية.",
    "خنشلة" to "ولاية في منطقة الأوراس.",
    "سوق أهراس" to "ولاية شرقية حدودية، وترتبط بتاريخ نوميديا والقديس أوغسطين.",
    "تيبازة" to "ولاية ساحلية تضم مواقع أثرية رومانية مدرجة عالمياً.",
    "ميلة" to "ولاية في الشرق الجزائري ذات تاريخ عريق.",
    "عين الدفلى" to "ولاية زراعية في الشمال الغربي.",
    "النعامة" to "ولاية في الهضاب العليا الغربية.",
    "عين تموشنت" to "ولاية ساحلية في الغرب.",
    "غرداية" to "ولاية وادي مزاب ذات تراث معماري وثقافي مميز.",
    "غليزان" to "ولاية في الشمال الغربي.",
    "تيميمون" to "ولاية صحراوية تشتهر بالقصور والواحات الحمراء.",
    "برج باجي مختار" to "ولاية حدودية في أقصى الجنوب الغربي.",
    "أولاد جلال" to "ولاية في منطقة الزيبان جنوب غرب بسكرة.",
    "بني عباس" to "ولاية صحراوية في الجنوب الغربي.",
    "عين صالح" to "ولاية صحراوية في وسط الجنوب.",
    "عين قزام" to "ولاية حدودية في أقصى الجنوب.",
    "تقرت" to "ولاية واحات في وادي ريغ.",
    "جانت" to "ولاية صحراوية تضم مناظر طاسيلي ناجر.",
    "المغير" to "ولاية في منطقة وادي ريغ.",
    "المنيعة" to "ولاية صحراوية تضم واحات وقصوراً تاريخية."
)

private val presidents = listOf(
    Entry("أحمد بن بلة", "رئيس الجمهورية من 1963 إلى 1965."),
    Entry("هواري بومدين", "رئيس مجلس الثورة ثم رئيس الجمهورية من 1965 إلى 1978."),
    Entry("الشاذلي بن جديد", "رئيس الجمهورية من 1979 إلى 1992."),
    Entry("محمد بوضياف", "رئيس المجلس الأعلى للدولة سنة 1992."),
    Entry("علي كافي", "رئيس المجلس الأعلى للدولة من 1992 إلى 1994."),
    Entry("اليمين زروال", "رئيس الدولة ثم رئيس الجمهورية من 1994 إلى 1999."),
    Entry("عبد العزيز بوتفليقة", "رئيس الجمهورية من 1999 إلى 2019."),
    Entry("عبد القادر بن صالح", "رئيس الدولة بالنيابة سنة 2019."),
    Entry("عبد المجيد تبون", "رئيس الجمهورية منذ ديسمبر 2019.")
)

private val countries = listOf(
    Entry("الجزائر", "العاصمة: الجزائر", "العملة: الدينار الجزائري • اللغة الرسمية: العربية والأمازيغية"),
    Entry("تونس", "العاصمة: تونس", "العملة: الدينار التونسي • اللغة: العربية"),
    Entry("المغرب", "العاصمة: الرباط", "العملة: الدرهم المغربي • اللغات الرسمية: العربية والأمازيغية"),
    Entry("ليبيا", "العاصمة: طرابلس", "العملة: الدينار الليبي • اللغة: العربية"),
    Entry("مصر", "العاصمة: القاهرة", "العملة: الجنيه المصري • اللغة: العربية"),
    Entry("موريتانيا", "العاصمة: نواكشوط", "العملة: الأوقية • اللغة الرسمية: العربية"),
    Entry("فرنسا", "العاصمة: باريس", "العملة: اليورو • اللغة: الفرنسية"),
    Entry("إسبانيا", "العاصمة: مدريد", "العملة: اليورو • اللغة: الإسبانية"),
    Entry("إيطاليا", "العاصمة: روما", "العملة: اليورو • اللغة: الإيطالية"),
    Entry("ألمانيا", "العاصمة: برلين", "العملة: اليورو • اللغة: الألمانية"),
    Entry("المملكة المتحدة", "العاصمة: لندن", "العملة: الجنيه الإسترليني • اللغة: الإنجليزية"),
    Entry("الولايات المتحدة", "العاصمة: واشنطن", "العملة: الدولار الأمريكي • اللغة الأكثر استعمالاً: الإنجليزية"),
    Entry("كندا", "العاصمة: أوتاوا", "العملة: الدولار الكندي • اللغتان الرسميتان: الإنجليزية والفرنسية"),
    Entry("البرازيل", "العاصمة: برازيليا", "العملة: الريال البرازيلي • اللغة: البرتغالية"),
    Entry("الصين", "العاصمة: بكين", "العملة: الرنمينبي • اللغة الرسمية: الصينية المندرينية"),
    Entry("اليابان", "العاصمة: طوكيو", "العملة: الين الياباني • اللغة: اليابانية"),
    Entry("الهند", "العاصمة: نيودلهي", "العملة: الروبية الهندية • لغات اتحادية: الهندية والإنجليزية"),
    Entry("تركيا", "العاصمة: أنقرة", "العملة: الليرة التركية • اللغة: التركية"),
    Entry("السعودية", "العاصمة: الرياض", "العملة: الريال السعودي • اللغة: العربية"),
    Entry("الإمارات", "العاصمة: أبوظبي", "العملة: الدرهم الإماراتي • اللغة: العربية"),
    Entry("قطر", "العاصمة: الدوحة", "العملة: الريال القطري • اللغة: العربية"),
    Entry("الأردن", "العاصمة: عمّان", "العملة: الدينار الأردني • اللغة: العربية"),
    Entry("العراق", "العاصمة: بغداد", "العملة: الدينار العراقي • اللغات الرسمية: العربية والكردية"),
    Entry("السنغال", "العاصمة: داكار", "العملة: فرنك غرب أفريقيا • اللغة الرسمية: الفرنسية"),
    Entry("جنوب أفريقيا", "العاصمة التنفيذية: بريتوريا", "العملة: الراند • للبلاد 12 لغة رسمية"),
    Entry("أستراليا", "العاصمة: كانبيرا", "العملة: الدولار الأسترالي • اللغة الأكثر استعمالاً: الإنجليزية")
)

private val history = listOf(
    Entry("العصور القديمة", "تعاقبت على أرض الجزائر حضارات أمازيغية وفينيقية ورومانية وغيرها."),
    Entry("مملكة نوميديا", "ارتبط تاريخ نوميديا بملوك مثل ماسينيسا ويوغرطة."),
    Entry("العهد الإسلامي", "انتشر الإسلام وتكونت دول ومراكز علمية وتجارية في المنطقة."),
    Entry("العهد العثماني", "كانت الجزائر إيالة عثمانية ذات مكانة متوسطية."),
    Entry("الاحتلال الفرنسي", "بدأ الاحتلال الفرنسي سنة 1830."),
    Entry("الثورة التحريرية", "اندلعت ثورة التحرير في 1 نوفمبر 1954."),
    Entry("الاستقلال", "استعادت الجزائر استقلالها في 5 يوليو 1962.")
)

private val heritage = listOf(
    Entry("قصبة الجزائر", "نسيج عمراني تاريخي مدرج ضمن قائمة التراث العالمي."),
    Entry("تيمقاد", "مدينة رومانية أثرية في ولاية باتنة."),
    Entry("جميلة", "موقع أثري روماني في ولاية سطيف."),
    Entry("قلعة بني حماد", "موقع أثري يعود إلى الدولة الحمادية."),
    Entry("وادي مزاب", "منطقة عمرانية وثقافية فريدة في غرداية."),
    Entry("طاسيلي ناجر", "منطقة طبيعية وأثرية مشهورة برسومها الصخرية."),
    Entry("تيبازة", "موقع أثري ساحلي يضم بقايا مدينة رومانية.")
)

private val sections = listOf(
    Section("ولايات الجزائر", "قائمة الولايات الـ58 ومعلومات تعريفية", Icons.Default.Map,
        wilayaNames.map { Entry(it, wilayaNotes[it] ?: "ولاية جزائرية.", "الترقيم الإداري ضمن قائمة الولايات المعروفة ذات 58 ولاية.") }),
    Section("رؤساء الجزائر", "رؤساء الجمهورية ورؤساء الدولة خلال المراحل الانتقالية", Icons.Default.AccountBalance, presidents),
    Section("دول العالم", "مجموعة أولية من الدول والعواصم والعملات واللغات", Icons.Default.Public, countries),
    Section("تاريخ الجزائر", "محطات أساسية من تاريخ البلاد", Icons.Default.History, history),
    Section("التراث والمعالم", "معالم ومواقع ثقافية وطبيعية جزائرية", Icons.Default.AccountBalance, heritage)
)

@Composable
fun EncyclopediaApp() {
    var selected by remember { mutableStateOf<Section?>(null) }
    var query by remember { mutableStateOf("") }
    var showDistance by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(Cream)) {
        Column(Modifier.fillMaxWidth().background(Navy).padding(horizontal = 18.dp, vertical = 20.dp)) {
            Text("🇩🇿  موسوعة الجزائر والعالم", color = Gold, fontSize = 23.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("معرفة الجزائر ونافذة على العالم", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(14.dp))
            OutlinedTextField(
                value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("ابحث عن ولاية أو دولة أو معلومة…") }, singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                    focusedBorderColor = Gold, unfocusedBorderColor = Color.White
                ), shape = RoundedCornerShape(15.dp)
            )
        }
        when {
            showDistance -> DistanceScreen(onBack = { showDistance = false })
            selected != null -> SectionScreen(selected!!, query, onBack = { selected = null })
            else -> LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(Modifier.fillMaxWidth().padding(18.dp)) {
                            Text("مرحباً بك في موسوعتك", color = Navy, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("تصفح المعلومات المحفوظة داخل التطبيق. لا يحتاج المحتوى الحالي إلى اتصال بالإنترنت.",
                                color = Color.DarkGray, fontSize = 14.sp, lineHeight = 22.sp)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                StatPill("58 ولاية")
                                StatPill("${countries.size} دولة")
                                StatPill("${presidents.size} أسماء")
                            }
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { showDistance = true },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Blue)
                    ) {
                        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SwapHoriz, null, tint = Color.White, modifier = Modifier.size(32.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("المسافة بين الولايات", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("حاسبة مسافة تقريبية بين مدن جزائرية محددة", color = Color.White, fontSize = 13.sp)
                            }
                            Icon(Icons.Default.ChevronLeft, null, tint = Gold)
                        }
                    }
                }
                items(sections.filter { s ->
                    query.isBlank() || s.title.contains(query) ||
                        s.entries.any { it.name.contains(query) || it.detail.contains(query) || it.extra.contains(query) }
                }) { section ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { selected = section },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(19.dp)
                    ) {
                        Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(54.dp).background(Navy, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                                Icon(section.icon, null, tint = Gold, modifier = Modifier.size(29.dp))
                            }
                            Spacer(Modifier.width(13.dp))
                            Column(Modifier.weight(1f)) {
                                Text(section.title, color = Navy, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text(section.subtitle, color = Color.Gray, fontSize = 13.sp)
                                Text("${section.entries.size} مدخلاً", color = Blue, fontSize = 12.sp)
                            }
                            Icon(Icons.Default.ChevronLeft, null, tint = Blue)
                        }
                    }
                }
                item {
                    Text("معلومات الإصدار: 1.1.0 • قاعدة البيانات الحالية تأسيسية وتحتاج استكمالاً رسمياً للدوائر والبلديات وبقية دول العالم.",
                        color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(10.dp))
                    Text("الجزائر في القلب 🇩🇿", color = Navy, fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp), textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun StatPill(value: String) {
    Surface(color = Cream, shape = RoundedCornerShape(30.dp)) {
        Text(value, color = Navy, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp))
    }
}

@Composable
private fun SectionScreen(section: Section, query: String, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().background(Color.White).clickable { onBack() }.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ArrowForward, contentDescription = "رجوع", tint = Navy)
            Spacer(Modifier.width(8.dp))
            Text("العودة إلى الأقسام", color = Navy, fontWeight = FontWeight.Bold)
        }
        Text(section.title, color = Navy, fontSize = 22.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp))
        val filtered = section.entries.filter {
            query.isBlank() || it.name.contains(query) || it.detail.contains(query) || it.extra.contains(query)
        }
        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لا توجد نتائج مطابقة.", color = Color.Gray)
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)) {
                items(filtered) { entry ->
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(15.dp)) {
                        Column(Modifier.fillMaxWidth().padding(16.dp)) {
                            Text(entry.name, color = Navy, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(Modifier.height(5.dp))
                            Text(entry.detail, color = Color.DarkGray, fontSize = 15.sp, lineHeight = 23.sp)
                            if (entry.extra.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(entry.extra, color = Blue, fontSize = 13.sp, lineHeight = 20.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class City(val name: String, val lat: Double, val lon: Double)
private val cities = listOf(
    City("الجزائر", 36.7538, 3.0588), City("وهران", 35.6969, -0.6331),
    City("قسنطينة", 36.3650, 6.6147), City("عنابة", 36.9000, 7.7667),
    City("تلمسان", 34.8888, -1.3160), City("بجاية", 36.7500, 5.0667),
    City("سطيف", 36.1900, 5.4100), City("باتنة", 35.5550, 6.1740),
    City("غرداية", 32.4909, 3.6735), City("ورقلة", 31.9500, 5.3167),
    City("بسكرة", 34.8500, 5.7333), City("تمنراست", 22.7850, 5.5228)
)

private fun distanceKm(a: City, b: City): Int {
    val r = 6371.0
    val dLat = Math.toRadians(b.lat - a.lat)
    val dLon = Math.toRadians(b.lon - a.lon)
    val x = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(a.lat)) * cos(Math.toRadians(b.lat)) * sin(dLon / 2).pow(2.0)
    return (2 * r * asin(sqrt(x))).roundToInt()
}

@Composable
private fun DistanceScreen(onBack: () -> Unit) {
    var from by remember { mutableStateOf(cities.first().name) }
    var to by remember { mutableStateOf(cities[1].name) }
    val result = distanceKm(cities.first { it.name == from }, cities.first { it.name == to })
    Column(Modifier.fillMaxSize().padding(18.dp)) {
        TextButton(onClick = onBack) { Icon(Icons.Default.ArrowForward, null); Text("رجوع") }
        Text("المسافة بين المدن", color = Navy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("اختر مدينتين من القائمة. النتيجة خط مستقيم تقريبي وليست مسافة الطريق.",
            color = Color.Gray, fontSize = 14.sp, lineHeight = 21.sp)
        Spacer(Modifier.height(18.dp))
        CityPicker("مدينة الانطلاق", from) { from = it }
        Spacer(Modifier.height(12.dp))
        CityPicker("مدينة الوصول", to) { to = it }
        Spacer(Modifier.height(20.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Navy), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("المسافة التقريبية", color = Color.White)
                Text("$result كم", color = Gold, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text("بين $from و$to", color = Color.White, fontSize = 14.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("هذه الحاسبة تغطي المدن المدرجة فقط، ويمكن توسيعها بإضافة إحداثيات بقية الولايات.",
            color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
private fun CityPicker(label: String, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, color = Navy, fontWeight = FontWeight.Bold)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selected, modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                Icon(Icons.Default.ArrowDropDown, null)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                cities.forEach { city ->
                    DropdownMenuItem(text = { Text(city.name) }, onClick = {
                        onSelected(city.name); expanded = false
                    })
                }
            }
        }
    }
}
