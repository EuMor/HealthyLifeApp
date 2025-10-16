package moroshkinem.healthylife.data.model

data class UserProfile(
    val weight: Float = 70f,        // кг
    val height: Int = 175,           // см
    val age: Int = 25,
    val gender: Gender = Gender.MALE,
    val dailyStepGoal: Int = 10000,
    val dailyCalorieGoal: Int = 2000,
    val dailySleepGoal: Float = 8f // ✅ ДОБАВЛЕНО

)

enum class Gender {
    MALE, FEMALE
}