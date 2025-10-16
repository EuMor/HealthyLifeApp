package moroshkinem.healthylife

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import moroshkinem.healthylife.data.DataStoreManager
import moroshkinem.healthylife.data.database.AppDatabase
import moroshkinem.healthylife.data.repos.MealRepository
import moroshkinem.healthylife.data.repos.MedicationRepository
import moroshkinem.healthylife.data.repos.SleepRepository
import moroshkinem.healthylife.data.repos.StepsRepository
import moroshkinem.healthylife.data.repos.WaterRepository
import moroshkinem.healthylife.data.sensors.StepCounterManager
import moroshkinem.healthylife.ui.screens.HomeScreen
import moroshkinem.healthylife.ui.screens.ProfileScreen
import moroshkinem.healthylife.ui.screens.StatsScreen
import moroshkinem.healthylife.ui.screens.WaterScreen
import moroshkinem.healthylife.ui.theme.HealthyLifeAppTheme
import moroshkinem.healthylife.ui.viewmodels.HomeViewModel
import moroshkinem.healthylife.ui.viewmodels.HomeViewModelFactory
import moroshkinem.healthylife.ui.viewmodels.ProfileViewModel
import moroshkinem.healthylife.ui.viewmodels.StatsViewModel
import moroshkinem.healthylife.ui.viewmodels.WaterViewModel
import moroshkinem.healthylife.util.appComponent


class HealthyLifeApp : Application() {
    lateinit var db: AppDatabase
    lateinit var waterRepository: WaterRepository
    lateinit var stepsRepository: StepsRepository // ← ДОБАВЬ
    lateinit var dataStoreManager: DataStoreManager
    lateinit var stepCounterManager: StepCounterManager
    lateinit var mealRepository: MealRepository
    lateinit var sleepRepository: SleepRepository
    lateinit var medicationRepository: MedicationRepository

    override fun onCreate() {
        super.onCreate()
        db = AppDatabase.getDatabase(this)
        waterRepository = WaterRepository(db.waterDao())
        stepsRepository = StepsRepository(db.stepsDao())  // ← ИНИЦИАЛИЗАЦИЯ
        dataStoreManager = DataStoreManager(this)
        mealRepository = MealRepository(db.mealDao())
        sleepRepository = SleepRepository(db.sleepDao())
        medicationRepository = MedicationRepository(db.medicalDao())
        stepCounterManager = StepCounterManager(
            context = this,
            dataStoreManager = dataStoreManager,
            stepsRepository = stepsRepository  // ← ПЕРЕДАЁМ
        )
        stepCounterManager.start()
    }
}


@Composable
fun HealthyLifeAppUI() {
    val context = LocalContext.current
    val appComponent = context.appComponent
    val navController = rememberNavController()
    //val appComponent = getAppComponent() // ✅ Теперь доступно

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            waterRepository = appComponent.waterRepository,
            dataStoreManager = appComponent.dataStoreManager,
            stepCounterManager = appComponent.stepCounterManager,
            stepsRepository = appComponent.stepsRepository,
            sleepRepository =  appComponent.sleepRepository,
            mealRepository = appComponent.mealRepository,
            medicationRepository = appComponent.medicationRepository
        )
    )
    // Создаём HomeViewModel
//    val homeViewModel: HomeViewModel = viewModel(
//        factory = object : ViewModelProvider.Factory {
//            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
//                    @Suppress("UNCHECKED_CAST")
//                    return HomeViewModel(appComponent.waterRepository, appComponent.dataStoreManager) as T
//                }
//                throw IllegalArgumentException("Unknown ViewModel")
//            }
//        }
//    )

    HealthyLifeAppTheme  {
        Surface {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(
                        viewModel = homeViewModel,
                        navigateToWater = { navController.navigate("water") },
                        navigateToStats = { navController.navigate("stats") },
                        navigateToProfile = { navController.navigate("profile") }
                    )
                }
                composable("water") {
                    WaterScreen(
                        viewModel = viewModel(factory = WaterViewModelFactory(
                            appComponent.waterRepository,
                            appComponent.dataStoreManager
                        ))
                    )
                }
                composable("stats") {
                    val statsViewModel: StatsViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
                                    @Suppress("UNCHECKED_CAST")
                                    return StatsViewModel(
                                        appComponent.waterRepository,
                                        appComponent.stepsRepository
                                    ) as T
                                }
                                throw IllegalArgumentException("Unknown ViewModel")
                            }
                        }
                    )
                    StatsScreen(viewModel = statsViewModel)
                }

                composable("profile") {
                    val profileViewModel: ProfileViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                                    @Suppress("UNCHECKED_CAST")
                                    return ProfileViewModel(appComponent.dataStoreManager) as T
                                }
                                throw IllegalArgumentException("Unknown ViewModel")
                            }
                        }
                    )
                    ProfileScreen(viewModel = profileViewModel)
                }
            }
        }
    }
}
class WaterViewModelFactory(
    private val repository: WaterRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WaterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WaterViewModel(repository, dataStoreManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}