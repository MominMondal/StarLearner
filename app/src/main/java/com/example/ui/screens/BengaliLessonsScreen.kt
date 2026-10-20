package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.ui.audio.LessonSpeechButton
import com.example.ui.audio.LessonSpeechManager
import com.example.ui.audio.rememberLessonSpeechManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ClassLevel
import com.example.data.model.CurriculumMedium
import com.example.service.SoundManagerService
import com.example.ui.components.ConfettiCelebrationOverlay
import com.example.ui.theme.BengaliColor
import com.example.ui.theme.BengaliColorLight
import com.example.ui.theme.PrimaryBlue

enum class MediumLessonCategory {
    ALPHABET_VOWELS,
    ALPHABET_CONSONANTS,
    NUMBERS_COUNTING,
    RHYMES_STORIES,
    STEM_VOCABULARY
}

data class GenericLetterCard(
    val letter: String,
    val pronunciation: String,
    val exampleWord: String,
    val englishMeaning: String,
    val emoji: String,
    val exampleSentence: String = ""
)

data class GenericRhymeStory(
    val title: String,
    val subtitle: String,
    val emoji: String,
    val lines: List<String>,
    val educationalNote: String
)

data class GenericNumberCard(
    val digitNative: String,
    val digitEnglish: String,
    val wordNative: String,
    val pronunciation: String,
    val counterEmoji: String,
    val count: Int
)

data class CbseConceptCard(
    val topic: String,
    val emoji: String,
    val summary: String,
    val keyPoints: List<String>,
    val funFact: String
)

@Composable
fun BengaliLessonsScreen(
    currentClassLevel: ClassLevel = ClassLevel.CLASS_2,
    initialMedium: CurriculumMedium = CurriculumMedium.BANGLA,
    onMediumChanged: (CurriculumMedium) -> Unit = {},
    onTakeQuiz: () -> Unit = {},
    onOpenPhotoLearning: () -> Unit = {},
    onCompleteLesson: (Int) -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val speechManager = rememberLessonSpeechManager()
    BackHandler {
        speechManager.stop()
        onBack()
    }
    val context = LocalContext.current

    var selectedMedium by remember(initialMedium) { mutableStateOf(initialMedium) }
    var selectedCategory by remember { mutableStateOf(MediumLessonCategory.ALPHABET_VOWELS) }
    var selectedLetterDetail by remember { mutableStateOf<GenericLetterCard?>(null) }
    var lessonCompletedNotice by remember { mutableStateOf(false) }
    var showLessonConfetti by remember { mutableStateOf(false) }

    // === BANGLA MEDIUM DATASETS ===
    val banglaVowels = remember {
        listOf(
            GenericLetterCard("অ", "O", "অজগর", "Python Snake", "🐍", "অ-তে অজগর আসছে তেড়ে"),
            GenericLetterCard("আ", "Aa", "আম", "Mango", "🥭", "আ-তে আমটি আমি খাব পেড়ে"),
            GenericLetterCard("ই", "I (Hroshwo-I)", "ইঁদুর", "Mouse", "🐁", "ই-তে ইঁদুর ছানা ভয়ে মরে"),
            GenericLetterCard("ঈ", "Ee (Deergho-I)", "ঈগল", "Eagle", "🦅", "ঈ-তে ঈগল পাখি পাছে ধরে"),
            GenericLetterCard("উ", "U (Hroshwo-U)", "উট", "Camel", "🐪", "উ-তে উট চলেছে মুখটি তুলে"),
            GenericLetterCard("ঊ", "Oo (Deergho-U)", "ঊর্মি", "Ocean Wave", "🌊", "ঊ-তে ঊর্মি হাসে সাগর জলে"),
            GenericLetterCard("ঋ", "Ri", "ঋষি", "Sage", "🧘", "ঋ-তে ঋষি মশাই বসেন পূজায়"),
            GenericLetterCard("এ", "E", "একতারা", "Ektara Musical Instrument", "🪕", "এ-তে একতারা বাজে বাউলের গানে"),
            GenericLetterCard("ঐ", "Oi", "ঐরাবত", "Elephant", "🐘", "ঐ-তে ঐরাবত চলেছে শান নিয়ে"),
            GenericLetterCard("ও", "O", "ওল", "Yam", "🥔", "ও-তে ওল খেলে ধরে গলা"),
            GenericLetterCard("ঔ", "Ou", "ঔষধ", "Medicine", "💊", "ঔ-তে ঔষধ খেতে মিছে বলা")
        )
    }

    val banglaConsonants = remember {
        listOf(
            GenericLetterCard("ক", "Ko", "কলম", "Pen", "✒️", "ক-তে কলম দিয়ে লিখি মোরা"),
            GenericLetterCard("খ", "Kho", "খরগোশ", "Rabbit", "🐇", "খ-তে খরগোশ ছানা লাফিয়ে বেড়ায়"),
            GenericLetterCard("গ", "Go", "গরু", "Cow", "🐄", "গ-তে গরু বাছুর মাঠে চরে"),
            GenericLetterCard("ঘ", "Gho", "ঘুড়ি", "Kite", "🪁", "ঘ-তে ঘুড়ি ওড়ে নীল আকাশে"),
            GenericLetterCard("ঙ", "Umo", "ব্যাঙ", "Frog", "🐸", "ঙ-তে ব্যাঙ ডাকে ঘ্যাঙর ঘ্যাং"),
            GenericLetterCard("চ", "Cho", "চাঁদ", "Moon", "🌙", "চ-তে চাঁদের আলো স্নিগ্ধ অতি"),
            GenericLetterCard("ছ", "Chho", "ছাতা", "Umbrella", "☂️", "ছ-তে ছাতা মাথায় বৃষ্টি নামে"),
            GenericLetterCard("জ", "Jo", "জাহাজ", "Ship", "🚢", "জ-তে জাহাজ চলে সাগর জলে"),
            GenericLetterCard("ঝ", "Jho", "ঝিনুক", "Oyster / Seashell", "🐚", "ঝ-তে ঝিনুক মাঝে মুক্তা দোলে"),
            GenericLetterCard("ট", "To", "টিয়া", "Parrot", "🦜", "ট-তে টিয়া পাখির ঠোঁটটি লাল"),
            GenericLetterCard("ড", "Do", "ডালিম", "Pomegranate", "🍎", "ড-তে ডালিম গাছে পাকা ফল"),
            GenericLetterCard("দ", "Do", "দোয়েল", "Magpie Robin", "🐦", "দ-তে দোয়েল আমাদের জাতীয় পাখি"),
            GenericLetterCard("ন", "No", "নৌকা", "Boat", "⛵", "ন-তে নৌকা বাওয়া মাঝি ভাই"),
            GenericLetterCard("প", "Po", "পাখি", "Bird", "🕊️", "প-তে পাখি গায় ডালে ডালে"),
            GenericLetterCard("ব", "Bo", "বাঘ", "Tiger", "🐅", "ব-তে বাঘ সুন্দরবনের রাজা"),
            GenericLetterCard("ম", "Mo", "ময়ূর", "Peacock", "🦚", "ম-তে ময়ূর পেখম তুলে নাচে"),
            GenericLetterCard("হ", "Ho", "হাতি", "Elephant", "🐘", "হ-তে হাতি চলে হেলেদুলে")
        )
    }

    val banglaNumbers = remember {
        listOf(
            GenericNumberCard("১", "1", "এক", "Eek", "🍎", 1),
            GenericNumberCard("২", "2", "দুই", "Dui", "🍌", 2),
            GenericNumberCard("৩", "3", "তিন", "Tin", "⭐", 3),
            GenericNumberCard("৪", "4", "চার", "Chaar", "🚗", 4),
            GenericNumberCard("৫", "5", "পাঁচ", "Paanch", "🎈", 5),
            GenericNumberCard("৬", "6", "ছয়", "Chhoy", "🌸", 6),
            GenericNumberCard("৭", "7", "সাত", "Saat", "🍓", 7),
            GenericNumberCard("৮", "8", "আট", "Aat", "🐠", 8),
            GenericNumberCard("৯", "9", "নয়", "Noy", "🦋", 9),
            GenericNumberCard("১০", "10", "দশ", "Dosh", "🏅", 10)
        )
    }

    val banglaRhymes = remember {
        listOf(
            GenericRhymeStory(
                title = "হাট্টিমাটিম টিম",
                subtitle = "প্রচলিত লোকছড়া",
                emoji = "🥚",
                lines = listOf(
                    "হাট্টিমাটিম টিম,",
                    "তারা মাঠে পাড়ে ডিম,",
                    "তাদের খাড়া দুটো শিং,",
                    "তারা হাট্টিমাটিম টিম!"
                ),
                educationalNote = "বাংলার শিশুদের অত্যন্ত জনপ্রিয় ঐতিহ্যবাহী লোকছড়া।"
            ),
            GenericRhymeStory(
                title = "আতা গাছে তোতা পাখি",
                subtitle = "লোকপ্রিয় শিশুতোষ ছড়া",
                emoji = "🦜",
                lines = listOf(
                    "আতা গাছে তোতা পাখি,",
                    "ডালিম গাছে মৌ,",
                    "এত ডাকি তবু কথা",
                    "কও না কেন বউ?"
                ),
                educationalNote = "ছন্দ ও মিষ্টি কথায় ভরপুর সুন্দর একটি ঐতিহ্যবাহী ছড়া।"
            ),
            GenericRhymeStory(
                title = "ভোর হলো দোর খোলো",
                subtitle = "কাজী নজরুল ইসলাম",
                emoji = "🌅",
                lines = listOf(
                    "ভোর হলো দোর খোলো,",
                    "খুকুমণি ওঠো রে!",
                    "ঐ ডাকে জুঁই-শাখে,",
                    "ফুল-খুকী ছোট রে!"
                ),
                educationalNote = "প্রকৃতির রূপ ও সকালের আহ্বানের বিখ্যাত অমর কবিতা।"
            )
        )
    }

    // === HINDI MEDIUM DATASETS ===
    val hindiVowels = remember {
        listOf(
            GenericLetterCard("अ", "A", "अनार", "Pomegranate", "🍎", "अ से अनार दानेदार, खाकर मुन्ना बने होशियार"),
            GenericLetterCard("आ", "Aa", "आम", "Mango", "🥭", "आ से आम फलों का राजा, कितना मीठा कितना ताजा"),
            GenericLetterCard("इ", "I (Hrasva)", "इमली", "Tamarind", "🌿", "इ से इमली खट्टी-मीठी, स्वाद लगे सबसे अच्छी"),
            GenericLetterCard("ई", "Ee (Deergha)", "ईख", "Sugarcane", "🎋", "ई से ईख गन्ने की मिठास, खाओ इसको सबके पास"),
            GenericLetterCard("उ", "U (Hrasva)", "उल्लू", "Owl", "🦉", "उ से उल्लू रात में जागे, दिन निकले तो डरकर भागे"),
            GenericLetterCard("ऊ", "Oo (Deergha)", "ऊन", "Wool", "🧶", "ऊ से ऊन भेंड़ से मिलता, दादी मां स्वेटर बुनती"),
            GenericLetterCard("ऋ", "Ri", "ऋषि", "Sage", "🧘", "ऋ से ऋषि ध्यान लगाते, हमको अच्छी सीख सिखाते"),
            GenericLetterCard("ए", "E", "एड़ी", "Heel", "🦶", "ए से एड़ी पैर का भाग, दौड़ लगाओ बेहिचक आग"),
            GenericLetterCard("ऐ", "Ai", "ऐनक", "Spectacles", "👓", "ऐ से ऐनक दादाजी पहनें, पुस्तक का हर अक्षर जानें"),
            GenericLetterCard("ओ", "O", "ओखली", "Mortar", "🥣", "ओ से ओखली दाना कूटे, मेहनत से हर बाधा छूटे"),
            GenericLetterCard("औ", "Au", "औरत", "Woman / Mother", "👩", "औ से औरत ममतामयी माँ, जिनकी छाया सबसे प्यारी"),
            GenericLetterCard("अं", "Am", "अंगूर", "Grapes", "🍇", "अं से अंगूर रसीले गुच्छे, खट्टे-मीठे प्यारे अच्छे")
        )
    }

    val hindiConsonants = remember {
        listOf(
            GenericLetterCard("क", "Ka", "कबूतर", "Pigeon", "🕊️", "क से कबूतर शांति का दूत, उड़ता जाए अंबर सूत"),
            GenericLetterCard("ख", "Kha", "खरगोश", "Rabbit", "🐇", "ख से खरगोश सफेद-सफेद, घास खाए निर्भय निर्भेद"),
            GenericLetterCard("ग", "Ga", "गमला", "Flower Pot", "🪴", "ग से गमला फूल खिलाए, आँगन की सुंदरता बढ़ाए"),
            GenericLetterCard("घ", "Gha", "घड़ी", "Clock", "⏰", "घ से घड़ी समय बताती, समय का मोल हमें सिखाती"),
            GenericLetterCard("च", "Cha", "चम्मच", "Spoon", "🥄", "च से चम्मच खीर खाओ, सेहत अपनी खूब बनाओ"),
            GenericLetterCard("छ", "Chha", "छतरी", "Umbrella", "☂️", "छ से छतरी बारिश में काम, धूप से भी दे आराम"),
            GenericLetterCard("ज", "Ja", "जहाज", "Ship", "🚢", "ज से जहाज समंदर में तैरे, ले जाए देशों की सैर पे"),
            GenericLetterCard("ट", "Ta", "टमाटर", "Tomato", "🍅", "ट से टमाटर लाल-लाल, गाल बना दे लाल-गुलाल"),
            GenericLetterCard("ड", "Da", "डमरू", "Drum", "🪘", "ड से डमरू डम-डम बाजे, नटखट बच्चे मिलकर नाचे"),
            GenericLetterCard("त", "Ta", "तरबूज", "Watermelon", "🍉", "त से तरबूज ठंडा मीठा, गर्मी में है सबसे सच्चा"),
            GenericLetterCard("द", "Da", "दवात", "Inkpot", "✒️", "द से दवात स्याही से भरी, लिखो ज्ञान की बातें खरी"),
            GenericLetterCard("न", "Na", "नल", "Tap / Water", "🚰", "न से नल पानी बहाए, बूँद-बूँद जीवन बचाए"),
            GenericLetterCard("प", "Pa", "पतंग", "Kite", "🪁", "प से पतंग गगन में डोले, हवा के संग अठखेली बोले"),
            GenericLetterCard("ब", "Ba", "बत्तख", "Duck", "🦆", "ब से बत्तख जल में तैरे, सफेद पंख पानी पर फेरे"),
            GenericLetterCard("म", "Ma", "मछली", "Fish", "🐟", "म से मछली जल की रानी, जीवन उसका शीतल पानी"),
            GenericLetterCard("स", "Sa", "सेब", "Apple", "🍎", "स से सेब एक रोज़ खाओ, डॉक्टर को तुम दूर भगाओ"),
            GenericLetterCard("ह", "Ha", "हाथी", "Elephant", "🐘", "ह से हाथी भारी-भरकम, सूंड हिलाता चलता हरदम")
        )
    }

    val hindiNumbers = remember {
        listOf(
            GenericNumberCard("१", "1", "एक", "Ek", "🍎", 1),
            GenericNumberCard("२", "2", "दो", "Do", "🍌", 2),
            GenericNumberCard("३", "3", "तीन", "Teen", "⭐", 3),
            GenericNumberCard("४", "4", "चार", "Chaar", "🚗", 4),
            GenericNumberCard("५", "5", "पाँच", "Paanch", "🎈", 5),
            GenericNumberCard("६", "6", "छह", "Chhah", "🌸", 6),
            GenericNumberCard("७", "7", "सात", "Saat", "🍓", 7),
            GenericNumberCard("८", "8", "आठ", "Aath", "🐠", 8),
            GenericNumberCard("९", "9", "नौ", "Nau", "🦋", 9),
            GenericNumberCard("१०", "10", "दस", "Das", "🏅", 10)
        )
    }

    val hindiRhymes = remember {
        listOf(
            GenericRhymeStory(
                title = "मछली जल की रानी है",
                subtitle = "लोकप्रिय बालगीत",
                emoji = "🐟",
                lines = listOf(
                    "मछली जल की रानी है,",
                    "जीवन उसका पानी है,",
                    "हाथ लगाओ तो डर जाएगी,",
                    "बाहर निकालो तो मर जाएगी!"
                ),
                educationalNote = "हिंदी भाषी बच्चों का सबसे पहला और लोकप्रिय बालगीत।"
            ),
            GenericRhymeStory(
                title = "तितली उड़ी",
                subtitle = "मजेदार बालगीत",
                emoji = "🦋",
                lines = listOf(
                    "तितली उड़ी बस पर चढ़ी,",
                    "सीट न मिली तो रोने लगी,",
                    "ड्राइवर ने बोला आजा मेरे पास,",
                    "तितली बोली ना बाबा ना!"
                ),
                educationalNote = "हंसी और मस्ती से भरपूर प्रसिद्ध बाल कविता।"
            ),
            GenericRhymeStory(
                title = "चंदा मामा दूर के",
                subtitle = "पारंपरिक लोरी व गीत",
                emoji = "🌙",
                lines = listOf(
                    "चंदा मामा दूर के,",
                    "पुए पकाएं बूर के,",
                    "आप खाएं थाली में,",
                    "मुन्ने को दें प्याली में!"
                ),
                educationalNote = "रात के चाँद और ममता से भरी प्यारी लोरी।"
            )
        )
    }

    // === CBSE (ENGLISH) MEDIUM DATASETS ===
    val cbseConcepts = remember {
        listOf(
            CbseConceptCard(
                topic = "Phonics & Word Families",
                emoji = "🔤",
                summary = "Master short vowel sounds and CVC (Consonant-Vowel-Consonant) blend patterns.",
                keyPoints = listOf(
                    "-at family: Cat, Hat, Bat, Mat",
                    "-an family: Can, Fan, Man, Pan",
                    "-ig family: Big, Dig, Fig, Pig",
                    "-op family: Hop, Mop, Pop, Top"
                ),
                funFact = "Recognizing word patterns helps young readers decode 80% of foundational story words!"
            ),
            CbseConceptCard(
                topic = "Place Value & STEM Math",
                emoji = "🔢",
                summary = "Understanding Tens and Ones, regrouping, and simple 2D / 3D geometry.",
                keyPoints = listOf(
                    "Place Value: In 34, 3 is in Tens place (30) and 4 is in Ones place (4).",
                    "Skip Counting: 2, 4, 6, 8, 10... and 5, 10, 15, 20...",
                    "2D Shapes: Triangle (3 sides), Rectangle (4 sides, opposite equal), Circle (curved).",
                    "Fractions: 1/2 is half, 1/4 is one quarter of a whole."
                ),
                funFact = "Zero was invented by ancient Indian mathematicians and made modern arithmetic possible!"
            ),
            CbseConceptCard(
                topic = "Science & 5 Senses",
                emoji = "🌱",
                summary = "Discover living organisms, plant anatomy, and our sensory wonders.",
                keyPoints = listOf(
                    "5 Senses: Eyes (Sight), Ears (Hearing), Nose (Smell), Tongue (Taste), Skin (Touch).",
                    "Plant Parts: Roots absorb water, stems support, leaves make food, flowers bloom.",
                    "Habitats: Fish live in water, birds in nests/trees, camels in dry deserts.",
                    "States of Matter: Solid (ice), Liquid (water), Gas (steam)."
                ),
                funFact = "Plants produce the oxygen we breathe every second through sunlight and photosynthesis!"
            ),
            CbseConceptCard(
                topic = "Moral Story: The Thirsty Crow",
                emoji = "🦅",
                summary = "A clever crow uses problem solving to drink water from a half-filled pitcher.",
                keyPoints = listOf(
                    "1. On a hot summer day, a crow found a pitcher with very little water at the bottom.",
                    "2. His beak could not reach the water level.",
                    "3. He thought of an ingenious idea: he picked up pebbles one by one with his beak.",
                    "4. He dropped pebbles into the pitcher until the water rose to the top!",
                    "5. He quenched his thirst happily. Moral: Where there is a will, there is a way!"
                ),
                funFact = "Crows and ravens are proven by scientists to be capable of multi-step tool use!"
            )
        )
    }

    val cbseEnglishLetters = remember {
        listOf(
            GenericLetterCard("A", "æ (Short A)", "Apple", "Crunchy red fruit", "🍎", "A is for Apple, sweet and round"),
            GenericLetterCard("B", "b (Plosive)", "Butterfly", "Colorful winged pollinator", "🦋", "B is for Butterfly fluttering high"),
            GenericLetterCard("C", "k (Hard C)", "Cat", "Friendly purring pet", "🐱", "C is for Cat sitting on the mat"),
            GenericLetterCard("D", "d (Voiced)", "Dolphin", "Smart sea mammal", "🐬", "D is for Dolphin swimming in the sea"),
            GenericLetterCard("E", "e (Short E)", "Elephant", "Gentle giant with trunk", "🐘", "E is for Elephant big and strong"),
            GenericLetterCard("F", "f (Fricative)", "Flower", "Fragrant garden bloom", "🌸", "F is for Flower smiling in the sun"),
            GenericLetterCard("G", "g (Hard G)", "Giraffe", "Tall spotted animal", "🦒", "G is for Giraffe reaching tall branches"),
            GenericLetterCard("H", "h (Aspirated)", "House", "Warm family home", "🏠", "H is for House where families stay"),
            GenericLetterCard("M", "m (Nasal)", "Moon", "Silver glowing night satellite", "🌙", "M is for Moon shining bright at night"),
            GenericLetterCard("S", "s (Sibilant)", "Sun", "Warm daytime star", "☀️", "S is for Sun giving light to all")
        )
    }

    val cbseNumbers = remember {
        listOf(
            GenericNumberCard("1", "1", "One", "wʌn", "🍎", 1),
            GenericNumberCard("2", "2", "Two", "tuː", "🍌", 2),
            GenericNumberCard("3", "3", "Three", "θriː", "⭐", 3),
            GenericNumberCard("4", "4", "Four", "fɔːr", "🚗", 4),
            GenericNumberCard("5", "5", "Five", "faɪv", "🎈", 5),
            GenericNumberCard("6", "6", "Six", "sɪks", "🌸", 6),
            GenericNumberCard("7", "7", "Seven", "ˈsɛv.ən", "🍓", 7),
            GenericNumberCard("8", "8", "Eight", "eɪt", "🐠", 8),
            GenericNumberCard("9", "9", "Nine", "naɪn", "🦋", 9),
            GenericNumberCard("10", "10", "Ten", "tɛn", "🏅", 10)
        )
    }

    val cbseRhymes = remember {
        listOf(
            GenericRhymeStory(
                title = "Twinkle, Twinkle, Little Star",
                subtitle = "Classic Nursery Rhyme",
                emoji = "⭐",
                lines = listOf(
                    "Twinkle, twinkle, little star,",
                    "How I wonder what you are!",
                    "Up above the world so high,",
                    "Like a diamond in the sky."
                ),
                educationalNote = "Teaches rhyme rhythm, wonder about space and astronomy."
            ),
            GenericRhymeStory(
                title = "Baa, Baa, Black Sheep",
                subtitle = "English Folk Rhyme",
                emoji = "🐑",
                lines = listOf(
                    "Baa, baa, black sheep, have you any wool?",
                    "Yes, sir, yes, sir, three bags full:",
                    "One for the master, and one for the dame,",
                    "And one for the little boy who lives down the lane."
                ),
                educationalNote = "Introduces animal sounds, counting bags, and sharing."
            ),
            GenericRhymeStory(
                title = "The Clever Crow & Pitcher",
                subtitle = "Aesop Moral Tale",
                emoji = "🦅",
                lines = listOf(
                    "A thirsty crow found a pitcher with water low,",
                    "His beak could not reach, but he would not let go!",
                    "Pebble by pebble he dropped into the clay,",
                    "The water rose up, and he flew happily away!"
                ),
                educationalNote = "Moral: Where there is a will and perseverance, there is always a way!"
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("medium_lessons_screen")
    ) {
        // Top App Bar
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("lessons_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF1E293B)
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "পাঠশালা • Lesson Hub",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(BengaliColorLight)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${currentClassLevel.label}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BengaliColor
                                    )
                                }
                            }
                            Text(
                                text = "Bangla • Hindi • CBSE (English) Mediums",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    // Photo Learning Direct Link
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFEF2F2))
                            .clickable {
                                SoundManagerService.playStarPop(context)
                                onOpenPhotoLearning()
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("lessons_photo_shortcut")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📸", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Photos",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB91C1C)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // === MEDIUM SELECTOR ROW ===
                Text(
                    text = "Select Medium (পড়াশোনার মাধ্যম / माध्यम)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("medium_switcher_row"),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CurriculumMedium.values().forEach { medium ->
                        val isSelected = medium == selectedMedium
                        val bgColor by animateColorAsState(
                            targetValue = if (isSelected) {
                                when (medium) {
                                    CurriculumMedium.BANGLA -> BengaliColor
                                    CurriculumMedium.HINDI -> Color(0xFFEA580C) // Vibrant saffron orange
                                    CurriculumMedium.CBSE_ENGLISH -> PrimaryBlue
                                }
                            } else Color(0xFFF1F5F9),
                            label = "medium_bg"
                        )
                        val textColor by animateColorAsState(
                            targetValue = if (isSelected) Color.White else Color(0xFF334155),
                            label = "medium_text"
                        )

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    speechManager.stop()
                                    selectedMedium = medium
                                    onMediumChanged(medium)
                                    selectedCategory = MediumLessonCategory.ALPHABET_VOWELS
                                    SoundManagerService.playCorrect(context)
                                }
                                .testTag("medium_chip_${medium.name.lowercase()}"),
                            color = bgColor,
                            shape = RoundedCornerShape(12.dp),
                            border = if (!isSelected) BorderStroke(1.dp, Color(0xFFE2E8F0)) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = medium.flagEmoji, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = medium.shortCode,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = textColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Navigation Sub-Tabs
                val currentTabs = remember(selectedMedium) {
                    when (selectedMedium) {
                        CurriculumMedium.BANGLA -> listOf(
                            MediumLessonCategory.ALPHABET_VOWELS to "স্বরবর্ণ",
                            MediumLessonCategory.ALPHABET_CONSONANTS to "ব্যঞ্জনবর্ণ",
                            MediumLessonCategory.NUMBERS_COUNTING to "১-১০",
                            MediumLessonCategory.RHYMES_STORIES to "ছড়া"
                        )
                        CurriculumMedium.HINDI -> listOf(
                            MediumLessonCategory.ALPHABET_VOWELS to "स्वर",
                            MediumLessonCategory.ALPHABET_CONSONANTS to "व्यंजन",
                            MediumLessonCategory.NUMBERS_COUNTING to "१-१०",
                            MediumLessonCategory.RHYMES_STORIES to "बालगीत"
                        )
                        CurriculumMedium.CBSE_ENGLISH -> listOf(
                            MediumLessonCategory.ALPHABET_VOWELS to "Phonics",
                            MediumLessonCategory.NUMBERS_COUNTING to "1-10",
                            MediumLessonCategory.STEM_VOCABULARY to "STEM Lab",
                            MediumLessonCategory.RHYMES_STORIES to "Stories"
                        )
                    }
                }
                val currentTabIndex = currentTabs.indexOfFirst { it.first == selectedCategory }.let { if (it >= 0) it else 0 }

                TabRow(
                    selectedTabIndex = currentTabIndex,
                    containerColor = Color.White,
                    contentColor = when (selectedMedium) {
                        CurriculumMedium.BANGLA -> BengaliColor
                        CurriculumMedium.HINDI -> Color(0xFFEA580C)
                        CurriculumMedium.CBSE_ENGLISH -> PrimaryBlue
                    },
                    indicator = { tabPositions ->
                        if (currentTabIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[currentTabIndex]),
                                color = when (selectedMedium) {
                                    CurriculumMedium.BANGLA -> BengaliColor
                                    CurriculumMedium.HINDI -> Color(0xFFEA580C)
                                    CurriculumMedium.CBSE_ENGLISH -> PrimaryBlue
                                },
                                height = 3.dp
                            )
                        }
                    }
                ) {
                    currentTabs.forEach { (cat, label) ->
                        Tab(
                            selected = selectedCategory == cat,
                            onClick = {
                                speechManager.stop()
                                selectedCategory = cat
                            },
                            text = { Text(label, fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // Main Body Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(14.dp))

                // Medium Header Banner
                val currentThemeColor = when (selectedMedium) {
                    CurriculumMedium.BANGLA -> BengaliColor
                    CurriculumMedium.HINDI -> Color(0xFFEA580C)
                    CurriculumMedium.CBSE_ENGLISH -> PrimaryBlue
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = currentThemeColor.copy(alpha = 0.1f)
                    ),
                    border = BorderStroke(1.dp, currentThemeColor.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${selectedMedium.flagEmoji} ${selectedMedium.label}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = selectedMedium.description,
                                fontSize = 11.sp,
                                color = Color(0xFF475569)
                            )
                        }

                        Button(
                            onClick = {
                                SoundManagerService.playCorrect(context)
                                onTakeQuiz()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = currentThemeColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Quiz", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Audio Read-Aloud Assistant Card for younger students
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tts_reading_assistant_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, currentThemeColor.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "🎧", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Audio Read-Aloud for Learners",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Tap 🔊 on any card to listen clearly at gentle reading pace!",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        if (speechManager.isSpeaking.value) {
                            Button(
                                onClick = { speechManager.stop() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Stop ⏹️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // === DYNAMIC CONTENT BASED ON SELECTED MEDIUM & CATEGORY ===
            when (selectedMedium) {
                CurriculumMedium.BANGLA -> {
                    when (selectedCategory) {
                        MediumLessonCategory.ALPHABET_VOWELS -> {
                            item {
                                SectionHeader(title = "বাংলা স্বরবর্ণ (১১টি)", subtitle = "অ আ ই ঈ উ ঊ ঋ এ ঐ ও ঔ — মুখে বলো ও শেখো")
                            }
                            val chunked = banglaVowels.chunked(2)
                            items(chunked) { pair ->
                                GenericLetterPairRow(
                                    pair = pair,
                                    themeColor = BengaliColor,
                                    onClick = { item ->
                                        selectedLetterDetail = item
                                        SoundManagerService.playCorrect(context)
                                    }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                        MediumLessonCategory.ALPHABET_CONSONANTS -> {
                            item {
                                SectionHeader(title = "বাংলা ব্যঞ্জনবর্ণ (৩৯টি)", subtitle = "ক থেকে হ পর্যন্ত সুন্দর বর্ণমালা ও ছবি")
                            }
                            val chunked = banglaConsonants.chunked(2)
                            items(chunked) { pair ->
                                GenericLetterPairRow(
                                    pair = pair,
                                    themeColor = BengaliColor,
                                    onClick = { item ->
                                        selectedLetterDetail = item
                                        SoundManagerService.playCorrect(context)
                                    }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                        MediumLessonCategory.NUMBERS_COUNTING -> {
                            item {
                                SectionHeader(title = "বাংলা সংখ্যা গণনা (১ থেকে ১০)", subtitle = "ছবি দেখে গুণে গুণে বাংলা সংখ্যা শিখি")
                            }
                            items(banglaNumbers) { numItem ->
                                GenericNumberRow(numItem = numItem, themeColor = BengaliColor)
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                        MediumLessonCategory.RHYMES_STORIES -> {
                            item {
                                SectionHeader(title = "মজার বাংলা ছড়া ও কবিতা", subtitle = "ছড়া পড়ো, আবৃত্তি করো ও আনন্দ নাও")
                            }
                            items(banglaRhymes) { rhyme ->
                                GenericRhymeCard(rhyme = rhyme, themeColor = BengaliColor)
                                Spacer(modifier = Modifier.height(14.dp))
                            }
                        }
                        else -> {}
                    }
                }

                CurriculumMedium.HINDI -> {
                    val hindiColor = Color(0xFFEA580C)
                    when (selectedCategory) {
                        MediumLessonCategory.ALPHABET_VOWELS -> {
                            item {
                                SectionHeader(title = "हिंदी स्वर (Vowels)", subtitle = "अ आ इ ई उ ऊ ऋ ए ऐ ओ औ अं — बोलो और सीखो")
                            }
                            val chunked = hindiVowels.chunked(2)
                            items(chunked) { pair ->
                                GenericLetterPairRow(
                                    pair = pair,
                                    themeColor = hindiColor,
                                    onClick = { item ->
                                        selectedLetterDetail = item
                                        SoundManagerService.playCorrect(context)
                                    }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                        MediumLessonCategory.ALPHABET_CONSONANTS -> {
                            item {
                                SectionHeader(title = "हिंदी व्यंजन (Consonants)", subtitle = "क से ह तक सुंदर वर्णमाला और आकर्षक चित्र")
                            }
                            val chunked = hindiConsonants.chunked(2)
                            items(chunked) { pair ->
                                GenericLetterPairRow(
                                    pair = pair,
                                    themeColor = hindiColor,
                                    onClick = { item ->
                                        selectedLetterDetail = item
                                        SoundManagerService.playCorrect(context)
                                    }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                        MediumLessonCategory.NUMBERS_COUNTING -> {
                            item {
                                SectionHeader(title = "हिंदी गिनती (१ से १० तक)", subtitle = "चित्र देखकर गिनें और हिंदी अंक सीखें")
                            }
                            items(hindiNumbers) { numItem ->
                                GenericNumberRow(numItem = numItem, themeColor = hindiColor)
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                        MediumLessonCategory.RHYMES_STORIES -> {
                            item {
                                SectionHeader(title = "प्यारे बालगीत व कविताएँ (Hindi Rhymes)", subtitle = "मछली जल की रानी है, तितली उड़ी, चंदा मामा")
                            }
                            items(hindiRhymes) { rhyme ->
                                GenericRhymeCard(rhyme = rhyme, themeColor = hindiColor)
                                Spacer(modifier = Modifier.height(14.dp))
                            }
                        }
                        else -> {}
                    }
                }

                CurriculumMedium.CBSE_ENGLISH -> {
                    when (selectedCategory) {
                        MediumLessonCategory.ALPHABET_VOWELS -> {
                            item {
                                SectionHeader(title = "Phonics & Word Discovery (CBSE)", subtitle = "Letter sounds, CVC blends, and vocabulary building")
                            }
                            val chunked = cbseEnglishLetters.chunked(2)
                            items(chunked) { pair ->
                                GenericLetterPairRow(
                                    pair = pair,
                                    themeColor = PrimaryBlue,
                                    onClick = { item ->
                                        selectedLetterDetail = item
                                        SoundManagerService.playCorrect(context)
                                    }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                        MediumLessonCategory.NUMBERS_COUNTING -> {
                            item {
                                SectionHeader(
                                    title = "CBSE Numbers & Counting (1 to 10)",
                                    subtitle = "Cardinal numbers, spelling, phonetic pronunciation & visual counters"
                                )
                            }
                            items(cbseNumbers) { numItem ->
                                GenericNumberRow(numItem = numItem, themeColor = PrimaryBlue)
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                        MediumLessonCategory.STEM_VOCABULARY -> {
                            item {
                                SectionHeader(title = "CBSE STEM & Science Lab", subtitle = "Place values, plant anatomy, matter & shapes")
                            }
                            items(cbseConcepts) { concept ->
                                CbseConceptCardView(concept = concept)
                                Spacer(modifier = Modifier.height(14.dp))
                            }
                        }
                        MediumLessonCategory.RHYMES_STORIES -> {
                            item {
                                SectionHeader(title = "Nursery Rhymes & Moral Stories", subtitle = "Classic rhymes, rhythm, phonemic awareness & Aesop tales")
                            }
                            items(cbseRhymes) { rhyme ->
                                GenericRhymeCard(rhyme = rhyme, themeColor = PrimaryBlue)
                                Spacer(modifier = Modifier.height(14.dp))
                            }
                        }
                        else -> {}
                    }
                }
            }

            // Bottom Lesson Completion Card
            item {
                Spacer(modifier = Modifier.height(20.dp))

                val currentThemeColor = when (selectedMedium) {
                    CurriculumMedium.BANGLA -> BengaliColor
                    CurriculumMedium.HINDI -> Color(0xFFEA580C)
                    CurriculumMedium.CBSE_ENGLISH -> PrimaryBlue
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("complete_medium_lesson_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Lesson Finished? Claim Reward! 🏆",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Complete this ${selectedMedium.label} module to earn 15 shiny stars!",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                SoundManagerService.playBadgeUnlock(context)
                                lessonCompletedNotice = true
                                showLessonConfetti = true
                                onCompleteLesson(15)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = currentThemeColor),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("medium_lesson_complete_button")
                        ) {
                            Text(text = "⭐ Complete Lesson (+15 Stars)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        if (lessonCompletedNotice) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "🎉 Super Job! 15 stars added to your ${selectedMedium.label} progress!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = currentThemeColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }

    // Letter / Concept Detail Dialog
    selectedLetterDetail?.let { item ->
        Dialog(onDismissRequest = { selectedLetterDetail = null }) {
            val currentThemeColor = when (selectedMedium) {
                CurriculumMedium.BANGLA -> BengaliColor
                CurriculumMedium.HINDI -> Color(0xFFEA580C)
                CurriculumMedium.CBSE_ENGLISH -> PrimaryBlue
            }

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("medium_letter_detail_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { selectedLetterDetail = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(currentThemeColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.letter,
                            fontSize = 60.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = currentThemeColor
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Pronounced: '${item.pronunciation}'",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = item.emoji, fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${item.letter} : ${item.exampleWord}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "English: ${item.englishMeaning}",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )

                            if (item.exampleSentence.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = item.exampleSentence,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = currentThemeColor,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LessonSpeechButton(
                        textToSpeak = if (item.exampleSentence.isNotEmpty()) "${item.letter}. ${item.exampleWord}. ${item.exampleSentence}. English: ${item.englishMeaning}" else "${item.letter}. ${item.exampleWord}. English: ${item.englishMeaning}",
                        speechManager = speechManager,
                        medium = selectedMedium,
                        itemId = "letter_dialog_${item.letter}",
                        buttonLabel = "Listen Aloud 🔊",
                        themeColor = currentThemeColor,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            speechManager.stop()
                            SoundManagerService.playCorrect(context)
                            selectedLetterDetail = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = currentThemeColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Got it! (Done)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    ConfettiCelebrationOverlay(
        isVisible = showLessonConfetti,
        title = "LESSON COMPLETED! 🎓",
        subtitle = "Superb job! You completed all the learning modules in ${selectedMedium.label}!",
        bonusText = "+15 Learning Stars Awarded! ⭐",
        badgeEmoji = "🎉",
        onDismiss = { showLessonConfetti = false }
    )
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun GenericLetterPairRow(
    pair: List<GenericLetterCard>,
    themeColor: Color,
    onClick: (GenericLetterCard) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        pair.forEach { item ->
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onClick(item) }
                    .testTag("letter_card_${item.letter}"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(themeColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.letter,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = themeColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = item.emoji, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.exampleWord,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Text(
                        text = item.englishMeaning,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        maxLines = 1
                    )
                }
            }
        }
        if (pair.size == 1) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun GenericNumberRow(
    numItem: GenericNumberCard,
    themeColor: Color
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { SoundManagerService.playCorrect(context) }
            .testTag("number_card_${numItem.digitNative}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(themeColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = numItem.digitNative,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = themeColor
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "${numItem.wordNative} (${numItem.pronunciation})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "English numeral: ${numItem.digitEnglish}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Visual counters
            Row {
                val displayCount = numItem.count.coerceAtMost(5)
                repeat(displayCount) {
                    Text(text = numItem.counterEmoji, fontSize = 16.sp)
                }
                if (numItem.count > 5) {
                    Text(text = " +${numItem.count - 5}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = themeColor)
                }
            }
        }
    }
}

@Composable
private fun GenericRhymeCard(
    rhyme: GenericRhymeStory,
    themeColor: Color
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("rhyme_card_${rhyme.title}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = rhyme.emoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = rhyme.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = rhyme.subtitle,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                IconButton(onClick = { SoundManagerService.playStarPop(context) }) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Rhyme Music",
                        tint = themeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = themeColor.copy(alpha = 0.06f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, themeColor.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    rhyme.lines.forEach { line ->
                        Text(
                            text = line,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "💡 ${rhyme.educationalNote}",
                fontSize = 11.sp,
                color = Color(0xFF475569)
            )
        }
    }
}

@Composable
private fun CbseConceptCardView(concept: CbseConceptCard) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("cbse_card_${concept.topic}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = concept.emoji, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = concept.topic,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = concept.summary,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = Color(0xFFEFF6FF),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    concept.keyPoints.forEach { point ->
                        Text(
                            text = "• $point",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E3A8A),
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "🌟 Fun Fact: ${concept.funFact}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBlue
            )
        }
    }
}
