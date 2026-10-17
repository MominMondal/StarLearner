package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.db.LessonEntity
import com.example.data.db.QuizAttemptEntity
import com.example.data.db.StudentProfileEntity
import com.example.data.db.WeeklyStudyLogEntity
import com.example.data.model.ClassLevel
import com.example.data.model.StudentProfile
import com.example.data.model.SubjectType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class OfflineRepository(private val database: AppDatabase) {

    val allLessons: Flow<List<LessonEntity>> = database.lessonDao().getAllLessons()
    val weeklyLogs: Flow<List<WeeklyStudyLogEntity>> = database.studyLogDao().getWeeklyLogs()
    val quizAttempts: Flow<List<QuizAttemptEntity>> = database.quizAttemptDao().getAllAttempts()
    val studentProfileFlow: Flow<StudentProfileEntity?> = database.studentProfileDao().getProfile()

    suspend fun initializeDefaultDataIfEmpty() {
        // Pre-populate lessons if empty
        val existingLessons = allLessons.firstOrNull()
        if (existingLessons.isNullOrEmpty()) {
            val initialLessons = listOf(
                // === CBSE (English) Medium Lessons ===
                LessonEntity("l_m1_1", SubjectType.MATH.name, ClassLevel.CLASS_1.name, "Counting 1 to 20", "Fun counting with fruits & toys", 15, true, 10, com.example.data.model.CurriculumMedium.CBSE_ENGLISH.name),
                LessonEntity("l_m1_2", SubjectType.MATH.name, ClassLevel.CLASS_1.name, "Basic Shapes Around Us", "Circles, squares & triangles", 12, true, 10, com.example.data.model.CurriculumMedium.CBSE_ENGLISH.name),
                LessonEntity("l_m2_1", SubjectType.MATH.name, ClassLevel.CLASS_2.name, "Two-Digit Addition", "Adding tens and ones with blocks", 20, true, 10, com.example.data.model.CurriculumMedium.CBSE_ENGLISH.name),
                LessonEntity("l_m2_2", SubjectType.MATH.name, ClassLevel.CLASS_2.name, "Subtraction Adventures", "Taking away numbers smoothly", 18, false, 0, com.example.data.model.CurriculumMedium.CBSE_ENGLISH.name),
                LessonEntity("l_m3_1", SubjectType.MATH.name, ClassLevel.CLASS_3.name, "Multiplication Tables", "Learn 2, 3, 4, 5 times tables", 22, true, 10, com.example.data.model.CurriculumMedium.CBSE_ENGLISH.name),
                LessonEntity("l_m3_2", SubjectType.MATH.name, ClassLevel.CLASS_3.name, "Telling Time & Clocks", "Hours and minutes made easy", 15, false, 0, com.example.data.model.CurriculumMedium.CBSE_ENGLISH.name),
                LessonEntity("l_m4_1", SubjectType.MATH.name, ClassLevel.CLASS_4.name, "Fractions & Parts of a Whole", "Halves, thirds, and quarters", 25, true, 10, com.example.data.model.CurriculumMedium.CBSE_ENGLISH.name),

                LessonEntity("l_e1_1", SubjectType.ENGLISH.name, ClassLevel.CLASS_1.name, "Phonics: Letter Sounds", "Listen and say A, B, C sounds", 12, true, 10, com.example.data.model.CurriculumMedium.CBSE_ENGLISH.name),
                LessonEntity("l_e1_2", SubjectType.ENGLISH.name, ClassLevel.CLASS_1.name, "Rhyming Word Pairs", "Cat & Hat, Dog & Log", 14, true, 10, com.example.data.model.CurriculumMedium.CBSE_ENGLISH.name),
                LessonEntity("l_e2_1", SubjectType.ENGLISH.name, ClassLevel.CLASS_2.name, "Action Words (Verbs)", "Jump, run, read, and write", 15, true, 10, com.example.data.model.CurriculumMedium.CBSE_ENGLISH.name),
                LessonEntity("l_e2_2", SubjectType.ENGLISH.name, ClassLevel.CLASS_2.name, "Opposites & Antonyms", "Hot & Cold, Big & Small", 16, true, 10, com.example.data.model.CurriculumMedium.CBSE_ENGLISH.name),
                LessonEntity("l_e3_1", SubjectType.ENGLISH.name, ClassLevel.CLASS_3.name, "Describing Words (Adjectives)", "Colors, sizes, and feelings", 18, false, 0, com.example.data.model.CurriculumMedium.CBSE_ENGLISH.name),
                LessonEntity("l_e4_1", SubjectType.ENGLISH.name, ClassLevel.CLASS_4.name, "Story Reading & Comprehension", "Read short adventures and answer", 25, false, 0, com.example.data.model.CurriculumMedium.CBSE_ENGLISH.name),

                LessonEntity("l_s1_1", SubjectType.SCIENCE.name, ClassLevel.CLASS_1.name, "Our Amazing 5 Senses", "Sight, smell, touch, taste, hearing", 15, true, 10, com.example.data.model.CurriculumMedium.CBSE_ENGLISH.name),
                LessonEntity("l_s1_2", SubjectType.SCIENCE.name, ClassLevel.CLASS_1.name, "Living vs Non-Living Things", "Animals and plants grow!", 15, true, 10, com.example.data.model.CurriculumMedium.CBSE_ENGLISH.name),
                LessonEntity("l_s2_1", SubjectType.SCIENCE.name, ClassLevel.CLASS_2.name, "Parts of a Green Plant", "Roots, stems, leaves and flowers", 18, true, 10, com.example.data.model.CurriculumMedium.CBSE_ENGLISH.name),
                LessonEntity("l_s2_2", SubjectType.SCIENCE.name, ClassLevel.CLASS_2.name, "Animal Habitats & Homes", "Forest, ocean, desert and jungle", 16, true, 10, com.example.data.model.CurriculumMedium.CBSE_ENGLISH.name),
                LessonEntity("l_s4_1", SubjectType.SCIENCE.name, ClassLevel.CLASS_4.name, "States of Matter", "Solids, liquids, and gases", 20, false, 0, com.example.data.model.CurriculumMedium.CBSE_ENGLISH.name),

                // === Bangla Medium (বাংলা মাধ্যম) Lessons ===
                LessonEntity("l_b1_1", SubjectType.BENGALI.name, ClassLevel.CLASS_1.name, "বাংলা স্বরবর্ণ (অ, আ, ই, ঈ)", "অ-তে অজগর, আ-তে আম ও স্বরবর্ণ শেখা", 12, true, 10, com.example.data.model.CurriculumMedium.BANGLA.name),
                LessonEntity("l_b1_2", SubjectType.BENGALI.name, ClassLevel.CLASS_1.name, "সহজ শব্দ ও ছবি", "ফল, ফুল ও পশুপাখির মিষ্টি নাম", 14, true, 10, com.example.data.model.CurriculumMedium.BANGLA.name),
                LessonEntity("l_b1_3", SubjectType.MATH.name, ClassLevel.CLASS_1.name, "বাংলা সংখ্যা গণনা (১-১০)", "১=এক, ২=দুই মজার গণনা ও ছবি", 10, true, 10, com.example.data.model.CurriculumMedium.BANGLA.name),
                LessonEntity("l_b2_1", SubjectType.BENGALI.name, ClassLevel.CLASS_2.name, "বাংলা ব্যঞ্জনবর্ণ (ক, খ, গ, ঘ)", "ক-তে কলম, খ-তে খরগোশ ও ব্যঞ্জনবর্ণ", 16, true, 10, com.example.data.model.CurriculumMedium.BANGLA.name),
                LessonEntity("l_b2_2", SubjectType.BENGALI.name, ClassLevel.CLASS_2.name, "মজার ছড়া ও শব্দ", "হাট্টিমাটিম টিম ও লোকছড়া", 15, true, 10, com.example.data.model.CurriculumMedium.BANGLA.name),
                LessonEntity("l_b2_3", SubjectType.MATH.name, ClassLevel.CLASS_2.name, "দুই অঙ্কের যোগ ও বিয়োগ", "দশক ও এককের হিসাব ও সমস্যা সমাধান", 18, true, 10, com.example.data.model.CurriculumMedium.BANGLA.name),
                LessonEntity("l_b3_1", SubjectType.BENGALI.name, ClassLevel.CLASS_3.name, "বিপরীত ও সমার্থক শব্দ", "ছোট-বড়, আলো-আঁধার, দিন-রাত ও রবি-সূর্য", 18, false, 0, com.example.data.model.CurriculumMedium.BANGLA.name),
                LessonEntity("l_b3_2", SubjectType.SCIENCE.name, ClassLevel.CLASS_3.name, "বাংলার প্রকৃতি ও ছয় ঋতু", "গ্রীষ্ম, বর্ষা, শরৎ, হেমন্ত, শীত, বসন্ত", 16, false, 0, com.example.data.model.CurriculumMedium.BANGLA.name),
                LessonEntity("l_b4_1", SubjectType.BENGALI.name, ClassLevel.CLASS_4.name, "বাংলা গল্প ও বাক্য পাঠ", "সুন্দর অনুচ্ছেদ ও ভাষা দক্ষতা", 22, false, 0, com.example.data.model.CurriculumMedium.BANGLA.name),

                // === Hindi Medium (हिंदी माध्यम) Lessons ===
                LessonEntity("l_h1_1", SubjectType.BENGALI.name, ClassLevel.CLASS_1.name, "हिंदी स्वर (अ, आ, इ, ई)", "अ से अनार, आ से आम व स्वर ज्ञान", 12, true, 10, com.example.data.model.CurriculumMedium.HINDI.name),
                LessonEntity("l_h1_2", SubjectType.ENGLISH.name, ClassLevel.CLASS_1.name, "चित्र और सरल शब्द", "फल, फूल और प्यारे जानवरों के नाम", 14, true, 10, com.example.data.model.CurriculumMedium.HINDI.name),
                LessonEntity("l_h1_3", SubjectType.MATH.name, ClassLevel.CLASS_1.name, "हिंदी गिनती (१ से १०)", "वस्तुओं को गिनना व अंक पहचान", 10, true, 10, com.example.data.model.CurriculumMedium.HINDI.name),
                LessonEntity("l_h2_1", SubjectType.BENGALI.name, ClassLevel.CLASS_2.name, "हिंदी व्यंजन (क, ख, ग, घ)", "क से कबूतर, ख से खरगोश व्यंजन", 16, true, 10, com.example.data.model.CurriculumMedium.HINDI.name),
                LessonEntity("l_h2_2", SubjectType.BENGALI.name, ClassLevel.CLASS_2.name, "मधुर बालगीत व कविता", "मछली जल की रानी है, तितली उड़ी", 15, true, 10, com.example.data.model.CurriculumMedium.HINDI.name),
                LessonEntity("l_h2_3", SubjectType.MATH.name, ClassLevel.CLASS_2.name, "दहाई और इकाई का जोड़", "दो अंकों की सरल गणितीय संक्रियाएँ", 18, true, 10, com.example.data.model.CurriculumMedium.HINDI.name),
                LessonEntity("l_h3_1", SubjectType.BENGALI.name, ClassLevel.CLASS_3.name, "विलोम व पर्यायवाची शब्द", "छोटा-बड़ा, सच-झूठ, दिन-रात", 18, false, 0, com.example.data.model.CurriculumMedium.HINDI.name),
                LessonEntity("l_h3_2", SubjectType.SCIENCE.name, ClassLevel.CLASS_3.name, "पेड़-पौधे और पर्यावरण", "जड़, तना, पत्ते और जीव-जगत", 16, false, 0, com.example.data.model.CurriculumMedium.HINDI.name),
                LessonEntity("l_h4_1", SubjectType.BENGALI.name, ClassLevel.CLASS_4.name, "कहानियाँ व नैतिक शिक्षा", "एकता में बल और सुंदर वाक्य रचना", 22, false, 0, com.example.data.model.CurriculumMedium.HINDI.name)
            )
            database.lessonDao().insertLessons(initialLessons)
        }

        // Pre-populate weekly study logs if empty
        val existingLogs = weeklyLogs.firstOrNull()
        if (existingLogs.isNullOrEmpty()) {
            val initialLogs = listOf(
                WeeklyStudyLogEntity("Mon", 0, minutesSpent = 25, lessonsCompleted = 2, quizzesAced = 1, starsGained = 25),
                WeeklyStudyLogEntity("Tue", 1, minutesSpent = 30, lessonsCompleted = 2, quizzesAced = 2, starsGained = 30),
                WeeklyStudyLogEntity("Wed", 2, minutesSpent = 20, lessonsCompleted = 1, quizzesAced = 1, starsGained = 20),
                WeeklyStudyLogEntity("Thu", 3, minutesSpent = 35, lessonsCompleted = 3, quizzesAced = 2, starsGained = 35),
                WeeklyStudyLogEntity("Fri", 4, minutesSpent = 15, lessonsCompleted = 1, quizzesAced = 1, starsGained = 15),
                WeeklyStudyLogEntity("Sat", 5, minutesSpent = 25, lessonsCompleted = 2, quizzesAced = 1, starsGained = 20),
                WeeklyStudyLogEntity("Sun", 6, minutesSpent = 20, lessonsCompleted = 1, quizzesAced = 1, starsGained = 20)
            )
            database.studyLogDao().insertOrUpdateLogs(initialLogs)
        }

        // Initialize default student profile entity if empty
        val existingProfile = studentProfileFlow.firstOrNull()
        if (existingProfile == null) {
            val defaultEntity = StudentProfileEntity(
                id = "current_student",
                displayName = "Alex Cadet",
                avatarId = "lion",
                classLevel = ClassLevel.CLASS_2.name,
                totalStars = 165,
                dailyStreak = 5,
                completedLessons = 12,
                quizzesCompleted = 9,
                mathStars = 65,
                englishStars = 55,
                scienceStars = 45,
                bengaliStars = 45,
                hindiStars = 40,
                learningMedium = com.example.data.model.CurriculumMedium.CBSE_ENGLISH.name,
                unlockedBadgesString = "math_whiz,early_bird,streak_3,science_star,word_master,bengali_star,hindi_star"
            )
            database.studentProfileDao().saveProfile(defaultEntity)
        }
    }

    suspend fun recordQuizAttempt(
        subject: SubjectType,
        classLevel: ClassLevel,
        score: Int,
        total: Int,
        starsEarned: Int
    ) {
        val attempt = QuizAttemptEntity(
            subject = subject.name,
            classLevel = classLevel.name,
            score = score,
            total = total,
            starsEarned = starsEarned,
            timestamp = System.currentTimeMillis()
        )
        database.quizAttemptDao().insertAttempt(attempt)

        // Log 10 minutes of study for the quiz
        database.studyLogDao().addStudyTime("Sun", minutes = 10, stars = starsEarned)
    }

    suspend fun saveStudentProfile(profile: StudentProfile) {
        val entity = StudentProfileEntity(
            id = "current_student",
            displayName = profile.displayName,
            avatarId = profile.avatarId,
            classLevel = profile.classLevel.name,
            totalStars = profile.totalStars,
            dailyStreak = profile.dailyStreak,
            completedLessons = profile.completedLessons,
            quizzesCompleted = profile.quizzesCompleted,
            mathStars = profile.mathStars,
            englishStars = profile.englishStars,
            scienceStars = profile.scienceStars,
            bengaliStars = profile.bengaliStars,
            hindiStars = profile.hindiStars,
            learningMedium = profile.learningMedium.name,
            unlockedBadgesString = profile.unlockedBadgeIds.joinToString(",")
        )
        database.studentProfileDao().saveProfile(entity)
    }

    suspend fun markLessonCompleted(lessonId: String, stars: Int = 10) {
        database.lessonDao().markLessonComplete(lessonId, stars, System.currentTimeMillis())
        database.studyLogDao().addStudyTime("Sun", minutes = 15, stars = stars)
    }
}
