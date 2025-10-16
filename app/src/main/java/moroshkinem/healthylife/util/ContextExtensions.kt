package moroshkinem.healthylife.util

import android.content.Context
import moroshkinem.healthylife.HealthyLifeApp

inline val Context.appComponent: HealthyLifeApp
    get() = this.applicationContext as HealthyLifeApp