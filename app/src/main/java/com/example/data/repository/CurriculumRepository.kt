package com.example.data.repository

import com.example.data.db.QuizAttemptEntity
import com.example.data.model.AnimalAvatar
import com.example.data.model.Badge
import com.example.data.model.ClassLevel
import com.example.data.model.DailyMission
import com.example.data.model.DifficultyLevel
import com.example.data.model.LeaderboardEntry
import com.example.data.model.QuizPerformanceDay
import com.example.data.model.QuizQuestion
import com.example.data.model.StudentProfile
import com.example.data.model.SubjectPerformanceMetric
import com.example.data.model.SubjectType
import com.example.data.model.TrophyTier
import com.example.data.model.VirtualTrophy

object CurriculumRepository {

    val animalAvatars = listOf(
        AnimalAvatar("lion", "Leo the Lion", "🦁", 0xFFF59E0B, "Brave & Adventurous"),
        AnimalAvatar("panda", "Pip the Panda", "🐼", 0xFF3B82F6, "Calm & Thoughtful"),
        AnimalAvatar("fox", "Felix the Fox", "🦊", 0xFFEF4444, "Quick & Clever"),
        AnimalAvatar("owl", "Ollie the Owl", "🦉", 0xFF8B5CF6, "Wise & Curious"),
        AnimalAvatar("bunny", "Bella the Bunny", "🐰", 0xFFEC4899, "Energetic & Joyful"),
        AnimalAvatar("bear", "Barnaby Bear", "🐻", 0xFF854D0E, "Strong & Helpful"),
        AnimalAvatar("monkey", "Milo the Monkey", "🐵", 0xFF10B981, "Playful & Creative"),
        AnimalAvatar("penguin", "Penny Penguin", "🐧", 0xFF0284C7, "Cool & Focused")
    )

    fun getAvatarById(id: String): AnimalAvatar {
        return animalAvatars.find { it.id == id } ?: animalAvatars.first()
    }

    fun getAllBadges(profile: StudentProfile): List<Badge> {
        val unlocked = profile.unlockedBadgeIds
        return listOf(
            Badge(
                id = "math_whiz",
                title = "Math Whiz",
                iconEmoji = "🧮",
                description = "Solved tricky arithmetic with ease!",
                requirementText = "Earn 50+ stars in Mathematics",
                isUnlocked = unlocked.contains("math_whiz") || profile.mathStars >= 50,
                currentProgress = profile.mathStars.coerceAtMost(50),
                maxProgress = 50
            ),
            Badge(
                id = "early_bird",
                title = "Early Bird",
                iconEmoji = "🌅",
                description = "Consistent morning learner shining bright!",
                requirementText = "Achieve a consecutive 3-day study streak",
                isUnlocked = unlocked.contains("early_bird") || profile.dailyStreak >= 3,
                currentProgress = profile.dailyStreak.coerceAtMost(3),
                maxProgress = 3
            ),
            Badge(
                id = "science_star",
                title = "Science Explorer",
                iconEmoji = "🔬",
                description = "Discovered the wonders of living things & space!",
                requirementText = "Earn 40+ stars in Science",
                isUnlocked = unlocked.contains("science_star") || profile.scienceStars >= 40,
                currentProgress = profile.scienceStars.coerceAtMost(40),
                maxProgress = 40
            ),
            Badge(
                id = "word_master",
                title = "Word Wizard",
                iconEmoji = "📚",
                description = "Mastered phonics, vocabulary and sentence skills!",
                requirementText = "Earn 40+ stars in English",
                isUnlocked = unlocked.contains("word_master") || profile.englishStars >= 40,
                currentProgress = profile.englishStars.coerceAtMost(40),
                maxProgress = 40
            ),
            Badge(
                id = "bengali_star",
                title = "বাংলা প্রতিভা (Bengali Whiz)",
                iconEmoji = "🇧🇩",
                description = "Mastered Bengali vowels, consonants, and vocabulary words!",
                requirementText = "Earn 40+ stars in Bengali",
                isUnlocked = unlocked.contains("bengali_star") || profile.bengaliStars >= 40,
                currentProgress = profile.bengaliStars.coerceAtMost(40),
                maxProgress = 40
            ),
            Badge(
                id = "hindi_star",
                title = "हिंदी रत्न (Hindi Star)",
                iconEmoji = "🇮🇳",
                description = "Mastered Hindi vowels, consonants, and vocabulary words!",
                requirementText = "Earn 40+ stars in Hindi Medium",
                isUnlocked = unlocked.contains("hindi_star") || profile.hindiStars >= 40,
                currentProgress = profile.hindiStars.coerceAtMost(40),
                maxProgress = 40
            ),
            Badge(
                id = "cbse_champion",
                title = "CBSE Scholar",
                iconEmoji = "🌍",
                description = "Excelled in CBSE English, Math and Science syllabus!",
                requirementText = "Earn 50+ stars in CBSE curriculum",
                isUnlocked = unlocked.contains("cbse_champion") || (profile.mathStars + profile.englishStars >= 50),
                currentProgress = (profile.mathStars + profile.englishStars).coerceAtMost(50),
                maxProgress = 50
            ),
            Badge(
                id = "streak_7",
                title = "Streak Flame Master",
                iconEmoji = "🔥",
                description = "Unstoppable dedication 7 days in a row!",
                requirementText = "Maintain a 7-day study streak",
                isUnlocked = unlocked.contains("streak_7") || profile.dailyStreak >= 7,
                currentProgress = profile.dailyStreak.coerceAtMost(7),
                maxProgress = 7
            ),
            Badge(
                id = "star_collector",
                title = "Star Collector",
                iconEmoji = "⭐",
                description = "A sky full of sparkling achievement stars!",
                requirementText = "Accumulate 100 total stars",
                isUnlocked = unlocked.contains("star_collector") || profile.totalStars >= 100,
                currentProgress = profile.totalStars.coerceAtMost(100),
                maxProgress = 100
            ),
            Badge(
                id = "quiz_champion",
                title = "Quiz Champion",
                iconEmoji = "🏆",
                description = "Completed multiple quiz challenges with high scores!",
                requirementText = "Complete 10 quizzes",
                isUnlocked = unlocked.contains("quiz_champion") || profile.quizzesCompleted >= 10,
                currentProgress = profile.quizzesCompleted.coerceAtMost(10),
                maxProgress = 10
            ),
            Badge(
                id = "galaxy_hero",
                title = "Galaxy Hero",
                iconEmoji = "🚀",
                description = "Reached the elite learning rank of the constellation!",
                requirementText = "Accumulate 250 total stars",
                isUnlocked = unlocked.contains("galaxy_hero") || profile.totalStars >= 250,
                currentProgress = profile.totalStars.coerceAtMost(250),
                maxProgress = 250
            )
        )
    }

    val quizQuestions = listOf(
        // === CLASS 1: MATH ===
        QuizQuestion(
            id = "m1_1",
            subject = SubjectType.MATH,
            classLevel = ClassLevel.CLASS_1,
            questionText = "Count the apples: 🍎🍎🍎 + 🍎🍎 = ?",
            options = listOf("4 apples", "5 apples", "6 apples", "3 apples"),
            correctAnswerIndex = 1,
            explanation = "3 apples plus 2 apples equals 5 apples in total! Great counting!",
            hint = "Count one by one: 1, 2, 3... and two more!"
        ),
        QuizQuestion(
            id = "m1_2",
            subject = SubjectType.MATH,
            classLevel = ClassLevel.CLASS_1,
            questionText = "Which shape has 3 sides and 3 corners?",
            options = listOf("Circle", "Square", "Triangle", "Rectangle"),
            correctAnswerIndex = 2,
            explanation = "A triangle always has 3 straight sides and 3 sharp corners!",
            hint = "Think of a slice of pizza 🍕!"
        ),
        QuizQuestion(
            id = "m1_3",
            subject = SubjectType.MATH,
            classLevel = ClassLevel.CLASS_1,
            questionText = "What number comes right after 8?",
            options = listOf("7", "9", "10", "6"),
            correctAnswerIndex = 1,
            explanation = "When counting upward: 7, 8, 9! Nine comes after 8.",
            hint = "Count up from 6: 6, 7, 8... what is next?"
        ),

        // === CLASS 1: ENGLISH ===
        QuizQuestion(
            id = "e1_1",
            subject = SubjectType.ENGLISH,
            classLevel = ClassLevel.CLASS_1,
            questionText = "Which word rhymes with 'CAT'?",
            options = listOf("Dog", "Hat", "Sun", "Cup"),
            correctAnswerIndex = 1,
            explanation = "'Cat' and 'Hat' end with the exact same '-at' sound!",
            hint = "Listen to the ending sound: C-AT... H-..."
        ),
        QuizQuestion(
            id = "e1_2",
            subject = SubjectType.ENGLISH,
            classLevel = ClassLevel.CLASS_1,
            questionText = "What is the missing vowel: S _ N (in the sky)?",
            options = listOf("A", "E", "U", "O"),
            correctAnswerIndex = 2,
            explanation = "The word is SUN! S-U-N spells the bright sun in the sky ☀️",
            hint = "It makes the 'uh' sound like in 'fun' and 'run'!"
        ),

        // === CLASS 1: SCIENCE ===
        QuizQuestion(
            id = "s1_1",
            subject = SubjectType.SCIENCE,
            classLevel = ClassLevel.CLASS_1,
            questionText = "Which body part do you use to SMELL a flower?",
            options = listOf("Eyes", "Ears", "Nose", "Tongue"),
            correctAnswerIndex = 2,
            explanation = "We smell sweet perfumes and flowers using our nose 👃!",
            hint = "It sits right in the middle of your face!"
        ),
        QuizQuestion(
            id = "s1_2",
            subject = SubjectType.SCIENCE,
            classLevel = ClassLevel.CLASS_1,
            questionText = "Which of these is a LIVING thing?",
            options = listOf("A puppy 🐶", "A plastic toy car 🚗", "A rock 🪨", "A pencil ✏️"),
            correctAnswerIndex = 0,
            explanation = "A puppy breathes, eats food, and grows, so it is alive!",
            hint = "Living things need food, water, and grow big!"
        ),

        // === CLASS 2: MATH ===
        QuizQuestion(
            id = "m2_1",
            subject = SubjectType.MATH,
            classLevel = ClassLevel.CLASS_2,
            questionText = "What is 15 + 23?",
            options = listOf("35", "38", "37", "40"),
            correctAnswerIndex = 1,
            explanation = "10 + 20 = 30, and 5 + 3 = 8. So 30 + 8 = 38!",
            hint = "Add the tens first (10+20), then add the ones (5+3)!"
        ),
        QuizQuestion(
            id = "m2_2",
            subject = SubjectType.MATH,
            classLevel = ClassLevel.CLASS_2,
            questionText = "How many tens are in the number 74?",
            options = listOf("4", "7", "70", "11"),
            correctAnswerIndex = 1,
            explanation = "In 74, the digit 7 is in the tens place (value 70) and 4 is ones!",
            hint = "Look at the first digit on the left!"
        ),
        QuizQuestion(
            id = "m2_3",
            subject = SubjectType.MATH,
            classLevel = ClassLevel.CLASS_2,
            questionText = "What is 50 - 18?",
            options = listOf("32", "38", "22", "42"),
            correctAnswerIndex = 0,
            explanation = "50 minus 10 is 40, minus 8 more is 32! Fantastic math!",
            hint = "Subtract 10 first to get 40, then take away 8."
        ),

        // === CLASS 2: ENGLISH ===
        QuizQuestion(
            id = "e2_1",
            subject = SubjectType.ENGLISH,
            classLevel = ClassLevel.CLASS_2,
            questionText = "What is the OPPOSITE of 'COLD'?",
            options = listOf("Wet", "Hot", "Dark", "Slow"),
            correctAnswerIndex = 1,
            explanation = "The opposite of cold (winter chill ❄️) is HOT (summer sunshine ☀️)!",
            hint = "Think of hot soup or a warm sunny day!"
        ),
        QuizQuestion(
            id = "e2_2",
            subject = SubjectType.ENGLISH,
            classLevel = ClassLevel.CLASS_2,
            questionText = "Which word is a NOUN (name of a person, place, or thing)?",
            options = listOf("Run", "Quickly", "Elephant", "Happy"),
            correctAnswerIndex = 2,
            explanation = "'Elephant' is an animal (a thing/creature), so it is a noun!",
            hint = "A noun is an animal, person, or object you can touch!"
        ),

        // === CLASS 2: SCIENCE ===
        QuizQuestion(
            id = "s2_1",
            subject = SubjectType.SCIENCE,
            classLevel = ClassLevel.CLASS_2,
            questionText = "What do green plants need to make their own food?",
            options = listOf("Milk and honey", "Sunlight, water and air", "Sand and rocks", "Darkness and ice"),
            correctAnswerIndex = 1,
            explanation = "Plants use sunlight, water, and air (photosynthesis) to create food!",
            hint = "Think about why plants need water and to be placed near windows!"
        ),
        QuizQuestion(
            id = "s2_2",
            subject = SubjectType.SCIENCE,
            classLevel = ClassLevel.CLASS_2,
            questionText = "Where do fish live and breathe?",
            options = listOf("In tree tops", "In water using gills", "In dry deserts", "In underground caves"),
            correctAnswerIndex = 1,
            explanation = "Fish have special gills that allow them to absorb oxygen from water 🐠",
            hint = "Their habitat has waves, rivers, and coral reefs!"
        ),

        // === CLASS 3: MATH ===
        QuizQuestion(
            id = "m3_1",
            subject = SubjectType.MATH,
            classLevel = ClassLevel.CLASS_3,
            questionText = "What is 7 × 6?",
            options = listOf("40", "42", "48", "36"),
            correctAnswerIndex = 1,
            explanation = "7 times 6 is 42! Excellent times table recall!",
            hint = "Think: 7 × 5 = 35. Add 7 more!"
        ),
        QuizQuestion(
            id = "m3_2",
            subject = SubjectType.MATH,
            classLevel = ClassLevel.CLASS_3,
            questionText = "If a clock's big hand points to 6 and small hand is between 3 and 4, what time is it?",
            options = listOf("3:00", "3:30", "4:30", "6:15"),
            correctAnswerIndex = 1,
            explanation = "When the minute hand points to 6, it represents half past the hour: 3:30!",
            hint = "The minute hand at 6 means 30 minutes past."
        ),

        // === CLASS 3: ENGLISH ===
        QuizQuestion(
            id = "e3_1",
            subject = SubjectType.ENGLISH,
            classLevel = ClassLevel.CLASS_3,
            questionText = "Choose the correct ADJECTIVE in: 'The brave knight climbed the tall tower.'",
            options = listOf("Tower", "Climbed", "Brave", "The"),
            correctAnswerIndex = 2,
            explanation = "'Brave' describes what kind of knight he was, making it an adjective!",
            hint = "An adjective describes or gives detail about a noun."
        ),

        // === CLASS 3: SCIENCE ===
        QuizQuestion(
            id = "s3_1",
            subject = SubjectType.SCIENCE,
            classLevel = ClassLevel.CLASS_3,
            questionText = "Water turning into steam/vapor when boiled is called:",
            options = listOf("Freezing", "Evaporation", "Melting", "Precipitation"),
            correctAnswerIndex = 1,
            explanation = "Liquid water heating up into gas is called evaporation!",
            hint = "Like water evaporating under the hot sun!"
        ),

        // === CLASS 4: MATH ===
        QuizQuestion(
            id = "m4_1",
            subject = SubjectType.MATH,
            classLevel = ClassLevel.CLASS_4,
            questionText = "What is 144 ÷ 12?",
            options = listOf("10", "12", "14", "16"),
            correctAnswerIndex = 1,
            explanation = "12 multiplied by 12 equals 144, so 144 ÷ 12 = 12!",
            hint = "Think what number multiplied by itself gives 144!"
        ),
        QuizQuestion(
            id = "m4_2",
            subject = SubjectType.MATH,
            classLevel = ClassLevel.CLASS_4,
            questionText = "Which fraction is equivalent to 1/2?",
            options = listOf("2/5", "3/6", "4/10", "1/4"),
            correctAnswerIndex = 1,
            explanation = "3/6 simplifies to 1/2 because 3 is half of 6!",
            hint = "Divide both numerator and denominator by 3."
        ),

        // === CLASS 4: ENGLISH ===
        QuizQuestion(
            id = "e4_1",
            subject = SubjectType.ENGLISH,
            classLevel = ClassLevel.CLASS_4,
            questionText = "What is a SYNONYM (word with similar meaning) for 'ENORMOUS'?",
            options = listOf("Tiny", "Gigantic", "Quiet", "Ancient"),
            correctAnswerIndex = 1,
            explanation = "'Gigantic' and 'enormous' both mean extremely large or huge!",
            hint = "Think of something giant, like a mountain!"
        ),

        // === CLASS 4: SCIENCE ===
        QuizQuestion(
            id = "s4_1",
            subject = SubjectType.SCIENCE,
            classLevel = ClassLevel.CLASS_4,
            questionText = "Which planet is known as the 'Red Planet' in our Solar System?",
            options = listOf("Venus", "Mars", "Jupiter", "Saturn"),
            correctAnswerIndex = 1,
            explanation = "Mars looks reddish in the night sky due to iron oxide (rust) on its surface!",
            hint = "It is the 4th planet from the Sun."
        ),

        // === CLASS 1: BENGALI (বাংলা প্রথম শ্রেণি) ===
        QuizQuestion(
            id = "b1_1",
            subject = SubjectType.BENGALI,
            classLevel = ClassLevel.CLASS_1,
            questionText = "কোনটি বাংলা স্বরবর্ণ?",
            options = listOf("ক", "আ", "গ", "ঘ"),
            correctAnswerIndex = 1,
            explanation = "'আ' হলো একটি স্বরবর্ণ! বাংলায় মোট ১১টি স্বরবর্ণ রয়েছে।",
            hint = "অ, আ, ই, ঈ এর মধ্যে আছে!",
            difficulty = DifficultyLevel.EASY
        ),
        QuizQuestion(
            id = "b1_2",
            subject = SubjectType.BENGALI,
            classLevel = ClassLevel.CLASS_1,
            questionText = "ছবিটি দেখে বলো, এটি কোন সুস্বাদু ফল? 🥭",
            options = listOf("আম", "জাম", "লিচু", "কলা"),
            correctAnswerIndex = 0,
            explanation = "এটি মিষ্টি পাকা আম (Mango)! আ-তে আম।",
            hint = "এটি ফলের রাজা!",
            difficulty = DifficultyLevel.EASY,
            imageEmoji = "🥭"
        ),
        QuizQuestion(
            id = "b1_3",
            subject = SubjectType.BENGALI,
            classLevel = ClassLevel.CLASS_1,
            questionText = "বাংলা বর্ণমালার প্রথম স্বরবর্ণ কোনটি?",
            options = listOf("আ", "অ", "ই", "ক"),
            correctAnswerIndex = 1,
            explanation = "'অ' হলো বাংলা স্বরবর্ণের একেবারে প্রথম বর্ণ।",
            hint = "অ-তে অজগর আসে তেড়ে!",
            difficulty = DifficultyLevel.MEDIUM
        ),
        QuizQuestion(
            id = "b1_4",
            subject = SubjectType.BENGALI,
            classLevel = ClassLevel.CLASS_1,
            questionText = "ছবি দেখে পশুটি চিনে নাও: 🐘",
            options = listOf("বাঘ", "সিংহ", "হাতি", "ঘোড়া"),
            correctAnswerIndex = 2,
            explanation = "এটি হলো বড় কানের হাতি (Elephant)! হ-তে হাতি।",
            hint = "এর একটি লম্বা শুঁড় আছে!",
            difficulty = DifficultyLevel.EASY,
            imageEmoji = "🐘"
        ),

        // === CLASS 2: BENGALI (বাংলা দ্বিতীয় শ্রেণি) ===
        QuizQuestion(
            id = "b2_1",
            subject = SubjectType.BENGALI,
            classLevel = ClassLevel.CLASS_2,
            questionText = "কোনটি বাংলা ব্যঞ্জনবর্ণ?",
            options = listOf("ই", "উ", "ক", "ঋ"),
            correctAnswerIndex = 2,
            explanation = "'ক' হলো বাংলা ভাষার প্রথম ব্যঞ্জনবর্ণ!",
            hint = "ক-তে কলম, ক-তে কাক!",
            difficulty = DifficultyLevel.EASY
        ),
        QuizQuestion(
            id = "b2_2",
            subject = SubjectType.BENGALI,
            classLevel = ClassLevel.CLASS_2,
            questionText = "'ছোট' শব্দের বিপরীত শব্দ কোনটি?",
            options = listOf("বড়", "লম্বা", "খাটো", "ধনী"),
            correctAnswerIndex = 0,
            explanation = "ছোট এর বিপরীত হলো বড়!",
            hint = "হাতি অনেক...?",
            difficulty = DifficultyLevel.EASY
        ),
        QuizQuestion(
            id = "b2_3",
            subject = SubjectType.BENGALI,
            classLevel = ClassLevel.CLASS_2,
            questionText = "ছবিতে থাকা আমাদের সুন্দর ফুলটি কী? 🪷",
            options = listOf("গোলাপ", "শাপলা", "সূর্যমুখী", "বেলি"),
            correctAnswerIndex = 1,
            explanation = "শাপলা বাংলাদেশের জাতীয় ফুল!",
            hint = "পানিতে ফুটে থাকে!",
            difficulty = DifficultyLevel.MEDIUM,
            imageEmoji = "🪷"
        ),
        QuizQuestion(
            id = "b2_4",
            subject = SubjectType.BENGALI,
            classLevel = ClassLevel.CLASS_2,
            questionText = "কাক ডাকে কীভাবে?",
            options = listOf("মিউ মিউ", "কা কা", "কুহু কুহু", "ঘেউ ঘেউ"),
            correctAnswerIndex = 1,
            explanation = "কাক সকালবেলা কা কা করে ডাকে!",
            hint = "কালো রঙের পাখিটির ডাক!",
            difficulty = DifficultyLevel.EASY
        ),

        // === CLASS 3: BENGALI (বাংলা তৃতীয় শ্রেণি) ===
        QuizQuestion(
            id = "b3_1",
            subject = SubjectType.BENGALI,
            classLevel = ClassLevel.CLASS_3,
            questionText = "'সূর্য' শব্দের সমার্থক শব্দ কোনটি?",
            options = listOf("চাঁদ", "রবি", "মেঘ", "পাহাড়"),
            correctAnswerIndex = 1,
            explanation = "'সূর্য' শব্দের সমার্থক হলো রবি, ভাস্কর, দিনমণি।",
            hint = "রবিবার যার নামে দিনটি শুরু!",
            difficulty = DifficultyLevel.MEDIUM
        ),
        QuizQuestion(
            id = "b3_2",
            subject = SubjectType.BENGALI,
            classLevel = ClassLevel.CLASS_3,
            questionText = "'জল' এর অপর নাম কী?",
            options = listOf("পানি", "বাতাস", "আগুন", "মাটি"),
            correctAnswerIndex = 0,
            explanation = "জল ও পানি একই জিনিস, যার অপর নাম জীবন!",
            hint = "আমরা তৃষ্ণা পেলে যা পান করি!",
            difficulty = DifficultyLevel.EASY
        ),
        QuizQuestion(
            id = "b3_3",
            subject = SubjectType.BENGALI,
            classLevel = ClassLevel.CLASS_3,
            questionText = "'দিন' এর বিপরীত শব্দ কোনটি?",
            options = listOf("রাত", "ভোর", "সন্ধ্যা", "দুপুর"),
            correctAnswerIndex = 0,
            explanation = "দিন এর বিপরীত হলো রাত!",
            hint = "যখন আকাশে চাঁদ ও তারা ওঠে!",
            difficulty = DifficultyLevel.EASY
        ),

        // === CLASS 4: BENGALI (বাংলা চতুর্থ শ্রেণি) ===
        QuizQuestion(
            id = "b4_1",
            subject = SubjectType.BENGALI,
            classLevel = ClassLevel.CLASS_4,
            questionText = "'বন' শব্দের সমার্থক শব্দ কোনটি?",
            options = listOf("নদী", "অরণ্য", "সাগর", "আকাশ"),
            correctAnswerIndex = 1,
            explanation = "বন, অরণ্য ও জঙ্গল একই অর্থ বোঝায়!",
            hint = "যেখানে অনেক বড় বড় গাছপালা থাকে!",
            difficulty = DifficultyLevel.HARD
        ),
        QuizQuestion(
            id = "b4_2",
            subject = SubjectType.BENGALI,
            classLevel = ClassLevel.CLASS_4,
            questionText = "'সকাল' শব্দের বিপরীত শব্দ কোনটি?",
            options = listOf("সন্ধ্যা", "দুপুর", "রাত", "ভোর"),
            correctAnswerIndex = 0,
            explanation = "সকাল এর বিপরীত হলো সন্ধ্যা!",
            hint = "যখন সূর্য ডুবে যায়!",
            difficulty = DifficultyLevel.MEDIUM
        ),
        QuizQuestion(
            id = "b4_3",
            subject = SubjectType.BENGALI,
            classLevel = ClassLevel.CLASS_4,
            questionText = "আমাদের মাতৃভাষার নাম কী?",
            options = listOf("বাংলা", "ইংরেজি", "হিন্দি", "আরবি"),
            correctAnswerIndex = 0,
            explanation = "আমাদের ভালোবাসার মিষ্টি মাতৃভাষা হলো বাংলা 🇧🇩!",
            hint = "যে ভাষায় আমরা মনের ভাব প্রকাশ করি!",
            difficulty = DifficultyLevel.EASY,
            medium = com.example.data.model.CurriculumMedium.BANGLA
        ),

        // === HINDI MEDIUM QUESTIONS ===
        QuizQuestion(
            id = "h1_1",
            subject = SubjectType.BENGALI,
            classLevel = ClassLevel.CLASS_1,
            questionText = "इनमें से कौन सा हिंदी स्वर है? (Hindi Vowel)",
            options = listOf("क", "अ", "ग", "घ"),
            correctAnswerIndex = 1,
            explanation = "'अ' हिंदी वर्णमाला का पहला स्वर है! अ से अनार 🍎",
            hint = "अ से अनार, आ से आम!",
            difficulty = DifficultyLevel.EASY,
            medium = com.example.data.model.CurriculumMedium.HINDI
        ),
        QuizQuestion(
            id = "h1_2",
            subject = SubjectType.BENGALI,
            classLevel = ClassLevel.CLASS_1,
            questionText = "चित्र देखकर बताओ यह कौन सा फल है? 🥭",
            options = listOf("आम (Mango)", "सेब (Apple)", "केला (Banana)", "अंगूर (Grapes)"),
            correctAnswerIndex = 0,
            explanation = "यह रसीला मीठा आम (Mango) है! आ से आम।",
            hint = "इसे फलों का राजा कहते हैं!",
            difficulty = DifficultyLevel.EASY,
            medium = com.example.data.model.CurriculumMedium.HINDI,
            imageEmoji = "🥭"
        ),
        QuizQuestion(
            id = "h2_1",
            subject = SubjectType.BENGALI,
            classLevel = ClassLevel.CLASS_2,
            questionText = "'मछली जल की रानी है' कविता में मछली का जीवन क्या है?",
            options = listOf("हवा", "पानी (जल)", "आग", "मिट्टी"),
            correctAnswerIndex = 1,
            explanation = "'मछली जल की रानी है, जीवन उसका पानी है!' 🐟",
            hint = "मछली किसमें तैरती है?",
            difficulty = DifficultyLevel.EASY,
            medium = com.example.data.model.CurriculumMedium.HINDI
        ),
        QuizQuestion(
            id = "h2_2",
            subject = SubjectType.MATH,
            classLevel = ClassLevel.CLASS_2,
            questionText = "हिंदी अंकों में २ + ३ का मान क्या होगा?",
            options = listOf("४ (चार)", "५ (पाँच)", "६ (छह)", "७ (सात)"),
            correctAnswerIndex = 1,
            explanation = "२ + ३ = ५ (पाँच)!",
            hint = "दो और तीन को मिलाएँ!",
            difficulty = DifficultyLevel.EASY,
            medium = com.example.data.model.CurriculumMedium.HINDI
        ),
        QuizQuestion(
            id = "h3_1",
            subject = SubjectType.BENGALI,
            classLevel = ClassLevel.CLASS_3,
            questionText = "'दिन' शब्द का सही विलोम (Opposite) शब्द क्या है?",
            options = listOf("रात", "सुबह", "शाम", "दोपहर"),
            correctAnswerIndex = 0,
            explanation = "दिन का विलोम शब्द 'रात' होता है! ☀️ ⟷ 🌙",
            hint = "जब आकाश में चाँद निकलता है!",
            difficulty = DifficultyLevel.EASY,
            medium = com.example.data.model.CurriculumMedium.HINDI
        ),
        QuizQuestion(
            id = "h4_1",
            subject = SubjectType.BENGALI,
            classLevel = ClassLevel.CLASS_4,
            questionText = "'सूर्य' का पर्यायवाची (Synonym) शब्द क्या है?",
            options = listOf("रवि", "चंदा", "तारा", "बादल"),
            correctAnswerIndex = 0,
            explanation = "'रवि', 'दिनकर' और 'भास्कर' सूर्य के पर्यायवाची शब्द हैं! ☀️",
            hint = "सवेरे पूर्व दिशा से उगता है!",
            difficulty = DifficultyLevel.MEDIUM,
            medium = com.example.data.model.CurriculumMedium.HINDI
        ),

        // === BANGLA MEDIUM (MATH & SCIENCE) ===
        QuizQuestion(
            id = "bm1_1",
            subject = SubjectType.MATH,
            classLevel = ClassLevel.CLASS_1,
            questionText = "বাংলা সংখ্যায় ৫ এর পর কোন সংখ্যাটি আসে?",
            options = listOf("৪", "৬ (ছয়)", "৭", "৩"),
            correctAnswerIndex = 1,
            explanation = "৫ এর পরের সংখ্যা হলো ৬ (ছয়)!",
            hint = "১, ২, ৩, ৪, ৫, ...?",
            difficulty = DifficultyLevel.EASY,
            medium = com.example.data.model.CurriculumMedium.BANGLA
        ),
        QuizQuestion(
            id = "bm2_1",
            subject = SubjectType.MATH,
            classLevel = ClassLevel.CLASS_2,
            questionText = "বাংলা সংখ্যায় ৩ + ৪ = কত?",
            options = listOf("৬", "৭ (সাত)", "৮", "৯"),
            correctAnswerIndex = 1,
            explanation = "৩ ও ৪ যোগ করলে ৭ (সাত) হয়!",
            hint = "তিন আর চার যোগ করো!",
            difficulty = DifficultyLevel.EASY,
            medium = com.example.data.model.CurriculumMedium.BANGLA
        ),
        QuizQuestion(
            id = "bs1_1",
            subject = SubjectType.SCIENCE,
            classLevel = ClassLevel.CLASS_1,
            questionText = "আমরা কোন অঙ্গের সাহায্যে চারপাশের সুন্দর দৃশ্য দেখি?",
            options = listOf("চোখ", "কান", "নাক", "জিভ"),
            correctAnswerIndex = 0,
            explanation = "চোখ হলো আমাদের দেখার পঞ্চইন্দ্রিয়!",
            hint = "যা দিয়ে তুমি বই পড়ো!",
            difficulty = DifficultyLevel.EASY,
            medium = com.example.data.model.CurriculumMedium.BANGLA
        ),
        QuizQuestion(
            id = "bs2_1",
            subject = SubjectType.SCIENCE,
            classLevel = ClassLevel.CLASS_2,
            questionText = "গাছের কোন অংশ মাটির নিচ থেকে পানি ও খনিজ টেনে নেয়?",
            options = listOf("পাতা", "মূল বা শিকড়", "ফুল", "শাখা"),
            correctAnswerIndex = 1,
            explanation = "মূল বা শিকড় গাছকে মাটিতে শক্ত করে ধরে রাখে এবং পানি টানে!",
            hint = "যা মাটির গভীরে থাকে!",
            difficulty = DifficultyLevel.EASY,
            medium = com.example.data.model.CurriculumMedium.BANGLA
        ),

        // === HINDI MEDIUM (SCIENCE & MATH & ENGLISH) ===
        QuizQuestion(
            id = "hm1_1",
            subject = SubjectType.MATH,
            classLevel = ClassLevel.CLASS_1,
            questionText = "हिंदी अंकों में ४ के बाद कौन सा अंक आता है?",
            options = listOf("३", "५ (पाँच)", "६", "२"),
            correctAnswerIndex = 1,
            explanation = "४ के बाद ५ (पाँच) आता है!",
            hint = "१, २, ३, ४, ...?",
            difficulty = DifficultyLevel.EASY,
            medium = com.example.data.model.CurriculumMedium.HINDI
        ),
        QuizQuestion(
            id = "hs1_1",
            subject = SubjectType.SCIENCE,
            classLevel = ClassLevel.CLASS_1,
            questionText = "सूंघने (Smell) के लिए हम शरीर के किस अंग का उपयोग करते हैं?",
            options = listOf("आँख", "नाक", "कान", "जीभ"),
            correctAnswerIndex = 1,
            explanation = "नाक से हम खुशबू और दुर्गंध सूंघते हैं! 👃",
            hint = "जिससे हम फूलों की खुशबू लेते हैं!",
            difficulty = DifficultyLevel.EASY,
            medium = com.example.data.model.CurriculumMedium.HINDI
        ),
        QuizQuestion(
            id = "hs2_1",
            subject = SubjectType.SCIENCE,
            classLevel = ClassLevel.CLASS_2,
            questionText = "हरे पेड़-पौधे अपना भोजन बनाने के लिए किसका उपयोग करते हैं?",
            options = listOf("धूप (सूर्य प्रकाश)", "अंधेरा", "बर्फ", "धुआँ"),
            correctAnswerIndex = 0,
            explanation = "पौधे धूप और जल से प्रकाश संश्लेषण करते हैं! ☀️🌱",
            hint = "सवेरे सूरज से मिलती है!",
            difficulty = DifficultyLevel.EASY,
            medium = com.example.data.model.CurriculumMedium.HINDI
        ),
        QuizQuestion(
            id = "he1_1",
            subject = SubjectType.ENGLISH,
            classLevel = ClassLevel.CLASS_1,
            questionText = "'Apple' का हिंदी अर्थ क्या होता है? 🍎",
            options = listOf("आम", "केला", "सेब", "अंगूर"),
            correctAnswerIndex = 2,
            explanation = "Apple मतलब मीठा लाल सेब! 🍎",
            hint = "लाल रंग का फल!",
            difficulty = DifficultyLevel.EASY,
            medium = com.example.data.model.CurriculumMedium.HINDI,
            imageEmoji = "🍎"
        )
    )

    fun getQuestions(
        subject: SubjectType,
        classLevel: ClassLevel,
        difficulty: DifficultyLevel = DifficultyLevel.EASY,
        medium: com.example.data.model.CurriculumMedium = com.example.data.model.CurriculumMedium.CBSE_ENGLISH
    ): List<QuizQuestion> {
        val mediumMatches = quizQuestions.filter { it.subject == subject && it.classLevel == classLevel && it.medium == medium }
        if (mediumMatches.isNotEmpty()) {
            val diff = mediumMatches.filter { it.difficulty == difficulty }
            return if (diff.isNotEmpty()) diff else mediumMatches
        }
        val subjectMediumMatches = quizQuestions.filter { it.subject == subject && it.medium == medium }
        if (subjectMediumMatches.isNotEmpty()) {
            return subjectMediumMatches
        }
        val classMatches = quizQuestions.filter { it.subject == subject && it.classLevel == classLevel }
        val pool = if (classMatches.isNotEmpty()) classMatches else quizQuestions.filter { it.subject == subject }
        val difficultyMatches = pool.filter { it.difficulty == difficulty }
        return if (difficultyMatches.isNotEmpty()) difficultyMatches else pool
    }

    fun getDefaultDailyMissions(): List<DailyMission> {
        return listOf(
            DailyMission(
                id = "mission_quiz",
                title = "Finish One Quiz",
                description = "Complete any Math, English, or Science quiz today",
                iconEmoji = "📝",
                targetCount = 1,
                currentCount = 0,
                rewardStars = 25
            ),
            DailyMission(
                id = "mission_review",
                title = "Review Yesterday's Lesson",
                description = "Revisit a completed lesson or practice questions",
                iconEmoji = "📖",
                targetCount = 1,
                currentCount = 0,
                rewardStars = 20
            ),
            DailyMission(
                id = "mission_perfect",
                title = "Earn 30+ Stars",
                description = "Solve questions accurately to collect daily stars",
                iconEmoji = "⭐",
                targetCount = 30,
                currentCount = 0,
                rewardStars = 35
            )
        )
    }

    fun getLeaderboard(currentProfile: StudentProfile): List<LeaderboardEntry> {
        val baseList = listOf(
            LeaderboardEntry(1, "Aarav Sharma", "🦁", ClassLevel.CLASS_3, 310, 14),
            LeaderboardEntry(2, "Maya Chen", "🦊", ClassLevel.CLASS_2, 280, 12),
            LeaderboardEntry(3, "Liam Miller", "🦉", ClassLevel.CLASS_4, 255, 9),
            LeaderboardEntry(4, "Zoe Patel", "🐼", ClassLevel.CLASS_2, 220, 8),
            LeaderboardEntry(5, "Ethan Walker", "🐰", ClassLevel.CLASS_1, 195, 6),
            LeaderboardEntry(6, "Sofia Ramirez", "🐻", ClassLevel.CLASS_3, 170, 5),
            LeaderboardEntry(7, "Noah Taylor", "🐵", ClassLevel.CLASS_4, 150, 4),
            LeaderboardEntry(8, "Emma Davis", "🐧", ClassLevel.CLASS_1, 130, 3)
        )

        val avatar = getAvatarById(currentProfile.avatarId)
        val userEntry = LeaderboardEntry(
            rank = 0, // calculated below
            name = currentProfile.displayName,
            avatarEmoji = avatar.emoji,
            classLevel = currentProfile.classLevel,
            stars = currentProfile.totalStars,
            streak = currentProfile.dailyStreak,
            isCurrentUser = true
        )

        val combined = (baseList + userEntry).sortedByDescending { it.stars }
        return combined.mapIndexed { index, entry ->
            entry.copy(rank = index + 1)
        }
    }

    fun getVirtualTrophies(profile: StudentProfile): List<VirtualTrophy> {
        return listOf(
            VirtualTrophy(
                id = "trophy_quiz_prodigy",
                title = "Quiz Prodigy Cup",
                subtitle = "High-Score Master",
                description = "Earned for achieving high scores and answering questions correctly in smart quiz mode!",
                category = "Quiz High-Score",
                tier = TrophyTier.GOLD,
                trophyEmoji = "🏆",
                requirementText = "Achieve high accuracy on a smart quiz",
                isUnlocked = profile.quizzesCompleted >= 1,
                currentProgress = profile.quizzesCompleted.coerceAtMost(1),
                targetProgress = 1,
                earnedDate = if (profile.quizzesCompleted >= 1) "Earned This Week" else null
            ),
            VirtualTrophy(
                id = "trophy_quiz_marksman",
                title = "Marksman Silver Cup",
                subtitle = "Precision Thinker",
                description = "Consistently high accuracy across at least 5 completed quizzes.",
                category = "Quiz High-Score",
                tier = TrophyTier.SILVER,
                trophyEmoji = "🎯",
                requirementText = "Complete 5 quizzes with high marks",
                isUnlocked = profile.quizzesCompleted >= 5,
                currentProgress = profile.quizzesCompleted.coerceAtMost(5),
                targetProgress = 5,
                earnedDate = if (profile.quizzesCompleted >= 5) "Earned Yesterday" else null
            ),
            VirtualTrophy(
                id = "trophy_math_olympus",
                title = "Math Olympus Trophy",
                subtitle = "Arithmetic Master",
                description = "Awarded for exceptional mastery of numbers, shapes, and arithmetic calculations.",
                category = "Subject Mastery",
                tier = TrophyTier.GOLD,
                trophyEmoji = "🥇",
                requirementText = "Earn 50+ stars in Mathematics",
                isUnlocked = profile.mathStars >= 50,
                currentProgress = profile.mathStars.coerceAtMost(50),
                targetProgress = 50,
                earnedDate = if (profile.mathStars >= 50) "Mastered" else null
            ),
            VirtualTrophy(
                id = "trophy_english_laureate",
                title = "English Laureate Goblet",
                subtitle = "Vocabulary Champion",
                description = "Awarded for reading comprehension, phonics, and grammar excellence.",
                category = "Subject Mastery",
                tier = TrophyTier.GOLD,
                trophyEmoji = "🥇",
                requirementText = "Earn 40+ stars in English",
                isUnlocked = profile.englishStars >= 40,
                currentProgress = profile.englishStars.coerceAtMost(40),
                targetProgress = 40,
                earnedDate = if (profile.englishStars >= 40) "Mastered" else null
            ),
            VirtualTrophy(
                id = "trophy_science_discovery",
                title = "Science Discovery Chalice",
                subtitle = "Junior Explorer",
                description = "Awarded for exploring living things, our solar system, and the natural world.",
                category = "Subject Mastery",
                tier = TrophyTier.GOLD,
                trophyEmoji = "🥇",
                requirementText = "Earn 40+ stars in Science",
                isUnlocked = profile.scienceStars >= 40,
                currentProgress = profile.scienceStars.coerceAtMost(40),
                targetProgress = 40,
                earnedDate = if (profile.scienceStars >= 40) "Mastered" else null
            ),
            VirtualTrophy(
                id = "trophy_bengali_scholar",
                title = "বাংলা পণ্ডিত কাপ (Bengali Cup)",
                subtitle = "Language Champion",
                description = "Awarded for exceptional mastery of Bengali alphabet, phonics, vocabulary, and stories.",
                category = "Subject Mastery",
                tier = TrophyTier.GOLD,
                trophyEmoji = "🇧🇩",
                requirementText = "Earn 40+ stars in Bengali Language",
                isUnlocked = profile.bengaliStars >= 40,
                currentProgress = profile.bengaliStars.coerceAtMost(40),
                targetProgress = 40,
                earnedDate = if (profile.bengaliStars >= 40) "Mastered" else null
            ),
            VirtualTrophy(
                id = "trophy_streak_fire",
                title = "7-Day Flame Chalice",
                subtitle = "Habit Champion",
                description = "Awarded to dedicated students who maintain an unbroken 7-day learning streak.",
                category = "Daily Habit",
                tier = TrophyTier.GOLD,
                trophyEmoji = "🔥",
                requirementText = "Maintain a 7-day daily study streak",
                isUnlocked = profile.dailyStreak >= 7,
                currentProgress = profile.dailyStreak.coerceAtMost(7),
                targetProgress = 7,
                earnedDate = if (profile.dailyStreak >= 7) "Active Streak" else null
            ),
            VirtualTrophy(
                id = "trophy_centurion_star",
                title = "Centurion Star Crown",
                subtitle = "Century of Brilliance",
                description = "Surpass the 100 total stars milestone by completing diverse lessons and quizzes.",
                category = "Star Milestones",
                tier = TrophyTier.DIAMOND,
                trophyEmoji = "👑",
                requirementText = "Collect 100 total stars",
                isUnlocked = profile.totalStars >= 100,
                currentProgress = profile.totalStars.coerceAtMost(100),
                targetProgress = 100,
                earnedDate = if (profile.totalStars >= 100) "Milestone Reached" else null
            ),
            VirtualTrophy(
                id = "trophy_quiz_grandmaster",
                title = "Quiz Grandmaster Urn",
                subtitle = "Quiz Mastermind",
                description = "Complete 10 quizzes with determination and high performance.",
                category = "Quiz High-Score",
                tier = TrophyTier.GOLD,
                trophyEmoji = "🏆",
                requirementText = "Complete 10 total smart quizzes",
                isUnlocked = profile.quizzesCompleted >= 10,
                currentProgress = profile.quizzesCompleted.coerceAtMost(10),
                targetProgress = 10,
                earnedDate = if (profile.quizzesCompleted >= 10) "Champion" else null
            ),
            VirtualTrophy(
                id = "trophy_first_milestone",
                title = "First Milestone Medal",
                subtitle = "Journey Begins",
                description = "Earned on your very first learning step into StarLearner.",
                category = "Milestones",
                tier = TrophyTier.BRONZE,
                trophyEmoji = "🥉",
                requirementText = "Complete your first lesson or quiz",
                isUnlocked = true,
                currentProgress = 1,
                targetProgress = 1,
                earnedDate = "Unlocked"
            ),
            VirtualTrophy(
                id = "trophy_galaxy_crown",
                title = "Constellation Sovereign",
                subtitle = "Constellation Legend",
                description = "The ultimate honor: Gather 250+ stars across all academic realms.",
                category = "Star Milestones",
                tier = TrophyTier.DIAMOND,
                trophyEmoji = "🚀",
                requirementText = "Reach 250 total stars",
                isUnlocked = profile.totalStars >= 250,
                currentProgress = profile.totalStars.coerceAtMost(250),
                targetProgress = 250,
                earnedDate = if (profile.totalStars >= 250) "Elite Status" else null
            )
        )
    }

    fun getSevenDayQuizPerformance(recentAttempts: List<QuizAttemptEntity> = emptyList()): List<QuizPerformanceDay> {
        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Today")
        val dateLabels = listOf("Oct 8", "Oct 9", "Oct 10", "Oct 11", "Oct 12", "Oct 13", "Oct 14")

        val defaultMathScores = listOf(68, 74, 82, 80, 88, 92, 96)
        val defaultEnglishScores = listOf(72, 75, 78, 85, 84, 90, 94)
        val defaultScienceScores = listOf(65, 70, 75, 82, 86, 90, 95)
        val defaultQuizzes = listOf(1, 2, 1, 3, 2, 2, 3)

        return daysOfWeek.mapIndexed { index, day ->
            val mathScore = defaultMathScores[index]
            val engScore = defaultEnglishScores[index]
            val sciScore = defaultScienceScores[index]
            val avg = (mathScore + engScore + sciScore) / 3
            QuizPerformanceDay(
                dayLabel = day,
                dateLabel = dateLabels[index],
                isToday = (index == daysOfWeek.lastIndex),
                mathScorePercent = mathScore,
                englishScorePercent = engScore,
                scienceScorePercent = sciScore,
                overallAveragePercent = avg,
                totalQuizzesTaken = defaultQuizzes[index]
            )
        }
    }

    fun getSubjectPerformanceMetrics(): List<SubjectPerformanceMetric> {
        return listOf(
            SubjectPerformanceMetric(
                subject = SubjectType.MATH,
                averageAccuracyPercent = 88,
                totalQuizzes = 8,
                improvementPercent = 28,
                trendIsPositive = true
            ),
            SubjectPerformanceMetric(
                subject = SubjectType.ENGLISH,
                averageAccuracyPercent = 84,
                totalQuizzes = 6,
                improvementPercent = 22,
                trendIsPositive = true
            ),
            SubjectPerformanceMetric(
                subject = SubjectType.SCIENCE,
                averageAccuracyPercent = 81,
                totalQuizzes = 5,
                improvementPercent = 30,
                trendIsPositive = true
            ),
            SubjectPerformanceMetric(
                subject = SubjectType.BENGALI,
                averageAccuracyPercent = 89,
                totalQuizzes = 7,
                improvementPercent = 25,
                trendIsPositive = true
            )
        )
    }

    fun getPhotoLearningCards(): List<com.example.data.model.PhotoLearningCard> {
        return listOf(
            com.example.data.model.PhotoLearningCard(
                id = "photo_mango",
                emoji = "🥭",
                englishWord = "Mango",
                bengaliWord = "আম",
                bengaliPronunciation = "Aam",
                category = "Fruits & Food",
                funFactEnglish = "Mango is known as the sweet 'King of Fruits'!",
                funFactBengali = "আম মিষ্টি ও সুস্বাদু, একে ফলের রাজা বলা হয়!",
                letterHint = "আ"
            ),
            com.example.data.model.PhotoLearningCard(
                id = "photo_apple",
                emoji = "🍎",
                englishWord = "Apple",
                bengaliWord = "আপেল",
                bengaliPronunciation = "Aapel",
                category = "Fruits & Food",
                funFactEnglish = "An apple a day keeps the doctor away!",
                funFactBengali = "আপেল খুব পুষ্টিকর ও শরীরের জন্য ভালো!",
                letterHint = "আ"
            ),
            com.example.data.model.PhotoLearningCard(
                id = "photo_banana",
                emoji = "🍌",
                englishWord = "Banana",
                bengaliWord = "কলা",
                bengaliPronunciation = "Kola",
                category = "Fruits & Food",
                funFactEnglish = "Bananas are rich in potassium and give instant energy!",
                funFactBengali = "কলা খেলে শরীরে প্রচুর শক্তি পাওয়া যায়!",
                letterHint = "ক"
            ),
            com.example.data.model.PhotoLearningCard(
                id = "photo_watermelon",
                emoji = "🍉",
                englishWord = "Watermelon",
                bengaliWord = "তরমুজ",
                bengaliPronunciation = "Tormuj",
                category = "Fruits & Food",
                funFactEnglish = "Watermelons are over 90% fresh water, perfect for summer!",
                funFactBengali = "গ্রীষ্মকালে লাল তরমুজ মন জুড়িয়ে দেয়!",
                letterHint = "ত"
            ),
            com.example.data.model.PhotoLearningCard(
                id = "photo_tiger",
                emoji = "🐅",
                englishWord = "Royal Bengal Tiger",
                bengaliWord = "বাঘ",
                bengaliPronunciation = "Bagh",
                category = "Animals & Birds",
                funFactEnglish = "Tigers are great swimmers and the pride of Sundarbans!",
                funFactBengali = "রয়েল বেঙ্গল টাইগার সুন্দরবনের গর্ব!",
                letterHint = "ব"
            ),
            com.example.data.model.PhotoLearningCard(
                id = "photo_elephant",
                emoji = "🐘",
                englishWord = "Elephant",
                bengaliWord = "হাতি",
                bengaliPronunciation = "Haati",
                category = "Animals & Birds",
                funFactEnglish = "Elephants are the largest gentle land animals on Earth!",
                funFactBengali = "হাতির বড় কান ও লম্বা শুঁড় থাকে!",
                letterHint = "হ"
            ),
            com.example.data.model.PhotoLearningCard(
                id = "photo_peacock",
                emoji = "🦚",
                englishWord = "Peacock",
                bengaliWord = "ময়ূর",
                bengaliPronunciation = "Moyur",
                category = "Animals & Birds",
                funFactEnglish = "Peacocks dance and spread their iridescent tail feathers in rain!",
                funFactBengali = "বৃষ্টির দিনে ময়ূর পেখম তুলে নাচে!",
                letterHint = "ম"
            ),
            com.example.data.model.PhotoLearningCard(
                id = "photo_magpie",
                emoji = "🐦",
                englishWord = "Doel (Magpie Robin)",
                bengaliWord = "দোয়েল",
                bengaliPronunciation = "Doel",
                category = "Animals & Birds",
                funFactEnglish = "The sweet-whistling Doel is a cherished national bird!",
                funFactBengali = "দোয়েল পাখি মিষ্টি গলায় শিস দিয়ে গান গায়!",
                letterHint = "দ"
            ),
            com.example.data.model.PhotoLearningCard(
                id = "photo_sun",
                emoji = "☀️",
                englishWord = "Sun",
                bengaliWord = "সূর্য",
                bengaliPronunciation = "Shurjo",
                category = "Nature & Colors",
                funFactEnglish = "The Sun gives warmth and light to everything on our planet!",
                funFactBengali = "সূর্য আমাদের আলো ও শক্তি দেয়!",
                letterHint = "স"
            ),
            com.example.data.model.PhotoLearningCard(
                id = "photo_moon",
                emoji = "🌙",
                englishWord = "Moon",
                bengaliWord = "চাঁদ",
                bengaliPronunciation = "Chaand",
                category = "Nature & Colors",
                funFactEnglish = "The silver Moon glows gently across the nighttime sky!",
                funFactBengali = "রাতের আকাশে রুপালি চাঁদ হাসে!",
                letterHint = "চ"
            ),
            com.example.data.model.PhotoLearningCard(
                id = "photo_lily",
                emoji = "🪷",
                englishWord = "Water Lily (Shapla)",
                bengaliWord = "শাপলা",
                bengaliPronunciation = "Shapla",
                category = "Nature & Colors",
                funFactEnglish = "Shapla flowers blossom peacefully on lakes and ponds!",
                funFactBengali = "শাপলা বাংলাদেশের জাতীয় ফুল!",
                letterHint = "শ"
            ),
            com.example.data.model.PhotoLearningCard(
                id = "photo_book",
                emoji = "📚",
                englishWord = "Book",
                bengaliWord = "বই",
                bengaliPronunciation = "Boi",
                category = "Everyday Things",
                funFactEnglish = "Books are our best friends that take us on exciting journeys!",
                funFactBengali = "বই পড়ে আমরা প্রতিদিন নতুন জ্ঞান শিখি!",
                letterHint = "ব"
            ),
            com.example.data.model.PhotoLearningCard(
                id = "photo_bus",
                emoji = "🚌",
                englishWord = "Bus",
                bengaliWord = "বাস",
                bengaliPronunciation = "Baas",
                category = "Everyday Things",
                funFactEnglish = "Buses take boys and girls to school and adventures!",
                funFactBengali = "বাসে চড়ে বন্ধুরা স্কুলে যায়!",
                letterHint = "ব"
            ),
            com.example.data.model.PhotoLearningCard(
                id = "photo_boat",
                emoji = "⛵",
                englishWord = "Sailboat",
                bengaliWord = "নৌকা",
                bengaliPronunciation = "Nouka",
                category = "Everyday Things",
                funFactEnglish = "Sailboats glide over blue river waters powered by gentle breezes!",
                funFactBengali = "নদীতে পাল তোলা নৌকা ভাসতে দেখতে দারুণ লাগে!",
                letterHint = "ন"
            )
        )
    }

    fun getPhotoQuizChallenges(): List<com.example.data.model.PhotoQuizChallenge> {
        return listOf(
            com.example.data.model.PhotoQuizChallenge(
                id = "pq_1",
                photoEmoji = "🥭",
                questionBengali = "ছবিটি দেখে বলো, এটি কোন সুমিষ্ট ফল?",
                questionEnglish = "Look at the picture: Which delicious fruit is this?",
                options = listOf("আম (Mango)", "কলা (Banana)", "আপেল (Apple)", "আনারস (Pineapple)"),
                correctIndex = 0,
                explanation = "সঠিক! এটি পাকা ও রসালো আম (Mango)।"
            ),
            com.example.data.model.PhotoQuizChallenge(
                id = "pq_2",
                photoEmoji = "🐅",
                questionBengali = "ছবি দেখে পশুটি চিনে নাও:",
                questionEnglish = "Look at the picture: Which animal is this?",
                options = listOf("হাতি (Elephant)", "বাঘ (Royal Bengal Tiger)", "সিংহ (Lion)", "হরিণ (Deer)"),
                correctIndex = 1,
                explanation = "চমৎকার! এটি হলো ডোরাকাটা বাঘ (Tiger)।"
            ),
            com.example.data.model.PhotoQuizChallenge(
                id = "pq_3",
                photoEmoji = "🪷",
                questionBengali = "পানিতে ফোটা এই সুন্দর ফুলটির নাম কী?",
                questionEnglish = "What is the name of this beautiful water flower?",
                options = listOf("গোলাপ (Rose)", "শাপলা (Water Lily)", "সূর্যমুখী (Sunflower)", "বেলি (Jasmine)"),
                correctIndex = 1,
                explanation = "অসাধারণ! শাপলা হলো আমাদের জাতীয় ফুল।"
            ),
            com.example.data.model.PhotoQuizChallenge(
                id = "pq_4",
                photoEmoji = "🐘",
                questionBengali = "বড় কানের এই শান্ত প্রাণীটির নাম কী?",
                questionEnglish = "What is the name of this big gentle animal?",
                options = listOf("জিরাফ (Giraffe)", "হাতি (Elephant)", "ঘোড়া (Horse)", "উট (Camel)"),
                correctIndex = 1,
                explanation = "একদম ঠিক! এটি হলো লম্বা শুঁড়ের হাতি (Elephant)।"
            ),
            com.example.data.model.PhotoQuizChallenge(
                id = "pq_5",
                photoEmoji = "⛵",
                questionBengali = "নদীতে পাল তুলে চলা যানটি কী?",
                questionEnglish = "What is this boat that sails on rivers?",
                options = listOf("উড়োজাহাজ (Aeroplane)", "নৌকা (Sailboat)", "রেলগাড়ি (Train)", "বাস (Bus)"),
                correctIndex = 1,
                explanation = "খুব ভালো! এটি নদীমাতৃক বাংলার পাল তোলা নৌকা।"
            ),
            com.example.data.model.PhotoQuizChallenge(
                id = "pq_6",
                photoEmoji = "☀️",
                questionBengali = "দিনের বেলা আকাশে কী আলো ছড়ায়?",
                questionEnglish = "What lights up our world in the daytime?",
                options = listOf("চাঁদ (Moon)", "সূর্য (Sun)", "তারা (Star)", "মেঘ (Cloud)"),
                correctIndex = 1,
                explanation = "দারুণ! সোনালি সূর্য প্রতিদিন সকালে পুব আকাশে ওঠে।"
            )
        )
    }
}

